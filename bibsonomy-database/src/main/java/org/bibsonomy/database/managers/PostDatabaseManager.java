package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.params.ResourcesParam;
import org.bibsonomy.database.params.SingleResourceParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.lucene.LuceneSearch;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.SimHash;

/**
 * TODO: rename count methods???
 * Used to create, read, update and delete posts from the database.
 * 
 * @author dzo
 * 
 * @version $Id$
 * @param <R> the resource
 * @param <P> the param
 */
public abstract class PostDatabaseManager<R extends Resource, P extends ResourcesParam<R> & SingleResourceParam<R>> extends AbstractDatabaseManager implements CrudableContent<R, P> {

	protected enum Action {
		/**
		 * a resource was created
		 */
		CREATE,
		/**
		 * a resource was updated
		 */
		UPDATE,
		/**
		 * a resource was deleted
		 */
		DELETE
	}

	private static final Log log = LogFactory.getLog(PostDatabaseManager.class);
	
	protected final GeneralDatabaseManager generalDb;
	protected final TagDatabaseManager tagDb;
	protected final DatabasePluginRegistry plugins;
	protected final PermissionDatabaseManager permissionDb;
	protected final String resourceClassName;
	
	/**
	 * inits the database managers and resource class name
	 */
	protected PostDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
		this.permissionDb = PermissionDatabaseManager.getInstance();
		this.resourceClassName = this.getResourceClassName();
	}

	@SuppressWarnings("unchecked")
	protected List<Post<R>> postList(final String query, final P param, final DBSession session) {
		return queryForList(query, param, session);
	}
	
	/**
     * TODO: method without param
	 * <em>/concept/tag/TAGNAME</em> --
	 * 
	 * @param param
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsByConceptByTag(final P param, final DBSession session) {
		return this.postList("get" + this.resourceClassName + "ByConceptByTag", param, session);
	}
	
	
	/**
	 * TODO: remove param method
	 * TODO: get conceptName from param
	 * 
	 * @param param
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsByConceptForGroup(final P param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ByConceptForGroup", param, session);
	}

//	/**
//	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
//	 * 
//	 * On the <em>/tag</em> page only public entries are shown (groupType must
//	 * be set to public) which have all of the given tags attached. On the
//	 * <em>/viewable/</em> page only posts are shown which are set viewable to
//	 * the given group and which have all of the given tags attached.
//	 * @param groupType
//	 * @param tagIndex
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return a list of posts
//	 */
//	public List<Post<R>> getPostsByTagNames(final GroupID groupType, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setGroupType(groupType);
//		param.setTagIndex(tagIndex);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		return this.getPostsByTagNames(param, session);
//	}	

//	/**
//	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
//	 * 
//	 * This method prepares queries which retrieve all resources for a given
//	 * user name (requestedUser) and given tags.<br/>
//	 * 
//	 * Additionally the group to be shown can be restricted. The queries are
//	 * built in a way, that not only public posts are retrieved, but also
//	 * friends or private or other groups, depending upon if userName us allowed
//	 * to see them.
//	 * 
//	 * @param requestedUserName 
//	 * @param userName 
//	 * @param tagIndex 
//	 * @param groupId 
//	 * @param visibleGroupIDs 
//	 * @param limit 
//	 * @param offset 
//	 * @param session 
//	 * @return list of resource posts
//	 */
//	public List<Post<R>> getPostsByTagNamesForUser(final String requestedUserName, final String userName, final List<TagIndex> tagIndex, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setRequestedUserName(requestedUserName);
//		param.setUserName(userName);
//		param.setTagIndex(tagIndex);
//		param.setGroupId(groupId);
//		param.setGroups(visibleGroupIDs);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
//		return this.postList("get" + this.resourceClassName + "ByTagNamesForUser", param, session);
//	}

	/**
	 * Counts the number of visible posts for a given list of tags
	 * 
	 * @param tagIndex a list of tags
	 * @param session DB session
	 * @param groupId is the group id
	 * @return the number of visible posts
	 */
	public Integer getPostsByTagNamesCount(final List<TagIndex> tagIndex, final int groupId, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupId(groupId);
		param.setTagIndex(tagIndex);
		
		return this.queryForObject("get" + this.resourceClassName + "ByTagNamesCount", param, Integer.class, session);
	}
	
	/**
	 * TODO: test me
	 * 
	 * Retrieves the number of resource items tagged by the tags present in tagIndex by user requestedUserName
	 * being visible to the logged in user
	 * 
	 * @param requestedUserName
	 * 			owner of the resource items
	 * @param loginUserName
	 * 			logged in user
	 * @param tagIndex
	 * 			a list of tags
	 * @param visibleGroupIDs
	 * 			a list of groupIDs the logged in user is member of
	 * @param session
	 * 			DB session
	 * @return the corresponding number of visible resource items
	 */
	public Integer getPostsByTagNamesForUserCount(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session) {
		final P param = this.getNewParam();
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setTagIndex(tagIndex);
		
		return this.queryForObject("get" + this.resourceClassName + "ByTagNamesForUserCount", param, Integer.class, session);
	}
	
