package helpers.database;


import helpers.constants;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class DBPrivnoteManager extends DBManager {

	private final static Log log = LogFactory.getLog(DBPrivnoteManager.class);

	
	/**
	 * Sets the private note of a users bibtex entry.
	 * 
	 * @param bean used to get the note, the hash and the user name
	 * @return <code>true</code> if exactly one database row got updated
	 */
	public static boolean setPrivnoteForUser (final String privnote, final String username, final String hash) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("UPDATE bibtex SET privnote = ? WHERE user_name = ? AND simhash" + constants.INTRA_HASH + " = ?");
				c.stmt.setString(1, privnote);
				c.stmt.setString(2, username);
				c.stmt.setString(3, hash);
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			log.fatal("could not set the private not for the user " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
}
