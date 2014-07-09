package org.bibsonomy.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.Version;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.comparator.DocumentCacheComparator;
import org.bibsonomy.model.Resource;

/**
 * abstract base class for managing lucene resource indices
 * 
 * @author fei
 *
 * @param <R> the resource of the index
 */
public class LuceneResourceIndex<R extends Resource> {
	private static final Log log = LogFactory.getLog(LuceneResourceIndex.class);

	/** directory prefix and id delimiter for different resource indeces */
	private static final String INDEX_PREFIX = "lucene_";
	private static final String INDEX_ID_DELIMITER = "-";
	

	/** gives read only access to the lucene index */
	private DirectoryReader indexReader;

	/** gives write access to the lucene index */
	private IndexWriter indexWriter;
	
	/** the base path of the index*/
	private String baseIndexPath;

	/** path to the lucene index */
	private String indexPath;

	/** directory of the lucene index */
	private Directory indexDirectory;

	/** default field tokenizer */
	private Analyzer analyzer;
	
	/** list containing content ids of cached delete operations */
	private final List<Integer> contentIdsToDelete;

	/** list posts to insert into index */
	private final Set<Document> postsToInsert;
	
	/** 
	 * set of usernames which where flagged as spammers since last update
	 * which should be removed from index during next update (blocking new posts
	 * to be inserted for given users) 
	 */
	private final Set<String> usersToFlag;
	
	/** flag indicating whether the index was cleanly initialized */
	private boolean isReady = false;
	
	/** id for identifying redundant resource indeces */
	private int indexId;
	
	/** keeps track of the newest log_date during last index update */
	private Long lastLogDate;
	
	/** keeps track of the newest tas_id during last index update */
	private Integer lastTasId;

	private Class<R> resourceClass;
	
	/** the maximum field length */
//	private IndexWriter.MaxFieldLength maxFieldLength;

	
	/**
	 * constructor disabled
	 */
	public LuceneResourceIndex() {
		// init data structures
		this.contentIdsToDelete = new LinkedList<Integer>();
		this.postsToInsert = new TreeSet<Document>(new DocumentCacheComparator());
		this.usersToFlag = new TreeSet<String>();
	}
	
	/**
	 * Get Statistics for this index
	 * @return LuceneIndexStatistics for this index
	 */
	public LuceneIndexStatistics getStatistics() {
        final LuceneIndexStatistics statistics = new LuceneIndexStatistics();
        if (!this.isIndexEnabled()) {
        	return statistics;
        }
        
        try {
        	//If index has changed, open new Reader to get latest info
        	this.openIndexReaderIfChanged();
        	// Get the ID of this index 
        	statistics.setIndexId(this.indexId);

        	statistics.setNumDocs(indexReader.numDocs());
        	statistics.setNumDeletedDocs(indexReader.numDeletedDocs());
        	statistics.setCurrentVersion(indexReader.getVersion());
        	statistics.setCurrent(indexReader.isCurrent());
        	/*
        	 * FIXME - For Lucene 4.9 we have to use IndexVersion Number here, instead of Date
        	 */
        	//statistics.setLastModified(new Date(IndexReader.lastModified(ir.directory())));
        } catch (IOException e1) {
        	log.error(e1);
        }

	    statistics.setNewestRecordDate(new Date(this.getLastLogDate()));
	    
	    return statistics;
	}

	/** 
	 * Close index-writer and index-reader and disable this index.
	 * @throws CorruptIndexException 
	 * @throws IOException 
	 */
	public void close() throws CorruptIndexException, IOException{
		this.closeIndexReader();
		this.closeIndexWriter();
		this.disableIndex();
	}
	
