package helpers.database;

import helpers.constants;

import java.sql.*;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;


/** Allows to pick or unpick publication posts for the basket.
 * @author rja
 *
 */
public class DBPickManager extends DBManager {
	
	/**
	 * Logger 
	 */
	private static final Log log = LogFactory.getLog(DBPickManager.class);
	
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
	 * TODO: this method has been hacked to allow several hashes to be picked at once.
	 * This is possible by giving a loooong string full of hashes and user names as 
	 * hash and giving a null owner.
	 * 
	 *  
	 * @param oneOrMoreHashes hash of the publication
	 * @param ownerInCaseOfOneHash owner of this particular publication
	 * @param user user for which we want to pick the entry
	 */
	public static void pickEntryForUser (final String oneOrMoreHashes, final String ownerInCaseOfOneHash, final String user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				
				try {
					c.conn.setAutoCommit(false);

					/*
					 * hack to allow several hashes to be picked at once
					 */
					HashMap<String, String> map = extractHashes(oneOrMoreHashes, ownerInCaseOfOneHash);
					
					pickHashes(user, c, map);
					
					c.conn.commit();	
				} catch(SQLException e) {
					log.fatal("could not pick post " + oneOrMoreHashes + "(" + ownerInCaseOfOneHash + ") for user " + user);
					c.conn.rollback();     //rollback all queries
				}
			} // if (c.init())
		} catch (SQLException e) {
			log.fatal("could not pick post " + oneOrMoreHashes + "(" + ownerInCaseOfOneHash + ") for user " + user);
		} finally {
			c.close(); // close database connection
		}
		
	}

	private static HashMap<String, String> extractHashes(final String oneOrMoreHashes, final String ownerInCaseOfOneHash) {
		HashMap<String,String> map = new HashMap<String,String>();
		if (oneOrMoreHashes.length() > 33 && ownerInCaseOfOneHash == null) {
			String[] hashesAndUsers = oneOrMoreHashes.split(" ");
			for (String hashAndUser: hashesAndUsers) {
				String[] singledOut = hashAndUser.split("/");
				map.put(singledOut[0].substring(1, singledOut[0].length()), singledOut[1]);
			}
		} else {
			map.put(oneOrMoreHashes, ownerInCaseOfOneHash);
		}
		return map;
	}

	private static void pickHashes(final String currUser, DBContext context, HashMap<String, String> hashes) throws SQLException {
		for (final String hash:hashes.keySet()) {
			final String owner = hashes.get(hash); 

			// test, if hash exists and get content id of hash
			context.stmt = context.conn.prepareStatement(SQL_SELECT_ID);
			context.stmt.setString(1, hash);
			context.stmt.setString(2, owner);
			context.rst = context.stmt.executeQuery();
			if(context.rst.next()) {
				// hash found in database, remember content_id and group 
				int content_id = context.rst.getInt("content_id");
				int groupid    = context.rst.getInt("group"); // group id of content        	      	  	    

				// test for duplicate in collector table 
				context.stmt = context.conn.prepareStatement(SQL_TEST_DUPLICATE);
				context.stmt.setString(1, currUser);
				context.stmt.setInt(2, content_id);	        	      	  
				context.rst = context.stmt.executeQuery();

				if(!context.rst.next()) {
					// user did not already pick this item  

					boolean pickOk = false;
					// check, if user may pick this item (group access rights)
					if (groupid == constants.SQL_CONST_GROUP_PUBLIC || currUser.equals(owner)) { 
						// content is public or currUser is owner		        	      	  	 	        	      	    
						pickOk = true;
					} else { 
						// check table groups for user+groupid
						if (groupid == constants.SQL_CONST_GROUP_FRIENDS) {
							// check if currUser is friend of owner
							context.stmt = context.conn.prepareStatement(SQL_TEST_FRIEND);
							context.stmt.setString(1, owner);
							context.stmt.setString(2, currUser);
							context.rst = context.stmt.executeQuery();

							pickOk = context.rst.next(); // currUser is friend of owner -> insert
						} else {
							// test whether user is in group belonging to item
							context.stmt = context.conn.prepareStatement(SQL_TEST_GROUP);
							context.stmt.setString(1, currUser);
							context.stmt.setInt(2, groupid);
							context.rst = context.stmt.executeQuery();

							pickOk = context.rst.next(); // user is part of this group -> insert
						}
					} // checking of group access rights

					if (pickOk) {
						// insert content_id into collector table		        	      	   	
						context.stmt = context.conn.prepareStatement(SQL_PICK_ID);
						context.stmt.setString(1, currUser);
						context.stmt.setInt(2, content_id);
						context.stmt.executeUpdate();
					}


				} // checking, if already picked
			} // checking, if hash exists
		}
	}
	
	/**
	 * removes the entry with hash from owner from user's collector table
	 * 
 	 * TODO: this method has been hacked to allow several hashes to be unpicked at once.
	 * This is possible by giving a loooong string full of hashes and user names as 
	 * hash and giving a null owner.
	 */
	public static boolean unPickEntryForUser (String oneOrMoreHashes, String ownerInCaseOfOneHash, String user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				try {
					c.conn.setAutoCommit(false);

					HashMap<String, String> map = extractHashes(oneOrMoreHashes, ownerInCaseOfOneHash);
					for (String hash:map.keySet()) {
						String owner = map.get(hash);
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
					}
					c.conn.commit();
				} catch (SQLException e) {
					log.fatal("could not unpick post " + oneOrMoreHashes + "(" + ownerInCaseOfOneHash + ") for user " + user);
					c.conn.rollback();
				}
			}
		} catch (SQLException e) {
			log.fatal("could not unpick post " + oneOrMoreHashes + "(" + ownerInCaseOfOneHash + ") for user " + user);
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
			log.fatal("could not unpick all posts from user " + user);
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
			log.fatal("could not get pick count for user " + user);
		} finally {
			c.close(); // close database connection
		}
		return 0;
	}
}
