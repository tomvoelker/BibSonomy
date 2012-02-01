/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.queries.delete.DeleteGroupQuery;
import org.bibsonomy.rest.client.queries.delete.DeletePostQuery;
import org.bibsonomy.rest.client.queries.delete.DeleteSyncDataQuery;
import org.bibsonomy.rest.client.queries.delete.DeleteUserQuery;
import org.bibsonomy.rest.client.queries.delete.RemoveUserFromGroupQuery;
import org.bibsonomy.rest.client.queries.get.GetFriendsQuery;
import org.bibsonomy.rest.client.queries.get.GetGroupDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetGroupListQuery;
import org.bibsonomy.rest.client.queries.get.GetLastSyncDataQuery;
import org.bibsonomy.rest.client.queries.get.GetPostDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetPostDocumentQuery;
import org.bibsonomy.rest.client.queries.get.GetPostsQuery;
import org.bibsonomy.rest.client.queries.get.GetTagDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetTagsQuery;
import org.bibsonomy.rest.client.queries.get.GetUserDetailsQuery;
import org.bibsonomy.rest.client.queries.get.GetUserListOfGroupQuery;
import org.bibsonomy.rest.client.queries.get.GetUserListQuery;
import org.bibsonomy.rest.client.queries.post.AddUsersToGroupQuery;
import org.bibsonomy.rest.client.queries.post.CreateGroupQuery;
import org.bibsonomy.rest.client.queries.post.CreatePostQuery;
import org.bibsonomy.rest.client.queries.post.CreateSyncPlanQuery;
import org.bibsonomy.rest.client.queries.post.CreateUserQuery;
import org.bibsonomy.rest.client.queries.post.CreateUserRelationshipQuery;
import org.bibsonomy.rest.client.queries.put.ChangeGroupQuery;
import org.bibsonomy.rest.client.queries.put.ChangePostQuery;
import org.bibsonomy.rest.client.queries.put.ChangeSyncStatusQuery;
import org.bibsonomy.rest.client.queries.put.ChangeUserQuery;
import org.bibsonomy.rest.client.util.ProgressCallback;
import org.bibsonomy.rest.client.util.ProgressCallbackFactory;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.util.ExceptionUtils;

/**
 * 
 * @version $Id$
 */
public class RestLogic implements LogicInterface {
	private static final Log log = LogFactory.getLog(RestLogic.class);

	private final User authUser;
	private final AuthenticationAccessor accessor;

	private final String apiURL;
	private final RendererFactory rendererFactory;
	private final RenderingFormat renderingFormat;
	private final ProgressCallbackFactory progressCallbackFactory;


	/**
	 * @param username the username
	 * @param apiKey the API key
	 * @param apiURL the API url
	 * @param renderingFormat
	 * @param progressCallbackFactory
	 */
	RestLogic(final String username, final String apiKey, final String apiURL, final RenderingFormat renderingFormat, final ProgressCallbackFactory progressCallbackFactory) {
		this.apiURL = apiURL;
		this.rendererFactory = new RendererFactory(new UrlRenderer(this.apiURL));
		this.renderingFormat = renderingFormat;
		this.progressCallbackFactory = progressCallbackFactory;

		this.authUser = new User(username);
		this.authUser.setApiKey(apiKey);
		this.accessor = null;
	}

	public RestLogic(final AuthenticationAccessor accessor, String apiUrl, RenderingFormat renderingFormat, ProgressCallbackFactory progressCallbackFactory) {
		this.apiURL = apiUrl;
		this.rendererFactory = new RendererFactory(new UrlRenderer(this.apiURL));
		this.renderingFormat = renderingFormat;
		this.progressCallbackFactory = progressCallbackFactory;

		this.authUser = new User(RESTConfig.USER_ME);
		this.accessor = accessor;
	}

