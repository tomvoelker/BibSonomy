/*
 * Created on 28.06.2007
 */
package org.bibsonomy.database;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.RestDatabaseManager;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.Order;

public class DBLogic implements LogicInterface {
	private final String loginUserName;
	private final DBLogicInterface dbLogic;
	
	protected DBLogic(final String loginUserName, final DBLogicInterface dbLogic) {
		this.loginUserName = loginUserName;
		this.dbLogic = dbLogic;
	}
	
	public static DBLogic getApiAccess(final String username, final String apiKey) {
		final RestDatabaseManager restDbM = RestDatabaseManager.getInstance();
		if (restDbM.validateUserAccess(username, apiKey) == false) {
			throw new ValidationException("Please authenticate yourself.");
		}
		return new DBLogic(username, restDbM);
	}

	public void addUserToGroup(String groupName, String userName) {
		this.dbLogic.addUserToGroup(groupName, userName);
	}

	public void deleteGroup(String groupName) {
		this.dbLogic.deleteGroup(groupName);
	}

	public void deletePost(String userName, String resourceHash) {
		if ((this.loginUserName == null) || (this.loginUserName.equals(userName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		this.dbLogic.deletePost(userName, resourceHash);
	}

	public void deleteUser(String userName) {
		if ((this.loginUserName == null) || (this.loginUserName.equals(userName) == false)) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		this.dbLogic.deleteUser(userName);
	}

	public Group getGroupDetails(String groupName) {
		return this.dbLogic.getGroupDetails(this.loginUserName, groupName);
	}

	public List<Group> getGroups(int start, int end) {
		return this.dbLogic.getGroups(this.loginUserName, start, end);
	}

	public Post<? extends Resource> getPostDetails(String resourceHash, String userName) {
		return this.dbLogic.getPostDetails(this.loginUserName, resourceHash, userName);
	}

	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end) {
		return this.dbLogic.getPosts(this.loginUserName, resourceType, grouping, groupingName, tags, hash, order, start, end);
	}

	public Tag getTagDetails(String tagName) {
		return this.dbLogic.getTagDetails(this.loginUserName, tagName);
	}

	public List<Tag> getTags(GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return this.dbLogic.getTags(this.loginUserName, grouping, groupingName, regex, start, end);
	}

	public User getUserDetails(String userName) {
		return this.dbLogic.getUserDetails(this.loginUserName, userName);
	}

	public List<User> getUsers(int start, int end) {
		return this.dbLogic.getUsers(this.loginUserName, start, end);
	}

	public List<User> getUsers(String groupName, int start, int end) {
		return this.dbLogic.getUsers(this.loginUserName, groupName, start, end);
	}

	public void removeUserFromGroup(String groupName, String userName) {
		if (this.loginUserName == null) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		// FIXME: IMPORTANT: not everybody may do this!
		// better do nothing than anything horribly wrong:  this.dbLogic.removeUserFromGroup(groupName, userName);
	}

	public void storeGroup(Group group) {
		if (this.loginUserName == null) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		this.dbLogic.storeGroup(this.loginUserName, group);
	}

	public <T extends Resource> void storePost(Post<T> post) {
		if (this.loginUserName == null) {
			throw new ValidationException("You are not authorized to perform the requested operation");
		}
		this.dbLogic.storePost(this.loginUserName, post);
	}

	public void storeUser(User user) {
		this.dbLogic.storeUser(this.loginUserName, user);
	}

	public String getAuthenticatedUser() {
		return this.loginUserName;
	}
}
