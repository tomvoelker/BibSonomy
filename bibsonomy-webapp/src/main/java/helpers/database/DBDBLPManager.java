package helpers.database;


import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;


public class DBDBLPManager extends DBManager {

	private static final Log log = LogFactory.getLog(DBDBLPManager.class);
	
	public static int deleteDuplicates (String user) {
		DBContext c = new DBContext();
		DBBibtexManager bibman = new DBBibtexManager();
		int deletedDuplicatesCtr = 0;
		try {
			if (c.init()) {
				bibman.prepareStatements(c.conn);
				
				List<String> duplicateKeys = getDuplicateKeys(c, user);
				
				c.stmt = c.conn.prepareStatement("SELECT content_id, simhash" + Bibtex.INTRA_HASH + " FROM bibtex WHERE user_name = ? AND bibtexkey = ? ORDER BY DATE DESC");
				
				for (String key: duplicateKeys) {
					// get content ids of all entries with key as bibtexkey
					c.stmt.setString(1, user);
					c.stmt.setString(2, key);
					c.rst = c.stmt.executeQuery();
					
					boolean first = true;
					while (c.rst.next()) {
						if (!first) {
							int contentId = c.rst.getInt("content_id");
							log.fatal("deleting duplicate content_id " + contentId + " () for user " + user);
							// don't delete first post, because it is the newest; delete all other posts
							bibman.deleteBibtex(c.conn, contentId);
							deletedDuplicatesCtr++;
						}
						first = false;
					}
					// wait a moment for database to recover
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		} catch (SQLException e) {
			log.fatal("could not get layout settings for user: " + e);
		} finally {
			c.close(); // close database connection
			bibman.closeStatements();
		}
		return deletedDuplicatesCtr;
	}
	
	/** Gets all (BibTeX)keys of entries from user DBLP which are duplicates.
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	private static List<String> getDuplicateKeys (DBContext c, String user) throws SQLException {
		LinkedList<String> list = new LinkedList<String>();
		
		c.stmt = c.conn.prepareStatement("SELECT bibtexkey, count(content_id) AS ctr FROM bibtex WHERE user_name = ? GROUP BY bibtexkey HAVING ctr > 1");
		c.stmt.setString(1, user);
		c.rst = c.stmt.executeQuery();
		
		while (c.rst.next()) {
			list.add(c.rst.getString("bibtexkey"));
		}

		return list;
	}
}
