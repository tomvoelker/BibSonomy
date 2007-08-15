package helpers;

import java.sql.*;

/*
 * fixes urls of dblp entries in bibtex table
 */
public class DBLPUrlFixer {

	/* give database name and password as environment variables DB and DB_PASS
	 * 
	 */
	private static final String DBLPURL = "http://dblp.uni-trier.de/";
	public static void main (String [] args){

		/* Establish all connections, result sets and statements 
		 * which we will need */
		Connection conn          = null;
		ResultSet rst            = null;
		PreparedStatement stmt_select = null;
		PreparedStatement stmt_update = null;


		try {
			String password = System.getenv("DB_PASS");
			String username = System.getenv("DB_USER");
			String database = System.getenv("DB");
			String hostname = System.getenv("DB_HOST");
			String port     = System.getenv("DB_PORT");
			String dburl = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			conn = DriverManager.getConnection (dburl, username, password);
			conn.setAutoCommit(false); // turn autocommit off
			System.out.println ("Database connection to " + hostname + " established");


			// statement to get rows
			stmt_select = conn.prepareStatement("SELECT content_id, url FROM bibtex WHERE user_name = 'dblp6'");
			stmt_update = conn.prepareStatement("UPDATE bibtex SET url = ? WHERE content_id = ?");

			rst = stmt_select.executeQuery();

			int ctr = 0;
			while (rst.next()) {

				String url = rst.getString("url");
				int contentId = rst.getInt("content_id");

				if (url != null) {
					/*
					 * FIX URL
					 */

					// remove ", "
					url.replaceAll(", ", "");

					if (url.startsWith("db/")) {
						url = DBLPURL + url;
					}

					stmt_update.setString(1, url);
					stmt_update.setInt(2, contentId);
					stmt_update.executeUpdate();
					ctr++;
				}
				if (ctr % 10000 == 0) {
					conn.commit();
					System.out.println(ctr);
				}
				
			} // iteration over result set

			conn.commit();

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
			if (stmt_select != null) {try {stmt_select.close();} catch (SQLException e) {}stmt_select = null;}
			if (stmt_update != null) {try {stmt_update.close();} catch (SQLException e) {}stmt_update = null;}
			if (conn != null) {try {conn.close();} catch (SQLException e) {}conn = null;}
		}
	}

}