/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.renderer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlBuilder;

/**
 * This renderer creates URLs according to BibSonomys REST URL scheme.
 *
 * @author rja
 */
public class UrlRenderer {

	/** parameter value for friend relationship */
	public final static String FRIEND_RELATIONSHIP = "friend";

	private final String apiUrl;

	/**
	 * creates a new url renderer
	 * @param apiUrl
	 */
	public UrlRenderer(final String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * creates a URL which points to the given user.
	 *
	 * @param name - the name of the user.
	 * @return A URL which points to the given user.
	 */
	public String createHrefForUser(final String name) {
		return this.getUrlBuilderForUser(name).asString();
	}

	/**
	 * creates a URL which points to the given tag.
	 *
	 * @param tag - the name of the tag.
	 * @return A URL which points to the given tag.
	 */
	public String createHrefForTag(final String tag) {
		return this.getUrlBuilderForTag(tag).asString();
	}

	/** Creates a URL which points to the given group.
	 *
	 * @param name - the name of the group.
	 * @return A URL which points to the given group.
	 */
	public String createHrefForGroup(final String name) {
		return this.getUrlBuilderForGroup(name).asString();
	}

	/**
	 * @param name
	 * @return a urlbuilder for the group url
	 */
	protected UrlBuilder getUrlBuilderForGroup(final String name) {
		final UrlBuilder builder = this.getUrlBuilderForGroups();
		builder.addPathElement(name);
		return builder;
	}

	/** Creates a URL which points to the given resource.
	 *
	 * @param userName - the name of the user which owns the resource.
	 * @param intraHash - the intra hash of the resource.
	 * @return A URL which points to the given resource.
	 */
	public String createHrefForResource(final String userName, final String intraHash) {
		return this.getUrlBuilderForUserPost(userName, intraHash).asString();
	}

	/**
	 * creates a URL which points to the given document attached to the given resource.
	 *
	 * @param userName - the name of the user which owns the resource (and document).
	 * @param intraHash - the intrahash of the resource.
	 * @param documentFileName - the name of the document.
	 * @return A URL which points to the given document.
	 */
	public String createHrefForResourceDocument(final String userName, final String intraHash, final String documentFileName) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForPostDocuments(userName, intraHash);
		urlBuilder.addPathElement(documentFileName);
		return urlBuilder.asString();
	}

