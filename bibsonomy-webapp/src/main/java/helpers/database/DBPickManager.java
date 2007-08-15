package helpers.database;

import helpers.constants;

import java.sql.*;
import resources.Bibtex;


public class DBPickManager extends DBManager {
	
	/*
	 * SQL Statements
	 */
	private static final String SQL_SELECT_ID	 	= "SELECT b.content_id, b.group FROM bibtex b WHERE b.simhash" + Bibtex.INTRA_HASH + " = ? AND b.user_name = ?";
	private static final String SQL_TEST_DUPLICATE	= "SELECT content_id FROM collector WHERE user_name = ? AND content_id = ?";
	private static final String SQL_TEST_FRIEND     = "SELECT f_user_name FROM friends WHERE user_name = ? AND f_user_name = ?";
	private static final String SQL_TEST_GROUP      = "SELECT g.group FROM groups g WHERE g.user_name = ? AND g.group = ?";
	private static final String SQL_PICK_ID	        = "INSERT INTO collector (user_name, content_id) VALUES (?, ?)";
	private static final String SQL_UNPICK_HASH	    = "DELETE FROM collector WHERE content_id IN "
                                                         + "(SELECT content_id FROM bibtex WHERE simhash" + Bibtex.INTRA_HASH + " = ? AND user_name = ?)"
                                                         + " AND user_name = ?";
	private static final String SQL_UNPICK_ALL	    = "DELETE FROM collector WHERE user_name = ?";
	
	private static final String SQL_LOG_UNPICK_HASH = "INSERT INTO log_collector (user_name, content_id, add_date) " +
													  "  SELECT c.user_name, c.content_id, c.date FROM collector c, bibtex b" +
													  "    WHERE c.content_id = b.content_id " +
													  "     AND b.simhash" + Bibtex.INTRA_HASH + " = ?" +
													  "     AND b.user_name = ? AND c.user_name = ?";
													  
	private static final String SQL_LOG_UNPICK_ALL  = "INSERT INTO log_collector (user_name, content_id, add_date) " + 
													  "  SELECT user_name, content_id, date FROM collector WHERE user_name = ?";
			 
	
	