//	/**
//	 * <em>/concept/user/MaxMustermann/EinTag</em><br/><br/>
//	 * 
//	 * This method prepares queries which retrieve all bookmarks for a given
//	 * user name (requestedUser) and given tags. The tags are interpreted as
//	 * supertags and the queries are built in a way that they results reflect
//	 * the semantics of
//	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 p. 91,
//	 * formular (4).<br/>
//	 * 
//	 * Additionally the group to be shown can be restricted. The queries are
//	 * built in a way, that not only public posts are retrieved, but also
//	 * friends or private or other groups, depending upon if userName us allowed
//	 * to see them.
//	 * 
//	 * @see PostDatabaseManager#getPostsByConceptForUser(ResourcesParam, DBSession)
//	 * 
//	 * @param loginUser
//	 * @param requestedUserName
//	 * @param visibleGroupIDs 
//	 * @param tagIndex
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsByConceptForUser(final String loginUser, final String requestedUserName, final List<Integer> visibleGroupIDs, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setUserName(loginUser);
//		param.setRequestedUserName(requestedUserName);
//		param.setGroups(visibleGroupIDs);
//		param.setTagIndex(tagIndex);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
//		return this.postList("get" + this.resourceClassName + "ByConceptForUser", param, session);
//	}
//	
//	/**
//	 * TODO: remove me!
//	 * <em>/concept/group/GruppenName/EinTag</em><br/><br/>
//	 * 
//	 * This method retrieves all bookmarks of all group members of the given
//	 * group which are tagged at least with one of the concept tags or its
//	 * subtags
//	 * 
//	 * @param param
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsByConceptForGroup(final P param, final DBSession session) {
//		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
//		return this.postList("get" + this.resourceClassName + "ByConceptForGroup", param, session);
//	}
//	
//	/**
//	 * TODO: add hash ID???
//	 * <em>/friends</em><br/><br/>
//	 * 
//	 * Prepares queries which show all posts of users which have currUser as
//	 * their friend.
//	 * 
//	 * @param user
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsByUserFriends(final String user, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setUserName(user);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		param.setGroupType(GroupID.FRIENDS);
//		return this.postList("get" + this.resourceClassName + "ByUserFriends", param, session);
//	}
	
	/**
	 * TODO: test me
	 * TODO: refactor use other {@link PostDatabaseManager#getPostsPopular(int, int, int, HashID, DBSession)}}?!?
	 * 
	 * This method prepares queries which retrieve all resources for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 * 
	 * @param limit 
	 * @param offset 
	 * @param hashId 
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsPopular(final int limit, final int offset, final HashID hashId, final DBSession session) {
		final P param = this.getNewParam();
		param.setOffset(offset);
		param.setLimit(limit);
		param.setSimHash(hashId);
		
		return this.postList("get" + this.resourceClassName + "Popular", param, session);
	}
	
	/**
	 * TODO: improve docs
	 * TODO: test me (bibtexs)
	 * 
	 * @param days
	 * @param limit
	 * @param offset
	 * @param hashId
	 * @param session 
	 * 
	 * @return list of posts
	 */
	public List<Post<R>> getPostsPopular(final int days, final int limit, final int offset, final HashID hashId, final DBSession session) {
		final P param = this.getNewParam();
		param.setDays(days);
		param.setOffset(offset);
		param.setLimit(limit);
		param.setSimHash(hashId);
		
		return this.postList("get" + this.resourceClassName + "Popular", param, session);
	}
	
	/**
	 * TODO: write tests
	 * 
	 * @param days
	 * @param session
	 * @return the number of days when a post was popular
	 */
	public int getPostPopularDays(final int days, final DBSession session){
		final P param = this.getNewParam();
		param.setDays(days);

		final Integer result = this.queryForObject("get" + this.resourceClassName + "PopularDays", param, Integer.class, session);
		
		return result == null ? 0 : result;
	}
	
//	/**
//	 * TODO: remove me!!!
//	 * This method prepares queries which retrieve all resources for the home
//	 * page of BibSonomy. These are typically the X last posted entries. Only
//	 * public posts are shown.
//	 * 
//	 * @param param
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsForHomepage(final P param, final DBSession session) {
//		if (FilterEntity.UNFILTERED.equals(param.getFilter())) {
//			return this.postList("get" + this.resourceClassName + "ForHomepageUnfiltered", param, session);
//		}
//		return this.postList("get" + this.resourceClassName + "ForHomepage", param, session);
//	}
//	
//	/**
//	 * TODO: add FilterEntity
//	 * @see PostDatabaseManager#getPostsForHomepage(ResourcesParam, DBSession)
//	 * 
//	 * @param groupType
//	 * @param limit
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsForHomepage(final GroupID groupType, final int limit, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setGroupType(groupType);
//		param.setLimit(limit);
//		return this.postList("get" + this.resourceClassName + "ForHomepage", param, session);
//	}
//
	
//	/**
//	 * Prepares a query which retrieves all bookmarks which are represented by
//	 * the given hash. Retrieves only public bookmarks!
//	 * 
//	 * @param requResource
//	 * @param groupType
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsByHash(final String requResource, final GroupID groupType, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setHash(requResource);
//		param.setGroupType(groupType);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		return this.postList("get" + this.resourceClassName + "ByHash", param, session);
//	}

//	/**
//	 * TODO move me to BibtexManager??
//	 * TODO improve docs
//	 * 
//	 * @param hash
//	 * @param hashId
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of bibtex posts
//	 */
//	public List<Post<R>> getPostsByHash(final String hash, final HashID hashId, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setSimHash(hashId);
//		param.setHash(hash);
//		param.setGroupType(GroupID.PUBLIC);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		return this.postList("get" + this.resourceClassName + "ByHash", param, session);
//	}
	
	/**
	 * Retrieves the number of posts represented by the given hash.
	 * 
	 * @param requHash 
	 * @param simHash 
	 * @param session
	 * @return number of posts for the given hash
	 */
	public Integer getPostsByHashCount(final String requHash, final HashID simHash, final DBSession session) {
		final P param = this.getNewParam();
		param.setHash(requHash);
		param.setSimHash(simHash);
		
		return this.queryForObject("get" + this.resourceClassName + "ByHashCount", param, Integer.class, session);
	}
	
	/**
	 * @param requHash 
	 * @param simHash 
	 * @param userName
	 * @param session
	 * @return number of resources for the given hash and a user
	 */
	public Integer getPostsByHashAndUserCount(final String requHash, final HashID simHash, final String userName, final DBSession session) {
		final P param = this.getNewParam();
		param.setHash(requHash);
		param.setSimHash(simHash);
		param.setUserName(userName);
		
		return this.queryForObject("get" + this.resourceClassName + "ByHashAndUserCount", param, Integer.class, session);
	}
	
	/**
	 * Prepares a query which retrieves the resources (which is represented by
	 * the given hash) for a given user. Since user name is given, full group
	 * checking is done, i.e. everbody who may see the resoucre will see it.
	 * 
	 * @param userName
	 * @param requHash
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param hashType
	 * @param session
	 * @return list of resource posts
	 */
	public List<Post<R>> getPostsByHashForUser(final String userName, final String requHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final HashID hashType, final DBSession session) {
		final P param = this.getNewParam();
		param.setUserName(userName);
		param.setRequestedUserName(requestedUserName);
		param.addGroups(visibleGroupIDs);
		param.setHash(requHash);
		param.setSimHash(hashType);
		
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ByHashForUser", param, session);
	}
	
//	/**
//	 * TODO:
//	 * Returns a list with resource posts identified by INTER-hash for a given
//	 * user
//	 * 
//	 * @param userName
//	 * @param requBibtex
//	 * @param requestedUserName
//	 * @param visibleGroupIDs
//	 * @param session
//	 * @return see at param method
//	 */
//	@Deprecated
//	public List<Post<R>> getHashForUser(final String userName, final String requBibtex, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
//		return this.getPostsByHashForUser(userName, requBibtex, requestedUserName, visibleGroupIDs, session, HashID.INTER_HASH);
//	}
	
	/**
	 * <em>/search/ein+lustiger+satz</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table.<br/>
	 * The search string, as given by the user will be mangled up in the method
	 * to do what the user expects (AND searching). Unfortunately this also
	 * destroys some other features (e.g. <em>phrase searching</em>).<br/>
	 * 
	 * If requestedUser is given, only (public) posts from the given user are
	 * searched. Otherwise all (public) posts are searched.
	 * 
	 * @param groupId
	 * @param search
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsSearch(final int groupId, final String search, final String requestedUserName, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.postList("get" + this.resourceClassName + "Search", param, session);
	}

	/**
	 * <em>/search/ein+lustiger+satz+group%3AmyGroup</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table with the requested group<br/>
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param search
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsSearchForGroup(final int groupId, final List<Integer> visibleGroupIDs, final String search, final String userName, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);
		
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "SearchForGroup", param, session);
	}

	/**
	 * FIXME: check implementation
	 * TODO: improve documentation
	 * 
	 * @param groupId
	 * @param search
	 * @param requestedUserName
	 * @param UserName 
	 * @param GroupNames 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsSearchLucene(final int groupId, final String search, final String requestedUserName, final String UserName, final Set<String> GroupNames,  final int limit, final int offset, final DBSession session) {
		final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
		final String group = groupDb.getGroupNameByGroupId(groupId, session);
		
		// get search results from lucene
		final LuceneSearch<R> lucene = this.getLuceneSearch();

//		ArrayList<Integer> contentIds = new ArrayList<Integer>();
		long starttimeQuery = System.currentTimeMillis();

		// FIXME : lucene integration
//		contentIds = lucene.searchLucene("contentid", search, groupId, limit, offset);
		final List<Post<R>> postpostList = lucene.searchLucene(group, search, requestedUserName, UserName, GroupNames, limit, offset);
		
		long endtimeQuery = System.currentTimeMillis();
		log.debug("Lucene" + this.resourceClassName + " complete query time: " + (endtimeQuery-starttimeQuery) + "ms");
		
//		long starttimeTable = System.currentTimeMillis();
//		LuceneHelper luceneTTable = new LuceneHelper();
//		// create temp. table
//		luceneTTable.createTTable(session);
//
//		// delete all content in temp. table
//		luceneTTable.truncateTTable(session);
//
//		// store content ids in temp. table
//		luceneTTable.fillTTable(contentIds, session);
//		long endtimeTable = System.currentTimeMillis();
//		LOGGER.debug("LuceneBookmark: filled temp. table with requested lucene ids in " + (endtimeTable-starttimeTable) + "ms");
//
//
//		return this.postList("getBookmarkSearchLucene", param, session);
		return postpostList;
	}
	
	/**
	 * Returns the number of bookmarks for a given search.
	 * 
	 * @param groupType
	 * @param search
	 * @param requestedUserName
	 * @param session
	 * @return number of bookmarks for a given search
	 */
	public Integer getPostsSearchCount(final GroupID groupType, final String search, final String requestedUserName, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupType(groupType);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		
		return this.queryForObject("get" + this.resourceClassName + "SearchCount", param, Integer.class, session);
	}

//	/**
//	 * <em>/viewable/EineGruppe</em><br/><br/>
//	 * 
//	 * Prepares queries to retrieve posts which are set viewable to group.
//	 * 
//	 * @param groupId
//	 * @param userName
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsViewable(final int groupId, final String userName, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setGroupId(groupId);
//		param.setUserName(userName);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
//			// show users own bookmarks, which are private, public or for friends
//			param.setRequestedUserName(param.getUserName());
//			return this.getPostsForUser(param, session);
//		}
//		
//		return this.postList("get" + this.resourceClassName + "Viewable", param, session);
//	}
//
//	/**
//	 * <em>/group/EineGruppe</em><br/><br/>
//	 * 
//	 * Prepares queries which show all bookmarks of all users belonging to the
//	 * group. This is an aggregated view of all posts of the group members.<br/>
//	 * Full viewable-for checking is done, i.e. everybody sees everything he is
//	 * allowed to see.<br/>
//	 * 
//	 * See also
//	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
//	 * 92, formula (9) for formal semantics of this query.
//	 * 
//	 * @param groupId
//	 * @param visibleGroupIDs 
//	 * @param userName
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsForGroup(final int groupId, final List<Integer> visibleGroupIDs, final String userName, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setGroupId(groupId);
//		param.setGroups(visibleGroupIDs);
//		param.setUserName(userName);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
//		return this.postList("get" + this.resourceClassName + "ForGroup", param, session);
//	}
//
	/**
	 * Returns the number of bookmarks belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends posts
	 * and friends posts are not included (same for publications)
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 * 
	 * @param requestedUserName 
	 * @param userName 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param session
	 * @return the (approximated) number of resources for the given group, see method above
	 */
	public Integer getPostsForGroupCount(final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		final P param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setGroups(visibleGroupIDs);
		param.setGroupId(groupId);
		
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.queryForObject("get" + this.resourceClassName + "ForGroupCount", param, Integer.class, session);
	}
	
	/**
	 * TODO: name!!
	 * TODO: test me
	 * TODO: improve docs
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param limit 
	 * @param offset 
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsForMyGroupPosts(final String requestedUserName, final String loginUserName, final int limit, final int offset, final List<Integer> visibleGroupIDs, final DBSession session) {
		final P param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);
		
		return this.postList("get" + this.resourceClassName + "ForMyGroupPosts", param, session);
	}
	
	/**
	 * TODO: test me!!!
	 * TODO: improve docs
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsForMyGroupPostsByTag(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final int limit, final int offset, final List<Integer> visibleGroupIDs, final DBSession session){
		final P param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);

		return this.postList("get" + this.resourceClassName + "ForMyGroupPostsByTag", param, session);
	}

//	/**
//	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
//	 * 
//	 * Does basically the same as getPostsForGroup with the additionaly
//	 * possibility to restrict the tags the posts have to have.
//	 * 
//	 * @param groupId
//	 * @param visibleGroupIDs 
//	 * @param userName
//	 * @param tagIndex
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsForGroupByTag(final int groupId, final List<Integer> visibleGroupIDs, final String userName,  List<TagIndex> tagIndex, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setGroupId(groupId); 
//		param.setGroups(visibleGroupIDs);
//		param.setUserName(userName);
//		param.setTagIndex(tagIndex);
//		
//		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
//		return this.postList("get" + this.resourceClassName + "ForGroupByTag", param, session);
//	}
//	
//	/**
//	 * <em>/user/MaxMustermann</em><br/><br/>
//	 * 
//	 * This method prepares queries which retrieve all bookmarks for a given
//	 * user name (requestedUserName). Additionally the group to be shown can be
//	 * restricted. The queries are built in a way, that not only public posts
//	 * are retrieved, but also friends or private or other groups, depending
//	 * upon if userName is allowed to see them.
//	 * 
//	 * @param userName
//	 * @param requestedUserName
//	 * @param groupId
//	 * @param visibleGroupIDs 
//	 * @param limit
//	 * @param offset
//	 * @param session
//	 * @return list of posts
//	 */
//	public List<Post<R>> getPostsForUser(final String userName, final String requestedUserName, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
//		final P param = this.getNewParam();
//		param.setUserName(userName);
//		param.setRequestedUserName(requestedUserName);
//		param.setGroupId(groupId);
//		param.setGroups(visibleGroupIDs);
//		param.setLimit(limit);
//		param.setOffset(offset);
//		
//		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
//		return this.postList("get" + this.resourceClassName + "ForUser", param, session);
//	}
//
	/**
	 * Returns the number of bookmarks for a given user.
	 * 
	 * @param requestedUserName 
	 * @param userName 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return the number of bookmarks of the requested User which the logged in user is allowed to see
	 * 
	 * groupId or
	 * visibleGroupIDs && userName && (userName != requestedUserName)
	 */
	public Integer getPostsForUserCount(final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		final P param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session); // set groups
		return this.queryForObject("get" + this.resourceClassName + "ForUserCount", param, Integer.class, session);
	}
	
	/**
	 * Get Bookmarks of users which the logged-in users is following.
	 * 
	 * @param loginUserName - 
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsByFollowedUsers(final String loginUserName, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.postList("get" + this.resourceClassName + "ByFollowedUsers", param, session);
	}

	/**
	 * TODO: name : Id oder ID???!
	 * Returns a contentId for a given bookmark.
	 * 
	 * @param hash
	 * @param userName
	 * @param session
	 * @return contentId for a given bookmark
	 */
	public Integer getContentIDForPost(final String hash, final String userName, final DBSession session) {
		if (!present(hash) || !present(userName)) {
			throw new RuntimeException("Hash and user name must be set");
		}
		final P param = this.getNewParam();
		param.setHash(hash);
		param.setUserName(userName);
		
		return this.queryForObject("getContentIDFor" + this.resourceClassName, param, Integer.class, session);
	}
	
	/**
	 * TODO: test me!!
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param tagIndex
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of posts that are available for some groups and tagged by a tag of the tagIndex
	 */
	public int getGroupPostsCountByTag(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session){			
		final P param = this.getNewParam();
		param.setTagIndex(tagIndex);
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);

		return (Integer) this.queryForObject("getGroup" + this.resourceClassName + "CountByTag", param, session);
	}
	
	/**
	 * TODO: tests!!
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of posts that are available for some groups
	 */
	public int getGroupPostsCount(final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){
		final P param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);
		
		return (Integer) this.queryForObject("getGroup" + this.resourceClassName + "Count", param, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.CrudableContent#getPostss(org.bibsonomy.database.params.P, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public List<Post<R>> getPosts(P param, DBSession session) {
		return this.getChain().getFirstElement().perform(param, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.CrudableContent#getPostsDetails(java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public Post<R> getPostDetails(final String authUser, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		final List<Post<R>> list = this.getPostsByHashForUser(authUser, resourceHash, userName, visibleGroupIDs, HashID.INTRA_HASH, session);
		
		if (list.isEmpty()) {
			log.debug(this.resourceClassName + "-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' not found");
			return null;
		}
		
		if (list.size() > 1) {
			log.warn("multiple " + this.resourceClassName + "-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
		}
		
		return list.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.CrudableContent#storePost(java.lang.String, org.bibsonomy.model.Post, java.lang.String, boolean, org.bibsonomy.database.util.DBSession)
	 */
	@Override	
	public boolean storePost(String userName, Post<R> post, String oldIntraHash, boolean update, DBSession session) {
		/*
		 * FIXME: we need to overwrite the userName in the post with the given userName
		 * (which comes from loginUser.getName() in DBLogic) - otherwise one can store
		 * posts under another name! 
		 */
		session.beginTransaction();
		try {
			/*
			 * the current intra hash of the resource
			 */
			final String intraHash = post.getResource().getIntraHash();

			/*
			 * the resource with the "old" intrahash, i.e. the one that was sent
			 * within the create/update resource request
			 */
			final List<Post<R>> oldPostsInDB;
			if (present(oldIntraHash)) {
				/*
				 * check if the hash sent within the request is correct
				 */
				if (!update && !oldIntraHash.equals(intraHash)) {
					throw new IllegalArgumentException(
							"Could not create new " + this.resourceClassName + ": The requested intrahash " 
							+ oldIntraHash + " is not correct for this " + this.resourceClassName + " (correct intrahash is " 
							+ intraHash + ")."
					);
				}
				// if yes, check if a post exists with the old intrahash				
				oldPostsInDB = this.getPostsByHashForUser(userName, oldIntraHash, userName, new ArrayList<Integer>(), HashID.INTRA_HASH, session);
			} else {
				if (update) {
					throw new IllegalArgumentException("Could not update post: no intrahash specified.");
				}
				oldPostsInDB = null;
			}

			/*
			 * get posts with the intrahash of the given post to check for possible duplicates 
			 */
			final List<Post<R>> newPostInDB = this.getPostsByHashForUser(userName, intraHash, userName, new ArrayList<Integer>(), HashID.INTRA_HASH, session);

			/*
			 * check if user is trying to create a resource that already exists
			 */
			if (present(newPostInDB)) {
				/*
				 * new resource exists ... 
				 */
				if (!update) {
					/*
					 * we don't do an update, so this is not allowed
					 */
					throw new IllegalArgumentException(
							"Could not create new " + this.resourceClassName + ": This " + this.resourceClassName +
							" already exists in your collection (intrahash: " + intraHash + ")");
				} else if (!intraHash.equals(oldIntraHash)) {
					/* 
					 * Although we're doing an update, the old intra hash is different from the new one
					 * in principle, this is OK, but not when the new hash already exists. Because that
					 * way we would delete the post with the old hash and post the new one - resulting
					 * in two posts with the same (new hash)
					 */
					throw new IllegalArgumentException("Could not create new bookmark: This bookmark already exists in your collection (intrahash: " + intraHash + ")");
				}

			}

			/*
			 * ALWAYS get a new contentId
			 */
			post.setContentId(this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, session));
			
			/*
			 * on update, do a delete first ...
			 */
			if (update) {
				if (present(oldPostsInDB)) {
					/*
					 * Resource entry DOES EXIST for this user -> delete old post 
					 */
					final Post<?> oldPost = oldPostsInDB.get(0);
					
					this.informPlugin(Action.UPDATE, post.getContentId(), oldPost.getContentId(), session);
					
					this.deletePost(userName, oldPost.getResource().getIntraHash(), true, session);
				} else {
					/*
					 * not found -> throw exception
					 */
					log.warn(this.resourceClassName + " with hash " + oldIntraHash + " does not exist for user " + userName);
					throw new ResourceNotFoundException(oldIntraHash);
				}
			}

			this.insertPost(post, session);

			// add the tags
			this.tagDb.insertTags(post, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
		return update;
	}
	
	/**
	 * inserts a post into the database
	 *
	 * @param post		the post to insert
	 * @param session
	 */
	protected void insertPost(final Post<R> post, final DBSession session) {
		if (!present(post.getResource())) throw new InvalidModelException("There is no resource for this post.");
		if (!present(post.getGroups())) throw new InvalidModelException("There are no groups for this post.");
		/*if (post.getGroups().contains(GroupID.PUBLIC) && post.getGroups().size() > 1) throw new InvalidModelException("Invalid constilation of groups for this post.");
		if (post.getGroups().contains(GroupID.PRIVATE) && post.getGroups().size() > 1) throw new InvalidModelException("Invalid constilation of groups for this post.");*/
		
		final P param = this.getInsertParam(post, session);
		
		// insert
		this.insertPost(param, session);
	}

	/**
	 * inserts a new post in db
	 * 
	 * @param param
	 * @param session
	 */
	protected void insertPost(final P param, final DBSession session) {
		session.beginTransaction();
		try {
			// Insert resource
			this.insert("insert" + this.resourceClassName, param, session);
			// Insert/Update SimHashes
			this.insertOrUpdatePostHash(param, session, false);
						
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * TODO: check this method
	 * inserts or updates the post hashes for the given resource (in param)
	 * 
	 * @param param
	 * @param session
	 * @param delete
	 */
	protected void insertOrUpdatePostHash(final P param, final DBSession session, final boolean delete) {
		for (final HashID hashId : this.getHashRange()) {
			final String hash = SimHash.getSimHash(param.getResource(), hashId);
			// no action on an empty hash
			if (present(hash)) {
				// XXX: BookmarkManager uses insert param and Bibtex uses new param ??!?
				param.setSimHash(hashId);
				param.setHash(hash);

				if (delete == true) {
					// decrement counter
					this.update("update" + this.resourceClassName + "Hash", param, session);
				} else {
					// insert new hash or increment its counter, if it already exists
					this.insert("insert" + this.resourceClassName + "Hash", param, session);
				}
			} 				
		}
	}

	/**
	 * @return the hash range of the resource
	 */
	protected abstract HashID[] getHashRange();

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.CrudableContent#deletePost(java.lang.String, java.lang.String, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public boolean deletePost(String userName, String resourceHash, DBSession session) {
		return this.deletePost(userName, resourceHash, false, session);
	}
	
	/**
	 * deletes a post from the database
	 * 
	 * @param userName
	 * @param resourceHash	the hash of the resource of the post to delete
	 * @param update		true if its called by {@link PostDatabaseManager#storePost(String, Post, String, boolean, DBSession)}
	 * @param session
	 * @return true iff the post was deleted successfully
	 */
	protected boolean deletePost(String userName, String resourceHash, boolean update, DBSession session) {
		// Used for userName, hash and contentId
		final P param = this.getNewParam();
		param.setRequestedUserName(userName);
		param.setUserName(userName);
		param.setHash(resourceHash);
		
		final List<Post<R>> posts = this.getPostsByHashForUser(userName, resourceHash, userName, new ArrayList<Integer>(), HashID.INTRA_HASH, session);
		
		if (posts.isEmpty()) {
			log.debug("post with hash \"" + resourceHash + "\" not found");
			return false;
		}
		// XXX: was on top but when post not found => endTransaction() wasn't called
		session.beginTransaction();
			
		try {
			final Post<R> onePost = posts.get(0);
			param.setRequestedContentId(onePost.getContentId());
			
			if (update == false) {
				this.informPlugin(Action.DELETE, onePost.getContentId(), Integer.MIN_VALUE, session);
			}
			
			this.tagDb.deleteTags(onePost, session);
			
			this.insertOrUpdatePostHash(param, session, true);
			
			// this.deletePost(param, session);
			this.delete("delete" + this.resourceClassName, param, session);
			
			session.commitTransaction();
			
		} finally {
			session.endTransaction();
		}
		
		return true;
	}

	/**
	 * @return the chain element
	 */
	protected abstract FirstChainElement<Post<R>, P> getChain();
	
	/**
	 * @return the class of the first generic param (<R>, Resource)
	 */
	private Class<?> getResourceClass() {
	   final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
	   return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}
	
	/**
	 * @return the simple class name of the first generic param (<R>, Resource)
	 */
	private String getResourceClassName() {
		return this.getResourceClass().getSimpleName();
	}
	
	/**
	 * this method is triggered by the store and delete methods to inform the plugins
	 * TODO: abstract???
	 * 
	 * @param action
	 * @param newContentId 
	 * @param oldContentId 
	 * @param session 
	 */
	protected abstract void informPlugin(final Action action, Integer newContentId, Integer oldContentId, final DBSession session);
	
	/** 
	 * @return a new <P> param
	 */
	protected abstract P getNewParam();
	
	/**
	 * @param post
	 * @param session
	 * @return new param for insert a resource
	 */
	protected abstract P getInsertParam(final Post<R> post, final DBSession session);
	
	/**
	 * @return the lucene search instance to use
	 */
	protected abstract LuceneSearch<R> getLuceneSearch();
}
