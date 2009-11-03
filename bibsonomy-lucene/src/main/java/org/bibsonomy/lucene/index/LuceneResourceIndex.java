package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.param.RecordType;
import org.bibsonomy.lucene.util.DBToolJDNIResource;
import org.bibsonomy.model.Resource;
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
	private static final String FLD_DATE = "date";

	private static final String COL_USER_NAME = "user_name";

	private static final String COL_CONTENT_ID = "content_id";

	protected static final Log log = LogFactory.getLog(LuceneResourceIndex.class);

	/** MAGIC KEY identifying the context environment for this class */
	private static final String CONTEXT_ENV_NAME = "java:/comp/env";
	
	/** MAGIC KEY identifying context variables for this class */
	private static final String CONTEXT_INDEX_PATH = "luceneIndexPath";

	/** gives access to the lucene index */
	IndexReader indexReader;
	
	/** path to the lucene index */
	private String luceneIndexPath;

	private SimpleAnalyzer analyzer;

	
	private static DBToolJDNIResource dbconn;
	private static Connection connection;
	
	// FIXME: think of some better way to map terms to columns
	
	/**
	 * constructor disabled
	 */
	protected LuceneResourceIndex(){
		init();
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
		} catch (CorruptIndexException e) {
			log.error("CorruptIndexException while opening IndexReader in updateIndexes ("+e.getMessage()+")", e);
		} catch (IOException e) {
			log.error("IOException while opening IndexReader in updateIndexes("+e.getMessage()+")", e);
		}
		
		this.analyzer = new SimpleAnalyzer();
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

		// search over all elements sort them reverse by date and return 1 top document (newest one)
		TopDocs topDocs = null;
		Document doc = null;
		try {
			topDocs = searcher.search(qp.parse("*:*"), null, hitsPerPage, sort);
			doc = searcher.doc(topDocs.scoreDocs[0].doc);
			// parse date
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
			newestDate = dateFormatter.parse(doc.get(FLD_DATE));//dateFormatter.parse("1815-12-10 00:00:00.0");
			searcher.close();
		} catch (ParseException e) {
			log.error("ParseException while parsing *:* in getNewestRecordDateFromIndex ("+e.getMessage()+")");
		} catch (java.text.ParseException e) {
			log.error("Error parsing date " + ((doc!=null)?doc.get(FLD_DATE):""));
			newestDate = new Date();
		} catch (IOException e) {
			log.error("Error reading index file " + this.luceneIndexPath);
		}
		
		if( newestDate!=null )
			return newestDate; 
		else
			return new Date();
	}
	
	/**
	 * delete resources (given by content ids) from index
	 *  
	 * @param indexReader index reader
	 * @param contentIdsToDelete list of content ids which should be deleted
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected boolean deleteDocumentsInIndex(List<Integer> contentIdsToDelete) throws CorruptIndexException, IOException {
		boolean allDocsDeleted = true;
		int s = 0;
		
		Iterator<Integer> i = contentIdsToDelete.iterator();
		while (i.hasNext()) {
			String docId = i.next().toString();
			Term term = new Term(COL_CONTENT_ID, docId );

			s = indexReader.deleteDocuments(term);
			if (s == 0) {
				log.debug("Document " +docId + " NOT deleted ("+s+")!");
				allDocsDeleted = false;
			}
			else {
				log.debug("Document " +docId + " deleted ("+s+" occurences)!");
			}
		}
		
		return allDocsDeleted;
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
		HashMap<String, String> contentFields = new HashMap<String, String>();
		
		String mergedfieldname = "mergedfields";

		
		for (LuceneData luceneDataContent : contents) {
			
			HashMap<String, String> content = luceneDataContent.getContent();
			
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
