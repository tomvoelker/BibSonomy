package org.bibsonomy.lucene.util.generator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.ranking.FileFolkRankDao;
import org.bibsonomy.lucene.ranking.FolkRankDao;
import org.bibsonomy.lucene.ranking.FolkRankInfo;
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
	
	private int numberOfThreads = 1;
	
	/** converts post model objects to lucene documents */
	private LuceneResourceConverter<R> resourceConverter;
	
	private LuceneResourceIndex<R> resourceIndex;
	
	private GenerateIndexCallback<R> callback = null;
	
	/** set to true if the generator is currently generating an index */
	private boolean isRunning;

	private int numberOfPosts;
	private int numberOfPostsImported;
	
	// FolkRank caches
	private String lastHash = "";
	private Map<Character, String> tagFieldValues = new HashMap<Character, String>();
	private Map<Character, String> userFieldValues = new HashMap<Character, String>();
	

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
		if (this.isRunning) {
			return;
		}
		
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
		
		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);
		
		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.dbLogic.getNumberOfPosts();
		log.info("Number of post entries: "+  this.numberOfPosts);
		
		// initialize variables
		final Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate  = this.dbLogic.getLastLogDate();
		
		if (lastLogDate == null) {
		    lastLogDate = new Date(System.currentTimeMillis() - 1000);
		}

		log.info("Start writing data to lucene index (with duplicate detection)");
		
		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		int lastContenId = -1;
		int postListSize = 0;
		do {
			postList = this.dbLogic.getPostEntries(lastContenId, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<R> post : postList) {
				post.setLastLogDate(lastLogDate);
				post.setLastTasId(lastTasId);
				executor.execute(new Runnable() {
					
					@Override
					public void run() {
						if (LuceneGenerateResourceIndex.this.isNotSpammer(post)) {  
							// create index document from post model
							final Document doc = LuceneGenerateResourceIndex.this.resourceConverter.readPost(post);
							try {
								LuceneGenerateResourceIndex.this.indexWriter.addDocument(doc);
								LuceneGenerateResourceIndex.this.importedPost(post);
							} catch (final IOException e) {
								log.error("error while inserting post " + post.getUser().getName() + "/" + post.getResource().getIntraHash(), e);
							}
						}
					}
				});
			}
			
			if (postListSize > 0) {
				lastContenId = postList.get(postListSize - 1).getContentId();
			}
		} while (postListSize == SQL_BLOCKSIZE);
		
		try {
			executor.shutdown();
			executor.awaitTermination(18, TimeUnit.HOURS);
			
			// optimize index
			log.info("optimizing index " + this.resourceIndex);
			this.indexWriter.optimize();
			
			// close resource indexWriter
			log.info("closing index " + this.resourceIndex);
			this.indexWriter.close();
			
			// all done
			// log.info("(" + i + " indexed entries, " + is + " not indexed spam entries)");
		} catch (final InterruptedException e) {
			log.error("lucene finished not in 18 hours", e);
		}
	}
	
	/**
	 * creates index of resource entries and adds FolkRanks to posts
	 * (experimental purpose)
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void createIndexFromDatabaseWithFolkRanks() throws CorruptIndexException, IOException {
		log.info("Filling index with database post entries.");
		
		final ExecutorService executor = Executors.newFixedThreadPool(this.numberOfThreads);
		
		// number of post entries to calculate progress
		// FIXME: the number of posts is wrong
		this.numberOfPosts = this.dbLogic.getNumberOfPosts();
		log.info("Number of post entries: "+  this.numberOfPosts);
		
		// initialize variables
		final Integer lastTasId = this.dbLogic.getLastTasId();
		Date lastLogDate  = this.dbLogic.getLastLogDate();
		
		if (lastLogDate == null) {
		    lastLogDate = new Date(System.currentTimeMillis() - 1000);
		}

		log.info("Start writing data to lucene index (with duplicate detection)");
		
		// TODO configure FolkRank file location (Spring?)
		String folkRankFile = this.resourceIndex.getResourceClass().getSimpleName();
		FolkRankDao folkRankDao = new FileFolkRankDao(folkRankFile);
		
		// read block wise all posts
		List<LucenePost<R>> postList = null;
		int skip = 0;
		//int lastContenId = -1;
		int lastOffset = 0;
		int postListSize = 0;
		do {
			postList = this.dbLogic.getPostEntriesOrderedByHash(lastOffset, SQL_BLOCKSIZE);
			postListSize = postList.size();
			skip += postListSize;
			log.info("Read " + skip + " entries.");

			// cycle through all posts of currently read block
			for (final LucenePost<R> post : postList) {
				post.setLastLogDate(lastLogDate);
				post.setLastTasId(lastTasId);
				//executor.execute(new Runnable() {
					
				// FIXME had to remove Thread creation because reading FolkRank values is not thread safe.
					//@Override
					//public void run() {
						if (LuceneGenerateResourceIndex.this.isNotSpammer(post)) {  
							// create index document from post model
							final Document doc = LuceneGenerateResourceIndex.this.resourceConverter.readPost(post);
							
							// FolkRanks
							String hash = post.getResource().getInterHash();
							addFolkRanksAsFullTextFields(doc, hash, folkRankDao);
							try {
								LuceneGenerateResourceIndex.this.indexWriter.addDocument(doc);
								LuceneGenerateResourceIndex.this.importedPost(post);
							} catch (final IOException e) {
								log.error("error while inserting post " + post.getUser().getName() + "/" + post.getResource().getIntraHash(), e);
							}
						}
					//}
				//});
			}
			
			if (postListSize > 0) {
				//lastContenId = postList.get(postListSize - 1).getContentId();
				lastOffset += postListSize;
			}
		} while (postListSize == SQL_BLOCKSIZE);
		
		try {
			executor.shutdown();
			executor.awaitTermination(18, TimeUnit.HOURS);
			
			// optimize index
			log.info("optimizing index " + this.resourceIndex);
			this.indexWriter.optimize();
			
			// close resource indexWriter
			log.info("closing index " + this.resourceIndex);
			this.indexWriter.close();
			
			// all done
			// log.info("(" + i + " indexed entries, " + is + " not indexed spam entries)");
		} catch (final InterruptedException e) {
			log.error("lucene finished not in 18 hours", e);
		}
	}
	
	/**
	 * 
	 * Adds available tag/user FolkRanks to given post.
	 * 
	 * @param doc
	 * @param hash
	 * @param folkRankDao
	 * @return
	 */
	protected int addFolkRanksAsFullTextFields(Document doc, String hash, FolkRankDao folkRankDao) {
		
		List<FolkRankInfo> folkRanks = folkRankDao.getTagUserFolkRanks(hash);
		
		if (folkRanks.size() == 0) {
			return 0; // no FolkRanks for this item
		}
		
		if (!hash.equals(lastHash)) {
			
			lastHash = hash;
			
			tagFieldValues.clear();
			userFieldValues.clear();
			
			for (FolkRankInfo folkRank : folkRanks) {
				
				String item = folkRank.getItem().toLowerCase();
				String dim = Integer.toString(folkRank.getDim());
				String weight = Float.toString(folkRank.getWeight());
				
				if (item.length() == 0 || item.length() > 50 || item.contains(" ")) {
					continue;
				}
				
				Map<Character, String> usedHashMap;
				if (dim.equals("0")) {
					usedHashMap = tagFieldValues;
				} else if (dim.equals("1")) {
					usedHashMap = userFieldValues;
				} else {
					continue;
				}
				
				char firstCharacter = item.charAt(0);
				
				if (!Character.isLetter(firstCharacter)) {
					firstCharacter = '#';
				}
				
				if (!usedHashMap.containsKey(firstCharacter)) {
					usedHashMap.put(firstCharacter, "");
				}
				
				usedHashMap.put(firstCharacter, usedHashMap.get(firstCharacter) + item + " " + weight + " ");			
			}
			
		} 
		
		for (char key : tagFieldValues.keySet()) {
			//addStoreField(doc, "frtag" + key, tagFieldValues.get(key).trim());
			doc.add(new Field("frtag" + key, tagFieldValues.get(key).trim(), Field.Store.YES, Field.Index.NO));
		}
		
		for (char key : userFieldValues.keySet()) {
			//addStoreField(doc, "fruser" + key, userFieldValues.get(key).trim());
			doc.add(new Field("fruser" + key, userFieldValues.get(key).trim(), Field.Store.YES, Field.Index.NO));
		}
		
		return folkRanks.size();
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

	/**
	 * @param numberOfThreads the numberOfThreads to set
	 */
	public void setNumberOfThreads(final int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/**
	 * @return the id of the index currently generating
	 */
	public int getGeneratingIndexId() {
		return this.resourceIndex.getIndexId();
	}
}
