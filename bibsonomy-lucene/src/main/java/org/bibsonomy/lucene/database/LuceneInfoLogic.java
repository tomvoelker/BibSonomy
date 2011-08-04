package org.bibsonomy.lucene.database;

import java.util.Collection;
import java.util.List;

/**
 * used by the search to retrieve friends and group members
 * @author dzo
 * @version $Id$
 */
public interface LuceneInfoLogic {
	
	/**
	 * get list of all friends for a given user
	 * 
	 * @param userName the user name
	 * @return all friends of given user 
	 */
	public Collection<String> getFriendsForUser(String userName);
	
	/**
	 * get given group's members
	 * 
	 * @param groupName
	 * @return the members of the group
	 */
	public List<String> getGroupMembersByGroupName(String groupName);
}
