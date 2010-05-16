package helpers.database;


import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import beans.UserBean;


/**
 * Still used in {@link SessionSettingsFilter} to set the settings of a user.
 * 
 * @author rja
 *
 */
@Deprecated
public class DBUserManager extends DBManager {

	private final static String COL_TAGBOX_STYLE   = "tagbox_style";
	private final static String COL_TAGBOX_SORT    = "tagbox_sort";
	private final static String COL_TAGBOX_MINFREQ = "tagbox_minfreq";
	private final static String COL_TAGBOX_TOOLTIP = "tagbox_tooltip";
	private final static String COL_LIST_ITEMCOUNT = "list_itemcount";
	private final static String COL_DEFAULT_LANG   = "lang";
	private final static String COL_API_KEY   	   = "api_key";
	private final static String LOG_LEVEL   	   = "log_level";
	
	private final static Log log = LogFactory.getLog(DBUserManager.class); 
	

	/**
	 * Sets the settings for a user.
	 * @param user used for input of settings like tagboxStyle, tagboxSort, ...
	 * @return <code>true</code> if exactly one database row got updated
	 */
	@Deprecated
	public static boolean setSettingsForUser (UserBean user) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				
				// prepare Statement
				c.stmt = c.conn.prepareStatement("UPDATE user " +
						                         "  SET " +
						                              COL_TAGBOX_STYLE   + " = ?, " +
						                              COL_TAGBOX_SORT    + " = ?, " +
						                              COL_TAGBOX_MINFREQ + " = ?, " +
						                              COL_TAGBOX_TOOLTIP + " = ?, " +
 						                              COL_LIST_ITEMCOUNT + " = ?,  " +
 						                              COL_DEFAULT_LANG + " = ?, " +
 						                              COL_API_KEY + " = ?, " +
 						                              LOG_LEVEL + " = ?," +
 						                              "confirmDelete = ? " +
						                         "  WHERE user_name = ?");
				c.stmt.setInt(1, user.getTagboxStyle());
				c.stmt.setInt(2, user.getTagboxSort());
				c.stmt.setInt(3, user.getTagboxMinfreq());
				c.stmt.setInt(4, user.getTagboxTooltip());
				c.stmt.setInt(5, user.getItemcount());
				c.stmt.setString(6, user.getDefaultLanguage());
				c.stmt.setString(7, user.getApiKey());
				c.stmt.setInt(8, user.getLogLevel());
				c.stmt.setBoolean(9, "true".equals(user.getConfirmDelete()));
				c.stmt.setString(10, user.getName());
				
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			log.fatal("Could not set settings for user " + user.getName() + ": " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
	
}