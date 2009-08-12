package helpers.database;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;

import beans.LayoutBean;


public class DBLayoutManager extends DBManager {

	private static final Log log = LogFactory.getLog(DBLayoutManager.class);
	
	public static void getLayoutSettingsForUser (LayoutBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement 
				c.stmt = c.conn.prepareStatement("SELECT hash,name FROM document WHERE user_name = ? AND content_id = " + 0);
				c.stmt.setString(1, bean.getUsername());
				c.rst = c.stmt.executeQuery();
				while (c.rst.next()) {
					final String hash = c.rst.getString("hash");
					final String name = c.rst.getString("name");
					if (hash.equals(JabrefLayoutUtils.userLayoutHash(bean.getUsername(), LayoutPart.ITEM))) {
						bean.setItemName(name);
						bean.setItemHash(hash);
					} else if (hash.equals(JabrefLayoutUtils.userLayoutHash(bean.getUsername(), LayoutPart.BEGIN))) {
						bean.setBeginName(name);
						bean.setBeginHash(hash);
					} else if (hash.equals(JabrefLayoutUtils.userLayoutHash(bean.getUsername(), LayoutPart.END))) {
						bean.setEndName(name);
						bean.setEndHash(hash);
					}
				}
			}
		} catch (SQLException e) {
			log.fatal("could not get layout settings for user: " + e);
		} finally {
			c.close(); // close database connection
		}
	}

	
	public static boolean deleteLayout (String user, String hash) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement 
				c.stmt = c.conn.prepareStatement("DELETE FROM document WHERE user_name = ? AND hash = ?");
				c.stmt.setString(1, user);
				c.stmt.setString(2, hash);
				return c.stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			log.fatal("could not delete layout settings for user " + user + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
	
	public static boolean insertLayout (String user, String hash, String fileName) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement 
				c.stmt = c.conn.prepareStatement("INSERT INTO document (hash, content_id, name, user_name, date) VALUES (?, ?, ?, ?, ?)");
				c.stmt.setString(1, hash);
				c.stmt.setInt(2, 0);
				c.stmt.setString(3, fileName);
				c.stmt.setString(4, user);
				c.stmt.setTimestamp(5, new Timestamp(new Date().getTime()));
				return c.stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			log.fatal("could not insert layout settings for user " + user + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
}
