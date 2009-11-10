package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.index.analyzer.SimpleKeywordAnalyzer;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.lucene.util.DBToolJDNIResource;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.tex.TexEncode;

import com.mysql.jdbc.Connection;

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
	/** coding whether index is opended for writing or reading */
	public static enum AccessMode {
		None, ReadOnly, WriteOnly;
	}
	/** indicating whether index is opended for writing or reading */
	private AccessMode accessMode;
	
	private static final String FLD_TAS = "tas";
	private static final String FLD_DATE = "date";
	private static final String FLD_LASTTASID = "lastTasId";

	private static final String COL_USER_NAME = "user_name";
	private static final String COL_CONTENT_ID = "content_id";

	protected static final Log log = LogFactory.getLog(LuceneResourceIndex.class);

	/** MAGIC KEY identifying the context environment for this class */
	private static final String CONTEXT_ENV_NAME = "java:/comp/env";
	
	/** MAGIC KEY identifying context variables for this class */
	private static final String CONTEXT_INDEX_PATH = "luceneIndexPath";
	private static final String FLD_MERGEDFIELD    = "mergedfields";

	/** gives read only access to the lucene index */
	IndexReader indexReader;

	/** gives write access to the lucene index */
	IndexWriter indexWriter;
	
	/** path to the lucene index */
	private String luceneIndexPath;

	private Analyzer analyzer;

	
	private static DBToolJDNIResource dbconn;
	private static Connection connection;
	
	
	/** list containing content ids of cached delete operations */
	private List<Integer> contentIdsToDelete;

	/** list containing content ids of cached delete operations */
	private List<Document> postsToInsert;
	
	/**
	 * constructor disabled
	 */
	protected LuceneResourceIndex(){
		init();
		
		// init data structures
		contentIdsToDelete = new LinkedList<Integer>();
		postsToInsert      = new LinkedList<Document>();
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
	 * perform all cached operations to index
	 */
	public void flush() {
		
		// remove cached posts from index
		log.debug("Performing " + contentIdsToDelete.size() + " delete operations");
		this.ensureReadAccess();
		for( Integer contentId : contentIdsToDelete ) {
			try {
				this.deleteDocumentForContentId(contentId);
			} catch (IOException e) {
				log.error("Error deleting post "+contentId+" from index", e);
			}
		};
		
		// add given posts to index
		log.debug("Performing " + postsToInsert.size() + " insert operations");
		this.ensureWriteAccess();
		try {
			this.insertRecordsIntoIndex3(postsToInsert);
		} catch (IOException e) {
			log.error("Error adding posts to index.", e);
		}
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
	
	/**
	 * get search for record containing given content_id
	 *  
	 * @param reader
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Document getRecordForContentId(Integer contentId) {
		int hitsPerPage = 1;
		
		IndexSearcher searcher = new IndexSearcher(indexReader);
		Sort sort = new Sort(FLD_LASTTASID,true);
		Query searchQuery = new TermQuery(new Term(COL_CONTENT_ID, contentId.toString()));

		// search over all elements sort them reverse by lastTasId and return first top document 
		TopDocs topDocs = null;
		Document doc = null;
		try {
			topDocs = searcher.search(searchQuery, null, hitsPerPage, sort);
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
	
	/**
	 * get most recent tas_id from index
	 *  
	 * @param reader
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public Integer getLastTasIdFromIndex() {
		Integer lastTasId = null;
		int hitsPerPage = 1;
		
		IndexSearcher searcher = new IndexSearcher(indexReader);
		Sort sort = new Sort(FLD_LASTTASID,true);
		Query searchQuery = new MatchAllDocsQuery();

		// search over all elements sort them reverse by lastTasId and return first top document 
		TopDocs topDocs = null;
		Document doc = null;
		try {
			topDocs = searcher.search(searchQuery, null, hitsPerPage, sort);
			doc     = searcher.doc(topDocs.scoreDocs[0].doc);
			// parse tas_id
			lastTasId = Integer.parseInt(doc.get(FLD_LASTTASID));
		} catch (IOException e) {
			log.error("Error reading index file " + this.luceneIndexPath);
		} finally {
			try {
				searcher.close();
			} catch (IOException e) {
				log.error("Error closing index "+this.luceneIndexPath+" for searching", e);
			}
		}
		return (lastTasId==0)?0:lastTasId;
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
	protected int deleteDocumentForContentId(Integer contentId) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
		Term term = new Term(COL_CONTENT_ID, contentId.toString() );
		return indexReader.deleteDocuments(term);
	}

	
	/**
	 * delete all documents of a given user from index
	 * 
	 * @param username
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public boolean deleteDocumentsInIndex(String username) throws CorruptIndexException, IOException {
		boolean allDocsDeleted = true;
		int s = 0;

		if (username.length() > 0) {
			Term term = new Term("user_name", username );

			s = indexReader.deleteDocuments(term);
			if (s == 0) {
				log.debug("Documents from user " + username + " NOT deleted ("+s+")!");
				allDocsDeleted = false;
			} else {
				log.debug("Document from user " + username + " deleted ("+s+" occurences)!");
				allDocsDeleted = true;
			}
		} else {
			log.debug("Username is empty, no documents deleted!");
			allDocsDeleted = false;
		}

		return allDocsDeleted;
	}

	/**
	 * deletes all resources of a given user from the index
	 * 
	 * @param reader
	 * @param username
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected boolean deleteDocumentsInIndex(IndexReader reader, String username) throws CorruptIndexException, IOException {
		boolean allDocsDeleted = true;
		int s = 0;
		
		if (username.length() > 0) {
			Term term = new Term(COL_USER_NAME, username );
	
			s = reader.deleteDocuments(term);
			if (s == 0) {
				log.debug("Documents from user " + username + " NOT deleted ("+s+")!");
				allDocsDeleted = false;
			} else {
				log.debug("Document from user " + username + " deleted ("+s+" occurences)!");
				allDocsDeleted = true;
			}
		} else {
			log.debug("Username is empty, no documents deleted!");
			allDocsDeleted = false;
		}

		return allDocsDeleted;
	}
	
	/**
	 * adds given resources into index
	 * 
	 * @param writer
	 * @param contents
	 * @param optimize flag indicating whether the index should be optimized after modifying the index
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected boolean insertRecordsIntoIndex(List<LuceneData> contents, boolean optimize) throws CorruptIndexException, IOException {
		//--------------------------------------------------------------------
		// open index for writing
		// TODO: implement a more efficient read/write-mode management
		//--------------------------------------------------------------------
		// close IndexReader
		try {
			log.debug("Closing index "+luceneIndexPath+" for reading");
			indexReader.close();
		} catch (IOException e) {
			log.error("IOException while reader.close() ("+e.getMessage()+")", e);
		}
		// open Lucene index for writing
		log.debug("Opening index "+luceneIndexPath+" for writing");
		IndexWriter indexWriter =
			new IndexWriter(luceneIndexPath, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
		
		//--------------------------------------------------------------------
		// insert records into the index
		//--------------------------------------------------------------------
		HashMap<String, String> contentFields = getContentFields();//new HashMap<String, String>();
		
		String mergedfieldname = "mergedfields";

		
		for (LuceneData luceneDataContent : contents) {
			
			Map<String, String> content = luceneDataContent.getContent();
			
			for (String contentField : contentFields.keySet()) {
				if (content.get(contentField) == null) {
					content.put(contentField, "");
				} 
			}
	
			// an additional field contains the concatenation of all other fields for fulltext search
			String mergedfields = "";

			Document doc = new Document();
			TexEncode tex = new TexEncode();
			for (String contentField : contentFields.keySet()) {

				if (contentField == "content_id") {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES, Field.Index.NOT_ANALYZED));
				} else if (contentField == "group") {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES, Field.Index.NOT_ANALYZED));
				} else if (contentField == FLD_DATE) {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES,Field.Index.NOT_ANALYZED));
				} else if (contentField == "year") {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES,Field.Index.NOT_ANALYZED));
				} else if ((contentField == "author")||(contentField == "tas")) {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES,Field.Index.ANALYZED));
					mergedfields = mergedfields + " " + tex.encode(content.get(contentField));
				} else if (contentField == "user_name") {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES,Field.Index.NOT_ANALYZED));
					mergedfields = mergedfields + " " + tex.encode(content.get(contentField));
				} else if ((contentField == "intrahash") || (contentField == "interhash")) {
					doc.add(new Field(contentField, content.get(contentField), Field.Store.YES, Field.Index.NOT_ANALYZED));
				} else {
					doc.add(new Field(contentField, tex.encode(content.get(contentField)), Field.Store.YES, Field.Index.NO));
					mergedfields = mergedfields + " " + tex.encode(content.get(contentField));
				}
			}
			// TODO Field.Store.NO
			doc.add(new Field(mergedfieldname, tex.encode(mergedfields), Field.Store.YES, Field.Index.ANALYZED));
	
			log.debug("add doc to index: " + doc.get("content_id"));
			
			indexWriter.addDocument(doc);
	
		}
		//--------------------------------------------------------------------
		// flush index
		//--------------------------------------------------------------------
		// commit changes
		indexWriter.commit();
		
		// optimize index if requested
		if (optimize) {
			log.debug("optimizing index " + luceneIndexPath);
			try {
				indexWriter.optimize();
			} catch (CorruptIndexException e) {
				log.error("CorruptIndexException while writer.optimize() ("+e.getMessage()+")");
			} catch (IOException e) {
				log.error("IOException while writer.optimize() ("+e.getMessage()+")");
			}
			log.debug("optimizing index " + luceneIndexPath + " DONE");
		}
		
		// close index writer 
		log.debug("Closing index "+luceneIndexPath+" for writing");
		indexWriter.close();
		
		// reopen index reader
		// TODO: implement a more efficient read/write-mode management
		log.debug("Opening index "+luceneIndexPath+" for reading");
		indexReader = IndexReader.open(luceneIndexPath);
		
		
		// all done
		log.info("Index "+luceneIndexPath+" updated.");
		// FIXME: why return false?
		return false;
	}
	
	/**
	 * adds given resources into index
	 * 
	 * @param writer
	 * @param contents
	 * @param optimize flag indicating whether the index should be optimized after altering it
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	@Deprecated
	public boolean insertRecordsIntoIndex2(List<HashMap<String, Object>> contents, boolean optimize) throws CorruptIndexException, IOException {
		//--------------------------------------------------------------------
		// open index for writing
		// TODO: implement a more efficient read/write-mode management
		//--------------------------------------------------------------------
		// close IndexReader
		try {
			log.debug("Closing index "+luceneIndexPath+" for reading");
			indexReader.close();
		} catch (IOException e) {
			log.error("IOException while reader.close() ("+e.getMessage()+")", e);
		}
		// open Lucene index for writing
		log.debug("Opening index "+luceneIndexPath+" for writing");
		IndexWriter indexWriter =
			new IndexWriter(luceneIndexPath, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
		
		//--------------------------------------------------------------------
		// insert records into the index
		//--------------------------------------------------------------------
		String mergedfieldname = "mergedfields";

		HashMap<String, String> contentFields = getContentFields();
		
		for (HashMap<String, Object> content : contents) {
			
			for (String contentField : contentFields.keySet()) {
				if (content.get(contentField) == null) {
					content.put(contentField, "");
				} 
			}
	
			String mergedfields = "";

			Document doc = new Document();
			TexEncode tex = new TexEncode();
			for (String contentField : contentFields.keySet()) {

				if (contentField == "content_id") {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				} else if (contentField == "group") {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				} else if (contentField == "date") {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES,Field.Index.NOT_ANALYZED));
				} else if (contentField == "year") {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES,Field.Index.NOT_ANALYZED));
				} else if ((contentField == "author")||(contentField == "tas")) {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES,Field.Index.ANALYZED));
					mergedfields = mergedfields + " " + tex.encode(content.get(contentField).toString());
				} else if (contentField == "user_name") {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES,Field.Index.NOT_ANALYZED));
					mergedfields = mergedfields + " " + tex.encode(content.get(contentField).toString());
				} else if ((contentField == "intrahash") || (contentField == "interhash")) {
					doc.add(new Field(contentField, content.get(contentField).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				} else {
					doc.add(new Field(contentField, tex.encode(content.get(contentField).toString()), Field.Store.YES, Field.Index.NO));
					mergedfields = mergedfields + " " + tex.encode(content.get(contentField).toString());
				}
			}
			// TODO Field.Store.NO
			doc.add(new Field(mergedfieldname, tex.encode(mergedfields), Field.Store.YES, Field.Index.ANALYZED));
	
			log.debug("add doc to index: " + doc.get("content_id"));
			
			indexWriter.addDocument(doc);
	
		}
		

		//--------------------------------------------------------------------
		// flush index
		//--------------------------------------------------------------------
		// commit changes
		indexWriter.commit();
		
		// optimize index if requested
		if (optimize) {
			log.debug("optimizing index " + luceneIndexPath);
			try {
				indexWriter.optimize();
			} catch (CorruptIndexException e) {
				log.error("CorruptIndexException while writer.optimize() ("+e.getMessage()+")");
			} catch (IOException e) {
				log.error("IOException while writer.optimize() ("+e.getMessage()+")");
			}
			log.debug("optimizing index " + luceneIndexPath + " DONE");
		}
		
		// close index writer 
		log.debug("Closing index "+luceneIndexPath+" for writing");
		indexWriter.close();
		
		// reopen index reader
		// TODO: implement a more efficient read/write-mode management
		log.debug("Opening index "+luceneIndexPath+" for reading");
		indexReader = IndexReader.open(luceneIndexPath);
		
		
		// all done
		log.info("Index "+luceneIndexPath+" updated.");
		// FIXME: why return false?
		return false;
	}

	/**
	 * adds given records to index
	 * 
	 * FIXME: merge all insertion methods and implement an efficient read/write management
	 *        probably by caching deleted/updated/inserted posts and perform operation combined
	 *        
	 * @param contents
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public boolean insertRecordsIntoIndex3(List<Document> contents) throws CorruptIndexException, IOException {
		if( contents.size()==0 )
			return true;
		
		//--------------------------------------------------------------------
		// open index for writing
		// TODO: implement a more efficient read/write-mode management
		//--------------------------------------------------------------------
		// close IndexReader
		try {
			log.debug("Closing index "+luceneIndexPath+" for reading");
			indexReader.close();
		} catch (IOException e) {
			log.error("IOException while reader.close() ("+e.getMessage()+")", e);
		}
		// open Lucene index for writing
		log.debug("Opening index "+luceneIndexPath+" for writing");
		IndexWriter indexWriter =
			new IndexWriter(luceneIndexPath, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
		
		//--------------------------------------------------------------------
		// insert records into the index
		//--------------------------------------------------------------------
		for( Document post : contents) {
			indexWriter.addDocument(post);
		}
		
		//--------------------------------------------------------------------
		// flush index
		//--------------------------------------------------------------------
		// commit changes
		indexWriter.commit();
		
		// close index writer 
		log.debug("Closing index "+luceneIndexPath+" for writing");
		indexWriter.close();
		
		// reopen index reader
		// TODO: implement a more efficient read/write-mode management
		log.debug("Opening index "+luceneIndexPath+" for reading");
		indexReader = IndexReader.open(luceneIndexPath);
		
		
		// all done
		log.info("Index "+luceneIndexPath+" updated.");
		// FIXME: why return false?
		return false;
	}
	
	/**
	 * update tag column in index for given posts
	 * 
	 * FIXME: this is quickly hacked due to zeitnot and probably inefficient
	 * 
	 * @param postsToUpdate list of posts whose tag
	 *    assignments have changed
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 * @throws StaleReaderException 
	 */
	@SuppressWarnings("unchecked")
	public void updateTagAssignments(List<Post<R>> postsToUpdate) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
		TexEncode tex = new TexEncode();

		// cache documents to update
		List<Document> updatedDocuments = new LinkedList<Document>();
		
		// process each post
		for( Post<R> post : postsToUpdate ) {
			log.debug("Updating post " + post.getResource().getTitle() + " ("+post.getContentId()+")");
			// get old post from index
			Document doc = getRecordForContentId(post.getContentId());
			// update field 'tas'
			// FIXME: apply generic data extraction framework
			doc.removeField(FLD_TAS);
			doc.removeField(FLD_MERGEDFIELD);
			String tags = "";
			for( Tag tag : post.getTags() ) {
				tags += " " + tag.getName();
			};
			doc.add(new Field(FLD_TAS, tex.encode(tags), Field.Store.YES, Field.Index.ANALYZED));
			// update field  'mergedfield'
			String mergedFields = "";
			for( Field field : (List<Field>)doc.getFields() ) {
				mergedFields += (field.stringValue()==null)?"":field.stringValue();
				mergedFields += " ";
			}
			doc.add(new Field(FLD_MERGEDFIELD, tex.encode(mergedFields), Field.Store.YES, Field.Index.ANALYZED));
			// update date 
			// FIXME: this is for setting the index' last change date - overriding the post's real date
			doc.removeField(FLD_DATE);
			// FIXME: use global type handling via spring!
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
			doc.add(new Field(FLD_DATE, dateFormatter.format(post.getDate()), Field.Store.YES,Field.Index.NOT_ANALYZED));			
			// cache document for update
			updatedDocuments.add(doc);
			if( this.deleteDocumentForContentId(post.getContentId())!=1 ) {
				log.error("Error updating tag assignment");
			}
		}
	
		// finally write updated posts to index
		// FIXME: implement an efficient read/write management for bundling insertions
		//        and updates
		this.insertRecordsIntoIndex3(updatedDocuments);
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
				log.debug("Closing index "+luceneIndexPath+" for writing");
				indexWriter.close();
			} catch (IOException e) {
				log.error("IOException while closing indexwriter", e);
			}
			accessMode = AccessMode.None;
			try {
				log.debug("Opening index "+luceneIndexPath+" for reading");
				indexReader = IndexReader.open(luceneIndexPath);
				accessMode  = AccessMode.ReadOnly;
			} catch (IOException e) {
				log.error("Error opening index reader", e);
			}
		}
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
				log.debug("Closing index "+luceneIndexPath+" for reading");
				indexReader.close();
			} catch (IOException e) {
				log.error("IOException while closing index reader", e);
			}
			accessMode = AccessMode.None;
			try {
				log.debug("Opening index "+luceneIndexPath+" for writing");
				indexWriter = new IndexWriter(luceneIndexPath, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
				accessMode  = AccessMode.WriteOnly;
			} catch (IOException e) {
				log.error("Error opening index writer", e);
			}
		}
	}

	
	/**
	 * get list of fields this resource consists of
	 * @return
	 */
	@Deprecated
	protected abstract HashMap<String, String> getContentFields();
	
	/**
	 * disables read operations on the index
	 */
	//public abstract void setWriteMode();

	/**
	 * disables write operations on the index
	 */
	//public abstract void setReadMode();

	/**
	 * get managed resource type
	 */
	protected abstract Class<? extends Resource> getResourceType();
	
	/**
	 * get managed resource record type
	 */
	protected abstract RecordType getRecordType();

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
