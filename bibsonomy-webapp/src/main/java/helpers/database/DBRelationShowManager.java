package helpers.database;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DBRelationShowManager extends DBManager {

	/** 
	 * Inserts the tuple (upper, user) into the table of shown concepts.
	 * @param upper the upper tag (supertag, concept) to show.
	 * @param user the username who wants to show the concept
	 * @throws SQLException
	 */
	public static void showConcept (String upper, String user) {
		DBContext c = new DBContext();
		try {
			c.init(); // initialize database
			c.stmt = c.conn.prepareStatement("INSERT IGNORE INTO picked_concepts (upper, user_name) VALUES(?,?)");
			c.stmt.setString(1, upper);
			c.stmt.setString(2, user);
			c.stmt.executeUpdate();
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationShowManager.class);
			log.fatal("could not show concept " + upper + " for user " + user + " : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
	}

	/**
	 * Removes the supertag from the table of shown concepts.
	 * @param upper
	 * @param user
	 * @throws SQLException
	 */
	public static void hideConcept (String upper, String user) {	
		DBContext c = new DBContext();
		try {
			c.init();
			c.stmt = c.conn.prepareStatement("DELETE FROM picked_concepts WHERE upper=? AND user_name=?");
			c.stmt.setString(1, upper);
			c.stmt.setString(2, user);
			c.stmt.executeUpdate();
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationShowManager.class);
			log.fatal("could not hide concept " + upper + " for user " + user + " : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
	}
	
	/**
	 * shows all concepts of the user.
	 * @param user the username who shows the concept
	 */
	public static void showAll (String user) {	
		DBContext c = new DBContext();
		try {
			c.init();
			c.stmt = c.conn.prepareStatement("INSERT IGNORE INTO picked_concepts (upper, user_name) SELECT upper, user_name FROM tagtagrelations WHERE user_name=? GROUP BY upper");
			c.stmt.setString(1, user);
			c.stmt.executeUpdate();
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationShowManager.class);
			log.fatal("could not show all concepts for user " + user + " : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
	}	
	
	/**
	 * Hides all concepts of the user.
	 * @param user
	 */
	public static void hideAll (String user) {	
		DBContext c = new DBContext();
		try {
			c.init();
			c.stmt = c.conn.prepareStatement("DELETE FROM picked_concepts WHERE user_name = ?");
			c.stmt.setString(1, user);
			c.stmt.executeUpdate();
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBRelationShowManager.class);
			log.fatal("could not hide all concepts for user " + user + " : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
	}	
	
}