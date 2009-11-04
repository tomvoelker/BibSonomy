package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.ResourcesParam;
import org.bibsonomy.database.params.SingleResourceParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.SimHash;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * TODO: rename count methods???
 * Used to create, read, update and delete posts from the database.
 * 
 * TODO: document the conventions
 * - sql ids
 * TODO: some methods are using int as type for groupId and some use GroupId
 * TODO: some params using setGroupType
 * 
 * 
 * @author dzo
 * 
 * @version $Id$
 * @param <R> the resource
 * @param <P> the param
 */
public abstract class PostDatabaseManager<R extends Resource, P extends ResourcesParam<R> & SingleResourceParam<R>> extends AbstractDatabaseManager implements CrudableContent<R, P> {
	private static final Log log = LogFactory.getLog(PostDatabaseManager.class);
	
	/** database managers */
	protected final GeneralDatabaseManager generalDb;
	protected final TagDatabaseManager tagDb;
	protected final DatabasePluginRegistry plugins;
	protected final PermissionDatabaseManager permissionDb;
	
	/** simple class name of the resource managed by the class */
	protected final String resourceClassName;
		
	/** instance of the lucene searcher */
	private ResourceSearch<R> resourceSearch; 
	
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

	/**
	 * TODO document me
	 * @param query
	 * @param param
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Post<R>> postList(final String query, final P param, final DBSession session) {
		return this.queryForList(query, param, session);
	}
	
	/**
	 * TODO: remove me
	 * 
	 * @param param
	 * @param session
	 * @return
	 */
	public List<Post<R>> getPostsByConceptByTag(final P param, final DBSession session) {
		return this.postList("get" + this.resourceClassName + "ByConceptByTag", param, session);
	}
	
	
	/**
	 * @param param
	 * @param session
	 * @return a lists of Posts of type R with the inbox content
	 */
	public List<Post<R>> getPostsFromInbox(final P param, final DBSession session) {
		return this.postList("get" + this.resourceClassName + "FromInbox", param, session);
	}
	/**
	 * XXX: add parameters to method?
	 * <em>/concept/tag/TAGNAME</em>
	 * 
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of posts
	 */
	public List<Post<R>> getPostsByConceptByTag(final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setOffset(offset);
		param.setLimit(limit);
		param.setTagIndex(tagIndex);
		
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
	@Deprecated
	public List<Post<R>> getPostsByConceptForGroup(final P param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ByConceptForGroup", param, session);
	}
	
	/**
	 * TODO: add parameters to method?
	 * <em>/concept/group/GruppenName/EinTag</em><br/><br/>
	 * 
	 * This method retrieves all posts of all group members of the given
	 * group which are tagged at least with one of the concept tags or its
	 * subtags
	 * 
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of posts
	 */
	public List<Post<R>> getPostsByConceptForGroup(final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setOffset(offset);
		param.setLimit(limit);
		param.setTagIndex(tagIndex);
		
		// XXX: from param method
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ByConceptForGroup", param, session);
	}
	
	/**
	 * TODO: refactor
	 * 
	 * <em>/concept/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUser) and given tags. The tags are interpreted as
	 * supertags and the queries are built in a way that they results reflect
	 * the semantics of
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 p. 91,
	 * formular (4).<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if userName us allowed
	 * to see them.
	 * 
	 * @param loginUser
	 * @param requestedUserName
	 * @param visibleGroupIDs 
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsByConceptForUser(final String loginUser, final String requestedUserName, final List<Integer> visibleGroupIDs, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setUserName(loginUser);
		param.setRequestedUserName(requestedUserName);
		param.setGroups(visibleGroupIDs);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ByConceptForUser", param, session);
	}

	/** 
	 * XXX: bibtex grouptype and id in xml
	 * 
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached.
	 * @param groupId
	 * @param tagIndex
	 * @param order TODO
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of posts
	 * 
	 */
	public List<Post<R>> getPostsByTagNames(final int groupId, final List<TagIndex> tagIndex, final Order order, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupId(groupId);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		
		if (present(order)) {
			param.setOrder(order);
			
			if (Order.FOLKRANK.equals(param.getOrder())){
				param.setGroupId(GroupID.PUBLIC.getId());
				return this.postList("get" + this.resourceClassName + "ByTagNamesAndFolkrank", param, session);
			}
		}
		
		return this.postList("get" + this.resourceClassName + "ByTagNames", param, session);
	}	

	/**
	 * XXX: added visibleGroupId params (not in bibtex)
	 * 
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all resources for a given
	 * user name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if userName us allowed
	 * to see them.
	 * 
	 * @param requestedUserName 
	 * @param tagIndex 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param limit 
	 * @param offset 
	 * @param session 
	 * @return list of resource posts
	 */
	public List<Post<R>> getPostsByTagNamesForUser(final String requestedUserName, final List<TagIndex> tagIndex, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setTagIndex(tagIndex);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		
		HashID.getSimHash(param.getSimHash()); // XXX: missing in Bookmark Manager 
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ByTagNamesForUser", param, session);
	}

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
	
	/**
	 * XXX: added HashID
	 * 
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have currUser as
	 * their friend.
	 * 
	 * @param user
	 * @param simHash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsByUserFriends(final String user, final HashID simHash, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setUserName(user);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(simHash);
		
		param.setGroupType(GroupID.FRIENDS);
		return this.postList("get" + this.resourceClassName + "ByUserFriends", param, session);
	}
	
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
	
	/**
	 * TODO: add FilterEntity??
	 * This method prepares queries which retrieve all resources for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 * @param limit
	 * @param offset 
	 * @param session
	 * 
	 * @return list of posts
	 */
	public List<Post<R>> getPostsForHomepage(final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(HashID.INTER_HASH);
		
		if (FilterEntity.UNFILTERED.equals(param.getFilter())) {
			return this.postList("get" + this.resourceClassName + "ForHomepageUnfiltered", param, session); //XXX: missing in BibTex.xml
			// XXX: only called by param method in Bookmark manager
		}
		return this.postList("get" + this.resourceClassName + "ForHomepage", param, session);
	}
	
	/**
	 * XXX: remove groupType parameter?!?
	 * XXX: added simHash
	 * 
	 * Prepares a query which retrieves all posts which are represented by
	 * the given hash. Retrieves only public bookmarks! //XXX: why we need groupID??!
	 * 
	 * @param requResource
	 * @param simHash
	 * @param groupId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsByHash(final String requResource, final HashID simHash, final int groupId, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setHash(requResource);
		param.setSimHash(simHash);
		param.setGroupId(groupId); // XXX bibtex param.setGroupType(GroupID.PUBLIC);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.postList("get" + this.resourceClassName + "ByHash", param, session);
	}
	
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
	
	/**
	 * TODO: remove method??! unused
	 * Returns a list with resource posts identified by INTER-hash for a given
	 * user
	 * 
	 * @param userName
	 * @param requBibtex
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return see at param method
	 */
	@Deprecated
	public List<Post<R>> getHashForUser(final String userName, final String requBibtex, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getPostsByHashForUser(userName, requBibtex, requestedUserName, visibleGroupIDs, HashID.INTER_HASH, session);
	}
	
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
		final List<Post<R>> postpostList;
		final ResourceSearch<R> lucene = this.getResourceSearch();
		if (present(lucene)) {
			final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
			final String group = groupDb.getGroupNameByGroupId(groupId, session);

			// get search results from lucene
			final long starttimeQuery = System.currentTimeMillis();
			postpostList = lucene.searchPosts(group, search, requestedUserName, UserName, GroupNames, limit, offset);
			final long endtimeQuery = System.currentTimeMillis();
			log.debug("Lucene" + this.resourceClassName + " complete query time: " + (endtimeQuery-starttimeQuery) + "ms");
		} else {
			postpostList = new LinkedList<Post<R>>();
			log.error("No resource searcher available.");
		}
		
		return postpostList;
	}
	
