package org.bibsonomy.database.managers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.ResourcesParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.lucene.LuceneSearch;
import org.bibsonomy.lucene.LuceneSearchBookmarks;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;


/**
 * Used to CRUD bookmarks from the database.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Daniel Zoller
 * @version $Id$
 */
public class BookmarkDatabaseManager extends PostDatabaseManager<Bookmark, BookmarkParam> {
	private static final Log log = LogFactory.getLog(BookmarkDatabaseManager.class);

	private final static BookmarkDatabaseManager singleton = new BookmarkDatabaseManager();
	
	private static final BookmarkChain chain = new BookmarkChain();
	private static final HashID[] hashRange = { HashID.SIM_HASH0 };
	
	/**
	 * @return BookmarkDatabaseManager
	 */
	public static BookmarkDatabaseManager getInstance() {
		return singleton;
	}

	private BookmarkDatabaseManager() {
		super();
	}

	/**
	 * Can be used to start a query that retrieves a list of bookmarks.
	 */
	@SuppressWarnings("unchecked")
	protected List<Post<Bookmark>> bookmarkList(final String query, final BookmarkParam param, final DBSession session) {
		return queryForList(query, param, session);
	}
	
	/**
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByTagNames(final BookmarkParam param, final DBSession session) {
		if (Order.FOLKRANK.equals(param.getOrder())){
			param.setGroupId(GroupID.PUBLIC.getId());
			return this.bookmarkList("getBookmarkByTagNamesAndFolkrank", param, session);
		}
		return this.bookmarkList("getBookmarkByTagNames", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByTagNames(BookmarkParam, DBSession)
	 * 
	 * @param groupType
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByTagNames(final GroupID groupType, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);

		// duplicated code
		if (Order.FOLKRANK.equals(param.getOrder())){
			param.setGroupId(GroupID.PUBLIC.getId());
			return this.bookmarkList("getBookmarkByTagNamesAndFolkrank", param, session);
		}
		return this.bookmarkList("getBookmarkByTagNames", param, session);
	}

	/**
	 * Counts the number of visible bookmark entries for a given list of tags
	 * 
	 * @param tagIndex a list of tags
	 * @param session DB session
	 * @param groupId is the group id
	 * @return the number of visible bookmark entries
	 */
	public Integer getBookmarkByTagNamesCount(final List<TagIndex> tagIndex, final int groupId, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setTagIndex(tagIndex);
		return this.queryForObject("getBookmarkByTagNamesCount", param, Integer.class, session);
	}	

	/**
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if userName us allowed
	 * to see them.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByTagNamesForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByTagNamesForUser", param, session);
	}

	/**
	 * Retrieves the number of bookmark items tagged by the tags present in tagIndex by user requestedUserName
	 * being visible to the logged in user
	 * 
	 * @param requestedUserName
	 * 			owner of the bookmark items
	 * @param loginUserName
	 * 			logged in user
	 * @param tagIndex
	 * 			a list of tags
	 * @param visibleGroupIDs
	 * 			a list of groupIDs the logged in user is member of
	 * @param session
	 * 			DB session
	 * @return the corresponding number of visible bibtex items
	 */
	public Integer getBookmarkByTagNamesForUserCount(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setTagIndex(tagIndex);
		return this.queryForObject("getBookmarkByTagNamesForUserCount", param, Integer.class, session);
	}	

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByTagNamesForUser(BookmarkParam, DBSession)
	 * @param requestedUserName 
	 * @param userName 
	 * @param tagIndex 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param limit 
	 * @param offset 
	 * @param session 
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByTagNamesForUser(final String requestedUserName, final String userName, final List<TagIndex> tagIndex, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setTagIndex(tagIndex);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByTagNamesForUser", param, session);
	}

