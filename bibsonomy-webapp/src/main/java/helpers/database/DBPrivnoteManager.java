package helpers.database;


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
		final DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				// hash is intrahash (simhash2)
				c.stmt = c.conn.prepareStatement("UPDATE bibtex SET privnote = ? WHERE user_name = ? AND simhash2 = ?");
				c.stmt.setString(1, privnote);
				c.stmt.setString(2, username);
				c.stmt.setString(3, hash);
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (final SQLException e) {
			log.error("could not set the private not for the user " + username + " hash " + hash, e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
}