	/**
	 * Returns the number of posts for a given search.
	 * 
	 * @param groupType
	 * @param search
	 * @param requestedUserName
	 * @param session
	 * @return number of posts for a given search
	 */
	public Integer getPostsSearchCount(final GroupID groupType, final String search, final String requestedUserName, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupType(groupType);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		
		return this.queryForObject("get" + this.resourceClassName + "SearchCount", param, Integer.class, session);
	}
	
	/**
	 * XXX: requestedGroupName or id
	 *  
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 * 
	 * @param requestedGroupName
	 * @param loginUserName 
	 * @param groupId
	 * @param simHash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsViewable(final String requestedGroupName, final String loginUserName, int groupId, final HashID simHash, final int limit, final int offset, final DBSession session) {
		if (GroupID.isSpecialGroupId(groupId)) {
			// show users own posts, which are private, public or for friends
			return this.getPostsForUser(loginUserName, loginUserName, HashID.INTER_HASH, groupId, new LinkedList<Integer>(), limit, offset, session);
		}
		
		final P param = this.getNewParam();
		param.setRequestedGroupName(requestedGroupName);
		param.setUserName(loginUserName);
		param.setGroupId(groupId);
		param.setSimHash(simHash);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.postList("get" + this.resourceClassName + "Viewable", param, session);
	}

	/**
	 * TODO: method without param
	 * TODO: check method
	 * 
	 * Returns viewable BibTexs for a given tag.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 * 
	 * @deprecated method must be implemented
	 */
	@Deprecated
	public List<Post<R>> getPostsViewableByTag(final P param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId())) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return this.getPostsByTagNamesForUser(param.getRequestedUserName(), param.getTagIndex(), param.getGroupId(), param.getGroups(), param.getLimit(), param.getOffset(), session);
		}
		return this.postList("get" + this.resourceClassName + "ViewableByTag", param, session);
	}
	
	/**
	 * TODO: only in bookmark!!!
	 * 
	 * <em>/group/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of all users belonging to the
	 * group. This is an aggregated view of all posts of the group members.<br/>
	 * Full viewable-for checking is done, i.e. everybody sees everything he is
	 * allowed to see.<br/>
	 * 
	 * See also
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
	 * 92, formula (9) for formal semantics of this query.
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsForGroup(final int groupId, final List<Integer> visibleGroupIDs, final String userName, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ForGroup", param, session);
	}

	/**
	 * Returns the number of posts belonging to the group.<br/><br/>
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
	 * @return the (approximated) number of posts for the given group, see method above
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

	/** 
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getPostsForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param userName
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsForGroupByTag(final int groupId, final List<Integer> visibleGroupIDs, final String userName,  List<TagIndex> tagIndex, int limit, int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setGroupId(groupId); 
		param.setGroups(visibleGroupIDs);
		param.setUserName(userName);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ForGroupByTag", param, session);
	}

	/**
	 * @see PostDatabaseManager#getPostsForUser(String, String, HashID, int, List, int, int, DBSession)
	 * 
	 * @param param
	 * @param session
	 * @return list of posts
	 */
	@Deprecated
	public List<Post<R>> getPostsForUser(P param, DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ForUser", param, session);
	}
	
	/**
	 * XXX: added userName + visible... (bibtex) and HashId (bookmark)
	 * 
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 * 
	 * ATTENTION! in case of a given groupId it is NOT checked if the user
	 * actually belongs to this group.
	 * 
	 * TODO: which of the two methods {@link #getPostsForUser(GenericParam, DBSession)} and {@link #getBibTexForUser(String, HashID, int, int, int, DBSession)} is deprecated? 
	 * Mark it with the appropriate tag! XXX: look at the todo
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param simHash TODO
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of posts
	 */
	public List<Post<R>> getPostsForUser(final String userName, final String requestedUserName, final HashID simHash, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setUserName(userName);
		param.setRequestedUserName(requestedUserName);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(simHash);
		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.postList("get" + this.resourceClassName + "ForUser", param, session);
	}

	/**
	 * Returns the number of posts for a given user.
	 * 
	 * @param requestedUserName 
	 * @param userName 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return the number of posts of the requested User which the logged in user is allowed to see
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
	 * Get posts of users which the logged-in users is following.
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
	 * Returns a contentId for a given post.
	 * 
	 * @param hash
	 * @param requestedUserName
	 * @param session
	 * @return contentId for a given post
	 */
	public Integer getContentIdForPost(final String hash, final String requestedUserName, final DBSession session) {
		if (!present(hash) || !present(requestedUserName)) {
			throw new RuntimeException("Hash and user name must be set");
		}
		final P param = this.getNewParam();
		param.setHash(hash);
		param.setRequestedUserName(requestedUserName);
		param.setSimHash(HashID.INTRA_HASH);
		
		return this.queryForObject("getContentIdFor" + this.resourceClassName, param, Integer.class, session);
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
	
	/**
	 * This method prepares a query which retrieves all posts the user
	 * has in his basket list. The result is shown on the page
	 * <em>/basket</em>. Since every user can only see his <em>own</em>
	 * basket page, we use userName as restriction for the user name and not
	 * requestedUserName.
	 * 
	 * @param loginUser
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<R>> getPostsFromBasketForUser(final String loginUser, final int limit, final int offset, final DBSession session) {
		final P param = this.getNewParam();
		param.setUserName(loginUser);
		param.setSimHash(HashID.INTER_HASH);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.postList("get" + this.resourceClassName + "FromBasketForUser", param, session);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.CrudableContent#getPosts(org.bibsonomy.database.params.P, org.bibsonomy.database.util.DBSession)
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
					throw new IllegalArgumentException("Could not create new " + this.resourceClassName + ": This " + this.resourceClassName + " already exists in your collection (intrahash: " + intraHash + ")");
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
					
					this.onPostUpdate(oldPost.getContentId(), post.getContentId(), session);
					
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
	 * called when a post was updated
	 * 
	 * @param oldContentId	the old content id of the post
	 * @param newContentId	the new content id of the post
	 * @param session
	 */
	protected abstract void onPostUpdate(Integer oldContentId, Integer newContentId, DBSession session);

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

				if (delete) {
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
		session.beginTransaction();
		try {
			final List<Post<R>> posts = this.getPostsByHashForUser(userName, resourceHash, userName, new ArrayList<Integer>(), HashID.INTRA_HASH, session);
			
			if (posts.isEmpty()) {
				log.debug("post with hash \"" + resourceHash + "\" not found");
				return false;
			}
			
			// Used for userName, hash and contentId
			final P param = this.getNewParam();
			param.setRequestedUserName(userName);
			param.setUserName(userName);
			param.setHash(resourceHash);
			
			final Post<R> onePost = posts.get(0);
			param.setRequestedContentId(onePost.getContentId());
			
			if (!update) {
				this.onPostDelete(onePost.getContentId(), session);
			}
			
			this.tagDb.deleteTags(onePost, session);
			
			this.insertOrUpdatePostHash(param, session, true);
			
			this.delete("delete" + this.resourceClassName, param, session);
			
			session.commitTransaction();
			
		} finally {
			session.endTransaction();
		}
		
		return true;
	}

	/**
	 * called when a post was deleted successfully
	 * 
	 * @param contentId	the content id of the post which was deleted
	 * @param session
	 */
	protected abstract void onPostDelete(Integer contentId, DBSession session);

	/**
	 * @return the chain
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
	protected ResourceSearch<R> getResourceSearch() {
		return resourceSearch;
	}

	/**
	 * @param resourceSearch
	 */
	public void setResourceSearch(ResourceSearch<R> resourceSearch) {
		this.resourceSearch = resourceSearch;
	}
}
