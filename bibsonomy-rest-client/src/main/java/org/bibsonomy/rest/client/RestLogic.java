/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.Author;
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
	private static final Log log = LogFactory.getLog(RestLogic.class);
	private final Bibsonomy bibsonomy;
	private final User authUser;

	public RestLogic(final String username, final String apiKey, final String apiURL) {
		this(username, apiKey);
		this.bibsonomy.setApiURL(apiURL);
	}

	public RestLogic(final String username, final String apiKey) {
		this.bibsonomy = new Bibsonomy(username, apiKey);
		this.authUser = new User(username);
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

	public void deletePosts(String userName, List<String> resourceHashes) {
		/* 
		 * FIXME: this iteration should be done on the server, i.e., DeletePostQuery should 
		 * support several posts ... although it's probably not so simple.
		 */ 		
		for (final String resourceHash: resourceHashes) {
			execute(new DeletePostQuery(userName, resourceHash));
		}
	}

	public void deleteUser(String userName) {
		execute(new DeleteUserQuery(userName));
	}

	public User getAuthenticatedUser() {
		return this.authUser;
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
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
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

	public List<Tag> getTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, String hash, Order order, int start, int end, String search, TagSimilarity relation) {
		GetTagsQuery query = new GetTagsQuery(start, end);
		query.setGrouping(grouping, groupingName);
		query.setFilter(regex);
		return execute(query);
	}

	public User getUserDetails(String userName) {
		return execute(new GetUserDetailsQuery(userName));
	}

	public void deleteUserFromGroup(String groupName, String userName) {
		execute(new RemoveUserFromGroupQuery(userName, groupName));
	}

	public String createGroup(Group group) {
		return execute(new CreateGroupQuery(group));
	}

	public List<String> createPosts(List<Post<?>> posts) {
		/* 
		 * FIXME: this iteration should be done on the server, i.e., CreatePostQuery should 
		 * support several posts ... although it's probably not so simple.
		 */ 
		final List<String> resourceHashes = new LinkedList<String>();
		for (Post<?> post: posts) {
			resourceHashes.add(execute(new CreatePostQuery(this.authUser.getName(), post)));
		}
		return resourceHashes;
	}

	public String createUser(User user) {
		return execute(new CreateUserQuery(user));
	}

	public String updateGroup(final Group group) {
		// groups cannot be renamed
		return execute(new ChangeGroupQuery(group.getName(), group));
	}

	public List<String> updatePosts(List<Post<?>> posts, PostUpdateOperation operation) {
		/* 
		 * FIXME: this iteration should be done on the server, i.e., CreatePostQuery should 
		 * support several posts ... although it's probably not so simple.
		 */ 
		final List<String> resourceHashes = new LinkedList<String>();
		for (Post<?> post: posts) {
			// hashes are recalculated by the server
			resourceHashes.add(execute(new ChangePostQuery(this.authUser.getName(), post.getResource().getIntraHash(), post)));
		}
		return resourceHashes;
	}

	public String updateUser(User user) {
		// accounts cannot be renamed
		return execute(new ChangeUserQuery(user.getName(), user));
	}

	public String createDocument(Document doc, String resourceHash) {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getDocument(final String userName, final String fileHash) {
		
		return null;
	}
	
	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteDocument(Document document, String resourceHash) {
		// TODO Auto-generated method stub

	}	

	public void createInetAddressStatus(InetAddress address, InetAddressStatus status) {
		// TODO Auto-generated method stub

	}

	public void deleteInetAdressStatus(InetAddress address) {
		// TODO Auto-generated method stub

	}

	public InetAddressStatus getInetAddressStatus(InetAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, StatisticsConstraint constraint, String search, List<String> tags) {
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

	public List<User> getUsers(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, UserRelation relation, String search, int start, int end) {
		// here we just simulate two possible answers of the user chain
		if (GroupingEntity.ALL.equals(grouping)) {
			return execute(new GetUserListQuery(start, end));
		}
		if (GroupingEntity.GROUP.equals(grouping)) {
			return execute(new GetUserListOfGroupQuery(groupingName, start, end));	
		}
		log.error("grouping entity " + grouping.name() + " not yet supported in RestLogic implementation.");
		return null;
	}	

	public String getClassifierSettings(ClassifierSettings key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateClassifierSettings(ClassifierSettings key, String value) {
		// TODO Auto-generated method stub
	}

	public int getClassifiedUserCount(Classifier classifier, SpamStatus status, int interval) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<User> getClassifiedUsers(Classifier classifier, SpamStatus status, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getClassifierHistory(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getClassifierComparison(int interval) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPostStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search, StatisticsConstraint constraint) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getOpenIDUser(String openID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags) {
		return 0;
	}

	public int getTagStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}


	public List<User> getFriendsOfUser(User loginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUserFriends(User loginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Author> getAuthors(GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUserRelationship(String sourceUser, String targetUser, UserRelation relation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createUserRelationship(String sourceUser, String targetUser, UserRelation relation) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<User> getUserRelationship(String sourceUser, UserRelation relation) {
		// TODO Auto_generated method stub
		return new ArrayList<User>();
	}

	@Override
	public int createBasketItems(List<Post<? extends Resource>> posts) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteBasketItems(List<Post<? extends Resource>> posts, boolean clearAll) {
		// TODO Auto-generated method stub
		return 0;
	}
	public int deleteMessages(String userName, List<Integer> contentIds) {
		return 0;
	}
	
}