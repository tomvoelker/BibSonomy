package org.bibsonomy.lucene.util.generator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * reads data from database and builds lucene index for all resource entries
 * 
 * @author sst
 * @author fei
 * @version $Id$
 * 
 * @param <R> the resource of the index to generate
 */
public class LuceneGenerateResourceIndex<R extends Resource> implements Runnable {
	protected static final Log log = LogFactory.getLog(LuceneGenerateResourceIndex.class);

	/** TODO: improve documentation */
	private static final int SQL_BLOCKSIZE = 25000;
	
	/** database logic */
	private LuceneDBInterface<R> dbLogic;
		
	/** writes the resource index */
	private IndexWriter indexWriter;
	
	/** converts post model objects to lucene documents */
	private LuceneResourceConverter<R> resourceConverter;
	
	private LuceneResourceIndex<R> resourceIndex;
	
	private GenerateIndexCallback<R> callback = null;
	
	/** the progress-percentage if index-generation is running */
	private int progressPercentage;
	
	/** set to true if the generator is currently generating an index */
	private boolean isRunning;
	
	/**
	 * constructor
	 */
	public LuceneGenerateResourceIndex() {
	}

	/**
	 * frees allocated resources and closes all files
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public void shutdown() throws CorruptIndexException, IOException {
		this.indexWriter.close();
		
		if (this.callback != null) {
		    this.callback.generatedIndex(this.resourceIndex);
		}
	}
	
	/**
	 * Read in data from database and build index. 
	 * 
	 * Database as well as index files are configured in the lucene.properties file.
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void generateIndex() throws CorruptIndexException, IOException, ClassNotFoundException, SQLException {
		// Allow only one index-generation at a time.
		if (this.isRunning) return;
		
		this.isRunning = true;
		
		// delete the old index, if exists
    	this.resourceIndex.deleteIndex();
    	
		// open index
		this.createEmptyIndex();

		// generate index
		this.createIndexFromDatabase();
		
		// activate the index
		this.resourceIndex.reset();
		
		this.isRunning = false;
	}
	
	/**
	 * Create empty index. Attributes must already be configured (via init()).
	 *  
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	protected void createEmptyIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		// create index, possibly overwriting existing index files
		log.info("Creating empty lucene index...");
		final Directory indexDirectory = FSDirectory.open(new File(this.resourceIndex.getIndexPath()));
		this.indexWriter = new IndexWriter(indexDirectory, this.resourceIndex.getAnalyzer(), true, this.resourceIndex.getMaxFieldLength()); 
	}
	
	/**
	 * creates index of resource entries
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void createIndexFromDatabase() throws CorruptIndexException, IOException {
		log.info("Filling index with database post entries.");
		
		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		final int numberOfPosts = this.dbLogic.getNumberOfPosts();
		this.progressPercentage = 0;
		log.info("Number of post entries: "+  this.dbLogic.getNumberOfPosts());
		
		// initialize variables
		final Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate  = this.dbLogic.getLastLogDate();
		
		if (lastLogDate == null) {
		    lastLogDate = new Date(System.currentTimeMillis() - 1000);
		}
		
		// get all relevant resources from corresponding resource table
		int i = 0;		// number of evaluated entries 
		int is = 0;		// number of spam entries 

		log.info("Start writing data to lucene index (with duplicate detection)");
		
		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		// track content_ids for duplicate detection
		final Map<Integer, Integer> dupMap = new HashMap<Integer, Integer>();
		int lastContenId = -1;
		int postListSize = 0;
		do {
			postList = this.dbLogic.getPostEntries(lastContenId, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<R> postEntry : postList) {
				// look for duplicates
				final Integer contentId = postEntry.getContentId();
				if (dupMap.containsKey(contentId)) {
					log.error("Found duplicate for content id '" + contentId + "' at '" + skip + "' (was '" + dupMap.get(contentId) + "')");
				} else {
					dupMap.put(contentId, skip);
				}
				// update management fields
				postEntry.setLastLogDate(lastLogDate);
				postEntry.setLastTasId(lastTasId);
				
				// create index document from post model
				final Document post = this.resourceConverter.readPost(postEntry);

				// add (non-spam) document to index
				// FIXME: is this check necessary?
				if (isNotSpammer(postEntry)) {
					indexWriter.addDocument(post);
					i++;
				} else {
					is++;
				}
				
				if (postListSize > 0) {
					lastContenId = postList.get(postListSize - 1).getContentId();
				}
			}
			
			this.progressPercentage = (int) Math.round(100 * ((double) skip / numberOfPosts));
			log.info(this.progressPercentage + "% of index-generation done!");
		} while (postListSize == SQL_BLOCKSIZE);

		// optimize index
		log.info("optimizing index " + this.resourceIndex);
		indexWriter.optimize();
		
		// close resource indexWriter
		log.info("closing index " + this.resourceIndex);
		indexWriter.close();

		// all done
		log.info("(" + i + " indexed entries, " + is + " not indexed spam entries)");
	}
	
	/**
	 * Get the progress-percentage
	 * @return the progressPercentage
	 */
	public int getProgressPercentage() {
		return progressPercentage;
	}
	
	/** Run the index-generation in a thread. */
	@Override
	public void run() {
        try {
			this.generateIndex();
		} catch (final Exception e) {
			log.error("Failed to generate " + this.resourceIndex + "-index!", e);
		} finally {
			try {
				this.shutdown();
			} catch (final Exception e) {
				log.error("Failed to close index-writer!", e);
			}
		}
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
	 * @param dbLogic the dbLogic to set
	 */
	public void setLogic(final LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(final GenerateIndexCallback<R> callback) {
		this.callback = callback;
	}
	
	/**
	 * @param resourceIndex the resourceIndex to set
	 */
	public void setResourceIndex(final LuceneResourceIndex<R> resourceIndex) {
		this.resourceIndex = resourceIndex;
	}
}