	/**
	 * initialize internal data structures
	 */
	protected void init() {
		try {
			this.indexPath = this.baseIndexPath + INDEX_PREFIX + this.resourceClass.getSimpleName() + LuceneResourceIndex.INDEX_ID_DELIMITER + this.indexId;
			
			this.indexDirectory = FSDirectory.open(new File(this.indexPath));
			
			try {
				if (IndexWriter.isLocked(this.indexDirectory)) {
					log.error("WARNING: Index " + indexPath + " is locked - forcibly unlock the index.");
					IndexWriter.unlock(this.indexDirectory);
					log.error("OK. Index unlocked.");
				}
			} catch (final IOException e) {
				log.fatal("Failed to unlock the index - dying.");
				throw e; 
			}
			
			try {
				this.openIndexWriter();
			} catch (final IOException e) {
				log.error("Error opening IndexWriter (" + e.getMessage() + ") - This is ok while creating a new index.");
				this.closeIndexWriter();
				throw e;
			}
			
			try {
				this.openIndexReader(false);
			} catch (final IOException e) {
				log.error("Error opening IndexReader (" + e.getMessage() + ") - This is ok while creating a new index.");
				this.closeIndexReader();
				throw e;
			}
			
			// everything went fine - enable the index
			this.enableIndex();
		} catch (final Exception e) {
			this.disableIndex();
		}
	}
	
	
	/**
	 * @return the latest log_date[ms] from index 
	 */
	public long getLastLogDate() {
		// FIXME: this synchronisation is very inefficient 
		synchronized(this) {
			if (!isIndexEnabled()) {
				return Long.MAX_VALUE;
			} else if (this.lastLogDate != null) {
				return this.lastLogDate;
			}
			
			//----------------------------------------------------------------
			// search over all elements sort them reverse by date 
			// and return 1 top document (newest one)
			//----------------------------------------------------------------
			// get all documents
			final Query matchAll = new MatchAllDocsQuery();
			// sort by last_log_date of type LONG in reversed order 
			final Sort sort = new Sort(new SortField(LuceneFieldNames.LAST_LOG_DATE, SortField.Type.LONG, true));
			
			final Document doc = searchIndex(matchAll, 1, sort);
			if (doc != null) {
				final String lastLogDate = doc.get(LuceneFieldNames.LAST_LOG_DATE);
				try {
					// parse date
					return Long.parseLong(lastLogDate);
				} catch (final NumberFormatException e) {
					log.error("Error parsing last_log_date " + lastLogDate);
				}
			}

			return Long.MAX_VALUE;
		}
	}
	
	/**
	 * set newest log_date[ms] 
	 * @param lastLogDate the lastLogDate to set
	 */
	public void setLastLogDate(final Long lastLogDate) {
		this.lastLogDate = lastLogDate;
	}
	
	/** 
	 * @return the newest tas_id from index
	 */
	public Integer getLastTasId() {
		synchronized(this) {
			if (!isIndexEnabled()) {
				return Integer.MAX_VALUE;
			} else if (this.lastTasId != null) {
				return this.lastTasId;
			}
			
			//----------------------------------------------------------------
			// search over all elements sort them reverse by last_tas_id
			// and return 1 top document (newest one)
			//----------------------------------------------------------------
			// get all documents
			final Query matchAll = new MatchAllDocsQuery();
			// order by last_tas_id of type INT in reversed order
			final Sort sort = new Sort(new SortField(LuceneFieldNames.LAST_TAS_ID, SortField.Type.INT, true));
			
			final Document doc = searchIndex(matchAll, 1, sort);
			if (doc != null) {
				final String lastTASId = doc.get(LuceneFieldNames.LAST_TAS_ID);
				try {
					return Integer.parseInt(lastTASId);
				} catch (final NumberFormatException e) {
					log.error("Error parsing last_tas_id " + lastTASId);
				}
			}
			
			return Integer.MAX_VALUE;
		}
	}
	
	/** 
	 * @param lastTasId the lastTasId to set
	 */
	public void setLastTasId(final Integer lastTasId) {
		this.lastTasId = lastTasId;
	}

