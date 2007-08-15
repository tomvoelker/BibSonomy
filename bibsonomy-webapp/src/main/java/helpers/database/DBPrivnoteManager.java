package helpers.database;


import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import beans.PrivnoteBean;
import resources.Bibtex;


public class DBPrivnoteManager extends DBManager {

	/**
	 * Sets the private note of a users bibtex entry.
	 * 
	 * @param bean used to get the note, the hash and the user name
	 * @return <code>true</code> if exactly one database row got updated
	 */
	public static boolean setPrivnoteForUser (PrivnoteBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("UPDATE bibtex SET privnote = ? WHERE user_name = ? AND simhash" + Bibtex.INTRA_HASH + " = ?");
				c.stmt.setString(1, bean.getPrivnote());
				c.stmt.setString(2, bean.getUsername());
				c.stmt.setString(3, bean.getHash());
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			/*
			 * TODO: first attempt to do logging when exceptions are thrown - code "stolen" from Jens'
			 * Database backend classes
			 */
			final Log log = LogFactory.getLog(DBPrivnoteManager.class);
			log.fatal("could not set the private not for the user " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
}
