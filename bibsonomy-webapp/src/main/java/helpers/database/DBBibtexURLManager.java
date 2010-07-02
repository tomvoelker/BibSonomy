package helpers.database;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.extra.BibTexExtra;

import resources.Bibtex;

@Deprecated
public class DBBibtexURLManager extends DBManager {
	
	private static final String SQL_SELECT_CONTENTID = "SELECT content_id FROM bibtex WHERE simhash" + Bibtex.INTRA_HASH + " = ? AND user_name = ?";
	private static final String SQL_INSERT_URL       = "INSERT INTO bibtexurls (url, text, content_id) VALUES (?,?,?)";
	private static final String SQL_DELETE_URL       = "DELETE FROM bibtexurls WHERE content_id = ? AND url = ?";
	private static final String SQL_SELECT_URL       = "SELECT u.url, u.text, u.date" +
			                                           "  FROM bibtexurls u " +
			                                           "    JOIN bibtex b ON u.content_id = b.content_id " +
			                                           "  WHERE b.simhash" + Bibtex.INTRA_HASH + " = ? " +
			                                           "    AND b.user_name = ? " +
			                                           "  ORDER BY u.date DESC";
	
	private static final Log log = LogFactory.getLog(DBBibtexURLManager.class);

	public static boolean createURL (BibTexExtra url, String hash, String user, boolean validCkey) {
		DBContext c = new DBContext();
		try {
			String urlstring = Bibtex.cleanUrl(url.getUrl().toString());
			/*
			 * check, that URL is not empty (otherwise deleting in database ... difficult ;-)
			 */
			if (!"".equals(urlstring) && c.init() && validCkey) {
				c.conn.setAutoCommit(false);
				
				int content_id = getContentID(hash, user, c);
				if ( content_id != Bibtex.UNDEFINED_CONTENT_ID) {
					/*
					 * hash + content_id exists --> insert
					 */
					c.stmt = c.conn.prepareStatement(SQL_INSERT_URL);
					c.stmt.setString(1, urlstring);
					c.stmt.setString(2, url.getText());
					c.stmt.setInt(3, content_id);
					c.stmt.executeUpdate();
				}
				c.conn.commit();
				return true;
			}
		} catch (SQLException e) {
			log.warn("Could not create URL for hash " + hash + " from user " + user + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}

	public static boolean deleteURL (BibTexExtra url, String hash, String user, boolean validCkey) {
		DBContext c = new DBContext();
		try {
			if (c.init() && validCkey) {
				c.conn.setAutoCommit(false);
				
				int content_id = getContentID(hash, user, c);
				if (content_id != Bibtex.UNDEFINED_CONTENT_ID) {
					/*
					 * hash + content_id exists --> delete
					 */
					c.stmt = c.conn.prepareStatement(SQL_DELETE_URL);
					c.stmt.setInt(1, content_id);
					c.stmt.setString(2, url.getUrl().toString());
					c.stmt.executeUpdate();
				}
				
				c.conn.commit();
				return true;
			}
		} catch (SQLException e) {
			log.fatal("Could not delete URL for hash " + hash + " from user " + user, e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}

	public static LinkedList<BibTexExtra> readURL (String hash, String user) {
		DBContext c = new DBContext();
		LinkedList<BibTexExtra> list = new LinkedList<BibTexExtra>();
		try {
			if (c.init()) {
				c.stmt = c.conn.prepareStatement(SQL_SELECT_URL);
				c.stmt.setString(1, hash);
				c.stmt.setString(2, user);
				c.rst = c.stmt.executeQuery();
				while (c.rst.next()) {
					list.add(new BibTexExtra (new URL(c.rst.getString("url")), c.rst.getString("text"), c.rst.getTimestamp("date")));
				}
			}
		} catch (SQLException e) {
			log.fatal("Could not get URL for hash " + hash + " from user " + user, e);
		} catch (MalformedURLException ex) {
			log.fatal("Could not get URL for hash " + hash + " from user " + user, ex);
		} finally {
			c.close(); // close database connection
		}
		return list;
	}



	private static int getContentID(String hash, String user, DBContext c) throws SQLException {
		c.stmt = c.conn.prepareStatement(SQL_SELECT_CONTENTID);
		c.stmt.setString(1, hash);
		c.stmt.setString(2, user);
		c.rst = c.stmt.executeQuery();
		if (c.rst.next()) {
			return c.rst.getInt("content_id");
		}
		return Bibtex.UNDEFINED_CONTENT_ID;
	}

}