	/** 
	 * Puts the publication with the given hash (from owner) into user's collector table.
	 *  
	 * @param hash hash of the publication
	 * @param owner owner of this particular publication
	 * @param user user for which we want to pick the entry
	 */
	public static void pickEntryForUser (String hash, String owner, String user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				
				try {
					c.conn.setAutoCommit(false);
					
					// test, if hash exists and get content id of hash
					c.stmt = c.conn.prepareStatement(SQL_SELECT_ID);
					c.stmt.setString(1, hash);
					c.stmt.setString(2, owner);
					c.rst = c.stmt.executeQuery();
					if(c.rst.next()) {
						// hash found in database, remember content_id and group 
						int content_id = c.rst.getInt("content_id");
						int groupid    = c.rst.getInt("group"); // group id of content        	      	  	    
						
						// test for duplicate in collector table 
						c.stmt = c.conn.prepareStatement(SQL_TEST_DUPLICATE);
						c.stmt.setString(1, user);
						c.stmt.setInt(2, content_id);	        	      	  
						c.rst = c.stmt.executeQuery();
						
						if(!c.rst.next()) {
							// user did not already pick this item  
							
							boolean pickOk = false;
							// check, if user may pick this item (group access rights)
							if (groupid == constants.SQL_CONST_GROUP_PUBLIC || user.equals(owner)) { 
								// content is public or currUser is owner		        	      	  	 	        	      	    
								pickOk = true;
							} else { 
								// check table groups for user+groupid
								if (groupid == constants.SQL_CONST_GROUP_FRIENDS) {
									// check if currUser is friend of owner
									c.stmt = c.conn.prepareStatement(SQL_TEST_FRIEND);
									c.stmt.setString(1, owner);
									c.stmt.setString(2, user);
									c.rst = c.stmt.executeQuery();
									
									pickOk = c.rst.next(); // currUser is friend of owner -> insert
								} else {
									// test whether user is in group belonging to item
									c.stmt = c.conn.prepareStatement(SQL_TEST_GROUP);
									c.stmt.setString(1, user);
									c.stmt.setInt(2, groupid);
									c.rst = c.stmt.executeQuery();
									
									pickOk = c.rst.next(); // user is part of this group -> insert
								}
							} // checking of group access rights
							
							if (pickOk) {
								// insert content_id into collector table		        	      	   	
								c.stmt = c.conn.prepareStatement(SQL_PICK_ID);
								c.stmt.setString(1, user);
								c.stmt.setInt(2, content_id);
								c.stmt.executeUpdate();
							}
							
							
						} // checking, if already picked
					} // checking, if hash exists
					
					c.conn.commit();	
				} catch(SQLException e) {
					System.out.println("DBPM:" + e);
					e.printStackTrace();
					c.conn.rollback();     //rollback all queries
				}
			} // if (c.init())
		} catch (SQLException e) {
			System.out.println("DBPM: " + e);
			e.printStackTrace();
		} finally {
			c.close(); // close database connection
		}
		
	}
	
	/*
	 * removes the entry with hash from owner from user's collector table
	 */
	public static boolean unPickEntryForUser (String hash, String owner, String user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				try {
					c.conn.setAutoCommit(false);

					// log 
					c.stmt = c.conn.prepareStatement(SQL_LOG_UNPICK_HASH);
					c.stmt.setString(1, hash);
					c.stmt.setString(2, owner);
					c.stmt.setString(3, user);
					c.stmt.executeUpdate();
					
					// delete one entry from list
					c.stmt = c.conn.prepareStatement(SQL_UNPICK_HASH);
					c.stmt.setString(1, hash);
					c.stmt.setString(2, owner);
					c.stmt.setString(3, user);
					c.stmt.executeUpdate();
					
					c.conn.commit();
				} catch (SQLException e) {
					System.out.println("DBPM:" + e);
					e.printStackTrace();
					c.conn.rollback();
				}
			}
		} catch (SQLException e) {
			System.out.println("DBPM: " + e);
			e.printStackTrace();
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
	
	
	/**
	 * Removes all entries from user's collector table.
	 * 
	 * @param user user whose collector entries shall be removed
	 * @return <code>true</code> if everything could be logged and removed
	 */
	public static boolean unPickAll (String user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				try {
					c.conn.setAutoCommit(false);
					
					// log
					c.stmt = c.conn.prepareStatement(SQL_LOG_UNPICK_ALL);
					c.stmt.setString(1, user);
					c.stmt.executeUpdate();				
					
					// delete one entry from list
					c.stmt = c.conn.prepareStatement(SQL_UNPICK_ALL);
					c.stmt.setString(1, user);
					c.stmt.executeUpdate();
					
					c.conn.commit();
					return true;
				} catch (SQLException e) {
					System.out.println("DBPM:" + e);
					e.printStackTrace();
					c.conn.rollback();
				}
			}
		} catch (SQLException e) {
			System.out.println("DBPM: " + e);
			e.printStackTrace();
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
	
	/**
	 * Gets the number of picked publication entries of the user.
	 * @param user the user name for which the method gets the count
	 * @return the number of publication entries the user has picked for basket
	 */
	public static int getPickCount (String user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				c.stmt = c.conn.prepareStatement("SELECT count(user_name) AS count FROM collector WHERE user_name = ?");
				c.stmt.setString(1, user);
				c.rst = c.stmt.executeQuery();
				if (c.rst.next()) {
					return c.rst.getInt("count");
				}
			}
		} catch (SQLException e) {
			System.out.println("DBPM: " + e);
			e.printStackTrace();
		} finally {
			c.close(); // close database connection
		}
		return 0;
	}
}