	private <T> T execute(final AbstractQuery<T> query) {
		try {
			query.setApiURL(this.apiURL);
			query.setRenderingFormat(this.renderingFormat);
			query.setRendererFactory(this.rendererFactory);
			query.execute(this.authUser.getName(), this.authUser.getApiKey(), this.accessor);
		} catch (final Exception ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "unable to execute " + query.toString());
		}
		return query.getResult();
	}

	private <T> T executeWithCallback(final AbstractQuery<T> query, final ProgressCallback callback) {
		query.setProgressCallback(callback);
		return this.execute(query);
	}

	@Override
	public void deleteGroup(final String groupName) {
		execute(new DeleteGroupQuery(groupName));
	}

	@Override
	public void deletePosts(final String userName, final List<String> resourceHashes) {
		/*
		 * FIXME: this iteration should be done on the server, i.e.,
		 * DeletePostQuery should support several posts ... although it's
		 * probably not so simple.
		 */
		for (final String resourceHash : resourceHashes) {
			execute(new DeletePostQuery(userName, resourceHash));
		}
	}

	@Override
	public void deleteUser(final String userName) {
		execute(new DeleteUserQuery(userName));
	}

	@Override
	public User getAuthenticatedUser() {
		return this.authUser;
	}

	@Override
	public Group getGroupDetails(final String groupName) {
		return execute(new GetGroupDetailsQuery(groupName));
	}

	@Override
	public List<Group> getGroups(final int start, final int end) {
		return execute(new GetGroupListQuery(start, end));
	}

	@Override
	public Post<? extends Resource> getPostDetails(final String resourceHash, final String userName) {
		return execute(new GetPostDetailsQuery(userName, resourceHash));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Resource> List<Post<T>> getPosts(final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final FilterEntity filter, final Order order, Date startDate, Date endDate, final int start, final int end) {
		// TODO: clientside chain of responsibility
		final GetPostsQuery query = new GetPostsQuery(start, end);
		query.setGrouping(grouping, groupingName);
		query.setResourceHash(hash);
		query.setResourceType(resourceType);
		query.setTags(tags);
		return (List) execute(query);
	}

	@Override
	public Tag getTagDetails(final String tagName) {
		return execute(new GetTagDetailsQuery(tagName));
	}

	@Override
	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final String regex, final TagSimilarity relation, final Order order, Date startDate, Date endDate, final int start, final int end) {
		final GetTagsQuery query = new GetTagsQuery(start, end);
		query.setGrouping(grouping, groupingName);
		query.setFilter(regex);
		return execute(query);
	}

	@Override
	public User getUserDetails(final String userName) {
		return execute(new GetUserDetailsQuery(userName));
	}

	@Override
	public void deleteUserFromGroup(final String groupName, final String userName) {
		execute(new RemoveUserFromGroupQuery(userName, groupName));
	}

	@Override
	public String createGroup(final Group group) {
		return execute(new CreateGroupQuery(group));
	}

	@Override
	public List<String> createPosts(final List<Post<?>> posts) {		
		/*
		 * FIXME: this iteration should be done on the server, i.e.,
		 * CreatePostQuery should support several posts ... although it's
		 * probably not so simple.
		 */
		final List<String> resourceHashes = new LinkedList<String>();
		for (final Post<?> post : posts) {
			resourceHashes.add(execute(new CreatePostQuery(this.authUser.getName(), post)));
		}
		return resourceHashes;
	}

	@Override
	public String createUser(final User user) {
		return execute(new CreateUserQuery(user));
	}

	@Override
	public String updateGroup(final Group group, final GroupUpdateOperation operation) {
		switch (operation) {
		case ADD_NEW_USER:
			return execute(new AddUsersToGroupQuery(group.getName(), group.getUsers()));
		default:
			// groups cannot be renamed
			return execute(new ChangeGroupQuery(group.getName(), group));
		}
	}

	@Override
	public List<String> updatePosts(final List<Post<?>> posts, final PostUpdateOperation operation) {
		/*
		 * FIXME: this iteration should be done on the server, i.e.,
		 * CreatePostQuery should support several posts ... although it's
		 * probably not so simple.
		 */
		final List<String> resourceHashes = new LinkedList<String>();
		for (final Post<?> post : posts) {
			// hashes are recalculated by the server
			resourceHashes.add(execute(new ChangePostQuery(this.authUser.getName(), post.getResource().getIntraHash(), post)));
		}
		return resourceHashes;
	}

	@Override
	public String updateUser(final User user, final UserUpdateOperation operation) {
		// accounts cannot be renamed
		return execute(new ChangeUserQuery(user.getName(), user));
	}

	@Override
	public String createDocument(final Document doc, final String resourceHash) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Document getDocument(final String userName, final String fileHash) {
		return null;
	}

	@Override
	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		/*
		 * FIXME: files are stored in /tmp and thus publicly readable! Make
		 * directory configurable!
		 */
		return executeWithCallback(new GetPostDocumentQuery(userName, resourceHash, fileName, "/tmp/"), this.progressCallbackFactory.createDocumentDownloadProgressCallback());
	}

	@Override
	public void deleteDocument(final Document document, final String resourceHash) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createInetAddressStatus(final InetAddress address, final InetAddressStatus status) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteInetAdressStatus(final InetAddress address) {
		throw new UnsupportedOperationException();
	}

	@Override
	public InetAddressStatus getInetAddressStatus(final InetAddress address) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Tag> getConcepts(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final String regex, final List<String> tags, final ConceptStatus status, final int start, final int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String createConcept(final Tag concept, final GroupingEntity grouping, final String groupingName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String updateConcept(final Tag concept, final GroupingEntity grouping, final String groupingName, final ConceptUpdateOperation operation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteConcept(final String concept, final GroupingEntity grouping, final String groupingName) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void deleteRelation(final String upper, final String lower, final GroupingEntity grouping, final String groupingName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tag getConceptDetails(final String conceptName, final GroupingEntity grouping, final String groupingName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> getUsers(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final UserRelation relation, final String search, final int start, final int end) {
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

	@Override
	public String getClassifierSettings(final ClassifierSettings key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClassifierSettings(final ClassifierSettings key, final String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getClassifiedUserCount(final Classifier classifier, final SpamStatus status, final int interval) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> getClassifiedUsers(final Classifier classifier, final SpamStatus status, final int limit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> getClassifierHistory(final String userName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> getClassifierComparison(final int interval, final int limit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Statistics getPostStatistics(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final FilterEntity filter, final StatisticsConstraint constraint, final Order order, Date startDate, Date endDate, final int start, final int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getOpenIDUser(final String openID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags, final boolean updateRelations) {
		//TODO maybe return 0;
		throw new UnsupportedOperationException();
	}

	@Override
	public int getTagStatistics(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String regex, final ConceptStatus status, Date startDate, Date endDate, final int start, final int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Author> getAuthors(final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final FilterEntity filter, final int start, final int end, final String search) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteUserRelationship(final String sourceUser, final String targetUser, final UserRelation relation, final String tag) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void createUserRelationship(final String sourceUser, final String targetUser, final UserRelation relation, final String tag) {
		/*
		 * Transform UserRelation into String. FIXME: shouldn't we do this in a
		 * nicer way?
		 */
		final String relationType;
		switch (relation) {
		case OF_FRIEND:
			relationType = CreateUserRelationshipQuery.FRIEND_RELATIONSHIP;
			break;
		case FOLLOWER_OF:
			relationType = CreateUserRelationshipQuery.FOLLOWER_RELATIONSHIP;
			break;
		default:
			throw new IllegalArgumentException("Only OF_FRIEND (for friend relations) and FOLLOWER_OF (for followers) are allowed values for the relation param.");
		}
		execute(new CreateUserRelationshipQuery(sourceUser, targetUser, relationType, tag));
	}

	@Override
	public List<User> getUserRelationship(final String sourceUser, final UserRelation relation, final String tag) {
		switch (relation) {
		case FRIEND_OF:
			return execute(new GetFriendsQuery(0, 100, sourceUser, RESTConfig.INCOMING_ATTRIBUTE_VALUE_RELATION));
		case OF_FRIEND:
			return execute(new GetFriendsQuery(0, 100, sourceUser, RESTConfig.OUTGOING_ATTRIBUTE_VALUE_RELATION));
		default:
			throw new UnsupportedOperationException("The user relation " + relation + " is currently not supported.");
		}
	}

	@Override
	public int createBasketItems(final List<Post<? extends Resource>> posts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteBasketItems(final List<Post<? extends Resource>> posts, final boolean clearAll) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int deleteInboxMessages(final List<Post<? extends Resource>> posts, final boolean clearInbox) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUsernameByLdapUserId(final String userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createReferences(final String postHash, final Set<String> references) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteReferences(final String postHash, final Set<String> references) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Date> getWikiVersions(final String userName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Wiki getWiki(final String userName, final Date date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createWiki(final String userName, final Wiki wiki) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateWiki(final String userName, final Wiki wiki) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteWiki(final String userName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createExtendedField(final Class<? extends Resource> resourceType, final String userName, final String intraHash, final String key, final String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteExtendedField(final Class<? extends Resource> resourceType, final String userName, final String intraHash, final String key, final String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, List<String>> getExtendedFields(final Class<? extends Resource> resourceType, final String userName, final String intraHash, final String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createDiscussionItem(final String interHash, final String username, final DiscussionItem comment) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateDiscussionItem(final String username, final String interHash, final DiscussionItem discussionItem) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void deleteDiscussionItem(final String username, final String interHash, final String commentHash) {
		throw new UnsupportedOperationException();

	}

	@Override
	public List<DiscussionItem> getDiscussionSpace(final String interHash) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createSyncService(final SyncService service, final boolean server) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSyncService(final URI service, final boolean server) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<URI> getSyncServices(final boolean server) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createSyncServer(final String userName, final SyncService server) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateSyncServer(final String userName, final SyncService server) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteSyncServer(final String userName, final URI service) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SyncService> getSyncService(final String userName, final URI service, final boolean server) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SynchronizationPost> getSyncPosts(final String userName, final Class<? extends Resource> resourceType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date syncDate, final SynchronizationStatus status, final String info) {
		this.execute(new ChangeSyncStatusQuery(service.toString(), resourceType, null, null, status, info));
	}

	@Override
	public void deleteSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date syncDate) {
		this.execute(new DeleteSyncDataQuery(service.toString(), resourceType, syncDate, null, null));
	}

	@Override
	public SynchronizationData getLastSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType) {
		return this.execute(new GetLastSyncDataQuery(service.toString(), resourceType, null, null));
	}

	@Override
	public List<SynchronizationPost> getSyncPlan(final String userName, final URI service, final Class<? extends Resource> resourceType, final List<SynchronizationPost> clientPosts, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		return this.execute(new CreateSyncPlanQuery(service.toString(), clientPosts, resourceType, strategy, direction));
	}

	@Override
	public List<SyncService> getAllSyncServices(final boolean server) {
		throw new UnsupportedOperationException();
	}

}