	/**
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
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByConceptForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByConceptForUser", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByConceptForUser(BookmarkParam, DBSession)
	 * 
	 * @param loginUser
	 * @param requestedUserName
	 * @param visibleGroupIDs 
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByConceptForUser(final String loginUser, final String requestedUserName, final List<Integer> visibleGroupIDs, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(loginUser);
		param.setRequestedUserName(requestedUserName);
		param.setGroups(visibleGroupIDs);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByConceptForUser", param, session);
	}

	/**
	 * <em>/concept/group/GruppenName/EinTag</em><br/><br/>
	 * 
	 * This method retrieves all bookmarks of all group members of the given
	 * group which are tagged at least with one of the concept tags or its
	 * subtags
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByConceptForGroup(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByConceptForGroup", param, session);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have currUser as
	 * their friend.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByUserFriends(final BookmarkParam param, final DBSession session) {
		// groupType must be set to friends
		param.setGroupType(GroupID.FRIENDS);
		return this.bookmarkList("getBookmarkByUserFriends", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByUserFriends(BookmarkParam, DBSession)
	 * 
	 * @param user
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByUserFriends(final String user, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(user);
		param.setGroupType(GroupID.FRIENDS); // groupType must be set to friends
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bookmarkList("getBookmarkByUserFriends", param, session);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForHomepage(final BookmarkParam param, final DBSession session) {
		if (FilterEntity.UNFILTERED.equals(param.getFilter())) {
			return this.bookmarkList("getBookmarkForHomepageUnfiltered", param, session);
		}
		return this.bookmarkList("getBookmarkForHomepage", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkForHomepage(BookmarkParam, DBSession)
	 * 
	 * @param groupType
	 * @param limit
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForHomepage(final GroupID groupType, final int limit, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setLimit(limit);
		return this.bookmarkList("getBookmarkForHomepage", param, session);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkPopular(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkPopular", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkPopular(BookmarkParam, DBSession)
	 * 
	 * @param limit 
	 * @param offset 
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkPopular(final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setOffset(offset);
		param.setLimit(limit);
		return this.bookmarkList("getBookmarkPopular", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkPopular(BookmarkParam, DBSession)
	 * 
	 * @param days
	 * @param limit
	 * @param offset
	 * @param session 
	 * 
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkPopular(final int days, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setDays(days);
		param.setOffset(offset);
		param.setLimit(limit);
		return this.bookmarkList("getBookmarkPopular", param, session);
	}

	/**
	 * Prepares a query which retrieves all bookmarks which are represented by
	 * the given hash. Retrieves only public bookmarks!
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByHash(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkByHash", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByHash(BookmarkParam, DBSession)
	 * 
	 * @param requBibtex
	 * @param groupType
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByHash(final String requBibtex, final GroupID groupType, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requBibtex);
		param.setGroupType(groupType);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bookmarkList("getBookmarkByHash", param, session);
	}

	/**
	 * Retrieves the number of bookmarks represented by the given hash.
	 * 
	 * @param param
	 * @param session
	 * @return number of bookmarks for the given hash
	 */
	public Integer getBookmarkByHashCount(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getBookmarkByHashCount", param, Integer.class, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByHashCount(BookmarkParam, DBSession)

	 * @param requHash 
	 * @param simHash 
	 * @param session
	 * @return number of  for the given hash
	 */
	public Integer getBookmarkByHashCount(final String requHash, final HashID simHash, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requHash);
		param.setSimHash(simHash);
		
		return this.queryForObject("getBookmarkByHashCount", param, Integer.class, session);
	}
	
