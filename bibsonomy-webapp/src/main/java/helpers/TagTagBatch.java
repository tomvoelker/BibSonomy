package helpers;


import java.sql.*;
import java.util.*;

public class TagTagBatch{ 

	/* database connection configuration */
	private static String DATABASE      = "bibsonomy";
	private static String DBHOST        = "gromit";
	private static String DBUSER        = "bibsonomy";
	private static String DBPASS        = "12_bib_kde";
	private static String BATCHTABLE    = "tagtag_batch";
	private static String CONNECTSTRING = "jdbc:mysql://"+DBHOST+":6033/"+DATABASE+"?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
	
	private static final int MAX_BATCH_ROWS = 1000;
	/* query statements */
	private static final String SET_ACTIVE_INC = "UPDATE " + BATCHTABLE + " SET isactive = 1 WHERE toinc = 1 LIMIT " + MAX_BATCH_ROWS;
	private static final String SET_ACTIVE_DEC = "UPDATE " + BATCHTABLE + " SET isactive = 1 WHERE toinc = 0 LIMIT " + MAX_BATCH_ROWS;
	private static final String GET_INC = "SELECT content_id, tags FROM " + BATCHTABLE + " WHERE toinc = 1 AND isactive = 1";
	private static final String GET_DEC = "SELECT content_id, tags FROM " + BATCHTABLE + " WHERE toinc = 0 AND isactive = 1";
	private static final String DEL_INC = "DELETE FROM " + BATCHTABLE + " WHERE toinc = 1 AND isactive = 1 AND content_id = ?";
	private static final String DEL_DEC = "DELETE FROM " + BATCHTABLE + " WHERE toinc = 0 AND isactive = 1 AND content_id = ?";

	private static final String INSERT_TAGTAG 	   = "INSERT INTO tagtag (t1, t2, ctr) VALUES (?,?,?)";
	private static final String UPDATE_TAGTAG_INC  = "UPDATE tagtag SET ctr=ctr+1 WHERE t1=? AND t2=?";
	private static final String UPDATE_TAGTAG_DEC  = "UPDATE tagtag SET ctr=ctr-1 WHERE t1=? AND t2=?";
	
	private static Random generator = new Random();
	/*
	 * Main method: does batch job processing for inserting/deleting tagtag combinations
	 */
	public static void main (String[] args) {
		
		Connection conn = null;
		
		try {
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (CONNECTSTRING, DBUSER, DBPASS);
        } catch (SQLException e) {
            System.err.println ("Cannot connect to database server: " + e);
        } catch (ClassNotFoundException e) {
        	System.err.println("Did not find com.mysql.jdbc.Driver: " + e);
        } catch (IllegalAccessException e) {
        	System.err.println(e);
        } catch (InstantiationException e) {
        	System.err.println(e);
        }

        try {
        	batch (conn, true);  // increment 
        	batch (conn, false); // decrement
        } catch (SQLException e) {
        	System.err.println ("Error inserting tagtag combinations into database: " + e);
        } 
        try {
        	conn.close();
        } catch (SQLException e) {
        	System.err.println("Could not close Connection: " + e);
        }
	}
	
	private static void batch (Connection conn, boolean increment) throws SQLException {
		conn.setAutoCommit(false);
		ResultSet rows;
		PreparedStatement set_active, get, del, update_tagtag;
		PreparedStatement insert_tagtag = null;
		if (increment) {
			set_active = conn.prepareStatement(SET_ACTIVE_INC);
			get        = conn.prepareStatement(GET_INC);
			del        = conn.prepareStatement(DEL_INC);
			update_tagtag = conn.prepareStatement(UPDATE_TAGTAG_INC);
			insert_tagtag = conn.prepareStatement(INSERT_TAGTAG);
		} else {
			set_active = conn.prepareStatement(SET_ACTIVE_DEC);
			get        = conn.prepareStatement(GET_DEC);
			del        = conn.prepareStatement(DEL_DEC);
			update_tagtag = conn.prepareStatement(UPDATE_TAGTAG_DEC);		
		}
		if (set_active.executeUpdate() != 0) {
			/* we got some rows for inc/dec */
			conn.commit(); // mark them as active immediately, so that no one deletes the jobs
			/* get the rows we have to inc/dec */
			rows = get.executeQuery();
			conn.commit();
			while (rows.next()) {
				/* we got a content_id and some tags to inc/dec ... */
				int content_id = rows.getInt("content_id");
				String tags    = rows.getString("tags");
				/* break string of tags into set of tags */  
				StringTokenizer tokenizer = new StringTokenizer(tags);
				Set tagset = new HashSet();
				while (tokenizer.hasMoreTokens()) {
					tagset.add(tokenizer.nextElement());
				}
				/* loop over all tagtag combinations and inc/dec them*/
				boolean done = false;
				int wait = 1;
				while (!done && wait < 60) {
					try {
						Iterator it1 = tagset.iterator();
						while (it1.hasNext()) {
							String tag1 = (String)it1.next();
							/* update tagtag table */
							Iterator it2 = tagset.iterator();
							while (it2.hasNext()) {
								String tag2 = (String)it2.next();
								/* different tags */
								if (! tag1.equals(tag2)) {
									update_tagtag.setString(1, tag1);
									update_tagtag.setString(2, tag2);
									if (update_tagtag.executeUpdate() == 0 && increment) {
										/* nothing updated, so insert new tagtag combination into table */
										 insert_tagtag.setString(1, tag1);
										 insert_tagtag.setString(2, tag2);
										 insert_tagtag.setInt(3, 1);
										 insert_tagtag.executeUpdate(); 
										//System.out.print(".");
									} else {
										//System.out.print("+");
									}
								}
							}
						}
						/* delete this batch job */
						del.setInt(1, content_id);
						if (del.executeUpdate() == 1) {
							conn.commit();
						} else {
							/* something really strange happened: we could not delete the batch job,
							 * so we should rollback everything we did
							 */
							conn.rollback();
							done = true;
						}
					} catch (SQLException e) {
						wait = wait*2;
						try {
							System.out.print("w");
							Thread.sleep(generator.nextInt(wait));
						} catch (InterruptedException ee) {
							System.out.print("i");
						}
					}
				}
			} /* next row */
		}
	}
}
