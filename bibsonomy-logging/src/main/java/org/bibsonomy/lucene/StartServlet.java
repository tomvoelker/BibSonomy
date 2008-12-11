	/**
 * 
 */
package org.bibsonomy.lucene;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


/**
 * @author sst
 *
 */



public class StartServlet extends HttpServlet {


	
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

/*		String id_field = "id";
		String bibid_field = "content_id";
		String date_field = "date";
		String group_field = "group";
		String content_field = "content";
		String username_field = "user_name";
*/
		String doc1_content = "uno";
		String doc2_content = "dos";
		String doc3_content = "uno dos tres";
		String doc4_content = "quatro";
		String doc5_content = "uno dos tres quatro cinco";
		String[] doc_content = {doc1_content, doc2_content, doc3_content, doc4_content, doc5_content};

		

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
		
		
		int dompath_length = 0;
		
		// Location of Lucene index
		String directory = "/home/stud/sst/bibsonomy/lucene_index/";

/*		try {
			
			// Store the index in memory:
		    //Directory directory = new RAMDirectory();
			
			// Use default analyzer
			StandardAnalyzer analyzer = new StandardAnalyzer();
			
			// add few sample documents
			IndexWriter writer = new IndexWriter(directory, analyzer,true);
				
				
			for(int i = 0; i < doc_content.length; i++){
                 Document doc = new Document();
                 
                 / * doc.add(new Field("fieldname", text, Field.Store.YES, Field.Index.TOKENIZED));
                  * 
                  * static Field.Store 	COMPRESS
                  *    Store the original field value in the index in a compressed form.
                  *     
                  * static Field.Store 	NO 
                  *    Do not store the field value in the index.  
                  *
                  * static Field.Store 	YES
                  *    Store the original field value in the index.  
                  * 
                  *  
                  * static Field.Index 	NO
          		  *     Do not index the field value.
          		  *     
				  * static Field.Index 	NO_NORMS
                  *     Index the field's value without an Analyzer, and disable the storing of norms.
                  *     
                  * static Field.Index 	TOKENIZED
                  *     Index the field's value so it can be searched.
                  *     
                  * static Field.Index 	UN_TOKENIZED
                  *     Index the field's value without using an Analyzer, so it can be searched. 
                  * /
                  
                 
                 doc.add(new Field(id_field, "ID_" + i, Field.Store.YES, Field.Index.NO));
                 doc.add(new Field(bibid_field, "Content_ID_" + i, Field.Store.YES, Field.Index.NO));
                 doc.add(new Field(date_field, "Date_" + i, Field.Store.YES, Field.Index.NO));
                 doc.add(new Field(group_field, "Group_" + i, Field.Store.YES, Field.Index.NO));
                 doc.add(new Field(username_field, "Username_" + i, Field.Store.YES, Field.Index.NO));
                 doc.add(new Field(content_field, doc_content[i], Field.Store.NO, Field.Index.TOKENIZED));
                 
         		 writer.addDocument(doc);
			}
	        writer.close();

			pw.flush();

			
	        // Do a simple search
	        //String[] search_fields = {title_field, author_field, content_field};
	        //BooleanClause.Occur[] boolean_clause_occur = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
	        String search_terms = "dos";
	        //Query query = MultiFieldQueryParser.parse(search_terms, search_fields, boolean_clause_occur, analyzer);

	        QueryParser parser = new QueryParser(content_field, analyzer);
	        Query query = parser.parse(search_terms);
	        
	        
	        IndexSearcher searcher = new IndexSearcher(directory);
	        Hits hits = searcher.search(query);

	        pw.println("Found <b>" + hits.length() + "</b> documents that matched: <i>" + search_terms + "</i> in <b>"+ searcher.maxDoc() +"</b> documents");
			pw.println("<br />");
	        
	        for(int i = 0; i < hits.length(); i++){
                 Document doc = hits.doc(i);
                 int lucene_id = hits.id(i);
                 String my_id = doc.get(id_field);
                 String bibid = doc.get(bibid_field);
                 String content = doc.get(content_field);
                 String group = doc.get(group_field);
                 String username = doc.get(username_field);
                 String date = doc.get(date_field);

                 pw.println("ID: " + my_id + " - BibID: " + bibid + " - Lucene ID: " + lucene_id + " - Group: " + group + " - username: " + username + " - Date: " + date);
         		 pw.println("<br />");

	        }
		} catch (Exception e){
			System.err.println("LuceneDemo caught: " + e.toString());
		}

*/			
		
		pw.println("<br />");
		pw.println("<hr>");
		pw.println("Liste aller Elemente im Index:");
		pw.println("<br />");
		
		
		IndexReader reader = IndexReader.open(directory);
		
		
		pw.println("Number of docs in index: " + reader.numDocs() + ".");
		pw.println("<br />");
		pw.println("<br />");

		pw.println("List of all docs in index: ");
		pw.println("<br />");

		for(int i = 0; i < reader.numDocs(); i++){
            Document doc = reader.document(i);
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
    		
    		
    		pw.println("ID: " + cid + " - group: " + group + " - date: " + date + " - user: " + user + 
    				" - desc: " + desc + " - ext: " + ext + " - url: " + url + " - tas: " + tas + " - type: " + type);
    		 pw.println("<br />");
    		if (i > 100) {
    			break;
    		}

       }

		

		pw.println("<br />");
		pw.println("<br />");
		

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




}