	/**
	 * flag given user as spammer - preventing further posts to be inserted and
	 * mark user's posts for deletion from index
	 * 
	 * @param username
	 */
	public void flagUser(final String username) {
		synchronized(this) {
			this.usersToFlag.add(username);
		}
	}
	
	/**
	 * unflag given user as spammer - enabling further posts to be inserted 
	 * 
	 * @param userName
	 */
	public void unFlagUser(final String userName) {
		synchronized(this) {
			this.usersToFlag.remove(userName);
		}
	}
	
	/**
	 * cache given post for deletion
	 * 
	 * @param contentId post's content id 
	 */
	public void deleteDocumentForContentId(final Integer contentId) {
		synchronized(this) {
			this.contentIdsToDelete.add(contentId);
		}
	}

	/**
	 * cache given posts for deletion
	 * 
	 * @param contentIdsToDelete list of content ids which should be removed from the index
	 */
	public void deleteDocumentsInIndex(final List<Integer> contentIdsToDelete) {
		synchronized(this) {
			this.contentIdsToDelete.addAll(contentIdsToDelete);
		}
	}
	
	/**
	 * cache given post for insertion
	 * 
	 * @param doc post document to insert into the index
	 */
	public void insertDocument(final Document doc) {
		synchronized(this) {
			this.postsToInsert.add(doc);
		}
	}

	/**
	 * cache given post for insertion
	 * 
	 * @param docs post documents to insert into the index
	 */
	public void insertDocuments(final List<Document> docs) {
		synchronized(this) {
			this.postsToInsert.addAll(docs);
		}
	}
	
	/**
	 * perform all cached operations to index
	 */
	public void flush() {
		synchronized(this) {
			if (!isIndexEnabled()) {
				return;
			}
			
			//----------------------------------------------------------------
			// remove cached posts from index
			//----------------------------------------------------------------
			log.debug("Performing " + contentIdsToDelete.size() + " delete operations");
			if ((contentIdsToDelete.size() > 0) || (usersToFlag.size() > 0) ) {
				// remove each cached post from index
				for (final Integer contentId : this.contentIdsToDelete) {
					try {
						this.purgeDocumentForContentId(contentId);
						log.debug("deleted post " + contentId);
					} catch (final IOException e) {
						log.error("Error deleting post " + contentId + " from index", e);
					}
				}
				
				// remove spam posts from index
				for (final String userName : this.usersToFlag) {
					try {
						//final int cnt = purgeDocumentsForUser(userName);
						//log.debug("Purged " + cnt + " posts for user " + userName);
						purgeDocumentsForUser(userName);
						log.debug("Purged posts for user " + userName);
					} catch (final IOException e) {
						log.error("Error deleting spam posts for user " + userName + " from index", e);
					}
				}
			}

			//----------------------------------------------------------------
			// add cached posts to index
			//----------------------------------------------------------------
			log.debug("Performing " + postsToInsert.size() + " insert operations");
			if (this.postsToInsert.size() > 0) {
				try {
					this.insertRecordsIntoIndex(postsToInsert);
				} catch (final IOException e) {
					log.error("Error adding posts to index.", e);
				}
			}
			
			//----------------------------------------------------------------
			// clear all cached data
			//----------------------------------------------------------------
			this.postsToInsert.clear();
			this.contentIdsToDelete.clear();
			this.usersToFlag.clear();
			
			//----------------------------------------------------------------
			// commit reader-changes 
			//----------------------------------------------------------------
			try {
				this.openIndexReaderIfChanged();
			} catch (final IOException e) {
				log.error("Error commiting index update.", e);
			}
		}
	}

	
	/**
	 * closes all writer and reader and reopens them
	 */
	public void reset() {
		synchronized(this) {
			if (!isIndexEnabled()) {
				try {
					init();
				} catch (final Exception e) {
					return;
				}
			}

			try {
				closeIndexReader();
			} catch (final IOException e) {
				log.error("IOException while closing index reader", e);
			}
			try {
				closeIndexWriter();
			} catch (final IOException e) {
				log.error("IOException while closing index writer", e);
			}
			try {
				openIndexWriter();
			} catch (final IOException e) {
				log.error("Error opening index writer", e);
			}
			try {
				openIndexReader(false);
			} catch (final IOException e) {
				log.error("Error opening index reader", e);
			}

			// delete the lists
			this.postsToInsert.clear();
			this.contentIdsToDelete.clear();
			this.usersToFlag.clear();

			// reset the cached query parameters
			this.lastLogDate = null;
			this.lastTasId = null;
		}
	}	
	
