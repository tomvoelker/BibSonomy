/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.Version;
import org.bibsonomy.es.IndexType;
import org.bibsonomy.es.IndexUpdater;
import org.bibsonomy.es.IndexUpdaterState;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.param.comparator.DocumentCacheComparator;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;

/**
 * abstract base class for managing lucene resource indices
 * 
 * @author fei
 *
 * @param <R> the resource of the index
 */
public class LuceneResourceIndex<R extends Resource> implements IndexUpdater<R> {
	private static final Log log = LogFactory.getLog(LuceneResourceIndex.class);

	/** directory prefix and id delimiter for different resource indeces */
	private static final String INDEX_PREFIX = "lucene_";
	private static final String INDEX_ID_DELIMITER = "-";
	

	/** gives read only access to the lucene index */
	//private DirectoryReader indexReader;
	private SearcherManager searcherManager;

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
	protected List<Integer> contentIdsToDelete;

	/** list posts to insert into index */
	private final Set<Document> postsToInsert;
	
	/** 
	 * set of usernames which where flagged as spammers since last update
	 * which should be removed from index during next update (blocking new posts
	 * to be inserted for given users) 
	 */
	protected Set<String> usersToFlag;
	
	/** flag indicating whether the index was cleanly initialized */
	private boolean isReady = false;
	
	/** id for identifying redundant resource indeces */
	private int indexId;
	
	/** keeps track of the newest log_date and tas_id during last index update */
	private IndexUpdaterState state;

	private Class<R> resourceClass;
	
	/** converts post model objects to documents of the index structure */
	protected LuceneResourceConverter<R> resourceConverter;

	/** all sessions which currently use this index */
	private final Set<LuceneSession> openSessions = new HashSet<>();
	
