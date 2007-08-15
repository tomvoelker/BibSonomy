package helpers.database;

import helpers.constants;

import java.sql.*;
import beans.GroupMembersBean;


public class DBGroupManager extends DBManager {
	/*
	 * gets group members depending on privacy level of group
	 */
	public static void getGroupMembers (GroupMembersBean bean) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// get privacy level of this group
				c.stmt = c.conn.prepareStatement("SELECT privlevel FROM groupids WHERE `group` = ?");
				c.stmt.setInt(1, bean.getGroup());
				c.rst = c.stmt.executeQuery();
				if (c.rst.next()) {
					// depending on privacy level, show group members
					int privlevel = c.rst.getInt("privlevel");
					c.stmt.close();
					c.stmt = null;
					if (privlevel == constants.SQL_CONST_PRIVLEVEL_PUBLIC) {
						/* member list public */
						c.stmt = c.conn.prepareStatement("SELECT user_name FROM groups WHERE `group` = ?");
					} else if (privlevel == constants.SQL_CONST_PRIVLEVEL_MEMBERS) {
						/* members can list members */
						// check, if currUser is member of group
						c.stmt = c.conn.prepareStatement("SELECT user_name FROM groups WHERE user_name = ? AND `group` = ?");
						c.stmt.setString(1, bean.getUsername());
						c.stmt.setInt(2, bean.getGroup());
						c.rst = c.stmt.executeQuery();
						if (c.rst.next()) {
							// user is a member of the group
							c.stmt = c.conn.prepareStatement("SELECT user_name FROM groups WHERE `group` = ?");
						} else {
							c.stmt.close();
							c.stmt = null;
						}
					}
					// get members
					if (c.stmt != null) {
						c.stmt.setInt(1, bean.getGroup());
						c.rst = c.stmt.executeQuery();
						while (c.rst.next()) {
							bean.addMember(c.rst.getString("user_name"));
						}
					}
					
				}
				
			}
		} catch (SQLException e) {
			System.out.println("DBSM: " + e);
		} finally {
			c.close(); // close database connection
		}
		
	}
}
