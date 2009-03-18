/**
 * Generate an empty lucene index
 */
package org.bibsonomy.batch.searchlucene;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;


/**
 * @author sst
 *
 */


public class GenerateEmptyLuceneIndex {

	public static void main(String[] args)
	throws IOException, ClassNotFoundException, SQLException 
	{

		// ADJUST THIS
		// Lucene index path
		// any existing index in this path would be deleted!
		// if path does not exist, it would be created
		String luceneBasePath = "/home/xxxxxx/bibsonomy/";
		final String luceneBookmarksPath = luceneBasePath+"lucene_bookmarks/"; 
		final String lucenePublicationsPath = luceneBasePath+"lucene_publications/"; 

		
		// Constants

		final String CONTENT_TYPE_BOOKMARK = "1";
		final String CONTENT_TYPE_BIBTEX = "2";
		final Log log = LogFactory.getLog(GenerateEmptyLuceneIndex.class);

		// Variables
		long starttime = System.currentTimeMillis();
		long endtime = 0;


		// field names in Lucene index
		String lField_contentid = "contentid";
		String lField_group = "group";
		String lField_date = "date";
		String lField_user = "user";
		String lField_desc = "desc";
		String lField_ext = "ext";
		String lField_url = "url";
		String lField_tas = "tas";
		String lField_type = "type";

		// open Lucene index 

		// Store the index in memory:
		//Directory directory = new RAMDirectory();

		// Use default analyzer
		SimpleAnalyzer analyzer_bm = new SimpleAnalyzer();
		SimpleAnalyzer analyzer_pub = new SimpleAnalyzer();

		// add few sample documents
		IndexWriter writer_bm = new IndexWriter(luceneBookmarksPath, analyzer_bm,true); // true überschreibt aktuellen index
		IndexWriter writer_pub = new IndexWriter(lucenePublicationsPath, analyzer_pub,true); // true überschreibt aktuellen index


//		***************************************************************************************************
//		Bookmark

		System.out.println("generate empty lucene index for bookmarks" );

		String bm_content_id = "";
		String bm_group = "";
		String bm_date = "";
		String bm_username = "";
		String bm_url = "";
		String bm_description = "";
		String bm_extended = "";
		String bm_tas = "";

		// Daten in Lucene speichern
		// lucene.speichern (bm_content_id, bm_group, bm_date, bm_username, bm_description, bm_extended, bm_url, bm_tas, CONTENT_TYPE_BOOKMARK)

		Document doc = new Document();
		doc.add(new Field(lField_contentid, bm_content_id, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(lField_group, bm_group, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(lField_date, bm_date, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(lField_user, bm_username, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_desc, bm_description, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_ext, bm_extended, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_url, bm_url, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_tas, bm_tas, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_type, CONTENT_TYPE_BOOKMARK, Field.Store.YES, Field.Index.UN_TOKENIZED));
		writer_bm.addDocument(doc);

		// close bookmark-indexWriter
		System.out.println("closing indexfile "+luceneBookmarksPath);	
		writer_bm.close();



//		***************************************************************************************************
//		BibTex


		System.out.println("generate empty lucene index for publications" );

		String bib_content_id = "";
		String bib_group = "";
		String bib_date = "";
		String bib_username = "";

		String bib_tas = "";

//		String bib_description = "";
		String bib_extended = "";
//		String bib_url = "";

		String bib_author = ""; 
		String bib_editor = ""; 
		String bib_title = ""; 
		String bib_journal = ""; 
		String bib_booktitle = ""; 
		String bib_volume = ""; 
		String bib_number = ""; 
		String bib_chapter = ""; 
		String bib_edition = ""; 
		String bib_month = ""; 
		String bib_day = ""; 
		String bib_howPublished = ""; 
		String bib_institution = ""; 
		String bib_organization = ""; 
		String bib_publisher = ""; 
		String bib_address = ""; 
		String bib_school = ""; 
		String bib_series = ""; 
		String bib_bibtexKey = ""; 
		String bib_url = ""; 
		String bib_type = ""; 
		String bib_description = ""; 
		String bib_annote = ""; 
		String bib_note = ""; 
		String bib_pages = ""; 
		String bib_bKey = ""; 
		String bib_crossref = ""; 
		String bib_misc = ""; 
		String bib_bibtexAbstract = ""; 
		String bib_year = "";


		// Daten in Lucene speichern
		// lucene.speichern (bm_content_id, bm_group, bm_date, bm_username, bm_description, bm_extended, bm_url, bm_tas, CONTENT_TYPE_BOOKMARK)

		doc = new Document();

		doc.add(new Field(lField_contentid, bib_content_id, Field.Store.YES, Field.Index.NO));
		doc.add(new Field(lField_group, bib_group, Field.Store.YES, Field.Index.NO));
		doc.add(new Field(lField_date, bib_date, Field.Store.YES, Field.Index.NO));
		doc.add(new Field(lField_user, bib_username, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_desc, bib_description, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_ext, bib_extended, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_url, bib_url, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_tas, bib_tas, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(lField_type, CONTENT_TYPE_BIBTEX, Field.Store.YES, Field.Index.TOKENIZED));

		doc.add(new Field("bib_author", bib_author, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_editor", bib_editor, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_title", bib_title, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_journal", bib_journal, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_booktitle", bib_booktitle, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_volume", bib_volume, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_number", bib_number, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_chapter", bib_chapter, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_edition", bib_edition, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_month", bib_month, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_day", bib_day, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_howPublished", bib_howPublished, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_institution", bib_institution, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_organization", bib_organization, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_publisher", bib_publisher, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_address", bib_address, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_school", bib_school, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_series", bib_series, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_bibtexKey", bib_bibtexKey, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_type", bib_type, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_annote", bib_annote, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_note", bib_note, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_pages", bib_pages, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_bKey", bib_bKey, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_crossref", bib_crossref, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_misc", bib_misc, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_bibtexAbstract", bib_bibtexAbstract, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("bib_year", bib_year, Field.Store.YES, Field.Index.TOKENIZED));


		writer_pub.addDocument(doc);


		// close publication-indexWriter
		System.out.println("closing indexfile "+lucenePublicationsPath);	
		writer_pub.close();



		System.out.println("done.");

	}



}