	//------------------------------------------------------------------------
	// private index access interface
	//------------------------------------------------------------------------
	/**
	 * write given post into the index
	 * 
	 * @param post
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void insertRecordIntoIndex(final Document post) throws CorruptIndexException, IOException {
		if (!this.usersToFlag.contains(post.get(LuceneFieldNames.USER_NAME))) { 
			// skip users which where flagged as spammers
			indexWriter.addDocument(post);
		}
	}	

	/**
	 * write given post into the index
	 * 
	 * @param post
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void insertRecordsIntoIndex(final Collection<Document> posts) throws CorruptIndexException, IOException {
		for( final Document post : posts ) {
			this.insertRecordIntoIndex(post);
		}
	}
	
	/**
	 * query the index
	 * 
	 * @param searchQuery the search query
	 * @param hitsPerPage maximal number of result items to retrieve
	 * @param ordering sort ordering
	 * @return
	 */
	private Document searchIndex(final Query searchQuery, final int hitsPerPage, final Sort ordering) {
		// prepare the index searcher
		final IndexSearcher searcher = new IndexSearcher(indexReader);

		// query the index
		try {
			final TopDocs topDocs = searcher.search(searchQuery, null, hitsPerPage, ordering);
			if (topDocs.totalHits > 0) {
				return searcher.doc(topDocs.scoreDocs[0].doc);
			}
		} catch (final Exception e) {
			log.error("Error performing index search in file " + this.indexPath, e);
		}
		
		return null;
	}	
	
