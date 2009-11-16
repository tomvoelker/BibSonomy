package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
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
public abstract class LuceneResourceIndex<R extends Resource> {
	/** coding whether index is opened for writing or reading */
	public static enum AccessMode {
		None, ReadOnly, WriteOnly;
	}
	/** indicating whether index is opened for writing or reading */
	private AccessMode accessMode;
	
	
	private static final String FLD_DATE          = "date";
	private static final String FLD_LAST_TAS_ID   = "last_tas_id";
	private static final String FLD_LAST_LOG_DATE = "last_log_date";
	private static final String FLD_USER_NAME     = "user_name";
	
	private static final String COL_CONTENT_ID = "content_id";

	protected static final Log log = LogFactory.getLog(LuceneResourceIndex.class);

	/** MAGIC KEY identifying the context environment for this class */
	private static final String CONTEXT_ENV_NAME = "java:/comp/env";
	
	/** MAGIC KEY identifying context variables for this class */
	private static final String CONTEXT_INDEX_PATH = "luceneIndexPath";







	/** gives read only access to the lucene index */
	IndexReader indexReader;

	/** gives write access to the lucene index */
	IndexWriter indexWriter;
	
	/** path to the lucene index */
	private String luceneIndexPath;

	private Analyzer analyzer;
	
	/** list containing content ids of cached delete operations */
	private List<Integer> contentIdsToDelete;

	/** list containing content ids of cached delete operations */
	private List<Document> postsToInsert;
	
	/** 
	 * set of usernames which where flagged as spammers since last update
	 * which should be removed from index during next update (blocking new posts
	 * to be inserted for given users) 
	 */
	private Set<String> usersToFlag;
	
	/**
	 * constructor disabled
	 */
	protected LuceneResourceIndex(){
		init();
		
		// init data structures
		contentIdsToDelete = new LinkedList<Integer>();
		postsToInsert      = new LinkedList<Document>();
		usersToFlag        = new TreeSet<String>();
	};
	
	/**
	 * initialize internal data structures
	 */
	private void init() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup(CONTEXT_ENV_NAME);
			
			String contextPathName = CONTEXT_INDEX_PATH+getResourceName();
			this.luceneIndexPath = (String) envContext.lookup(contextPathName);
		} catch (NamingException e) {
			log.error("NamingException requesting JNDI environment variables ' ("+e.getMessage()+")", e);
		}

		try {
			indexReader = IndexReader.open(luceneIndexPath);
			accessMode = AccessMode.ReadOnly;
		} catch (CorruptIndexException e) {
			log.error("CorruptIndexException while opening IndexReader in updateIndexes ("+e.getMessage()+")", e);
		} catch (IOException e) {
			log.error("IOException while opening IndexReader in updateIndexes("+e.getMessage()+")", e);
		}
		
		this.analyzer = new StandardAnalyzer();
	}
	

	
	/**
	 * get most recent post's date from index
	 *  
	 * @param reader
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Date getNewestRecordDateFromIndex() {
		synchronized(this) {
			this.ensureReadAccess();
			
			Date newestDate = null;
	
			int hitsPerPage = 1;
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			QueryParser qp = new QueryParser(COL_CONTENT_ID,new StandardAnalyzer());
			Sort sort = new Sort(FLD_DATE,true);
	
			// FIXME: dates shouldn't be stored in a text format
			// search over all elements sort them reverse by date and return 1 top document (newest one)
			TopDocs topDocs = null;
			Document doc = null;
			try {
				topDocs = searcher.search(qp.parse("*:*"), null, hitsPerPage, sort);
				doc = searcher.doc(topDocs.scoreDocs[0].doc);
				// parse date
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
				newestDate = dateFormatter.parse(doc.get(FLD_DATE));//dateFormatter.parse("1815-12-10 00:00:00.0");
			} catch (ParseException e) {
				log.error("ParseException while parsing *:* in getNewestRecordDateFromIndex ("+e.getMessage()+")");
			} catch (java.text.ParseException e) {
				log.error("Error parsing date " + ((doc!=null)?doc.get(FLD_DATE):""));
				newestDate = new Date();
			} catch (IOException e) {
				log.error("Error reading index file " + this.luceneIndexPath);
			} finally {
				try {
					searcher.close();
				} catch (IOException e) {
					log.error("Error closing index "+this.luceneIndexPath+" for searching", e);
				}
			}
			
			if( newestDate!=null )
				return newestDate; 
			else
				return new Date();
		}
	}

	/**
	 * get latest log_date[ms] from index 
	 * @return
	 */
	public long getLastLogDate() {
		synchronized(this) {
			this.ensureReadAccess();
			
			Long lastLogDate = null;
	
			int hitsPerPage = 1;
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			QueryParser qp = new QueryParser(FLD_LAST_LOG_DATE,new StandardAnalyzer());
			Sort sort = new Sort(FLD_LAST_LOG_DATE,true);
	
			// FIXME: dates shouldn't be stored in a text format
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
			} catch (IOException e) {
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
			} catch (IOException e) {
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
	 * get search for record containing given content_id
	 *  
	 * @param reader
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Document getRecordForContentId(Integer contentId) {
		synchronized(this) {
			ensureReadAccess();
			
			int hitsPerPage = 1;

			IndexSearcher searcher = new IndexSearcher(indexReader);
			Sort sort = new Sort(FLD_DATE,true);
			Query searchQuery = new TermQuery(new Term(COL_CONTENT_ID, contentId.toString()));

			// search over all elements sort them reverse by date and return first top document 
			TopDocs topDocs = null;
			Document doc = null;
			try {
				topDocs = searcher.search(searchQuery, null, hitsPerPage, sort);
				if( topDocs.scoreDocs.length>0 )
					doc     = searcher.doc(topDocs.scoreDocs[0].doc);
			} catch (IOException e) {
				log.error("Error reading index file " + this.luceneIndexPath);
			} finally {
				try {
					searcher.close();
				} catch (IOException e) {
					log.error("Error closing index "+this.luceneIndexPath+" for searching", e);
				}
			}
			return doc;
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
			if( !this.usersToFlag.contains(doc.get(FLD_USER_NAME)) ) { 
				// skip users which where flagged as spammers
				this.postsToInsert.add(doc);
			}
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
		indexWriter.addDocument(post);
	}	

	/**
	 * write given post into the index
	 * 
	 * @param post
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void insertRecordsIntoIndex(List<Document> posts) throws CorruptIndexException, IOException {
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
		Term term = new Term(COL_CONTENT_ID, contentId.toString() );
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
	 * closes all writer and reader and reopens the index readern
	 */
	public void reset() {
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
		indexWriter = new IndexWriter(luceneIndexPath, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
		accessMode  = AccessMode.WriteOnly;
	}

	private void closeIndexWriter() throws CorruptIndexException, IOException {
		log.debug("Closing index "+luceneIndexPath+" for writing");
		indexWriter.commit();
		// FIXME: handle index-optimization
		// optimize index if requested
		/*
		if (optimize) {
			log.debug("optimizing index " + luceneIndexPath);
			indexWriter.optimize();
			log.debug("optimizing index " + luceneIndexPath + " DONE");
		}
		*/
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


}
