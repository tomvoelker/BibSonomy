package helpers;

import java.sql.*;
import resources.Bibtex;
import java.util.*;
import java.util.Date;



/**
 * Inserts the new simhashes into the database.
 * 
 * Was used, when we introduced the similarity hashes. 
 *
 * @author rja
 */
public class HashCalculator {
	
	/* give database name and password as environment variables DB and DB_PASS
	 * 
	 */
	public static void main (String [] args){

		/* Establish all connections, result sets and statements 
		 * which we will need */
		Connection conn          = null;
		ResultSet rst            = null;
		PreparedStatement stmtP  = null;
		PreparedStatement stmtPU = null;
		PreparedStatement stmtPI = null;
		PreparedStatement stmtPS = null;
		PreparedStatement stmtPC = null;
	
		
		try {
			String password = System.getenv("DB_PASS");
			String username = System.getenv("DB_USER");
			String database = System.getenv("DB");
			String hostname = System.getenv("DB_HOST");
			String port     = System.getenv("DB_PORT");
			String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			conn = DriverManager.getConnection (url, username, password);
			conn.setAutoCommit(false); // turn autocommit off
			System.out.println ("Database connection to " + hostname + " established");
			
			
			// statement to get rows
			stmtP  = conn.prepareStatement("SELECT title, year, author, editor, journal, booktitle, entrytype, number, volume, content_id FROM bibtex WHERE user_name != 'dblp' LIMIT ? OFFSET ? FOR UPDATE");
			//stmtPU = conn.prepareStatement("UPDATE bibtex SET simhash0 = ?, simhash1 = ?, simhash2 = ?, simhash3 = ? WHERE content_id = ?");
			stmtPU = conn.prepareStatement("UPDATE bibtex SET simhash2 = ? WHERE content_id = ?");
			stmtPI = conn.prepareStatement("INSERT INTO bibhash (hash, type) VALUES (?,?)");
			stmtPS = conn.prepareStatement("UPDATE bibhash SET ctr = ctr + 1 WHERE hash = ? AND type = ?");
			//stmtPC = conn.prepareStatement("DELETE FROM bibhash"); // clear bibhash table
			//stmtPC.executeUpdate();
			
			System.out.println("HashCalculator: starting DB access " + new Date());
			
			HashSet<String> h2 = new HashSet<String>();
			
			final int limit = 10000;  // how many rows at a time?
			int round = 0;           // which round?
			boolean moreRows = true; // false, if we updated all rows
			while (moreRows) {
				// get rows
				stmtP.setInt(1, limit);
				stmtP.setInt(2, limit * round);
				rst = stmtP.executeQuery();
				
				
				moreRows = false;
				// get bibtex entries from database
				while (rst.next()) {
					moreRows = true; // get more rows in next while (moreRows) loop
					// fill bibtex object
					Bibtex bib = fillBibtex(rst);
					// update hashes in bibtex table
					stmtPU.setString(1, bib.getSimHash(Bibtex.SIM_HASH_2));
					//stmtPU.setString(3, bib.getSimHash(Bibtex.SIM_HASH_2));
					//stmtPU.setString(4, bib.getSimHash(Bibtex.SIM_HASH_3));
					stmtPU.setInt(2, bib.getContentID());
					stmtPU.executeUpdate();
					
					if (h2.contains(bib.getSimHash(Bibtex.SIM_HASH_2))) {
						stmtPS.setString(1, bib.getSimHash(Bibtex.SIM_HASH_2));	stmtPS.setInt(2, Bibtex.SIM_HASH_2); stmtPS.executeUpdate();
					} else {
						stmtPI.setString(1, bib.getSimHash(Bibtex.SIM_HASH_2)); stmtPI.setInt(2, Bibtex.SIM_HASH_2); stmtPI.executeUpdate();
						h2.add(bib.getSimHash(Bibtex.SIM_HASH_2));
					}

					/* increment similarity hash counters */
					 
/*					stmtPS.setString(1, bib.getSimHash(Bibtex.SIM_HASH_2)); stmtPS.setInt(2, Bibtex.SIM_HASH_2);
					if (stmtPS.executeUpdate() == 0) { stmtPI.setString(1, bib.getSimHash(Bibtex.SIM_HASH_2)); stmtPI.setInt(2, Bibtex.SIM_HASH_2); stmtPI.executeUpdate(); }
					stmtPS.setString(1, bib.getSimHash(Bibtex.SIM_HASH_3));	stmtPS.setInt(2, Bibtex.SIM_HASH_3); 
					if (stmtPS.executeUpdate() == 0) { stmtPI.setString(1, bib.getSimHash(Bibtex.SIM_HASH_3)); stmtPI.setInt(2, Bibtex.SIM_HASH_3); stmtPI.executeUpdate(); }
					*/
				} // iteration over result set
				conn.commit();
				round ++;
				System.out.println(round + " " + new Date());
				
			}
		
		} catch (SQLException e) {
			System.out.println (e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println (e);
			e.printStackTrace();			
		} catch (InstantiationException e) {
			System.out.println (e);
			e.printStackTrace();					
		} catch (ClassNotFoundException e) {
			System.out.println (e);
			e.printStackTrace();			
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst != null)  {try {rst.close();} catch (SQLException e) {} rst = null;}
			if (stmtP != null) {try {stmtP.close();} catch (SQLException e) {}stmtP = null;}
			if (stmtPU != null) {try {stmtPU.close();} catch (SQLException e) {}stmtPU = null;}
			if (stmtPI != null) {try {stmtPI.close();} catch (SQLException e) {}stmtPI = null;}
			if (stmtPS != null) {try {stmtPS.close();} catch (SQLException e) {}stmtPS = null;}
			if (stmtPC != null) {try {stmtPC.close();} catch (SQLException e) {}stmtPC = null;}
			if (conn != null) {try {conn.close();} catch (SQLException e) {}conn = null;}
		}
	}
	