	/**
	 * removes given post from index
	 * 
	 * @param contentId post's content id 
	 * @return number of posts deleted from index
	 * 
	 * @throws StaleReaderException
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	private void purgeDocumentForContentId(final Integer contentId) throws CorruptIndexException, LockObtainFailedException, IOException {
		final Term term = new Term(LuceneFieldNames.CONTENT_ID, contentId.toString());
		purgeDocuments(term);
	}
	
	/**
	 * delete all documents of a given user from index
	 * 
	 * @param username
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void purgeDocumentsForUser(final String username) throws CorruptIndexException, IOException {
		// delete each post owned by given user
		final Term term = new Term(LuceneFieldNames.USER_NAME, username);
		purgeDocuments(term);
	}

	/**
	 * remove posts matching to given search term from index
	 * 
	 * @param searchTerm
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void purgeDocuments(final Term searchTerm) throws CorruptIndexException, IOException {
		this.indexWriter.deleteDocuments(searchTerm);
	}
	
	/**
	 * Opens a new indexWriter. This method does not close a possible old Writer.
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	protected void openIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		//open new indexWriter
		log.debug("Opening indexWriter " + this.indexPath);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_30, this.analyzer);
		iwc.setOpenMode(OpenMode.APPEND);		
		indexWriter = new IndexWriter(indexDirectory, iwc);
	}
	
	/**
	 * Opens a new indexReader. This method does not close a possible old Writer.
	 * @param applyAllDeletes
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void openIndexReader(boolean applyAllDeletes) throws CorruptIndexException, IOException {
		//open new IndexReader
		if (indexWriter != null) {
			log.debug("Opening indexReader " + indexPath);
			this.indexReader = DirectoryReader.open(indexWriter,applyAllDeletes);
			return;
		}
		log.debug("Cannot open index " + indexPath + " for reading: IndexWriter missing");
	}
	
	/**
	 * Opens a new indexReader, only if the index has changed. This method closes the old reader if the index has changed.
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void openIndexReaderIfChanged() throws CorruptIndexException, IOException {
		if (indexReader != null) {
			log.debug("Re-Opening indexReader " + indexPath + " : Checking for changes");
			DirectoryReader newIndexReader = DirectoryReader.openIfChanged(indexReader);
			if (newIndexReader != null) {
				log.debug("Re-Opening indexReader " + indexPath + " : found changes");
				indexReader.close();
				indexReader = newIndexReader;
			} else {
				log.debug("Re-Opening indexReader " + indexPath + " : no changes");
			}
		}
	}
	
	protected void closeIndexWriter() throws CorruptIndexException, IOException {
		if (this.indexWriter != null) {
			log.debug("Closing indexWriter " + indexPath);
			//close index for writing
			IndexWriter iw = indexWriter;
			indexWriter = null;
			iw.close();
		}
	}

	protected void closeIndexReader() throws IOException {
		if (this.indexReader != null) {
			log.debug("Closing indexReader " + indexPath);
			DirectoryReader id = indexReader;
			indexReader = null;
			id.close();
		}
	}

	/**
	 * deletes the index
	 */
	public void deleteIndex() {
		try {
			this.close();
			final File directory = new File(this.indexPath);
			final Directory indexDirectory = FSDirectory.open(directory);
			
			log.info("Deleting index " + directory.getAbsolutePath() + "...");
			for (final String filename: indexDirectory.listAll()) {
			    indexDirectory.deleteFile(filename);
			    log.debug("Deleted " + filename);
			}
			log.info("Success.");
		} catch (final NoSuchDirectoryException e) {
			log.warn("Tried to delete the lucene-index-directory but it could not be found.", e);
		} catch (final IOException e) {
			log.error("Could not delete directory-content before index-generation or index-copy.", e);
		}
	}
	
	
	/**
	 * 
	 * @return a new index searcher for this index
	 * @throws IOException
	 */
	public IndexSearcher createIndexSearcher() throws IOException {
		//return new IndexSearcher(FSDirectory.open(new File(this.getIndexPath())));
		/*
		 * FIXME - potential error?
		 */
		return new IndexSearcher(this.indexReader);
	}
	
	/**
	 * @return the indexId
	 */
	public int getIndexId() {
		return indexId;
	}
	
	/**
	 * @return the luceneIndexPath
	 */
	public String getIndexPath() {
		return this.indexPath;
	}
	
	/**
	 * disable this index when open fails
	 */
	public void disableIndex() {
		this.isReady = false;
	}
	
	/**
	 * enable this index
	 */
	public void enableIndex() {
		this.isReady = true;
	}

	/**
	 * checks, whether the index is readily initialized
	 * @return true, if index is ready - false, otherwise
	 */
	public boolean isIndexEnabled() {
		return this.isReady;
	}
	
	/**
	 * @return the resourceClass
	 */
	public Class<R> getResourceClass() {
		return resourceClass;
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(final Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @return the postsToInsert
	 */
	public Set<Document> getPostsToInsert() {
		return this.postsToInsert;
	}

	/**
	 * @return the usersToFlag
	 */
	public Set<String> getUsersToFlag() {
		return usersToFlag;
	}

	/**
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * @param analyzer the analyzer to set
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @param indexId the indexId to set
	 */
	public void setIndexId(final int indexId) {
		this.indexId = indexId;
	}
	
	/**
	 * @param baseIndexPath the baseIndexPath to set
	 */
	public void setBaseIndexPath(final String baseIndexPath) {
		this.baseIndexPath = baseIndexPath;
	}

	@Override
	public String toString() {
		return this.resourceClass.getSimpleName() + INDEX_ID_DELIMITER + this.indexId;
	}
}
