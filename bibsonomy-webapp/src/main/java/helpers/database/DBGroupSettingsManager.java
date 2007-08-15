package helpers.database;


import java.sql.*;

import beans.GroupSettingsBean;


/**
 * Manages the privacy level of groups.
 *
 */
public class DBGroupSettingsManager extends DBManager{
	
	/**
	 * Gets the privacy level of the given group (in bean) and returns it in the bean.
	 *  
	 * @param bean used for input (username) and output (privlevel)
	 */
	public static void getPrivlevel (GroupSettingsBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("SELECT privlevel FROM groupids WHERE group_name = ?");
				c.stmt.setString(1, bean.getUsername());
				c.rst = c.stmt.executeQuery();
				if (c.rst.next()) {
					// fill bean
					bean.setPrivlevel(c.rst.getInt(1));
				}
			}
		} catch (SQLException e) {
			System.out.println("DBGSM: " + e);
		} finally {
			c.close(); // close database connection
		}
		
	}

	/**
	 * Sets the privacy level of the given group (in bean).
	 * 
	 * @param bean contains group name
	 * 
	 * @return <code>true</code> if exactly one database row got updated
	 */
	public static boolean setPrivlevel (GroupSettingsBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("UPDATE groupids SET privlevel = ? WHERE group_name = ?");
				c.stmt.setInt(1, bean.getPrivlevel());
				c.stmt.setString(2, bean.getUsername());
				return c.stmt.executeUpdate() == 1; // return true, if exactly one row got updated 
			}
		} catch (SQLException e) {
			System.out.println("DBGSM: " + e);
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
}
