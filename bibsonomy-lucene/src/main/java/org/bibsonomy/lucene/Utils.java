package org.bibsonomy.lucene;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

/**
 * Utility class to provide some often used methods 
 * 
 * @version $Id$
 *
 */

public class Utils {

	/**
	 * 
	 * @param s source
	 * @param r replacement - SpecialLuceneCharacters will be replaced by r 
	 * @return
	 */
	
	public static String replaceSpecialLuceneChars(String s, String r) {
		s = s.replaceAll("[\\,\\&\\|\\(\\)\\[\\]\\{\\}\\~\\*\\^\\?\\:\\\\]", r);
		return s;
	}

	
	/**
	 * replace special lucene characters used in queries like ?*~:()[]{}&|:\ with " "
	 * @return the String
	 */
	public static String replaceSpecialLuceneChars(String s) {
		s = replaceSpecialLuceneChars (s, " ");
		return s;
	}

	/**
	 * remove special lucene characters used in queries like ?*~:()[]{}&|:\ 
	 * @return the String
	 */
	public static String resmoveSpecialLuceneChars(String s) {
		s = replaceSpecialLuceneChars (s, "");
		return s;
	}

	
	
	public static String getNewestRecordDateFromIndex(IndexReader reader) throws CorruptIndexException, IOException {
		final Log LOGGER = LogFactory.getLog(LuceneUpdater.class);
		String newestDate = "";
		
/*		System.out.println( "reader.maxDoc():  " + reader.maxDoc());
		System.out.println( "reader.numDocs(): " + reader.numDocs());
		System.out.println( "reader.document(maxDoc()-1):  " + reader.document(reader.maxDoc()-1));
		System.out.println( "reader.document(0):  " + reader.document(0));
*/
		int hitsPerPage = 1;
		
		IndexSearcher searcher = new IndexSearcher(reader);
		QueryParser qp = new QueryParser("content_id",new StandardAnalyzer());
		Sort sort = new Sort("date",true);

		// search over all elements sort them reverse by date and return 1 top document (newest one)
		TopDocs topDocs = null;
		try {
			topDocs = searcher.search(qp.parse("*:*"), null, hitsPerPage, sort);
		} catch (ParseException e) {
			LOGGER.error("ParseException while parsing *:* in getNewestRecordDateFromIndex ("+e.getMessage()+")");
		} 
		
		Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
/*
		System.out.println("qp.parse(\"*:*\") = "+qp.parse("*:*").toString());
		
		System.out.println("topDocs.totalHits: "+topDocs.totalHits);
		System.out.println("doc.content_id: "+doc.get("content_id"));
		System.out.println("doc.date: "+doc.get("date");
*/		
		newestDate = doc.get("date");

		searcher.close();
		
		return newestDate;
	}
	
	
	public static LuceneIndexStatistics getStatistics(String lucenePath) {
		final Log LOGGER = LogFactory.getLog(LuceneUpdater.class);
		
		// open Lucene index for reading
		IndexReader reader=null;
		LuceneIndexStatistics indexStatistics = new LuceneIndexStatistics();

		try {
			reader = IndexReader.open(lucenePath);
			indexStatistics.setNewestRecordDate(Utils.getNewestRecordDateFromIndex(reader));
			indexStatistics.setCurrent(reader.isCurrent());
			indexStatistics.setLastModified(IndexReader.lastModified(lucenePath));
			indexStatistics.setCurrentVersion(IndexReader.getCurrentVersion(lucenePath));
		} catch (CorruptIndexException e) {
			LOGGER.error("CorruptIndexException in getStatistics ("+e.getMessage()+")");
		} catch (IOException e) {
			LOGGER.error("IOException in getStatistics("+e.getMessage()+")");
		}

		indexStatistics.setNumDocs(reader.numDocs());
		indexStatistics.setNumDeletedDocs(reader.numDeletedDocs());
		
		return indexStatistics;
	}
	
}