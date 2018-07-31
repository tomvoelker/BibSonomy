/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.logic.util;

import java.net.InetAddress;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.SyncSettingsUpdateOperation;
import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ReadOnlyDatabaseException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ProjectQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.metadata.PostMetaData;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.user.remote.RemoteUserId;

/**
 * an adapter for a {@link LogicInterface} that only supports read only methods
 *
 * @author dzo
 */
public class ReadOnlyLogic implements LogicInterface {
	
	private final LogicInterface logicinterface;

	/**
	 * @param logicinterface
	 */
	public ReadOnlyLogic(LogicInterface logicinterface) {
		super();
		this.logicinterface = logicinterface;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#getPosts(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, java.lang.String, org.bibsonomy.common.enums.SearchType, java.util.Set, org.bibsonomy.model.enums.Order, java.util.Date, java.util.Date, int, int)
	 */
	@Override
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, SearchType searchType, Set<Filter> filters, Order order, Date startDate, Date endDate, int start, int end) {
		return this.logicinterface.getPosts(resourceType, grouping, groupingName, tags, hash, search, searchType, filters, order, startDate, endDate, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#getPostDetails(java.lang.String, java.lang.String)
	 */
	@Override
	public Post<? extends Resource> getPostDetails(String resourceHash, String userName) throws ResourceMovedException, ObjectNotFoundException {
		return this.logicinterface.getPostDetails(resourceHash, userName);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#deletePosts(java.lang.String, java.util.List)
	 */
	@Override
	public void deletePosts(String userName, List<String> resourceHashes) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#getPostMetaData(org.bibsonomy.common.enums.HashID, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<PostMetaData> getPostMetaData(HashID hashType, String resourceHash, String userName, String metaDataPluginKey) {
		return this.logicinterface.getPostMetaData(hashType, resourceHash, userName, metaDataPluginKey);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#createPosts(java.util.List)
	 */
	@Override
	public List<String> createPosts(List<Post<? extends Resource>> posts) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#updatePosts(java.util.List, org.bibsonomy.common.enums.PostUpdateOperation)
	 */
	@Override
	public List<String> updatePosts(List<Post<? extends Resource>> posts, PostUpdateOperation operation) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#getPostStatistics(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.util.Set, org.bibsonomy.model.enums.Order, java.util.Date, java.util.Date, int, int)
	 */
	@Override
	public Statistics getPostStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, Set<Filter> filters, Order order, Date startDate, Date endDate, int start, int end) {
		return this.logicinterface.getPostStatistics(resourceType, grouping, groupingName, tags, hash, search, filters, order, startDate, endDate, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.GoldStandardPostLogicInterface#createRelations(java.lang.String, java.util.Set, org.bibsonomy.model.enums.GoldStandardRelation)
	 */
	@Override
	public void createRelations(String postHash, Set<String> references, GoldStandardRelation relation) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.GoldStandardPostLogicInterface#deleteRelations(java.lang.String, java.util.Set, org.bibsonomy.model.enums.GoldStandardRelation)
	 */
	@Override
	public void deleteRelations(String postHash, Set<String> references, GoldStandardRelation relation) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.DiscussionLogicInterface#createDiscussionItem(java.lang.String, java.lang.String, org.bibsonomy.model.DiscussionItem)
	 */
	@Override
	public void createDiscussionItem(String interHash, String username, DiscussionItem discussionItem) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.DiscussionLogicInterface#updateDiscussionItem(java.lang.String, java.lang.String, org.bibsonomy.model.DiscussionItem)
	 */
	@Override
	public void updateDiscussionItem(String username, String interHash, DiscussionItem discussionItem) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.DiscussionLogicInterface#deleteDiscussionItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteDiscussionItem(String username, String interHash, String discussionItemHash) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.DiscussionLogicInterface#getDiscussionSpace(java.lang.String)
	 */
	@Override
	public List<DiscussionItem> getDiscussionSpace(String interHash) {
		return this.logicinterface.getDiscussionSpace(interHash);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getAutoSyncServer()
	 */
	@Override
	public List<SyncService> getAutoSyncServer() {
		return this.logicinterface.getAutoSyncServer();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncServiceDetails(java.net.URI)
	 */
	@Override
	public SyncService getSyncServiceDetails(URI serviceURI) {
		return this.logicinterface.getSyncServiceDetails(serviceURI);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncServices(boolean, java.lang.String)
	 */
	@Override
	public List<SyncService> getSyncServices(boolean server, String sslDn) {
		return this.logicinterface.getSyncServices(server, sslDn);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncServiceSettings(java.lang.String, java.net.URI, boolean)
	 */
	@Override
	public List<SyncService> getSyncServiceSettings(String userName, URI service, boolean server) {
		return this.logicinterface.getSyncServiceSettings(userName, service, server);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#createSyncService(org.bibsonomy.model.sync.SyncService, boolean)
	 */
	@Override
	public void createSyncService(SyncService service, boolean server) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#deleteSyncService(java.net.URI, boolean)
	 */
	@Override
	public void deleteSyncService(URI service, boolean server) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#createSyncServer(java.lang.String, org.bibsonomy.model.sync.SyncService)
	 */
	@Override
	public void createSyncServer(String userName, SyncService server) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#updateSyncServer(java.lang.String, org.bibsonomy.model.sync.SyncService)
	 */
	@Override
	public void updateSyncServer(String userName, SyncService server, SyncSettingsUpdateOperation operation) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#deleteSyncServer(java.lang.String, java.net.URI)
	 */
	@Override
	public void deleteSyncServer(String userName, URI service) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncPosts(java.lang.String, java.lang.Class)
	 */
	@Override
	public List<SynchronizationPost> getSyncPosts(String userName, Class<? extends Resource> resourceType) {
		return this.logicinterface.getSyncPosts(userName, resourceType);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#updateSyncData(java.lang.String, java.net.URI, java.lang.Class, java.util.Date, org.bibsonomy.model.sync.SynchronizationStatus, java.lang.String, java.util.Date)
	 */
	@Override
	public void updateSyncData(String userName, URI service, Class<? extends Resource> resourceType, Date syncDate, SynchronizationStatus status, String info, Date newDate) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#deleteSyncData(java.lang.String, java.net.URI, java.lang.Class, java.util.Date)
	 */
	@Override
	public void deleteSyncData(String userName, URI service, Class<? extends Resource> resourceType, Date syncDate) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getLastSyncData(java.lang.String, java.net.URI, java.lang.Class)
	 */
	@Override
	public SynchronizationData getLastSyncData(String userName, URI service, Class<? extends Resource> resourceType) {
		return this.logicinterface.getLastSyncData(userName, service, resourceType);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.sync.SyncLogicInterface#getSyncPlan(java.lang.String, java.net.URI, java.lang.Class, java.util.List, org.bibsonomy.model.sync.ConflictResolutionStrategy, org.bibsonomy.model.sync.SynchronizationDirection)
	 */
	@Override
	public List<SynchronizationPost> getSyncPlan(String userName, URI service, Class<? extends Resource> resourceType, List<SynchronizationPost> clientPosts, ConflictResolutionStrategy strategy, SynchronizationDirection direction) {
		return this.logicinterface.getSyncPlan(userName, service, resourceType, clientPosts, strategy, direction);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getAuthenticatedUser()
	 */
	@Override
	public User getAuthenticatedUser() {
		return this.logicinterface.getAuthenticatedUser();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUsers(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, org.bibsonomy.model.enums.Order, org.bibsonomy.common.enums.UserRelation, java.lang.String, int, int)
	 */
	@Override
	public List<User> getUsers(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, UserRelation relation, String search, int start, int end) {
		return this.logicinterface.getUsers(resourceType, grouping, groupingName, tags, hash, order, relation, search, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDeletedGroupUsers(int, int)
	 */
	@Override
	public List<User> getDeletedGroupUsers(int start, int end) {
		return this.logicinterface.getDeletedGroupUsers(start, end);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUserStatistics(org.bibsonomy.common.enums.GroupingEntity, java.util.Set, org.bibsonomy.common.enums.Classifier, org.bibsonomy.common.enums.SpamStatus, java.util.Date, java.util.Date)
	 */
	@Override
	public Statistics getUserStatistics(GroupingEntity grouping, Set<Filter> filters, Classifier classifier, SpamStatus status, Date startDate, Date endDate) {
		return this.logicinterface.getUserStatistics(grouping, filters, classifier, status, startDate, endDate);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUserDetails(java.lang.String)
	 */
	@Override
	public User getUserDetails(String userName) {
		return this.logicinterface.getUserDetails(userName);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getWikiVersions(java.lang.String)
	 */
	@Override
	public List<Date> getWikiVersions(String userName) {
		return this.logicinterface.getWikiVersions(userName);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getWiki(java.lang.String, java.util.Date)
	 */
	@Override
	public Wiki getWiki(String userName, Date date) {
		return this.logicinterface.getWiki(userName, date);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createWiki(java.lang.String, org.bibsonomy.model.Wiki)
	 */
	@Override
	public void createWiki(String userName, Wiki wiki) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateWiki(java.lang.String, org.bibsonomy.model.Wiki)
	 */
	@Override
	public void updateWiki(String userName, Wiki wiki) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getGroups(boolean, int, int)
	 */
	@Override
	public List<Group> getGroups(boolean pending, String userName, int start, int end) {
		return this.logicinterface.getGroups(pending, null, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getGroupDetails(java.lang.String)
	 */
	@Override
	public Group getGroupDetails(String groupName, final boolean pending) {
		return this.logicinterface.getGroupDetails(groupName, pending);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getTags(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String, org.bibsonomy.common.enums.TagSimilarity, org.bibsonomy.model.enums.Order, java.util.Date, java.util.Date, int, int)
	 */
	@Override
	public List<Tag> getTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, String regex, TagSimilarity relation, Order order, Date startDate, Date endDate, int start, int end) {
		return this.logicinterface.getTags(resourceType, grouping, groupingName, tags, hash, search, regex, relation, order, startDate, endDate, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getAuthors(org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, org.bibsonomy.model.enums.Order, org.bibsonomy.common.enums.FilterEntity, int, int, java.lang.String)
	 */
	@Override
	public List<Author> getAuthors(GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		return this.logicinterface.getAuthors(grouping, groupingName, tags, hash, order, filter, start, end, search);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getTagDetails(java.lang.String)
	 */
	@Override
	public Tag getTagDetails(String tagName) {
		return this.logicinterface.getTagDetails(tagName);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getTagRelation(int, int, org.bibsonomy.common.enums.TagRelation, java.util.List)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public List<Tag> getTagRelation(int start, int end, TagRelation relation, List<String> tagNames) {
		return this.logicinterface.getTagRelation(start, end, relation, tagNames);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateTags(org.bibsonomy.model.User, java.util.List, java.util.List, boolean)
	 */
	@Override
	public int updateTags(User user, List<Tag> tagsToReplace, List<Tag> replacementTags, boolean updateRelations) {
		throwReadOnlyException();
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteUser(java.lang.String)
	 */
	@Override
	public void deleteUser(String userName) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteGroup(java.lang.String)
	 */
	@Override
	public void deleteGroup(String groupName, boolean pending, boolean quickDelete) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createUser(org.bibsonomy.model.User)
	 */
	@Override
	public String createUser(User user) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateUser(org.bibsonomy.model.User, org.bibsonomy.common.enums.UserUpdateOperation)
	 */
	@Override
	public String updateUser(User user, UserUpdateOperation operation) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createGroup(org.bibsonomy.model.Group)
	 */
	@Override
	public String createGroup(Group group) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateGroup(org.bibsonomy.model.Group, org.bibsonomy.common.enums.GroupUpdateOperation, org.bibsonomy.model.GroupMembership)
	 */
	@Override
	public String updateGroup(Group group, GroupUpdateOperation operation, GroupMembership membership) {
		throwReadOnlyException();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#restoreGroup(org.bibsonomy.model.Group)
	 */
	@Override
	public String restoreGroup(final Group group) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createDocument(org.bibsonomy.model.Document, java.lang.String)
	 */
	@Override
	public String createDocument(Document document, String resourceHash) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public Document getDocument(String userName, String fileHash) {
		return this.logicinterface.getDocument(userName, fileHash);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDocument(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Document getDocument(String userName, String resourceHash, String fileName) {
		return this.logicinterface.getDocument(userName, resourceHash, fileName);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDocuments(java.lang.String)
	 */
	@Override
	public List<Document> getDocuments(String userName) {
		return this.logicinterface.getDocuments(userName);
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getDocumentStatistics(org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.Set, java.util.Date, java.util.Date)
	 */
	@Override
	public Statistics getDocumentStatistics(GroupingEntity groupingEntity, String grouping, Set<Filter> filters, Date startDate, Date endDate) {
		return this.logicinterface.getDocumentStatistics(groupingEntity, grouping, filters, startDate, endDate);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteDocument(org.bibsonomy.model.Document, java.lang.String)
	 */
	@Override
	public void deleteDocument(Document document, String resourceHash) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateDocument(java.lang.String, java.lang.String, java.lang.String, org.bibsonomy.model.Document)
	 */
	@Override
	public void updateDocument(String userName, String resourceHash, String documentName, Document document) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createInetAddressStatus(java.net.InetAddress, org.bibsonomy.common.enums.InetAddressStatus)
	 */
	@Override
	public void createInetAddressStatus(InetAddress address, InetAddressStatus status) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getInetAddressStatus(java.net.InetAddress)
	 */
	@Override
	public InetAddressStatus getInetAddressStatus(InetAddress address) {
		return this.logicinterface.getInetAddressStatus(address);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteInetAdressStatus(java.net.InetAddress)
	 */
	@Override
	public void deleteInetAdressStatus(InetAddress address) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getConceptDetails(java.lang.String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public Tag getConceptDetails(String conceptName, GroupingEntity grouping, String groupingName) {
		return this.logicinterface.getConceptDetails(conceptName, grouping, groupingName);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createConcept(org.bibsonomy.model.Tag, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public String createConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateConcept(org.bibsonomy.model.Tag, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, org.bibsonomy.common.enums.ConceptUpdateOperation)
	 */
	@Override
	public String updateConcept(Tag concept, GroupingEntity grouping, String groupingName, ConceptUpdateOperation operation) {
		throwReadOnlyException();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteConcept(java.lang.String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public void deleteConcept(String concept, GroupingEntity grouping, String groupingName) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteRelation(java.lang.String, java.lang.String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String)
	 */
	@Override
	public void deleteRelation(String upper, String lower, GroupingEntity grouping, String groupingName) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getClassifiedUsers(org.bibsonomy.common.enums.Classifier, org.bibsonomy.common.enums.SpamStatus, int)
	 */
	@Override
	public List<User> getClassifiedUsers(Classifier classifier, SpamStatus status, int limit) {
		return this.logicinterface.getClassifiedUsers(classifier, status, limit);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getClassifierSettings(org.bibsonomy.common.enums.ClassifierSettings)
	 */
	@Override
	public String getClassifierSettings(ClassifierSettings key) {
		return this.logicinterface.getClassifierSettings(key);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#updateClassifierSettings(org.bibsonomy.common.enums.ClassifierSettings, java.lang.String)
	 */
	@Override
	public void updateClassifierSettings(ClassifierSettings key, String value) {
		 throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getClassifierHistory(java.lang.String)
	 */
	@Override
	public List<User> getClassifierHistory(String userName) {
		return this.getClassifierHistory(userName);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getClassifierComparison(int, int)
	 */
	@Override
	public List<User> getClassifierComparison(int interval, int limit) {
		return this.logicinterface.getClassifierComparison(interval, limit);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getOpenIDUser(java.lang.String)
	 */
	@Override
	public String getOpenIDUser(String openID) {
		return this.logicinterface.getOpenIDUser(openID);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUsernameByLdapUserId(java.lang.String)
	 */
	@Override
	public String getUsernameByLdapUserId(String userId) {
		return this.logicinterface.getUsernameByLdapUserId(userId);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUsernameByRemoteUserId(org.bibsonomy.model.user.remote.RemoteUserId)
	 */
	@Override
	public String getUsernameByRemoteUserId(RemoteUserId remoteUserId) {
		return this.logicinterface.getUsernameByRemoteUserId(remoteUserId);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createExtendedField(java.lang.Class, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createExtendedField(Class<? extends Resource> resourceType, String userName, String intraHash, String key, String value) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteExtendedField(java.lang.Class, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteExtendedField(Class<? extends Resource> resourceType, String userName, String intraHash, String key, String value) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getExtendedFields(java.lang.Class, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, List<String>> getExtendedFields(Class<? extends Resource> resourceType, String userName, String intraHash, String key) {
		return this.logicinterface.getExtendedFields(resourceType, userName, intraHash, key);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getConcepts(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.common.enums.ConceptStatus, int, int)
	 */
	@Override
	public List<Tag> getConcepts(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		return this.logicinterface.getConcepts(resourceType, grouping, groupingName, regex, tags, status, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getTagStatistics(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, org.bibsonomy.common.enums.ConceptStatus, java.util.Set, java.util.Date, java.util.Date, int, int)
	 */
	@Override
	public int getTagStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String regex, ConceptStatus status, Set<Filter> filters, Date startDate, Date endDate, int start, int end) {
		return this.logicinterface.getTagStatistics(resourceType, grouping, groupingName, tags, regex, status, filters, startDate, endDate, start, end);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getUserRelationship(java.lang.String, org.bibsonomy.common.enums.UserRelation, java.lang.String)
	 */
	@Override
	public List<User> getUserRelationship(String sourceUser, UserRelation relation, String tag) {
		return this.logicinterface.getUserRelationship(sourceUser, relation, tag);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteUserRelationship(java.lang.String, java.lang.String, org.bibsonomy.common.enums.UserRelation, java.lang.String)
	 */
	@Override
	public void deleteUserRelationship(String sourceUser, String targetUser, UserRelation relation, String tag) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createUserRelationship(java.lang.String, java.lang.String, org.bibsonomy.common.enums.UserRelation, java.lang.String)
	 */
	@Override
	public void createUserRelationship(String sourceUser, String targetUser, UserRelation relation, String tag) {
		throwReadOnlyException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#createBasketItems(java.util.List)
	 */
	@Override
	public int createClipboardItems(List<Post<? extends Resource>> posts) {
		throwReadOnlyException();
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteBasketItems(java.util.List, boolean)
	 */
	@Override
	public int deleteClipboardItems(List<Post<? extends Resource>> posts, boolean clearBasket) {
		throwReadOnlyException();
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#deleteInboxMessages(java.util.List, boolean)
	 */
	@Override
	public int deleteInboxMessages(List<Post<? extends Resource>> posts, boolean clearInbox) {
		throwReadOnlyException();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#addResourceRelation(org.bibsonomy.model.ResourcePersonRelation)
	 */
	@Override
	public void addResourceRelation(ResourcePersonRelation resourcePersonRelation) throws ResourcePersonAlreadyAssignedException {
		throwReadOnlyException();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createOrUpdatePerson(org.bibsonomy.model.Person)
	 */
	@Override
	public void createOrUpdatePerson(Person person) {
		throwReadOnlyException();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#updatePerson(org.bibsonomy.model.Person, org.bibsonomy.common.enums.PersonUpdateOperation)
	 */
	@Override
	public void updatePerson(Person person, PersonUpdateOperation operation) {
		throwReadOnlyException();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createPersonName(org.bibsonomy.model.PersonName)
	 */
	@Override
	public void createPersonName(PersonName withPersonId) {
		throwReadOnlyException();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonById(org.bibsonomy.model.enums.PersonIdType, java.lang.String)
	 */
	@Override
	public Person getPersonById(PersonIdType idType, String id) {
		return this.logicinterface.getPersonById(idType, id);
	}
	
	/**
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonByUser(String)
	 */
	public Person getPersonByUser(String userName) {
		return this.logicinterface.getPersonByUser(userName);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public PersonSuggestionQueryBuilder getPersonSuggestion(String queryString) {
		return this.logicinterface.getPersonSuggestion(queryString);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PostLogicInterface#getPublicationSuggestion(java.lang.String)
	 */
	@Override
	public List<Post<BibTex>> getPublicationSuggestion(String queryString) {
		return this.logicinterface.getPublicationSuggestion(queryString);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getResourceRelations()
	 */
	@Override
	public ResourcePersonRelationQueryBuilder getResourceRelations() {
		return this.logicinterface.getResourceRelations();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getTags(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, java.lang.String, org.bibsonomy.common.enums.SearchType, java.lang.String, org.bibsonomy.common.enums.TagSimilarity, org.bibsonomy.model.enums.Order, java.util.Date, java.util.Date, int, int)
	 */
	@Override
	public List<Tag> getTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, SearchType searchType, String regex, TagSimilarity relation, Order order, Date startDate, Date endDate, int start, int end) {
		return this.logicinterface.getTags(resourceType, grouping, groupingName, tags, hash, search, searchType, regex, relation, order, startDate, endDate, start, end);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#linkUser(java.lang.String)
	 */
	@Override
	public void linkUser(String personId) {
		throwReadOnlyException();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removePersonName(java.lang.Integer)
	 */
	@Override
	public void removePersonName(Integer personNameId) {
		throwReadOnlyException();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removeResourceRelation(int)
	 */
	@Override
	public void removeResourceRelation(int resourceRelationId) {
		throwReadOnlyException();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#unlinkUser(java.lang.String)
	 */
	@Override
	public void unlinkUser(String username) {
		throwReadOnlyException();
	}
	
	private static void throwReadOnlyException() throws RuntimeException {
		throw new ReadOnlyDatabaseException();
	}

	/**
	 * @param logicAccess
	 * @param readOnly
	 * @return the mask logic
	 */
	public static LogicInterface maskLogic(LogicInterface logicAccess, boolean readOnly) {
		if (!readOnly) {
			return logicAccess;
		}
		return new ReadOnlyLogic(logicAccess);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getPersonMatches(java.lang.String)
	 */
	@Override
	public List<PersonMatch> getPersonMatches(String personID) {
		return this.logicinterface.getPersonMatches(personID);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#denieMerge(org.bibsonomy.model.PersonMatch)
	 */
	@Override
	public void denieMerge(PersonMatch match) {

	}
	
	@Override
	public boolean acceptMerge(PersonMatch match) {
		return false;
	}
	
	@Override
	public PersonMatch getPersonMatch(int matchID) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#conflictMerge(int, org.json.JSONArray)
	 */
	@Override
	public Boolean conflictMerge(int formMatchId, Map<String, String> map) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getForwardId(java.lang.String)
	 */
	@Override
	public String getForwardId(String personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Project> getProjects(ProjectQueryBuilder builder) {
		return this.logicinterface.getProjects(builder);
	}

	@Override
	public Project getProjectDetails(String projectId) {
		return this.logicinterface.getProjectDetails(projectId);
	}

	@Override
	public JobResult createProject(Project project) {
		throwReadOnlyException();
		return JobResult.buildFailure(null);
	}

	@Override
	public JobResult updateProject(String projectId, Project project) {
		throwReadOnlyException();
		return JobResult.buildFailure(null);
	}

	@Override
	public boolean deleteProject(String projectId) {
		throwReadOnlyException();
		return false;
	}
}