	/**
	 * @param requHash 
	 * @param simHash 
	 * @param userName
	 * @param session
	 * @return number of bookmarks for the given hash and a user
	 */
	public Integer getBookmarkByHashAndUserCount(final String requHash, final HashID simHash, final String userName, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requHash);
		param.setSimHash(simHash);
		param.setUserName(userName);
		return this.queryForObject("getBookmarkByHashAndUserCount", param, Integer.class, session);
	}

	/**
	 * Prepares a query which retrieves the bookmark (which is represented by
	 * the given hash) for a given user. Since user name is given, full group
	 * checking is done, i.e. everbody who may see the bookmark will see it.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 * 
	 * @deprecated  replaced by {@link PostDatabaseManager#getPostsByHashForUser(ResourcesParam, DBSession)}
	 */
	@Deprecated
	public List<Post<Bookmark>> getBookmarkByHashForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByHashForUser", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkByHashForUser(BookmarkParam, DBSession)
	 * 
	 * @param userName
	 * @param requBibtex
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @param hashType
	 * @return list of bookmark posts
	 * 
	 * @deprecated replaced by {@link PostDatabaseManager#getPostsByHashForUser(String, String, String, List, HashID, DBSession)}
	 */
	@Deprecated
	public List<Post<Bookmark>> getBookmarkByHashForUser(final String userName, final String requBibtex, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session, final HashID hashType) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(userName);
		param.addGroups(visibleGroupIDs);
		param.setHash(requBibtex);
		param.setRequestedUserName(requestedUserName);
		param.setSimHash(hashType);
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByHashForUser", param, session);
	}

	/**
	 * Returns a list with bookmark posts identified by INTER-hash for a given
	 * user
	 * 
	 * @param userName
	 * @param requBibtex
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkHashForUser(final String userName, final String requBibtex, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		return getBookmarkByHashForUser(userName, requBibtex, requestedUserName, visibleGroupIDs, session, HashID.INTER_HASH);
	}

	/**
	 * <em>/search/ein+lustiger+satz</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table.<br/>
	 * 
	 * The search string, as given by the user will be mangled up in the method
	 * to do what the user expects (AND searching). Unfortunately this also
	 * destroys some other features (e.g. <em>phrase searching</em>).<br/>
	 * 
	 * If requestedUser is given, only (public) posts from the given user are
	 * searched. Otherwise all (public) posts are searched.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkSearch(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkSearch", param, session);
	}


	/**
	 * @see BookmarkDatabaseManager#getBookmarkSearch(BookmarkParam, DBSession)
	 * 
	 * @param groupId
	 * @param search
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkSearch(final int groupId, final String search, final String requestedUserName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bookmarkList("getBookmarkSearch", param, session);
	}
	
	
	/**
	 * <em>/search/ein+lustiger+satz+group%3AmyGroup</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table with the requested group<br/>
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkSearchForGroup(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkSearchForGroup", param, session);
	}
	
	/**
	 * @see BookmarkDatabaseManager#getBookmarkSearchForGroup(BookmarkParam, DBSession)
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param search
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkSearchForGroup(final int groupId, final List<Integer> visibleGroupIDs, final String search, final String userName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkSearchForGroup", param, session);
	}

	/**
	 * Returns the number of bookmarks for a given search.
	 * 
	 * @param param
	 * @param session
	 * @return number of bookmarks for a given search
	 */
	public Integer getBookmarkSearchCount(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getBookmarkSearchCount", param, Integer.class, session);
	}


	/**
	 * @see BookmarkDatabaseManager#getBookmarkSearchCount(BookmarkParam, DBSession)
	 * 
	 * @param groupType
	 * @param search
	 * @param requestedUserName
	 * @param session
	 * @return number of bookmarks for a given search
	 */
	public Integer getBookmarkSearchCount(final GroupID groupType, final String search, final String requestedUserName, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		return this.queryForObject("getBookmarkSearchCount", param, Integer.class, session);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 * @param param 
	 * @param session 
	 * @return list of bookmarks
	 */
	public List<Post<Bookmark>> getBookmarkViewable(final BookmarkParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBookmarkForUser(param, session);
		}				
		return this.bookmarkList("getBookmarkViewable", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkViewable(BookmarkParam, DBSession)
	 * 
	 * @param groupId
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmarks
	 */
	public List<Post<Bookmark>> getBookmarkViewable(final int groupId, final String userName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		// duplicated code
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBookmarkForUser(param, session);
		}
		
		return this.bookmarkList("getBookmarkViewable", param, session);
	}

	/**
	 * @param param
	 * @param session
	 * @return list of bookmarks
	 */
	public List<Post<Bookmark>> getBookmarkViewableByTag(final BookmarkParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBookmarkByTagNamesForUser(param, session);
		}
		return this.bookmarkList("getBookmarkViewableByTag", param, session);
	}

	/**
	 * <em>/group/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries which show all bookmarks of all users belonging to the
	 * group. This is an aggregated view of all posts of the group members.<br/>
	 * Full viewable-for checking is done, i.e. everybody sees everything he is
	 * allowed to see.<br/>
	 * 
	 * See also
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
	 * 92, formula (9) for formal semantics of this query.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForGroup(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroup", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkForGroup(BookmarkParam, DBSession)
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForGroup(final int groupId, final List<Integer> visibleGroupIDs, final String userName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		// duplicated code
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroup", param, session);
	}

	/**
	 * Returns the number of bookmarks belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 * 
	 * @param param
	 * @param session
	 * @return number of bookmarks for a given group
	 */
	public Integer getBookmarkForGroupCount(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.queryForObject("getBookmarkForGroupCount", param, Integer.class, session);
	}

	/**
	 * Returns the number of bookmarks belonging to this group
	 *  
	 * @see BookmarkDatabaseManager#getBookmarkForGroupCount(BookmarkParam, DBSession)
	 * 
	 * @param requestedUserName 
	 * @param userName
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param session
	 * @return the (approximated) number of bookmarks for the given group, see method above
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 */
	public Integer getBookmarkForGroupCount(final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setGroups(visibleGroupIDs);
		param.setGroupId(groupId);
		
		// duplicated code
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.queryForObject("getBookmarkForGroupCount", param, Integer.class, session);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBookmarkForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForGroupByTag(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroupByTag", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkForGroupByTag(BookmarkParam, DBSession)
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param userName
	 * @param tagIndex
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForGroupByTag(final int groupId, final List<Integer> visibleGroupIDs, final String userName,  List<TagIndex> tagIndex, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId); 
		param.setGroups(visibleGroupIDs);
		param.setUserName(userName);
		param.setTagIndex(tagIndex);
		
		// duplicated code
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroupByTag", param, session);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForUser", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager#getBookmarkForUser(BookmarkParam, DBSession)
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkForUser(final String userName, final String requestedUserName, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(userName);
		param.setRequestedUserName(requestedUserName);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.getBookmarkForUser(param, session);
	}

	/**
	 * Returns the number of bookmarks for a given user.
	 * 
	 * @param param
	 * @param session
	 * @return number of bookmarks for a given user
	 */
	public Integer getBookmarkForUserCount(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session); // set groups
		return this.queryForObject("getBookmarkForUserCount", param, Integer.class, session);
	}

	/**
	 * Returns the number of bookmarks for a given user.
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
	public Integer getBookmarkForUserCount(final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		
		// Duplicated code
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session); // set groups
		return this.queryForObject("getBookmarkForUserCount", param, Integer.class, session);
	}

	/**
	 * Returns a contentId for a given bookmark.
	 * 
	 * @param param
	 * @param session
	 * @return contentId for a given bookmark
	 */
	public Integer getContentIDForBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getContentIDForBookmark", param, Integer.class, session);
	}

	/**
	 * @param requBookmark
	 * @param userName
	 * @param session
	 * @return contentId for a given bookmark
	 */
	public Integer getContentIDForBookmark(final String requBookmark, final String userName, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requBookmark);
		param.setUserName(userName);
		return this.queryForObject("getContentIDForBookmark", param, Integer.class, session);
	}

	/**
	 * @param param 
	 * @param session 
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByConceptByTag(final BookmarkParam param, final DBSession session){
		return this.bookmarkList("getBookmarkByConceptByTag", param, session);
	}

	/**
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of bookmarks that are available for some groups
	 */
	public int getGroupBookmarkCount(final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){			
		BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);

		return (Integer) this.queryForObject("getGroupBookmarkCount", param, session);
	}

	/**
	 * @param requestedUserName
	 * @param loginUserName
	 * @param tagIndex
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of bookmarks that are available for some groups and tagged by a tag of the tagIndex
	 */
	public int getGroupBookmarkCountByTag(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session){			
		BookmarkParam param = new BookmarkParam();
		param.setTagIndex(tagIndex);
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);

		return (Integer) this.queryForObject("getGroupBookmarkCountByTag", param, session);
	}

	/**
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param limit 
	 * @param offset 
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarksForMyGroupPosts(final String requestedUserName, final String loginUserName, final int limit, final int offset, final List<Integer> visibleGroupIDs, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);

		return this.bookmarkList("getBookmarksForMyGroupPosts",param,session);
	}

	/**
	 * @param requestedUserName
	 * @param loginUserName
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarksForMyGroupPostsByTag(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final int limit, final int offset, final List<Integer> visibleGroupIDs, final DBSession session){
		BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);

		return this.bookmarkList("getBookmarksForMyGroupPostsByTag",param,session);
	}

	/**
	 * @param days
	 * @param session
	 * @return the number of days when a bookmark was popular
	 */
	public int getBookmarkPopularDays(final int days, final DBSession session){
		final BookmarkParam param = new BookmarkParam();
		param.setDays(days);

		final Integer result = this.queryForObject("getBookmarkPopularDays", param, Integer.class, session);
		if (result != null) {
			return result;
		}
		
		return 0;
	}
	
	
	/**
	 * Get Bookmarks of users which the logged-in users is following.
	 * 
	 * @param loginUserName - 
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByFollowedUsers(final String loginUserName, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bookmarkList("getBookmarkByFollowedUsers",param,session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#informPlugin(org.bibsonomy.database.managers.PostDatabaseManager.Action, java.lang.Integer, java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void informPlugin(org.bibsonomy.database.managers.PostDatabaseManager.Action action, Integer newContentId, Integer oldContentId, DBSession session) {
		switch (action) {
			case UPDATE:
				this.plugins.onBookmarkUpdate(oldContentId, newContentId, session);
				break;
			case CREATE:
				this.plugins.onBookmarkInsert(newContentId, session);
			case DELETE:
				this.plugins.onBookmarkDelete(newContentId, session);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getChain()
	 */
	@Override
	protected FirstChainElement<Post<Bookmark>, BookmarkParam> getChain() {
		return chain;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getHashRange()
	 */
	@Override
	protected HashID[] getHashRange() {
		return hashRange;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getInsertParam(org.bibsonomy.model.Post, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected BookmarkParam getInsertParam(final Post<Bookmark> post, final DBSession session) {
		final BookmarkParam insert = this.getNewParam();
		
		insert.setResource(post.getResource());
		insert.setDate(post.getDate());
		insert.setRequestedContentId(post.getContentId());
		insert.setHash(post.getResource().getIntraHash());
		insert.setDescription(post.getDescription());
		insert.setUserName(post.getUser().getName());
		insert.setUrl(post.getResource().getUrl());	

		// in field group in table bookmark, insert the id for PUBLIC, PRIVATE or the id of the FIRST group in list
		final int groupId = post.getGroups().iterator().next().getGroupId();
		insert.setGroupId(groupId);
		
		return insert;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getLuceneSearch()
	 */
	@Override
	protected LuceneSearch<Bookmark> getLuceneSearch() {
		return LuceneSearchBookmarks.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getNewParam()
	 */
	@Override
	protected BookmarkParam getNewParam() {
		return new BookmarkParam();
	}
}