package org.bibsonomy.rest.client;

import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.client.queries.delete.DeleteGroupQuery;
import org.bibsonomy.rest.client.queries.delete.DeletePostQuery;
import org.bibsonomy.rest.client.queries.delete.DeleteUserQuery;
import org.bibsonomy.rest.client.queries.delete.RemoveUserFromGroupQuery;
import org.bibsonomy.rest.client.queries.get.GetGroupDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetGroupListQuery;
import org.bibsonomy.rest.client.queries.get.GetPostDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetPostsQuery;
import org.bibsonomy.rest.client.queries.get.GetTagDetailsQuery;
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
		this(username, apiKey);
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
		// TODO: only the username is used, but a whole user object is
		// transmitted, so a dummy with only username is used here.
		// -> This could lead to future problems
		final User dummyUserObject = new User();
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

	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end, String search) {
		// TODO: clientside chain of responsibility
		final GetPostsQuery query = new GetPostsQuery(start, end);
		query.setGrouping(grouping, groupingName);
		query.setResourceHash(hash);
		query.setResourceType(resourceType);
		query.setTags(tags);
		return (List) execute(query);
	}

	public Tag getTagDetails(String tagName) {
		return execute(new GetTagDetailsQuery(tagName));
	}

	public List<Tag> getTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, String hash, Order order, int start, int end, String search) {
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
		return execute(new GetUserListOfGroupQuery(groupName, start, end));
	}

	public void removeUserFromGroup(String groupName, String userName) {
		execute(new RemoveUserFromGroupQuery(userName, groupName));
	}

	public String createGroup(Group group) {
		return execute(new CreateGroupQuery(group));
	}

	public String createPost(Post<?> post) {
		return execute(new CreatePostQuery(this.authUserName, post));
	}

	public String createUser(User user) {
		return execute(new CreateUserQuery(user));
	}

	public String updateGroup(final Group group) {
		// groups cannot be renamed
		return execute(new ChangeGroupQuery(group.getName(), group));
	}

	public String updatePost(final Post<?> post) {
		// hashes are recalculated by the server
		return execute(new ChangePostQuery(this.authUserName, post.getResource().getIntraHash(), post));
	}

	public String updateUser(User user) {
		// accounts cannot be renamed
		return execute(new ChangeUserQuery(user.getName(), user));
	}

	public String addDocument(Document doc, String resourceHash) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteDocument(String userName, String resourceHash, String fileName) {
		// TODO Auto-generated method stub
		
	}	
	
	public void addInetAddressStatus(InetAddress address, InetAddressStatus status) {
		// TODO Auto-generated method stub
		
	}

	public void deleteInetAdressStatus(InetAddress address) {
		// TODO Auto-generated method stub
		
	}

	public InetAddressStatus getInetAddressStatus(InetAddress address) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, StatisticsConstraint constraint, String search) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Tag> getConcepts(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		
	}

	public String updateConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteConcept(String concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		
	}

	public void deleteRelation(String upper, String lower, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		
	}

	public Tag getConceptDetails(String conceptName, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<User> getUsers(List<String> tags, Order order, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}
}