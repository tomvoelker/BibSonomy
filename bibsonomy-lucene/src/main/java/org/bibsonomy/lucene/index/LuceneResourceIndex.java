package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.param.comparator.DocumentCacheComparator;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.model.Resource;

/**
 * abstract base class for managing lucene resource indices
 * 
 * TODO: should we use a singleton?
 * TODO: implement a consistent management of read/write access
 *  
 * @author fei
 *
 * @param <R>
 */
public abstract class LuceneResourceIndex<R extends Resource> extends LuceneBase {
	protected static final Log log = LogFactory.getLog(LuceneResourceIndex.class);

	/** coding whether index is opened for writing or reading */
	public static enum AccessMode {
		None, ReadOnly, WriteOnly;
	}
	/** indicating whether index is opened for writing or reading */
	private AccessMode accessMode;

	/** gives read only access to the lucene index */
	IndexReader indexReader;

	/** gives write access to the lucene index */
	IndexWriter indexWriter;
	
	/** path to the lucene index */
	private String luceneIndexPath;

	/** default field tokenizer */
	private Analyzer analyzer;
	
	/** list containing content ids of cached delete operations */
	private List<Integer> contentIdsToDelete;

	/** list containing content ids of cached delete operations */
	private Set<Document> postsToInsert;
	
	/** 
	 * set of usernames which where flagged as spammers since last update
	 * which should be removed from index during next update (blocking new posts
	 * to be inserted for given users) 
	 */
	private Set<String> usersToFlag;

	/** flag indicating whether the index should be optimized during next update */
	private boolean optimizeIndex;
	
	/** flag indicating whether the index was cleanly initialized */
	private boolean isReady = false;
	
	/**
	 * constructor disabled
	 */
	protected LuceneResourceIndex(){
		try {
			init();
		} catch (Exception e) {
			disableIndex();
		}
		
		// init data structures
		contentIdsToDelete = new LinkedList<Integer>();
		postsToInsert      = new TreeSet<Document>(new DocumentCacheComparator());
		usersToFlag        = new TreeSet<String>();
		optimizeIndex      = false;
	};
	


	/**
	 * initialize internal data structures
	 * @throws IOException 
	 * @throws NamingException 
	 */
	private void init() throws IOException, NamingException {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup(CONTEXT_ENV_NAME);
			
			String contextPathName = CONTEXT_INDEX_PATH+getResourceName();
			this.luceneIndexPath = (String) envContext.lookup(contextPathName);
		} catch (NamingException e) {
			log.error("NamingException requesting JNDI environment variables ' ("+e.getMessage()+")", e);
			throw e;
		}

		try {
			Directory dir = FSDirectory.getDirectory(luceneIndexPath);
			if( IndexWriter.isLocked(dir) ) {
				log.error("WARNING: Index "+luceneIndexPath+" is locked - forcibly unlock the index.");
				IndexWriter.unlock(dir);
				log.error("OK. Index unlocked.");
			}
		} catch (IOException e) {
			log.fatal("Failed to unlock the index - dying.");
			throw e; 
		}
		
		try {
			indexReader = IndexReader.open(luceneIndexPath);
			accessMode = AccessMode.ReadOnly;
		} catch( IOException e) {
			log.error("Error opening IndexReader ("+e.getMessage()+")", e);
			throw e;
		}
		