	private boolean closed = true;
	
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
			synchronized (this) {
				this.searcherManager.maybeRefreshBlocking();
			}
			try (LuceneSession session = openSession()) {
				session.execute(new LuceneSessionOperation<Void,IOException>() {
					@Override
					public Void doOperation(IndexSearcher searcher) throws IOException {
						final DirectoryReader indexReader = (DirectoryReader) searcher.getIndexReader();
						
						// Get the ID of this index
						statistics.setIndexId(LuceneResourceIndex.this.indexId);
						statistics.setNumDocs(indexReader.numDocs());
						statistics.setNumDeletedDocs(indexReader.numDeletedDocs());
						statistics.setCurrentVersion(indexReader.getVersion());
						statistics.setCurrent(indexReader.isCurrent());
						return null;
					}
				});
			}

		} catch (IOException e1) {
			log.error(e1);
		}
		statistics.setNewestRecordDate(this.getLastLogDate());

		return statistics;
	}

	/** 
	 * Close index-writer and index-reader and disable this index.
	 * @throws CorruptIndexException 
	 * @throws IOException 
	 */
	public void close() throws CorruptIndexException, IOException{
		if (!closed) {
			log.info("closing " + this);
			closed = true;
			this.closeSearcherManager();
			this.closeIndexWriter();
			this.closeDirectory();
			this.disableIndex();
		}
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private void closeDirectory() throws IOException {
		if (this.indexDirectory != null) {
			this.indexDirectory.close();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}

	/**
	 * initialize internal data structures
	 */
	protected void init() {
		try {
			this.indexPath = this.baseIndexPath + INDEX_PREFIX + this.resourceClass.getSimpleName() + LuceneResourceIndex.INDEX_ID_DELIMITER + this.indexId;
			
			if (!closed) {
				throw new IllegalStateException("index already opened: " + this);
			}
			closed = false;
			log.info("opening " + this);
			this.indexDirectory = FSDirectory.open(new File(this.indexPath));

			try {
				if (IndexWriter.isLocked(this.indexDirectory)) {
					for (int retry = 0; retry < 3; ++retry) {
						log.warn("WARNING: Index " + indexPath + " is still locked - waiting.");
						Thread.sleep(5000);
						if (IndexWriter.isLocked(this.indexDirectory)) {
							log.warn("WARNING: Index " + indexPath + " is still locked - trying to reopen directory.");
							this.indexDirectory.close();
							this.indexDirectory = FSDirectory.open(new File(this.indexPath));
						}
						if (IndexWriter.isLocked(this.indexDirectory)) {
							log.warn("WARNING: Index " + indexPath + " is still locked - forcibly unlock the index.");
							IndexWriter.unlock(this.indexDirectory);
						}
						if (IndexWriter.isLocked(this.indexDirectory)) {
							log.warn("Unlocking index " + indexPath + " failed silently");
							if (indexDirectory.fileExists(IndexWriter.WRITE_LOCK_NAME)) {
								log.error("Trying to unlock index " + indexPath + " with some more emphasis");
								indexDirectory.clearLock(IndexWriter.WRITE_LOCK_NAME);
							}
						}
						if (IndexWriter.isLocked(this.indexDirectory)) {
							log.warn("WARNING: Index " + indexPath + " is still locked - trying to reopen directory.");
							this.indexDirectory.close();
							this.indexDirectory = FSDirectory.open(new File(this.indexPath));
						}
						if (IndexWriter.isLocked(this.indexDirectory)) {
							log.warn("Unlocking index " + indexPath + " failed again - doing it the rude way");
							File lockFile = new File(new File(this.indexPath), IndexWriter.WRITE_LOCK_NAME);
							if (lockFile.exists() == false) {
								log.error("lockfile does not exist: " + lockFile);
							} else {
								lockFile.delete();
							}
						}
						if (IndexWriter.isLocked(this.indexDirectory)) {
							log.warn("WARNING: Index " + indexPath + " is still locked - trying to reopen directory.");
							this.indexDirectory.close();
							this.indexDirectory = FSDirectory.open(new File(this.indexPath));
						} else {
							log.warn("OK. unlocked index " + indexPath + ".");
							break;
						}
					}
				}
			} catch (final IOException e) {
				log.fatal("Failed to unlock the index - dying.");
				throw e; 
			}
			
			synchronized (this) {
				try {
					this.openIndexWriter();
				} catch (final IOException e) {
					log.error("Error opening IndexWriter (" + e.getMessage() + ") - This is ok while creating a new index.");
					this.closeIndexWriter();
					throw e;
				}
				
				try {
					this.openSearcherManager();
				} catch (final IOException e) {
					log.error("Error opening SearcherManager (" + e.getMessage() + ") - This is ok while creating a new index.");
					this.closeSearcherManager();
					throw e;
				}
			}
			
			// everything went fine - enable the index
			this.enableIndex();
		} catch (final Exception e) {
			this.disableIndex();
		}
	}
	
	
	@Override
	public Date getLastLogDate() {
		// FIXME: this synchronisation is very inefficient 
		synchronized(this) {
			if (!isIndexEnabled()) {
				return null;
			} else if ((this.state != null) && (this.state.getLast_log_date() != null)) {
				return this.state.getLast_log_date();
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
					return new Date(Long.parseLong(lastLogDate));
				} catch (final NumberFormatException e) {
					log.error("Error parsing last_log_date " + lastLogDate);
				}
			}

			return null;
		}
	}
	
	/**
	 * set newest log_date[ms] 
	 * @param lastLogDate the lastLogDate to set
	 */
	public void setLastLogDate(final Date lastLogDate) {
		if (this.state == null) {
			this.state = new IndexUpdaterState();
		}
		this.state.setLast_log_date(lastLogDate);
	}
	
	/** 
	 * @return the newest tas_id from index
	 */
	@Override
	public Integer getLastTasId() {
		synchronized(this) {
			if (!isIndexEnabled()) {
				return Integer.MAX_VALUE;
			} else if ((this.state != null) && (this.state.getLast_tas_id() != null)) {
				return this.state.getLast_tas_id();
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
		if (this.state == null) {
			this.state = new IndexUpdaterState();
		}
		this.state.setLast_tas_id(lastTasId);
	}

	/**
	 * flag given user as spammer - preventing further posts to be inserted and
	 * mark user's posts for deletion from index
	 * 
	 * @param username
	 */
	@Override
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
	@Override
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
	@Override
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
		Object val = doc.get(LuceneFieldNames.LAST_LOG_DATE);
		if (val == null) {
			throw new IllegalArgumentException();
		}
		try {
			Long.parseLong((String) val);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
		
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
	@Override
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
			// commit writer- and reader-changes 
			//----------------------------------------------------------------
			try {
				this.indexWriter.commit();
				this.searcherManager.maybeRefresh();
			} catch (final IOException e) {
				log.error("Error commiting index update.", e);
			}
		}
	}

	
	/**
	 * closes IndexWriter and SearchManager and reopens them
	 */
	public synchronized void reset() {
		if (!isIndexEnabled()) {
			try {
				init();
			} catch (final Exception e) {
				return;
			}
		}
			
		try {
			openIndexWriter();
			try {
				openSearcherManager();
			} catch (final IOException e) {
				log.error("Error opening SearcherManager", e);
			}
		} catch(final IndexNotFoundException e) {
			log.error("Error opening IndexWriter (" + e.getMessage() + ") - This is ok while creating a new index.");
		} catch (final IOException e) {
			log.error("Error opening IndexWriter", e);
		}

		// delete the lists
		this.postsToInsert.clear();
		this.contentIdsToDelete.clear();
		this.usersToFlag.clear();

		// reset the cached query parameters
		this.state = null;
		
		if ((this.indexWriter != null) && (this.searcherManager != null)) {
			this.enableIndex();
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
		try (LuceneSession session = openSession()) {
			return session.execute(new LuceneSessionOperation<Document,IOException>() {
				@Override
				public Document doOperation(IndexSearcher searcher) throws IOException {
					final TopDocs topDocs = searcher.search(searchQuery, null, hitsPerPage, ordering);
					if (topDocs.totalHits > 0) {
						return searcher.doc(topDocs.scoreDocs[0].doc);
					}
					return null;
				}
			});
		} catch (final Exception e) {
			log.error("Error performing index search in file " + this.indexPath, e);
			return null;
		}
	}	
	
	/**
	 * removes given post from index
	 * 
	 * @param contentId post's content id 
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
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void purgeDocuments(final Term searchTerm) throws CorruptIndexException, IOException {
		this.indexWriter.deleteDocuments(searchTerm);
	}
	
	/**
	 * Opens a new indexWrite, closes the old one if exists.
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	private synchronized void openIndexWriter() throws CorruptIndexException, LockObtainFailedException, IndexNotFoundException, IOException  {
		closeIndexWriter();
		//open new indexWriter
		log.debug("Opening indexWriter " + this.indexPath);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48, this.analyzer);
		iwc.setOpenMode(OpenMode.APPEND);		
		indexWriter = new IndexWriter(indexDirectory, iwc);
	}
	
	private void closeIndexWriter() throws CorruptIndexException, IOException {
		if (this.indexWriter != null) {
			synchronized (this) {
				while (this.openSessions.size() > 0) {
					try {
						log.debug("waiting to close indexWriter " + indexPath);
						this.wait();
					} catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
				log.debug("Closing indexWriter " + indexPath);
				this.disableIndex();
				// close index for writing
				indexWriter.close();
				indexWriter = null;
			}
		}
	}
	
	/**
	 * Opens a new SearchManager, closes the old one if exists.
	 * @throws IOException
	 */
	private synchronized void openSearcherManager() throws IOException {
		closeSearcherManager();
		//open new SearchManager
		this.searcherManager = new SearcherManager(this.indexDirectory, new SearcherFactory());
		log.debug("Opening searcherManager " + this.indexPath);
	}
	
	private void closeSearcherManager() throws IOException {
		if (this.searcherManager != null) {
			synchronized (this) {
				while (this.openSessions.size() > 0) {
					try {
						log.debug("waiting to close searchManager " + indexPath);
						this.wait();
					} catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
				log.debug("closing searchManager " + indexPath);
				this.disableIndex();
				this.searcherManager.close();
				this.searcherManager = null;
			}
			
			
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
	 * Get a new index searcher for this index. Make sure to release it after search,
	 * by calling releaseIndexSearcher(searcher);
	 * @return IndexSearcher
	 * @throws IOException
	 */
	protected IndexSearcher acquireIndexSearcher() throws IOException {
		try {
			// FIXME: closing and exchanging the searcherManager should be done in a better way
			// when the updater finished, race-conditions can occur where we don't have a searcheManager for a short time (the reference to it is set to null because it is closed and should then no longer be used)
			for (int i = 0; i < 10; ++i) {
				if (searcherManager != null) {
					return this.searcherManager.acquire();
				}
				Thread.sleep(i * 100);
			}
		} catch (InterruptedException e) {
		}
		throw new IllegalStateException("no searcherManager available");
	}
	
	/**
	 * Releases a previously acquired IndexSearcher.
	 * @param searcher
	 */
	protected void releaseIndexSearcher(IndexSearcher searcher) {
		try {
			if (searcher != null) {
				this.searcherManager.release(searcher);
			}
		} catch (IOException e) {
			log.error("Could not release IndexSearcher", e);
		}
	}
	
	/**
	 * opens a {@link LuceneSession}. All usage of the lucene index should be done via a {@link LuceneSession} returned by this method and all returned objects should be closed (best done via try with resources).
	 * @return a new a {@link LuceneSession} to access the index.
	 */
	public synchronized LuceneSession openSession() {
		final LuceneSession rVal = new LuceneSession(this);
		this.openSessions.add(rVal);
		return rVal;
	}
	
	/**
	 * releases resources of associated to the {@link LuceneSession}. This is meant to be called only by the close method of the {@link LuceneSession}
	 * 
	 * @param session the session to be closed
	 */
	protected synchronized void closeSession(LuceneSession session) {
		openSessions.remove(session);
		if (openSessions.size() == 0) {
			this.notifyAll();
		}
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#setSystemInformation(java.lang.Integer, java.util.Date)
	 */
	@Override
	public void setSystemInformation(IndexUpdaterState state) {
		this.state = state;
	}
	

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#setContentIdsToDelete(java.util.List)
	 */
	@Override
	public void deleteDocumentsForContentIds(List<Integer> contentIdsToDelete) {
		this.deleteDocumentsInIndex(contentIdsToDelete);
	}
	

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#insertDocument(org.bibsonomy.lucene.param.LucenePost, long)
	 */
	@Override
	public void insertDocument(LucenePost<R> post, Date currentLogDate) {
		if (currentLogDate != null) {
			post.setLastLogDate(currentLogDate);
		}
		final Document postDoc = (Document)this.resourceConverter.readPost(post, IndexType.LUCENE);
		this.insertDocument(postDoc);	
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#deleteIndexForForUser(java.lang.String)
	 */
	@Override
	public void deleteIndexForUser(String userName) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#deleteIndexForIndexId(long)
	 */
	@Override
	public void deleteIndexForIndexId(long indexId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 */
	public LuceneResourceConverter<R> getResourceConverter() {
		return this.resourceConverter;
	}

	/**
	 * @param resourceConverter
	 */
	public void setResourceConverter(LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonRelation(java.lang.String, java.util.List)
	 */
	@Override
	public void updateIndexWithPersonRelation(String interHash, List<ResourcePersonRelation> newRels) {
		// because it is intended to completely replace lucene with elasticsearch, this is only implemented for elasticsearch
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonNameInfo(org.bibsonomy.model.PersonName, org.apache.commons.collections.LRUMap)
	 */
	@Override
	public void updateIndexWithPersonNameInfo(PersonName name, LRUMap updatedInterhashes) {
		// because it is intended to completely replace lucene with elasticsearch, this is only implemented for elasticsearch
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#updateIndexWithPersonInfo(org.bibsonomy.model.Person, org.apache.commons.collections.LRUMap)
	 */
	@Override
	public void updateIndexWithPersonInfo(Person per, LRUMap updatedInterhashes) {
		// because it is intended to completely replace lucene with elasticsearch, this is only implemented for elasticsearch
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#onUpdateComplete()
	 */
	@Override
	public void onUpdateComplete() {
		// activating the index is done elsewhere by some other legacy magic
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.IndexUpdater#getUpdaterState()
	 */
	@Override
	public IndexUpdaterState getUpdaterState() {
		if (state == null) {
			state = new IndexUpdaterState();
		}
		final Integer lastTasId = this.getLastTasId();
		// keeps track of the newest log_date during last index update
		final Date lastLogDate = this.getLastLogDate();
		state.setLast_log_date(lastLogDate);
		state.setLast_tas_id(lastTasId);
		return state;
	}
}
