package org.bibsonomy.lucene;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class Search {

	/** base path to lucene index */
	private String luceneBasePath = "/home/bibsonomy/lucene/";

	/** bookmark path to lucene index */
	private String luceneBookmarksPath = luceneBasePath+"lucene_bookmarks/"; 

	/** publication path to lucene index */
	private String lucenePublicationsPath = luceneBasePath+"lucene_publications/"; 

	/** duration of last query in milliseconds */
	private Long duration;

	/** lucene analyzer, must be the same as at indexing */
	SimpleAnalyzer analyzer = new SimpleAnalyzer();
	



	/**
	 * @return the duration in milliseconds
	 */
	public Long getDuration() {
		return duration;
	}


	/**
	 * @param duration the duration to set in milliseconds
	 */
	private void setDuration(Long duration) {
		this.duration = duration;
	}

		
	
	/** get ArrayList of strings of field id from lucene index
	 * @param String idname fieldname of returning value
	 * @param char LuceneIndex lucene index to use b for bookmar, p for publications
	 * @throws IOException 
	 * @throws CorruptIndexException */
	public ArrayList<String> SearchLucene(String idname, char luceneIndex, String search_terms) throws CorruptIndexException, IOException{

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
			 luceneIndexPath = luceneBookmarksPath; 

			// grouptype == 1 setzen, um vergleichbar zu sein mit alter afrage
			 querystring = lField_group+":1 AND (" + lField_desc + ":("+ search_terms +") " + lField_tas + ":("+ search_terms +") " + lField_ext + ":("+ search_terms +") " + lField_url + ":("+ search_terms +") )" ;
//			String querystring = lField_group+":" ;
//			String querystring = search_terms;
		
		}
		else
		{
			 luceneIndexPath = lucenePublicationsPath; 
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
				
				for(int i = 0; i < hits.length(); i++){
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


}
