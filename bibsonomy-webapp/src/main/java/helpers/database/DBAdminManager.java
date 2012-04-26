package helpers.database;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import beans.AdminBean;


/**
 * Provides methods only available for admins (like handling spammers, groups, api keys).
 * 
 * @author rja
 * @version $Id$
 */
@Deprecated
public class DBAdminManager extends DBManager {
	
	/**
	 * add a user to the negative spammerlist. So he is marked NOT as a spammer and will not appear longer in any suggestion list
	 * @param bean the AdminBean reference
	 */
	public static void removeUserFromSpammerlist(final AdminBean bean) {
		final DBContext c = new DBContext();

		try {
			if (c.init()) {
				c.stmt = c.conn.prepareStatement("UPDATE user " +
						                         "  SET " +
						                         "    spammer_suggest = 0, " +
						                         "    updated_by = ?, " +
						                         "    updated_at = ?, " +
						                         "    to_classify = " + DBAdminManager.SQL_CONST_TO_CLASSIFY_FALSE + 						                         
						                         "  WHERE user_name = ?");
				c.stmt.setString(1, bean.getCurrUser());
				c.stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
				c.stmt.setString(3, bean.getUser());

				if(c.stmt.executeUpdate() == 1) {
					bean.addInfo("user '" + bean.getUser() + "' was removed from spammer suggestion list.");
				} else {
					bean.addError("user '" + bean.getUser() + "' could not be removed from the list. The user was not found.");
				}
			}
		} catch (final SQLException e) {		
			bean.addError("Sorry, an error occured: " + e);
		}		
	}


	/**  
	 * add or remove a tagname on the 'blacklist' of tags used by spammers (
	 * or add a clean tag  to the list which won't be listed in the recommendation list in future
	 * 
	 * @param bean the AdminBean reference
	 * @param flag 
	 * 		<code>true</code>: flag tag as spammertag
	 * 		<code>false</code>: remove tag from spammertag list
	 * @param type
	 * 		if <code>0</code> tag is added to negative spammertag list so it is no longer in the suggestions lists
	 * 		
	 */
	public static void flagSpammerTag(final AdminBean bean, final boolean flag, final int type) {
		final DBContext c = new DBContext();

		try {
			if (c.init()) {

				// remove tag from list
				if (!flag) {				
					c.stmt = c.conn.prepareStatement("DELETE FROM spammer_tags WHERE tag_name = ?");				
					c.stmt.setString(1, bean.getTag());				
					if (c.stmt.executeUpdate() == 1) {					
						bean.addInfo("tag '" + bean.getTag() + "' removed from list.");
					} else {
						bean.addError("tag '" + bean.getTag() + "' could not be removed. It was not found in the list.");
					}				
				} else {  // add tag to list (1 = spammertag, 0 = clean tag from suggestion list)
					c.stmt = c.conn.prepareStatement("INSERT INTO spammer_tags (tag_name,spammer) VALUES (?,?)");
					c.stmt.setString(1, bean.getTag());
					c.stmt.setInt(2, type);
					if (c.stmt.executeUpdate() == 1) {
						if (type == 1) 
							bean.addInfo("tag '" + bean.getTag() + "' was added to the list.");
						else
							bean.addInfo("tag '" + bean.getTag() + "' was removed from recommendation list.");
					} else {
						if (type == 1)
							bean.addError("tag '" + bean.getTag() + "' is already in the list.");					
					}
				}		
			}
		} catch (final SQLException e) {			
			bean.addError("Sorry, an error occured: " + e);
		}
	}


	public static final int SQL_CONST_TO_CLASSIFY_FALSE = 0;

}