	/**
	 *
	 * @param userName
	 * @param intraHash
	 * @return the url to all documents of the specified post
	 */
	public String createHrefForResourceDocuments(final String userName, final String intraHash) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForPostDocuments(userName, intraHash);
		return urlBuilder.asString();
	}

	/**
	 *
	 * @param userName
	 * @param intraHash
	 * @return the url builder for documents of the specified post
	 */
	protected UrlBuilder createUrlBuilderForPostDocuments(final String userName, final String intraHash) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForUserPost(userName, intraHash);
		urlBuilder.addPathElement(RESTConfig.DOCUMENTS_SUB_PATH);
		return urlBuilder;
	}

	/**
	 * @param userName
	 * @param intraHash
	 * @return the url builder for
	 */
	protected UrlBuilder getUrlBuilderForUserPost(final String userName, final String intraHash) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForUserPosts(userName);
		urlBuilder.addPathElement(intraHash);
		return urlBuilder;
	}

	/**
	 * @param userName
	 * @return the url to all user's posts
	 */
	public String createHrefForUserPosts(final String userName) {
		return this.createUrlBuilderForUserPosts(userName).asString();
	}

	protected UrlBuilder createUrlBuilderForUserPosts(final String userName) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForUser(userName);
		urlBuilder.addPathElement(RESTConfig.POSTS_URL);
		return urlBuilder;
	}

	/**
	 * @return a builder for the base api path
	 */
	public UrlBuilder createUrlBuilderForApi() {
		return new UrlBuilder(this.apiUrl);
	}

	/**
	 * @param userName the name of the user
	 * @return the urlbuilder for the specified user path
	 */
	public UrlBuilder getUrlBuilderForUser(final String userName) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForUsers();
		urlBuilder.addPathElement(userName);
		return urlBuilder;
	}
	
	/**
	 * @param userName
	 * @param tagString
	 * @param resourceType
	 * @return
	 */
	public UrlBuilder getUrlBuilderForUser(String userName, String tagString, Class<? extends Resource> resourceType) {
		final UrlBuilder builder = this.getUrlBuilderForUser(userName);
		
		if (tagString != null) {
			builder.addParameter(RESTConfig.TAGS_PARAM, tagString);
		}
		
		if (resourceType != Resource.class) {
			builder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}
		return builder;
	}

	/**
	 * @param tag
	 * @return the urlbuilder for the specified tag
	 */
	protected UrlBuilder getUrlBuilderForTag(final String tag) {
		final UrlBuilder urlBuilder = createURLBuilderForTags();
		urlBuilder.addPathElement(tag);
		return urlBuilder;
	}

	/**
	 *
	 * @return The API URL currently used to render URLs.
	 */
	public String getApiUrl() {
		return this.createUrlBuilderForApi().asString();
	}

	/**
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 * @param syncDate
	 * @param status
	 * @return the href for Sync
	 */
	public String createHrefForSync(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction, final Date syncDate, final SynchronizationStatus status) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForSync(serviceURI, resourceType, strategy, direction, syncDate, status);
		return urlBuilder.asString();
	}

	/**
	 *
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 * @param syncDate
	 * @param status
	 * @return the builder for sync
	 */
	public UrlBuilder createUrlBuilderForSync(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction, final Date syncDate, final SynchronizationStatus status) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForApi();
		urlBuilder.addPathElement(RESTConfig.SYNC_URL);
		urlBuilder.addPathElement(serviceURI);
		if (present(resourceType)) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}
		if (present(strategy)) {
			urlBuilder.addParameter(RESTConfig.SYNC_STRATEGY_PARAM, strategy.getConflictResolutionStrategy());
		}
		if (present(direction)) {
			urlBuilder.addParameter(RESTConfig.SYNC_DIRECTION_PARAM, direction.getSynchronizationDirection());
		}

		if (present(syncDate)) {
			urlBuilder.addParameter(RESTConfig.SYNC_DATE_PARAM, RESTConfig.serializeDate(syncDate));
		}

		if (present(status)) {
			urlBuilder.addParameter(RESTConfig.SYNC_STATUS, status.toString());
		}
		return urlBuilder;
	}

	/**
	 * @param grouping
	 * @param groupingName
	 * @param conceptName
	 * @return
	 */
	public String createHrefForConcept(final GroupingEntity grouping, final String groupingName, final String conceptName) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForConcept(grouping, groupingName, conceptName);
		return urlBuilder.toString();
	}

	/**
	 *
	 * @param grouping
	 * @param groupingName
	 * @param conceptName
	 * @return  a url builder for the specified concept
	 */
	protected UrlBuilder createUrlBuilderForConcept(final GroupingEntity grouping, final String groupingName, final String conceptName) {
		final UrlBuilder urlBuilder;
		switch (grouping) {
		case USER:
			urlBuilder = this.getUrlBuilderForUser(groupingName);
			break;
		case GROUP:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not implemented yet");
			// urlBuilder = this.getUrlBuilderForGroup(groupingName);
			// break;
		case ALL:
			urlBuilder = this.createUrlBuilderForApi();
			break;
		default:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not available for concept details query");
		}
		urlBuilder.addPathElement(RESTConfig.CONCEPTS_URL);
		urlBuilder.addPathElement(conceptName);
		return urlBuilder;
	}



	/**
	 * @param grouping
	 * @param groupingName
	 * @param conceptName
	 * @param subTag
	 * @return
	 */
	public String createHrefForConceptWithSubTag(final GroupingEntity grouping, final String groupingName, final String conceptName, final String subTag) {
		final UrlBuilder builder = this.createUrlBuilderForConcept(grouping, groupingName, conceptName);

		if (subTag != null) {
			builder.addParameter(RESTConfig.SUB_TAG_PARAM, subTag);
		}
		return builder.asString();
	}

	/**
	 * @param groupname
	 * @return the path to all members of a group
	 */
	public String createHrefForGroupMembers(final String groupname) {
		final UrlBuilder builder = this.createUrlBuilderForGroupMembers(groupname);
		return builder.asString();
	}

	/**
	 * @param groupname
	 * @param start
	 * @param end
	 * @return the path to all members of a group
	 */
	public String createHrefForGroupMembers(final String groupname, final int start, final int end) {
		final UrlBuilder builder = this.createUrlBuilderForGroupMembers(groupname);
		applyStartEnd(builder, start, end);
		return builder.asString();
	}

	protected static void applyStartEnd(final UrlBuilder builder, final int start, final int end) {
		builder.addParameter(RESTConfig.START_PARAM, String.valueOf(start));
		builder.addParameter(RESTConfig.END_PARAM, String.valueOf(end));
	}

	/**
	 * @param groupname
	 * @return the url builder
	 */
	public UrlBuilder createUrlBuilderForGroupMembers(final String groupname) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForGroup(groupname);
		urlBuilder.addPathElement(RESTConfig.USERS_URL);
		return urlBuilder;
	}

	/**
	 * @param start
	 * @param end
	 * @return the api url for a list of users
	 */
	public String createHrefForUsers(final int start, final int end) {
		final UrlBuilder builder = this.createUrlBuilderForUsers();
		applyStartEnd(builder, start, end);
		return builder.asString();
	}

	/**
	 * @return the url bulder
	 */
	public UrlBuilder createUrlBuilderForUsers() {
		final UrlBuilder builder = this.createUrlBuilderForApi();
		builder.addPathElement(RESTConfig.USERS_URL);
		return builder;
	}

	/**
	 * @param groupName
	 * @param userName
	 * @return the href to the group member
	 */
	public String createHrefForGroupMember(final String groupName, final String userName) {
		final UrlBuilder builder = this.createUrlBuilderForGroupMembers(groupName);
		builder.addPathElement(userName);
		return builder.asString();
	}

	/**
	 * @param grouping
	 * @param groupingName
	 * @param status
	 * @param resourceType
	 * @param tags
	 * @param regex
	 * @return
	 */
	public String createHrefForConcepts(final GroupingEntity grouping, final String groupingName, final ConceptStatus status, final Class<? extends Resource> resourceType, final List<String> tags, final String regex) {
		UrlBuilder urlBuilder;

		switch (grouping) {
		case USER:
			urlBuilder = this.getUrlBuilderForUser(groupingName);
			urlBuilder.addPathElement(RESTConfig.CONCEPTS_URL);
			break;
		case GROUP:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not implemented yet");
			//url = URL_GROUPS + "/" + this.groupingName + "/" + URL_CONCEPTS;
			//break;
		case ALL:
			urlBuilder = this.createUrlBuilderForApi();
			urlBuilder.addPathElement(RESTConfig.CONCEPTS_URL);
			break;
		default:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not available for concept query");
		}

		if (status != null) {
			urlBuilder.addParameter(RESTConfig.CONCEPT_STATUS_PARAM, status.toString().toLowerCase());
		}

		if (resourceType != null) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}

		if (regex != null) {
			urlBuilder.addParameter(RESTConfig.REGEX_PARAM, regex);
		}

		if (present(tags)) {
			urlBuilder.addParameter(RESTConfig.TAGS_PARAM, StringUtils.appendDelimited(new StringBuilder(), tags, "+").toString());
		}
		return urlBuilder.asString();
	}

	/**
	 * @return the url builder
	 */
	public UrlBuilder createUrlBuilderForFriends() {
		final UrlBuilder urlBuilder = this.createUrlBuilderForApi();
		urlBuilder.addPathElement(RESTConfig.FRIENDS_SUB_PATH);
		return urlBuilder;
	}

	/**
	 * @param username
	 * @param relation
	 * @param start
	 * @param end
	 * @return the users friends
	 */
	public String createHrefForFriends(final String username, final String relation, final int start, final int end) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForUser(username);
		urlBuilder.addPathElement(RESTConfig.FRIENDS_SUB_PATH);
		urlBuilder.addParameter(RESTConfig.ATTRIBUTE_KEY_RELATION, relation);
		applyStartEnd(urlBuilder, start, end);
		return urlBuilder.asString();
	}

	/**
	 * @param username
	 * @param relation
	 * @param start
	 * @param end
	 * @return the users friends
	 */
	public String createHrefForUserRelationship(final String username, final String relation, final String tag) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForUser(username);
		final String friendOrFollower = FRIEND_RELATIONSHIP.equals(relation) ? RESTConfig.FRIENDS_SUB_PATH : RESTConfig.FOLLOWERS_SUB_PATH;
		present(tag);
		urlBuilder.addPathElement(friendOrFollower);
		urlBuilder.addParameter(RESTConfig.ATTRIBUTE_KEY_RELATION, relation);
		return urlBuilder.asString();
	}

	/**
	 * @return the groups overview url
	 */
	public String createHrefForGroups() {
		final UrlBuilder builder = this.getUrlBuilderForGroups();
		return builder.asString();
	}

	/**
	 * @param start
	 * @param end
	 * @return the groups overview url
	 */
	public String createHrefForGroups(final int start, final int end) {
		final UrlBuilder builder = this.getUrlBuilderForGroups();
		applyStartEnd(builder, start, end);
		return builder.asString();
	}

	/**
	 * @return url builder for groups
	 */
	public UrlBuilder getUrlBuilderForGroups() {
		final UrlBuilder builder = this.createUrlBuilderForApi();
		builder.addPathElement(RESTConfig.GROUPS_URL);
		return builder;
	}

	/**
	 * @param userName
	 * @param resourceHash
	 * @return
	 */
	public String createHrefForClipboadEntry(final String userName,
			final String resourceHash) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForClipboard(userName);
		urlBuilder.addPathElement(resourceHash);
		return urlBuilder.asString();
	}

	/**
	 * @param userName
	 * @return
	 */
	private UrlBuilder getUrlBuilderForClipboard(final String userName) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForUser(userName);
		urlBuilder.addPathElement(RESTConfig.CLIPBOARD_SUBSTRING);
		return urlBuilder;
	}

	/**
	 * @param userName
	 * @param clearAll
	 * @return the clipboard url for the specified user
	 */
	public String createHrefForClipboard(final String userName, final Boolean clearAll) {
		final UrlBuilder urlBuilder = this.getUrlBuilderForClipboard(userName);
		if (present(clearAll)) {
			urlBuilder.addParameter("clear", String.valueOf(clearAll));
		}
		return urlBuilder.asString();
	}
	
	/**
	 * creates the href for a community post
	 * @param hash
	 * @return href for the community post
	 */
	public String createHrefForCommunityPost(final String hash) {
		final UrlBuilder builder = this.createHrefForCommunity(hash);
		return builder.asString();
	}

	/**
	 * @param hash
	 * @param relation
	 * @return the path to the references of a community post
	 */
	public String createHrefForCommunityPostReferences(final String hash, final GoldStandardRelation relation) {
		final UrlBuilder builder = this.createHrefForCommunity(hash);
		switch (relation) {
		case REFERENCE:
			builder.addPathElement(RESTConfig.RELATION_REFERENCE);
			break;
		case PART_OF:
			builder.addPathElement(RESTConfig.RELATION_PARTOF);
			break;
		default:
			throw new IllegalArgumentException("relation " + relation + " not supported");
		}
		return builder.asString();
	}

	protected UrlBuilder createHrefForCommunity(final String hash) {
		final UrlBuilder builder = this.createUrlBuilderForPosts();
		builder.addPathElement(RESTConfig.COMMUNITY_SUB_PATH);
		builder.addPathElement(hash);
		return builder;
	}

	/**
	 * @return the added posts path
	 */
	public String createHrefForAddedPosts() {
		final UrlBuilder builder = this.createUrlBuilderForPostAdded();
		return builder.asString();
	}

	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param start
	 * @param end
	 * @return
	 */
	public String createHrefForAddedPosts(final GroupingEntity grouping, final String groupingValue, final Class<? extends Resource> resourceType, final int start, final int end) {
		final UrlBuilder builder = this.createUrlBuilderForPostAdded();
		applyStandardPostQueryParams(grouping, groupingValue, resourceType, start, end, builder);
		return builder.asString();
	}

	protected static void applyStandardPostQueryParams(final GroupingEntity grouping, final String groupingValue, final Class<? extends Resource> resourceType, final int start, final int end, final UrlBuilder builder) {
		applyStartEnd(builder, start, end);

		if (resourceType != Resource.class) {
			builder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, resourceType.toString().toLowerCase());
		}

		applyGrouping(builder, grouping, groupingValue);
	}

	protected UrlBuilder createUrlBuilderForPostAdded() {
		final UrlBuilder builder = this.createUrlBuilderForPosts();
		builder.addPathElement(RESTConfig.POSTS_ADDED_SUB_PATH);
		return builder;
	}

	protected UrlBuilder createUrlBuilderForPosts() {
		final UrlBuilder builder = this.createUrlBuilderForApi();
		builder.addPathElement(RESTConfig.POSTS_URL);
		return builder;
	}

	/**
	 * @param builder
	 * @param grouping
	 * @param groupingValue
	 */
	private static void applyGrouping(final UrlBuilder builder, final GroupingEntity grouping, final String groupingValue) {
		switch (grouping) {
		case USER:
			builder.addParameter("user", groupingValue);
			break;
		case GROUP:
			builder.addParameter("group", groupingValue);
			break;
		case VIEWABLE:
			builder.addParameter("viewable", groupingValue);
			break;
		default:
			break;
		}
	}
	
	/**
	 * @return the post popular path
	 */
	public String createHrefForPopularPosts() {
		final UrlBuilder builder = this.createUrlBuilderForPostPopular();
		return builder.asString();
	}

	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param start
	 * @param end
	 * @return
	 */
	public String createHrefForPopularPosts(final GroupingEntity grouping, final String groupingValue, final Class<? extends Resource> resourceType, final int start, final int end) {
		final UrlBuilder builder = this.createUrlBuilderForPostPopular();
		applyStandardPostQueryParams(grouping, groupingValue, resourceType, start, end, builder);
		return builder.asString();
	}

	/**
	 * @return
	 */
	private UrlBuilder createUrlBuilderForPostPopular() {
		final UrlBuilder builder = this.createUrlBuilderForPosts();
		builder.addPathElement(RESTConfig.POSTS_POPULAR_SUB_PATH);
		return builder;
	}
	
	/**
	 * @return the base path for global posts
	 */
	public String createHrefForPosts() {
		final UrlBuilder builder = this.createUrlBuilderForPosts();
		return builder.asString();
	}

	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param tags
	 * @param resourceHash
	 * @param search
	 * @param order
	 * @param start
	 * @param end
	 * @return
	 */
	public String createHrefForPosts(final GroupingEntity grouping,
			final String groupingValue, final Class<? extends Resource> resourceType,
			final List<String> tags, final String resourceHash, final String search, final Order order,
			final int start, final int end) {
		final UrlBuilder urlBuilder = createUrlBuilderForPosts(grouping, groupingValue, resourceType, tags, resourceHash, search, order);

		applyStartEnd(urlBuilder, start, end);
		return urlBuilder.asString();
	}

	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param tags
	 * @param resourceHash
	 * @param search
	 * @param order
	 * @return
	 */
	public UrlBuilder createUrlBuilderForPosts(final GroupingEntity grouping, final String groupingValue, final Class<? extends Resource> resourceType, final List<String> tags, final String resourceHash, final String search, final Order order) {
		final UrlBuilder urlBuilder = this.createUrlBuilderForPosts();

		applyPostParamsToBuilder(grouping, groupingValue, resourceType, tags, resourceHash, search, order, urlBuilder);
		return urlBuilder;
	}

	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param tags
	 * @param resourceHash
	 * @param search
	 * @param order
	 * @param urlBuilder
	 */
	private void applyPostParamsToBuilder(final GroupingEntity grouping, final String groupingValue, final Class<? extends Resource> resourceType, final List<String> tags, final String resourceHash, final String search, final Order order, final UrlBuilder urlBuilder) {
		if (resourceType != Resource.class) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}

		final String groupingParameterName = this.getGroupingParameterName(grouping);
		if (groupingParameterName != null) {
			urlBuilder.addParameter(groupingParameterName, groupingValue);
		}

		if (present(tags)) {
			final StringBuilder tagsStringBuilder = new StringBuilder();
			for (final String tag : tags) {
				tagsStringBuilder.append(tag).append(' ');
			}
			tagsStringBuilder.setLength(tagsStringBuilder.length() - 1);
			urlBuilder.addParameter(RESTConfig.TAGS_PARAM, tagsStringBuilder.toString());
		}

		if (present(resourceHash)) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_PARAM, resourceHash);
		}

		if (order != null) {
			urlBuilder.addParameter(RESTConfig.ORDER_PARAM, order.toString());
		}

		if (present(search)) {
			urlBuilder.addParameter(RESTConfig.SEARCH_PARAM, search);
		}
	}
	
	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param tags
	 * @param hash
	 * @param search
	 * @param order
	 * @return
	 */
	public UrlBuilder createUrlBuilderForAddedPosts(GroupingEntity grouping, String groupingValue, Class<? extends Resource> resourceType, List<String> tags, String hash, String search, Order order) {
		final UrlBuilder builder = this.createUrlBuilderForPostAdded();
		this.applyPostParamsToBuilder(grouping, groupingValue, resourceType, tags, hash, search, order, builder);
		return builder;
	}
	
	/**
	 * @param grouping
	 * @param groupingValue
	 * @param resourceType
	 * @param tags
	 * @param hash
	 * @param search
	 * @param order
	 * @return
	 */
	public UrlBuilder createUrlBuilderForPopularPosts(GroupingEntity grouping, String groupingValue, Class<? extends Resource> resourceType, List<String> tags, String hash, String search, Order order) {
		final UrlBuilder builder = this.createUrlBuilderForPostPopular();
		this.applyPostParamsToBuilder(grouping, groupingValue, resourceType, tags, hash, search, order, builder);
		return builder;
	}

	public String getGroupingParameterName(final GroupingEntity grouping) {
		String groupingParameterName;
		switch (grouping) {
		case USER:
			groupingParameterName = "user";
			break;
		case GROUP:
			groupingParameterName = "group";
			break;
		case VIEWABLE:
			groupingParameterName = "viewable";
			break;
		case ALL:
			groupingParameterName = null;
			break;
		case FRIEND:
			groupingParameterName = "friend";
			break;
			// CLIPBOARD is already handled separately and therefore not covered here
		default:
			throw new UnsupportedOperationException("The grouping " + grouping + " is currently not supported by this query.");
		}
		return groupingParameterName;
	}
	
	/**
	 * @return the base path for urls
	 */
	public String createHrefForTags() {
		final UrlBuilder urlBuilder = createURLBuilderForTags();
		return urlBuilder.asString();
	}

	/**
	 * @return
	 */
	private UrlBuilder createURLBuilderForTags() {
		final UrlBuilder urlBuilder = this.createUrlBuilderForApi();
		urlBuilder.addPathElement(RESTConfig.TAGS_URL);
		return urlBuilder;
	}

	/**
	 * @param resourceType
	 * @param tagNames
	 * @param grouping
	 * @param groupingValue
	 * @param filter
	 * @param relation
	 * @param order
	 * @param start
	 * @param end
	 * @return
	 */
	public String createHrefForTags(final Class<? extends Resource> resourceType, final List<String> tagNames, final GroupingEntity grouping, final String groupingValue, final String filter, final TagRelation relation, final Order order, final int start, final int end) {
		final UrlBuilder urlBuilder = createURLBuilderForTags();
		if (present(tagNames)) {
			urlBuilder.addPathElement(StringUtils.implodeStringCollection(tagNames, "+"));
		}
		applyStartEnd(urlBuilder, start, end);

		if (order != null) {
			urlBuilder.addParameter(RESTConfig.ORDER_PARAM, order.toString());
		}

		applyGrouping(urlBuilder, grouping, groupingValue);

		if (present(filter)) {
			urlBuilder.addParameter(RESTConfig.FILTER_PARAM, filter);
		}

		if (resourceType != null && resourceType != Resource.class) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}

		if (present(relation)) {
			// add relation parameter.
			urlBuilder.addParameter(RESTConfig.RELATION_PARAM, relation.toString());
		}

		return urlBuilder.asString();
	}

	/**
	 * @param resourceType
	 * @param grouping
	 * @param groupingValue
	 * @param hash
	 * @param regex
	 * @param order
	 * @return
	 */
	public UrlBuilder createUrlBuilderForTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingValue, String hash, String regex, Order order) {
		final UrlBuilder builder = this.createURLBuilderForTags();
		
		if (grouping != GroupingEntity.ALL && groupingValue != null) {
			builder.addParameter(grouping.toString().toLowerCase(), groupingValue);
		}
		if (regex != null) {
			builder.addParameter(RESTConfig.REGEX_PARAM, regex);
		}
		if (order == Order.FREQUENCY) {
			builder.addParameter(RESTConfig.ORDER_PARAM, order.toString().toLowerCase());
		}
		if (resourceType != Resource.class) {
			builder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceUtils.toString(resourceType).toLowerCase());
		}
		if (hash != null) {
			builder.addParameter(RESTConfig.RESOURCE_PARAM, hash);
		}
		
		return builder;
	}
}
