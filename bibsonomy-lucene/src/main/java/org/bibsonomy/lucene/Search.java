package org.bibsonomy.lucene;

import java.io.IOException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.bibsonomy.common.enums.GroupID;

public class Search {

	/** base path to lucene index */
//	private String luceneBasePath = "/home/bibsonomy/lucene/";
//	private String luceneBasePath = "/home/stud/sst/bibsonomy/";
	private String luceneBasePath = "";

	/** bookmark path to lucene index */
	private String luceneBookmarksPath = luceneBasePath+"lucene_bookmarks/"; 

	/** publication path to lucene index */
	private String lucenePublicationsPath = luceneBasePath+"lucene_publications/"; 

	/** duration of last query in milliseconds */
	private Long duration;

	/** lucene analyzer, must be the same as at indexing */
	SimpleAnalyzer analyzer = new SimpleAnalyzer();




	public Search() throws RuntimeException {
		try {
			/*
			 * FIXME: this should NOT be done on each instanciation of this class!
			 * better do this in a static block on class loading or make the class
			 * a singleton (if possible).
			 * 
			 */
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");

			/* set current path to lucene index, given by environment parameter in tomcat's context.xml
			 * 
			 *   <Environment name="luceneIndexPath" type="java.lang.String" value="/home/bibsonomy/lucene"/>
			 */
			this.setLuceneBasePath( (String) envContext.lookup("luceneIndexPath") );
			this.setLuceneBookmarksPath(this.getLuceneBasePath()+"lucene_bookmarks/");
			this.setLucenePublicationsPath(this.getLuceneBasePath()+"lucene_publications/");
		} catch (final NamingException e) {
			/*
			 * FIXME: rethrowing the exception as runtime ex is maybe not the best solution
			 */
			throw new RuntimeException(e);
		}
	}


	/**
	 * @return the duration in milliseconds
	 */
	public Long getDuration() {
		return duration;
	}


	/**getBibTexSearch
	 * @param duration the duration to set in milliseconds
	 */
	private void setDuration(Long duration) {
		this.duration = duration;
	}



	/** get ArrayList of strings of field id from lucene index
	 * 
	 * for pagination see http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * @param String idname fieldname of returning value
	 * @param char LuceneIndex lucene index to use b for bookmar, p for publications
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * */
	public ArrayList<String> searchLucene(char luceneIndex, String idname, String search_terms, GroupID grouptype, int limit, int offset) throws IOException {

		// get starttime to calculate duration of execution of this method
		long starttime = System.currentTimeMillis();
		long endtime = 0;

		String luceneIndexPath = "";
		Boolean debug = false;
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

		String querystring = "";


		if (luceneIndex == 'b')
		{
			luceneIndexPath = this.getLuceneBookmarksPath(); 

			// grouptype == 1 setzen, um vergleichbar zu sein mit alter afrage
			querystring = lField_group+":1 AND (" + lField_desc + ":("+ search_terms +") " + lField_tas + ":("+ search_terms +") " + lField_ext + ":("+ search_terms +") " + lField_url + ":("+ search_terms +") )" ;
//			String querystring = lField_group+":" ;
//			String querystring = search_terms;

		}
		else
		{
			luceneIndexPath = this.getLucenePublicationsPath();
			// TODO set query string
		}

		// declare ArrayList cidsArray for list of String to return
		ArrayList<String> cidsArray = new ArrayList<String>();


		// do not search for nothing in lucene index
		if ( (search_terms != null) && (!search_terms.isEmpty()) )
		{


			// open lucene index
			IndexReader reader = IndexReader.open(luceneIndexPath);


			QueryParser myParser = new QueryParser(lField_desc, analyzer);
			Query query;
			try {

				if (debug)
				{
					System.out.println("Lucene-Querystring: " + querystring);
				}

				query = myParser.parse(querystring);

				IndexSearcher searcher = new IndexSearcher(luceneBookmarksPath);
				Hits hits = searcher.search(query);


				int hitslimit = (((offset+limit)<hits.length())?(offset+limit):hits.length());

				for(int i = offset; i < hitslimit; i++){
					Document doc = hits.doc(i);
					cidsArray.add(doc.get(idname));
				}	 

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		


		}

		// get endtime and set it in class variable
		endtime = System.currentTimeMillis();
		this.setDuration(endtime-starttime);
		return cidsArray;
	};


	public void SearchDatabase()
	{
		// get starttime to calculate duration of execution of this method
		long starttime = System.currentTimeMillis();
		long endtime = 0;

		// IS this needed here or should database or whatever do this query?
		// if so, this class is only for lucene queryies, without any database access


		// get endtime and set it in class variable
		endtime = System.currentTimeMillis();
		this.setDuration(endtime-starttime);
	}


	/**
	 * @return the luceneBasePath
	 */
	public String getLuceneBasePath() {
		return luceneBasePath;
	}


	/**
	 * @param luceneBasePath the luceneBasePath to set
	 */
	public void setLuceneBasePath(String luceneBasePath) {
		this.luceneBasePath = luceneBasePath;
	}


	/**
	 * @return the luceneBookmarksPath
	 */
	private String getLuceneBookmarksPath() {
		return luceneBookmarksPath;
	}


	/**
	 * @param luceneBookmarksPath the luceneBookmarksPath to set
	 */
	private void setLuceneBookmarksPath(String luceneBookmarksPath) {
		this.luceneBookmarksPath = luceneBookmarksPath;
	}


	/**
	 * @return the lucenePublicationsPath
	 */
	private String getLucenePublicationsPath() {
		return lucenePublicationsPath;
	}


	/**
	 * @param lucenePublicationsPath the lucenePublicationsPath to set
	 */
	private void setLucenePublicationsPath(String lucenePublicationsPath) {
		this.lucenePublicationsPath = lucenePublicationsPath;
	}


}
