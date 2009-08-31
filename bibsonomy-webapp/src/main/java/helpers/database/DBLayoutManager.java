package helpers.database;


import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;

import beans.LayoutBean;


/**
 * Still used to display the active layouts on the /settings page.
 * 
 * @author rja
 *
 */
public class DBLayoutManager extends DBManager {

	private static final Logger log = Logger.getLogger(DBLayoutManager.class);
	
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
	
}
