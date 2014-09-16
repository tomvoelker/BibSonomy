package org.bibsonomy.lucene.util.generator;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

/**
 * reads data from database and builds ElasticSearch index for all resource entries
 * 
 * @author lka
 * 
 * @param <R>
 *            the resource of the index to generate
 */
public class ElasticSearchGenerateResourceIndex<R extends Resource> implements Runnable {

	public static final String TMP_INDEX_SUFFIX = ".tmp";

	protected static final Log log = LogFactory.getLog(ElasticSearchGenerateResourceIndex.class);

	/** the number of posts to fetch from the database by a single generating step */
	private static final int SQL_BLOCKSIZE = 25000;

	/** database logic */
	private LuceneDBInterface<R> dbLogic;

	private int numberOfThreads = 1;

	/** converts post model objects to lucene documents */
	private LuceneResourceConverter<R> resourceConverter;

	/** set to true if the generator is currently generating an index */
	private boolean isRunning;

	private int numberOfPosts;
	private int numberOfPostsImported;

	//Node for ElasticSearch client
	private Node node;
	
	/**
	 * frees allocated resources and closes all files
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public void shutdown() throws CorruptIndexException, IOException {

		//closing the node
		log.info("closing node " + this.node);
		//on shutdown
		this.node.close();
					
	}

	/**
	 * Read in data from database and build index.
	 * 
	 * Database as well as index files are configured in the lucene.properties
	 * file.
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void generateIndex() throws CorruptIndexException, IOException, ClassNotFoundException, SQLException {
		// Allow only one index-generation at a time.
		if (this.isRunning) {
			return;
		}

		this.isRunning = true;

		// generate index
		this.createIndexFromDatabase();

		this.isRunning = false;
	}

//	/**
//	 * deletes the old index and replaces it with the new one
//	 */
//	public void replaceIndex() {
//		
//	}


	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void createIndexFromDatabase() throws CorruptIndexException, IOException {
		log.info("Filling index with database post entries.");

		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);

		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.dbLogic.getNumberOfPosts();
		log.info("Number of post entries: " + this.numberOfPosts);

		// initialize variables
		final Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate = this.dbLogic.getLastLogDate();

		if (lastLogDate == null) {
			lastLogDate = new Date(System.currentTimeMillis() - 1000);
		}

		log.info("Start writing data to lucene index (with duplicate detection)");

		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		int lastContenId = -1;
		int postListSize = 0;
		
		//on startup
		this.node = nodeBuilder().client(true).node();
		Client client = node.client();
					
		do {
			postList = this.dbLogic.getPostEntries(lastContenId, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<R> post : postList) {
				post.setLastLogDate(lastLogDate);
				post.setLastTasId(lastTasId);

				if (ElasticSearchGenerateResourceIndex.this.isNotSpammer(post)) {
					// create index document from post model
					final Document doc = ElasticSearchGenerateResourceIndex.this.resourceConverter.readPost(post);
					IndexResponse response = client.prepareIndex("bibsonomy", "posts")
												  	.setSource(doc)
												  	.execute()
												  	.actionGet();
					
					ElasticSearchGenerateResourceIndex.this.importedPost(post);
				}

			}

			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId();
			}
		} while (postListSize == SQL_BLOCKSIZE);

		try {
			executor.shutdown();
			executor.awaitTermination(18, TimeUnit.HOURS);

			// all done
			// log.info("(" + i + " indexed entries, " + is +
			// " not indexed spam entries)");
		} catch (final InterruptedException e) {
			log.error("lucene finished not in 18 hours", e);
		}
	}

	
	protected synchronized void importedPost(final LucenePost<R> post) {
		// update counter
		this.numberOfPostsImported++;
	}

	/**
	 * test if given post is a spam post
	 * 
	 * @param post
	 * @return <code>true</code> iff the post user is a spammer
	 */
	protected boolean isNotSpammer(final Post<? extends Resource> post) {
		for (final Group group : post.getGroups()) {
			if (group.getGroupId() < 0) {
				/*
				 * spammer group found => user is spammer
				 */
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the progress-percentage
	 * 
	 * @return the progressPercentage
	 */
	public int getProgressPercentage() {
		return (int) Math.round(100 * ((double) this.numberOfPostsImported / this.numberOfPosts));
	}

	/** Run the index-generation in a thread. */
	@Override
	public void run() {
		try {
			this.generateIndex();
		} catch (final Exception e) {
			log.error("Failed to generate " + this.node + "-index!", e);
		} finally {
			try {
				this.shutdown();
			} catch (final Exception e) {
				log.error("Failed to close node!", e);
			}
		}
	}

	/**
	 * @param dbLogic
	 *            the dbLogic to set
	 */
	public void setLogic(final LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	/**
	 * @param resourceConverter
	 *            the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @param numberOfThreads
	 *            the numberOfThreads to set
	 */
	public void setNumberOfThreads(final int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

}
