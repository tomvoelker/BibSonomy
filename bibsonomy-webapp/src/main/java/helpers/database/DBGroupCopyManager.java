package helpers.database;

import helpers.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import resources.Resource;

public class DBGroupCopyManager <Type extends Resource> {
	
	private PreparedStatement stmtP = null;
	private ResultSet rst           = null;
	
	/* this String has to be used to prepare the statement used in getGroup and getCopiesForGroup */
	private static final String SQL_SELECT_GROUP = "SELECT g.group,g.defaultgroup FROM groups g, groupids i WHERE g.user_name = ? AND i.group_name = ? AND g.group=i.group";
	
	public void prepareStatements (Connection conn) throws SQLException {
		stmtP = conn.prepareStatement(SQL_SELECT_GROUP);
	}
	
	public void closeStatements () {
		if (stmtP != null) {try {stmtP.close(); } catch (SQLException e) {} stmtP = null;}
		if (rst   != null) {try { rst.close();  } catch (SQLException e) {} rst   = null;}
	}
	
	/*
	 * gets all users from resource for which we have to copy that resource
	 * than copies the resource for every user and adds it to a list
	 * 
	 * duplicates are ignored, that means if a resource exists already in a group,
	 * it will not be overwritten
	 * 
	 */
	public LinkedList<Type> getCopiesForGroup (Type res, DBContentManager man) throws SQLException {
		LinkedList<Type> copies = new LinkedList<Type>();
		/* iterate over all users ("groups") for which we have to post to */
		for (String usergroup: res.getUsersToPost()) {
			// check, if currUser is allowed to post as this user (~ in this group)
			//int groupid = getGroup(stmtP, res.getUser(), usergroup);
			/* look, if user is in usergroup */
			stmtP.setString(1, res.getUser()); 
			stmtP.setString(2, usergroup);
			rst = stmtP.executeQuery();
			if (rst.next()) {
				/* user is in this group --> copy post */
				int postgroup = rst.getInt("defaultgroup");  /* id for visibility of post */
				/* check if bookmark already exists */
				int oldcontentid = man.getContentID (usergroup, res.getHash());
				if (oldcontentid == Resource.UNDEFINED_CONTENT_ID) {
					// resource does not exist --> copy it!
					try {
						Type rescopy = (Type)res.clone();
						rescopy.addFromTag(res.getUser());
						rescopy.setUser(usergroup);
						rescopy.setGroup(usergroup);
						rescopy.setGroupid(postgroup);
						copies.add(rescopy);
					} catch (CloneNotSupportedException e) {
					}
				}
			}
			
		}
		// add for:USER tags
		for (String user:res.getUsersToPost()) {
			res.addForTag(user);
		}
		return copies;
	}
	
	/*
	 * returns the ID of Group group, if user is in group
	 */
	public int getGroup (String user, String group) throws SQLException {
		if (group == null || group.equals("public")) {
			return constants.SQL_CONST_GROUP_PUBLIC;
		}
		if (group.equals("private")) {
			return constants.SQL_CONST_GROUP_PRIVATE;
		}
		if (group.equals("friends")) {
			return constants.SQL_CONST_GROUP_FRIENDS;
		}
		stmtP.setString(1, user);
		stmtP.setString(2, group);
		rst = stmtP.executeQuery();
		if (rst.next()) {
			return rst.getInt("group");
		}
		return constants.SQL_CONST_GROUP_PUBLIC;
	}
}