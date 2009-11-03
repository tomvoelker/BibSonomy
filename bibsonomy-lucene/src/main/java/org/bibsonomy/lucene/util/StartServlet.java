/**
 * 
 */
package org.bibsonomy.lucene.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
//import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;


/**
 * @author sst
 *
 */



public class StartServlet extends HttpServlet {

/*	
	public List<String> getAllBibsonomyData () {
		
		List<String> result = new ArrayList<String>();
	
				
		return result;
		
	}

	
	public static List<String> getMatches(Pattern pattern, String text, int splitAtSpace) {
		List<String> matches = new ArrayList<String>();
		Matcher m = pattern.matcher(text);
		while(m.find()) {
			
			if (m.group(1).contains(" ") && splitAtSpace==1 ) {
				String[] tempMatchesM = m.group(1).split(" ");
				for (String s : tempMatchesM) {
					matches.add(s);
				}
			}
			else {
				matches.add(m.group(1));
			}
			 
		}
		return matches;
	} 

	public static List<String> getMatches(Pattern pattern, String text) {
		return getMatches(pattern, text, 0);
	} 

	
	public void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {


		
		Boolean debug = true;
		
		
		// Database
		String db_host = "www.biblicious.org";
		int db_port = 3306;
		String db_name = "bibsonomy";
		String db_user = "bibsonomy";
		String db_pass = "xxxxxxxxx";
	
		// instantiiere Datenbankverbindung 
		DBTool dbconn = new DBTool();
		
		// konfiguriere Datenbankverbindungsinstanz
		dbconn.setDatabase(db_name);
		dbconn.setHost(db_host);
		dbconn.setUser(db_user);
		dbconn.setPasswd(db_pass);
		dbconn.setPort(db_port);
		
		Connection connection = null;

		// establish database connection
		try {
			connection = dbconn.getDBConnection();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		Date currentTime;
		SimpleDateFormat dateFormatter; // formatiert das Datum
		String dateString = "";         // String für formatiertes Datum
		
		
		
		
		
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

		String doc1_content = "uno";
		String doc2_content = "dos";
		String doc3_content = "uno dos tres";
		String doc4_content = "quatro";
		String doc5_content = "uno dos tres quatro cinco";
		String[] doc_content = {doc1_content, doc2_content, doc3_content, doc4_content, doc5_content};

		String cids = "";
		ArrayList<String> cidsArray = new ArrayList<String>();
		

		// Antwort am Anfang senden, damit Browser nicht warten muss
		response.setContentType("text/html");
		PrintWriter pw = new PrintWriter (response.getOutputStream());
		

		pw.println("");
		pw.println("<head><title>DONT'T PANIC</title></head>");
		pw.println("");
		pw.println("<body>");
		pw.println("");
		pw.println("Hallo!<br />");
		pw.println("<br />");
		pw.println("<div style=\"text-align: center; font-size: 400%; color: red; text-decoration: blink;\">DON'T PANIC!</div>");
		pw.println("<br />");
		pw.println("Das ist ein Lucene-Test.<br />");
		pw.println("<br />");
		pw.println("<hr>");
		pw.println("<br />");
		pw.println("(Hier kommen irgendwelche Debug-Ausgaben hin)<br />");
		pw.println("<br />");
		pw.println("<br />");
		pw.println("<br />");
		
		
		// Location of Lucene index
		String luceneBasePath = "/home/stud/sst/bibsonomy/";
//		String luceneBasePath = "/home/bibsonomy/lucene/";
		
		String luceneBookmarksPath = luceneBasePath+"lucene_bookmarks/"; 
		String lucenePublicationsPath = luceneBasePath+"lucene_publications/"; 

		// Use default analyzer
		//SimpleAnalyzer analyzer = new SimpleAnalyzer();
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());
		analyzer.addAnalyzer(lField_group, new SimpleKeywordAnalyzer());
		
	
		pw.println("<br />");
		pw.println("<hr>");
		pw.println("Liste aller Elemente im Index:");
		pw.println("<br />");
		
		
		IndexReader reader = IndexReader.open(luceneBookmarksPath);
		
		
		pw.println("Number of docs in index: " + reader.numDocs() + ".");
		pw.println("<br />");
		pw.println("<br />");


try {
	ResultSet rset = dbconn.rawQuery("SELECT count(*) FROM bookmark JOIN urls u USING (book_url_hash)", connection, false);
	
	rset.next();
	
	pw.println("<span style=\"color:red;font-weight:bold;\">"+rset.getLong(1)+"</span> records total in database<br />");
} catch (SQLException e) {
	if (debug)
	{
	System.out.println("<br /><B>Fehler in tas_query - " + e.getMessage() + "</b><br />");
	}
}      

		
		
		

		pw.println("<br />");
		pw.println("<br />");
		pw.println("<hr>");
		
		String search_terms = "";
			
			
		search_terms = req.getParameter("s");

		if (search_terms == null)
		{
			search_terms = "";
		}
				
			
        if (search_terms.isEmpty()) {
        	search_terms = "windows";
        }		
		
		// suchanfrage
		
		pw.println("Searchanfrage");

		long starttime = System.currentTimeMillis();
		long endtime = 0;
		
		// - suchanfrage

		String sql_test_offset = "0";		
		String sql_test_limit = "100";		
		String sql_test_grouptype = "1";		
		String sql_test_search = search_terms;		
		
		// SQL - old 

		long starttime2 = System.currentTimeMillis();
		long endtime2 = 0;

		pw.println("<br />");
		pw.println("Processing old query (without lucene)... search terms: "+sql_test_search);
		pw.println("<br />");

		String oldquery = " SELECT x.content_id, x.interhash, x.intrahash, x.title, x.description, x.date, "+
					" x.user_name, x.book_url, x.count, x.tag_name, x.group, x.group_name" +
					" FROM (SELECT bb.content_id, bb.book_url_hash AS interhash, bb.book_url_hash AS intrahash, title, description, bb.date, "+
					" bb.user_name, bb.book_url, bb.book_url_ctr AS count, t.tag_name, NULL AS `group`, NULL AS group_name   "+
					" FROM ( SELECT b.content_id, b.book_url_hash, b.book_description AS title, b.book_extended AS description, b.date,"+
					" b.user_name, u.book_url, u.book_url_ctr"+
					" FROM bookmark b, urls u, search_bookmark s"+
					" WHERE s.group = '"+sql_test_grouptype+"' "+
					" AND MATCH (s.content) AGAINST ('"+sql_test_search+"' IN BOOLEAN MODE)"+
					" AND u.book_url_hash = b.book_url_hash"+
					" AND s.content_id = b.content_id"+
		      		" ORDER BY s.date DESC"+
		      		//" LIMIT "+sql_test_limit+" OFFSET "+sql_test_offset+
		      		" ) AS bb"+
					" LEFT OUTER JOIN tas AS t ON t.content_id = bb.content_id"+
					" ORDER BY bb.date DESC, bb.content_id) as x";
	


		if (debug)
		{
			System.out.println(oldquery);
		}

		try {
		ResultSet rset = dbconn.rawQuery(oldquery, connection, false);
		long i = 0;

		while (rset.next()) {
//			System.out.println(rset.getString(1) + " : " + rset.getString(2));

			if (i<10)
//			if (1<10)
			{
				for (int j=1; j<6;j++){
//				for (int j=1; j<13;j++){
					pw.println(rset.getString(j) + " ~#~ ");
				}
	//    		pw.println("ID: " + rset.getLong("content_id") + " - group: " + rset.getString("group") + " - date: " + rset.getString("date") + " - user: " + rset.getString("user_name") + " - url: " + rset.getString("book_url")); 
	    		pw.println("<br />");
			}
			i++;

//log.debug(rset.)
		}
		pw.println("found <span style=\"color:red;font-weight:bold;\">"+i+"</span> records in database<br />");
	} catch (SQLException e) {
		if (debug)
		{
			System.out.println("<br /><B>Fehler in tas_query - " + e.getMessage() + "</b><br />");
		}
	}      
		
		
		
		pw.println("done");
		pw.println("<br />");
		endtime2 = System.currentTimeMillis();
		pw.println((endtime2-starttime2) + "ms");	
		pw.println("<br />");
		pw.println((endtime2-starttime2)/1000 + "s");	
		pw.println("<br />");
		pw.println("<br />");
		
		
		
		
		
		// Do a simple search
        //String[] search_fields = {title_field, author_field, content_field};
        //BooleanClause.Occur[] boolean_clause_occur = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
        //String search_terms = "windows";
        //Query query = MultiFieldQueryParser.parse(search_terms, search_fields, boolean_clause_occur, analyzer);


		long starttime3 = System.currentTimeMillis();
		long endtime3 = 0;

		pw.println("<br />");
		pw.println("Processing lucene query...");
		pw.println("<br />");

		
// grouptype == 1 setzen, um vergleichbar zu sein mit alter afrage
        
		QueryParser myParser = new QueryParser(lField_desc, analyzer);
        Query query;
		try {
   
			
		
			String querystring = lField_group+":1 AND (" + lField_desc + ":("+ search_terms +") " + lField_tas + ":("+ search_terms +") " + lField_ext + ":("+ search_terms +") " + lField_url + ":("+ search_terms +") )" ;
//			String querystring = lField_group+":" ;
			//String querystring = search_terms;
		  
			if (debug)
			{
				System.out.println("Lucene-Querystring [assembled]: " + querystring);
			}
	        query = myParser.parse(querystring);

	        if (debug)
			{
				System.out.println("Lucene-Querystring [analyzed]:  " + query.toString());
			}
	        


	        
	        IndexSearcher searcher = new IndexSearcher(luceneBookmarksPath);
	        Hits hits = searcher.search(query);

	        pw.println("Found <span style=\"color:purple;font-weight:bold;\">" + hits.length() + "</span> documents which match: <i>" + search_terms + "</i> in <b>"+ searcher.maxDoc() +"</b> documents");
			pw.println("<br />");
	        
			cids = "";
			
			for(int i = 0; i < hits.length(); i++){
	            Document doc = hits.doc(i);
	            int lucene_id = i;
	            String cid = doc.get(lField_contentid);
	            String group = doc.get(lField_group);
	            String date = doc.get(lField_date);
	            String user = doc.get(lField_user);
	            String desc = doc.get(lField_desc);
	            String ext = doc.get(lField_ext);
	            String url = doc.get(lField_url);
	            String tas = doc.get(lField_tas);
	            String type = doc.get(lField_type);
	    		
	    		
	            if (!cids.isEmpty()){
	            	cids += ",";
	            }
	            cids += doc.get(lField_contentid);
				cidsArray.add(doc.get(lField_contentid));
	            
				if (i<10)
				{
		    		pw.println("ID: " + cid + " - group: " + group + " - date: " + date + " - user: " + user+ " - url: " + url); 
		    				//" - desc: " + desc + " - ext: " + ext + " - url: " + url + " - tas: " + tas + " - type: " + type);
		    		pw.println("<br />");
				}
			
			}	 
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

		pw.println("done");
		pw.println("<br />");
		endtime3 = System.currentTimeMillis();
		pw.println((endtime3-starttime3) + "ms");	
		pw.println("<br />");
		pw.println((endtime3-starttime3)/1000 + "s");	
		pw.println("<br />");
		pw.println("<br />");
		
		
		pw.println("<br />");
		pw.println("Processing new sql query (with lucene)...");
		pw.println("<br />");
		pw.println("done");
		pw.println("<br />");
        

		if (!cids.isEmpty())
		{
			
			try {
			// create temp. table
			Integer ErrCodeTempC = dbconn.rawUpdate("CREATE TEMPORARY TABLE IF NOT EXISTS tempcids (cid bigint, PRIMARY KEY (cid)) ENGINE=MEMORY;", connection);

			// delete all content in temp. table
			Integer ErrCodeTempD = dbconn.rawUpdate("TRUNCATE TABLE tempcids; ", connection);
			
			
			// store content ids in temp. table
			for  (String e: cidsArray)
			{
				Integer ErrCodeTempS = dbconn.rawUpdate("INSERT INTO tempcids (cid) VALUE ("+e+");", connection);
			}			
			
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			String newquery = " SELECT bb.content_id, bb.book_url_hash AS interhash, bb.book_url_hash AS intrahash, title, description, bb.date, "+
						" bb.user_name, bb.book_url, bb.book_url_ctr AS count, t.tag_name, NULL AS `group`, NULL AS group_name   "+
						" FROM (SELECT b.content_id, b.book_url_hash, b.book_description AS title, b.book_extended AS description, b.date, "+
						" b.user_name, u.book_url, u.book_url_ctr "+
	
	//					" FROM bookmark b, urls u, search_bookmark s "+
						" FROM bookmark b, urls u "+
						" , tempcids tids "+   // new for temp-table
	//TODO				" WHERE s.group = '"+sql_test_grouptype+"' "+
	//					" AND MATCH (s.content) AGAINST ('"+sql_test_search+"' IN BOOLEAN MODE) "+
						" WHERE "+
    // new for temp-table	" b.content_id IN ("+cids+") "+
						" tids.cid = b.content_id "+
						" AND "+
						" u.book_url_hash = b.book_url_hash "+
	//					" AND s.content_id = b.content_id "+
	//TODO	      		" ORDER BY s.date DESC "+
	//		      		" LIMIT "+sql_test_limit+" OFFSET "+sql_test_offset+") AS bb "+
	
						" ) AS bb "+
						" LEFT OUTER JOIN tas AS t ON t.content_id = bb.content_id "+
						" ORDER BY bb.date DESC, bb.content_id ";

	
			if (debug)
			{
				System.out.println(newquery);
			}
			
	
			try {
				ResultSet rset = dbconn.rawQuery(newquery, connection, false);
				long i = 0;
		
				if (rset != null)
				{
					while (rset.next()) {
			//			System.out.println(rset.getString(1) + " : " + rset.getString(2));
			
						
			//    		pw.println("ID: " + rset.getLong("content_id") + " - group: " + rset.getString("group") + " - date: " + rset.getString("date") + " - user: " + rset.getString("user_name") + " - url: " + rset.getString("book_url")); 

						
						
						if (i<10)
//						if (1<10)
						{

							for (int j=1; j<6;j++){
//							for (int j=1; j<13;j++){
								pw.println(rset.getString(j) + " ~#~ ");
							}
							
							
							pw.println("<br />");
						}
						i++;
						
						
			//log.debug(rset.)
					}
				}
	
				pw.println("found <span style=\"color:red;font-weight:bold;\">"+i+"</span> records in database<br />");
	
			} catch (SQLException e) {
				if (debug)
				{
					System.out.println("Fehler in tas_query - " + e.getMessage() );
				}
			}      
		}
		else {
			// cids is empty
			pw.println("lucene has nound no entries in index<br />");
		}
		
		
		pw.println("done");
		pw.println("<br />");
		endtime3 = System.currentTimeMillis();
		pw.println((endtime3-starttime3) + "ms");	
		pw.println("<br />");
		pw.println((endtime3-starttime3)/1000 + "s");	
		pw.println("<br />");
		pw.println("<br />");

    	
		
		
		// /- suchanfrage

		pw.println("<br />");
		pw.println("<br />");
		endtime = System.currentTimeMillis();
		pw.println((endtime-starttime) + "ms");	
		pw.println("<br />");
		pw.println((endtime-starttime)/1000 + "s");	

		// /suchanfrage
		
		
		pw.println("<br />");
		pw.println("<hr>");
		pw.println("</body>");
		pw.println("</html>");


//		pw.println("");
	    pw.close();

	    
	
	}

	public void doPost(HttpServletRequest req, HttpServletResponse response)
	throws ServletException, IOException {
		doGet (req, response);
	}

*/


}