		// everything went fine - enable the index
		enableIndex();
	}
	
	
	/**
	 * get latest log_date[ms] from index 
	 * @return
	 */
	public long getLastLogDate() {
		synchronized(this) {
			if( !isIndexEnabled() ) {
				return 0;
			}
			
			this.ensureReadAccess();
			
			Long lastLogDate = null;
	
			int hitsPerPage = 1;
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			QueryParser qp = new QueryParser(FLD_LAST_LOG_DATE,new StandardAnalyzer());
			Sort sort = new Sort(FLD_LAST_LOG_DATE,true);
	
			// search over all elements sort them reverse by date and return 1 top document (newest one)
			TopDocs topDocs = null;
			Document doc = null;
			try {
				topDocs = searcher.search(qp.parse("*:*"), null, hitsPerPage, sort);
				doc = searcher.doc(topDocs.scoreDocs[0].doc);
				// parse date
				lastLogDate = Long.parseLong(doc.get(FLD_LAST_LOG_DATE)); 
			} catch (ParseException e) {
				log.error("ParseException while parsing *:* in getNewestRecordDateFromIndex ("+e.getMessage()+")");
			} catch (Exception e) {
				log.error("Error reading index file " + this.luceneIndexPath);
			} finally {
				try {
					searcher.close();
				} catch (IOException e) {
					log.error("Error closing index "+this.luceneIndexPath+" for searching", e);
				}
			}
			
			if( lastLogDate!=null )
				return lastLogDate; 
			else
				return 0;
		}
	}
	
	/**
	 * get newest tas_id from index
	 * @return
	 */
	public Integer getLastTasId() {
		synchronized(this) {
			if( !isIndexEnabled() ) {
				return -1;
			}
			
			this.ensureReadAccess();
			
			Integer lastTasId = null;
	
			int hitsPerPage = 1;
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			QueryParser qp = new QueryParser(FLD_LAST_TAS_ID, new StandardAnalyzer());
			Sort sort = new Sort(FLD_LAST_TAS_ID,true);
	
			// search over all elements sort them reverse by date and return 1 top document 
			// newest one
			TopDocs topDocs = null;
			Document doc = null;
			try {
				topDocs = searcher.search(qp.parse("*:*"), null, hitsPerPage, sort);
				doc = searcher.doc(topDocs.scoreDocs[0].doc);
				lastTasId = Integer.parseInt(doc.get(FLD_LAST_TAS_ID));
			} catch (ParseException e) {
				log.error("ParseException while parsing *:* in getLastTasId", e);
			} catch (Exception e) {
				log.error("Error reading index file " + this.luceneIndexPath);
			} finally {
				try {
					searcher.close();
				} catch (IOException e) {
					log.error("Error closing index "+this.luceneIndexPath+" for searching", e);
				}
			}
			
			if( lastTasId!=null )
				return lastTasId; 
			else
				return -1;
		}
	}
	
	/**
	 * triggers index optimization during next update
	 */
	public void optimizeIndex() {
		synchronized(this) {
			this.optimizeIndex = true;
		}
	}

	/**
	 * flag given user as spammer - preventing further posts to be inserted and
	 * mark user's posts for deletion from index
	 * 
	 * @param username
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void flagUser(String username) {
		synchronized(this) {
			this.usersToFlag.add(username);
		}
	}
	
	/**
	 * unflag given user as spammer - enabling further posts to be inserted 
	 * 
	 * @param username
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void unFlagUser(String userName) {
		synchronized(this) {
			this.usersToFlag.remove(userName);
		}
	}
	
	//------------------------------------------------------------------------
	// public index access interface (all operations are cached)
	//------------------------------------------------------------------------
	/**
	 * cache given post for deletion
	 * 
	 * @param contentId post's content id 
	 * @return number of posts deleted from index
	 * 
	 * @throws StaleReaderException
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public void deleteDocumentForContentId(Integer contentId) {
		synchronized(this) {
			this.contentIdsToDelete.add(contentId);
		}
	}

	/**
	 * cache given post for insertion
	 * 
	 * @param doc post document to insert into the index
	 */
	public void insertDocument(Document doc) {
		synchronized(this) {
			this.postsToInsert.add(doc);
		}
	}

	/**
	 * cache given post for insertion
	 * 
	 * @param doc post document to insert into the index
	 */
	public void insertDocuments(List<Document> docs) {
		synchronized(this) {
			this.postsToInsert.addAll(docs);
		}
	}
	
	/**
	 * perform all cached operations to index
	 */
	public void flush() {
		synchronized(this) {
			if( !isIndexEnabled() ) {
				return;
			}
			
			boolean readUpdate  = false;
			boolean writeUpdate = false;
			//----------------------------------------------------------------
			// remove cached posts from index
			//----------------------------------------------------------------
			log.debug("Performing " + contentIdsToDelete.size() + " delete operations");
			if( (contentIdsToDelete.size()>0) || (usersToFlag.size()>0) ) {
				this.ensureReadAccess();
				
				// remove each cached post from index
				for( Integer contentId : contentIdsToDelete ) {
					try {
						this.purgeDocumentForContentId(contentId);
					} catch (IOException e) {
						log.error("Error deleting post "+contentId+" from index", e);
					}
				}
				
				// remove spam posts form index
				for( String userName : usersToFlag ) {
					try {
						int cnt = purgeDocumentsForUser(userName);
						log.debug("Purged " +cnt+ " posts for user " +userName);
					} catch (IOException e) {
						log.error("Error deleting spam posts for user "+userName+" from index", e);
					}
				}
				
				readUpdate = true;
			}

			//----------------------------------------------------------------
			// add cached posts to index
			//----------------------------------------------------------------
			log.debug("Performing " + postsToInsert.size() + " insert operations");
			if( postsToInsert.size()>0 ) {
				this.ensureWriteAccess();
				try {
					this.insertRecordsIntoIndex(postsToInsert);
				} catch (IOException e) {
					log.error("Error adding posts to index.", e);
				}
				writeUpdate = true;
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
			// FIXME: this is a bit ugly...
			if( readUpdate && !writeUpdate ) {
				try {
					closeIndexReader();
					openIndexReader();
				} catch (IOException e) {
					log.error("Error commiting index update.", e);
				}
			} else
				ensureReadAccess();
		}
	}

	
	/**
	 * closes all writer and reader and reopens the index reader
	 */
	public void reset() {
		synchronized(this) {
			if( !isIndexEnabled() ) {
				return;
			}
			switch(this.accessMode) {
			case ReadOnly:
				accessMode = AccessMode.None;
				try {
					closeIndexReader();
				} catch (IOException e) {
					log.error("IOException while closing index reader", e);
				}
				try {
					openIndexReader();
				} catch (IOException e) {
					log.error("Error opening index reader", e);
				}
				break;
			case WriteOnly:
				accessMode = AccessMode.None;
				try {
					closeIndexWriter();
				} catch (IOException e) {
					log.error("IOException while closing index reader", e);
				}
				try {
					openIndexWriter();
				} catch (IOException e) {
					log.error("Error opening index reader", e);
				}
				break;
			default:
				// nothing to do
			}

			this.postsToInsert.clear();
			this.contentIdsToDelete.clear();
			this.usersToFlag.clear();
		}
	}	
	//------------------------------------------------------------------------
	// private index access interface
	//------------------------------------------------------------------------
	/**
	 * write given post into the index
	 * 
	 * @param post
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void insertRecordIntoIndex(Document post) throws CorruptIndexException, IOException {
		if( !this.usersToFlag.contains(post.get(FLD_USER_NAME)) ) { 
			// skip users which where flagged as spammers
			indexWriter.addDocument(post);
		}
	}	

	/**
	 * write given post into the index
	 * 
	 * @param post
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void insertRecordsIntoIndex(Collection<Document> posts) throws CorruptIndexException, IOException {
		for( Document post : posts ) {
			this.insertRecordIntoIndex(post);
		}
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
	private int purgeDocumentForContentId(Integer contentId) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
		Term term = new Term(FLD_CONTENT_ID, contentId.toString() );
		return purgeDocuments(term);
	}
	
	/**
	 * delete all documents of a given user from index
	 * 
	 * @param username
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private int purgeDocumentsForUser(String username) throws CorruptIndexException, IOException {
		// delete each post owned by given user
		Term term = new Term(FLD_USER_NAME, username );
		return purgeDocuments(term);
	}

	/**
	 * remove posts matching to given search term from index
	 * 
	 * @param searchTerm
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private int purgeDocuments(Term searchTerm) throws CorruptIndexException, IOException {
		return this.indexReader.deleteDocuments(searchTerm);
	}
	
	/**
	 * sets access mode to read-only
	 */
	private void ensureReadAccess() {
		//--------------------------------------------------------------------
		// open index for reading
		//--------------------------------------------------------------------
		// close IndexWriter
		if( accessMode != AccessMode.ReadOnly ) {
			try {
				closeIndexWriter();
			} catch (IOException e) {
				log.error("IOException while closing indexwriter", e);
			}
			accessMode = AccessMode.None;
			try {
				openIndexReader();
			} catch (IOException e) {
				log.error("Error opening index reader", e);
			}
		}
	}

	private void openIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		log.debug("Opening index "+luceneIndexPath+" for writing");
		indexWriter = new IndexWriter(luceneIndexPath, getAnalyzer(), false, IndexWriter.MaxFieldLength.UNLIMITED);
		accessMode  = AccessMode.WriteOnly;
	}

	private void closeIndexWriter() throws CorruptIndexException, IOException {
		log.debug("Closing index "+luceneIndexPath+" for writing");
		indexWriter.commit();
		// optimize index if requested
		if( this.optimizeIndex ) {
			log.debug("optimizing index " + luceneIndexPath);
			indexWriter.optimize();
			log.debug("optimizing index " + luceneIndexPath + " DONE");
			this.optimizeIndex = false;
		}
		// close index for writing
		indexWriter.close();
	}

	private void openIndexReader() throws CorruptIndexException, IOException {
		log.debug("Opening index "+luceneIndexPath+" for reading");
		indexReader = IndexReader.open(luceneIndexPath);
		accessMode  = AccessMode.ReadOnly;
	}

	private void closeIndexReader() throws IOException {
		log.debug("Closing index "+luceneIndexPath+" for reading");
		indexReader.close();
	}

	/**
	 * sets access mode to write-only
	 */
	private void ensureWriteAccess() {
		//--------------------------------------------------------------------
		// open index for reading
		//--------------------------------------------------------------------
		// close IndexWriter
		if( accessMode != AccessMode.WriteOnly ) {
			try {
				closeIndexReader();
			} catch (IOException e) {
				log.error("IOException while closing index reader", e);
			}
			accessMode = AccessMode.None;
			try {
				openIndexWriter();
			} catch (IOException e) {
				log.error("Error opening index writer", e);
			}
		}
	}
	
	/**
	 * disable this index when open fails
	 * FIXME: implement me
	 */
	public void disableIndex() {
		this.isReady = false;
	}
	
	/**
	 * disable this index when initialization failed
	 * FIXME: implement me
	 */
	private void enableIndex() {
		this.isReady = true;
	}

	/**
	 * checks, whether the index is readily initialized
	 * @return true, if index is ready - false, otherwise
	 */
	private boolean isIndexEnabled() {
		return this.isReady;
	}
	
	
	/**
	 * get managed resource type
	 */
	protected abstract Class<? extends Resource> getResourceType();
	
	/**
	 * get managed resource name
	 * @return
	 */
	private String getResourceName() {
		String name = getResourceType().getCanonicalName();
		if (name.lastIndexOf('.') > 0) {
	        name = name.substring(name.lastIndexOf('.')+1);
	    }
		
		return name;
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public Set<Document> getPostsToInsert() {
		return this.postsToInsert;
	}

	public Set<String> getUsersToFlag() {
		return this.usersToFlag;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}
}
