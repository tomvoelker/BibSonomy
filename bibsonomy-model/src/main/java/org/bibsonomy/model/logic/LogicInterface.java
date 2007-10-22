package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * This interface is an adapter to the bibsonomys core functionality. <br/>
 * The methods returning
 * information return in general, if there are no matches, an empty set (if a
 * list is requested), or null (if a single entity is requested (a post, eg)).
 * <br/><b>Please try to be as close to the method-conventions as possible.
 * </b>If something is unclear, guess, check occurences and document your
 * result. If you have to change a convention, check all occurences and document
 * it properly! Try to check each possibility with a test-case.
 * 
 * TODO: split this interface as it might grow too much
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Jens Illig <illig@innofinity.de>
 * @author Christian Kramer
 * @version $Id$
 */
public interface LogicInterface extends PostLogicInterface {

	public String getAuthenticatedUser();
	
	/**
	 * Returns all users
	 * 
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(int start, int end);

	/**
	 * Returns all users who are members of the specified group
	 * 
	 * @param groupName name of the group
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(String groupName, int start, int end);

	/**
	 * Returns details about a specified user
	 * 
	 * @param userName name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public User getUserDetails(String userName);

	/**
	 * Returns all groups of the system.
	 * 
	 * @param end
	 * @param start
	 * @return a set of groups, an empty set else
	 */
	public List<Group> getGroups(int start, int end);

	/**
	 * Returns details of one group.
	 * 
	 * @param groupName
	 * @return the group's details, null else
	 */
	public Group getGroupDetails(String groupName);

	/**
	 * Returns a list of tags which can be filtered.
	 * 
	 * @param grouping
	 *            grouping tells whom tags are to be shown: the tags of a user,
	 *            of a group or of the viewables.
	 * @param groupingName
	 *            name of the grouping. if grouping is user, then its the
	 *            username. if grouping is set to {@link GroupingEntity#ALL},
	 *            then its an empty string!
	 * @param regex
	 *            a regular expression used to filter the tagnames
	 * @param resourceType
	 * 			  a resourceType (i.e. Bibtex or Bookmark) to get tags
	 *  		  only from a bookmark or a bibtex entry
	 * @param start
	 * @param end
	 * @return a set of tags, en empty set else
	 */
	public <T extends Resource> List<Tag> getTags(GroupingEntity grouping, String groupingName, String regex, Class<T> resourceType, int start, int end);

	/**
	 * Returns details about a tag. Those details are:
	 * <ul>
	 * <li>details about the tag itself, like number of occurrences etc</li>
	 * <li>list of subtags</li>
	 * <li>list of supertags</li>
	 * <li>list of correlated tags</li>
	 * </ul>
	 * 
	 * @param authUserName name of the authenticated user
	 * @param tagName name of the tag
	 * @return the tag's details, null else
	 */
	public Tag getTagDetails(String tagName);

	/**
	 * Removes the given user.
	 * 
	 * @param userName the user to delete
	 */
	public void deleteUser(String userName);

	/**
	 * Removes the given group.
	 * 
	 * @param groupName the group to delete
	 */
	public void deleteGroup(String groupName);

	/**
	 * Removes an user from a group.
	 * 
	 * @param groupName the group to change
	 * @param userName the user to remove
	 */
	public void removeUserFromGroup(String groupName, String userName);

	/**
	 * Adds a user to the database.
	 * 
	 * @param user  the user to add
	 * @return userid the user id of the created user
	 */
	public String createUser(User user);
	
	/**
	 * Updates a user to the database.
	 * 
	 * @param user  the user to update
	 * @return userid the user id of the updated user
	 */
	public String updateUser(User user);
	
	/**
	 * Adds a group to the database.
	 * 
	 * @param group  the group to add
	 * @return groupID the group id of the created group
	 */
	public String createGroup(Group group);
	
	/**
	 * Updates a group in the database.
	 * 
	 * @param group  the group to update
	 * @return groupID the group id of the updated group
	 */
	public String updateGroup(Group group);
		
	/**
	 * Adds an existing user to an existing group.
	 * 
	 * @param groupName  name of the existing group
	 * @param user  user to add
	 */
	public void addUserToGroup(String groupName, String userName);
}