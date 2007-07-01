/*
 * Created on 01.07.2007
 */
package org.bibsonomy.rest.client;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.Order;
import org.bibsonomy.rest.client.queries.delete.DeleteGroupQuery;
import org.bibsonomy.rest.client.queries.delete.DeletePostQuery;
import org.bibsonomy.rest.client.queries.delete.DeleteUserQuery;
import org.bibsonomy.rest.client.queries.delete.RemoveUserFromGroupQuery;
import org.bibsonomy.rest.client.queries.get.GetGroupDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetGroupListQuery;
import org.bibsonomy.rest.client.queries.get.GetPostDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetPostsQuery;
import org.bibsonomy.rest.client.queries.get.GetTagsQuery;
import org.bibsonomy.rest.client.queries.get.GetUserDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetUserListOfGroupQuery;
import org.bibsonomy.rest.client.queries.get.GetUserListQuery;
import org.bibsonomy.rest.client.queries.post.AddUserToGroupQuery;
import org.bibsonomy.rest.client.queries.post.CreateGroupQuery;
import org.bibsonomy.rest.client.queries.post.CreatePostQuery;
import org.bibsonomy.rest.client.queries.post.CreateUserQuery;
import org.bibsonomy.rest.client.queries.put.ChangeGroupQuery;
import org.bibsonomy.rest.client.queries.put.ChangePostQuery;
import org.bibsonomy.rest.client.queries.put.ChangeUserQuery;
import org.bibsonomy.util.ExceptionUtils;

public class RestLogic implements LogicInterface {
	private static final Logger log = Logger.getLogger(RestLogic.class);
	private final Bibsonomy bibsonomy;
	private final String authUserName;

	public RestLogic(final String username, final String apiKey, final String apiURL) {
		this(apiKey, apiURL);
		this.bibsonomy.setApiURL(apiURL);
	}
	
	public RestLogic(final String username, final String apiKey) {
		this.bibsonomy = new Bibsonomy(username, apiKey);
		this.authUserName = username;
	}
	
	private <T> T execute(AbstractQuery<T> query) {
		try {
			bibsonomy.executeQuery(query);
		} catch (Exception ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "unable to execute " + query.toString());
		}
		return query.getResult();
	}
	
	public void addUserToGroup(String groupName, String userName) {
		final User dummyUserObject = new User(); // TODO: only the username is used, but a whole user object is transmitted, so a dumm with only username is used here. This could lead to future problems
		dummyUserObject.setName(userName);
		execute(new AddUserToGroupQuery(groupName, dummyUserObject));
	}

	public void deleteGroup(String groupName) {
		execute(new DeleteGroupQuery(groupName));
	}

	public void deletePost(String userName, String resourceHash) {
		execute(new DeletePostQuery(userName, resourceHash));
	}

	public void deleteUser(String userName) {
		execute(new DeleteUserQuery(userName));
	}

	public String getAuthenticatedUser() {
		return this.authUserName;
	}

	public Group getGroupDetails(String groupName) {
		return execute(new GetGroupDetailsQuery(groupName));
	}

	public List<Group> getGroups(int start, int end) {
		return execute(new GetGroupListQuery(start, end));
	}

	public Post<? extends Resource> getPostDetails(String resourceHash, String userName) {
		return execute(new GetPostDetailsQuery(userName, resourceHash));
	}

	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end) {
		final GetPostsQuery query = new GetPostsQuery(start, end);
		query.setGrouping(grouping, groupingName);
		query.setResourceHash(hash);
		query.setResourceType(resourceType);
		query.setTags(tags);
		return (List) execute(query);
	}

	public Tag getTagDetails(String tagName) {
		return getTagDetails(tagName);
	}

	public List<Tag> getTags(GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		GetTagsQuery query = new GetTagsQuery(start, end);
		query.setGrouping(grouping, groupingName);
		query.setFilter(regex);
		return execute(query);
	}

	public User getUserDetails(String userName) {
		return execute(new GetUserDetailsQuery(userName));
	}

	public List<User> getUsers(int start, int end) {
		return execute(new GetUserListQuery(start, end));
	}

	public List<User> getUsers(String groupName, int start, int end) {
		return execute(new GetUserListOfGroupQuery(groupName,start, end));
	}

	public void removeUserFromGroup(String groupName, String userName) {
		execute(new RemoveUserFromGroupQuery(userName, groupName));
	}

	public void createGroup(Group group) {
		execute(new CreateGroupQuery(group));
	}

	public void createPost(Post<?> post) {
		execute(new CreatePostQuery(this.authUserName, post));
	}

	public void createUser(User user) {
		execute(new CreateUserQuery(user));
	}

	public void updateGroup(final Group group) {
		execute(new ChangeGroupQuery(group.getName(), group)); // groups cannot be renamed
	}

	public void updatePost(final Post<?> post) {
		execute(new ChangePostQuery(this.authUserName, post.getResource().getIntraHash(), post)); // hashes are recalculated by the server
	}

	public void updateUser(User user) {
		execute(new ChangeUserQuery(user.getName(), user)); // accounts cannot be renamed
	}

}