	// fills only the fields, which are really used for hash calculation! add fields, which are needed for new hashes
	private static Bibtex fillBibtex(ResultSet rst) throws SQLException {
		Bibtex bib = new Bibtex();
		 
		bib.setContentID(rst.getInt("content_id"));

		//bib.setAddress(rst.getString("address"));
		//bib.setAnnote(rst.getString("annote"));
		bib.setAuthor(rst.getString("author"));
		bib.setBooktitle(rst.getString("bookTitle"));
		//bib.setChapter(rst.getString("chapter"));
		//bib.setCrossref(rst.getString("crossref"));
		//bib.setEdition(rst.getString("edition"));
		bib.setEditor(rst.getString("editor"));
		//bib.setHowpublished(rst.getString("howpublished"));
		//bib.setInstitution(rst.getString("institution"));
		bib.setJournal(rst.getString("journal"));
		//bib.setKey(rst.getString("bKey"));
		//bib.setMonth(rst.getString("month"));
		//bib.setNote(rst.getString("note"));
		bib.setNumber(rst.getString("number"));
		//bib.setOrganization(rst.getString("organization"));
		//bib.setPages(rst.getString("pages"));
		//bib.setPublisher(rst.getString("publisher"));
		//bib.setSchool(rst.getString("school"));
		//bib.setSeries(rst.getString("series"));
		bib.setTitle(rst.getString("title"));
		//bib.setType(rst.getString("type"));
		bib.setVolume(rst.getString("volume"));
		bib.setYear(rst.getString("year"));
		//bib.setUrl(rst.getString("url"));
		//bib.setDay(rst.getString("day"));
		//bib.setBibtexAbstract(rst.getString("bibtexAbstract"));
		
		//bib.setMisc(rst.getString("misc"));
		//bib.setBibtexKey(rst.getString("bibtexKey"));		                   				        	    
		//bib.setDescription(rst.getString("description"));
		bib.setEntrytype(rst.getString("entrytype"));
		return bib;
	}	
}