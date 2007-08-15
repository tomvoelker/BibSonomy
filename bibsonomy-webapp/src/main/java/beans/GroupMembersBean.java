/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;

import helpers.database.DBGroupManager;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;


public class GroupMembersBean implements Serializable {
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	private SortedSet<String> members = null;
	private String username = "";
	private int group;
	
	// inserts the data into the DB, if everything is valid
	public void queryDB() {
		DBGroupManager.getGroupMembers (this);
	}

	
	public GroupMembersBean() {
		
	}
	
	// members
	public SortedSet<String> getMembers() {
		if (members == null) {
			members = new TreeSet<String>();
			queryDB();
		}
		return members;
	}
	public void addMember (String error) {
		members.add(error);
	}
	
	public int getCount () {
		return getMembers().size();
	}


	// user name
	public String getUsername() {
		return username;
	}
	public void setUsername(String currUser) {
		this.username = currUser;
	}

	// group
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
}

