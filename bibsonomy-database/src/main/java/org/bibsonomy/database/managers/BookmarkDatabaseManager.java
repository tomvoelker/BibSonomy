package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
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
//	private static final Log log = LogFactory.getLog(BookmarkDatabaseManager.class);

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
	 * TODO how to get group type with id???
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
	 * XXXDZ
	 * 
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached.
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
	 * XXXDZ
	 * 
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
	@Deprecated
	private List<Post<Bookmark>> getBookmarkByTagNamesForUser(final String requestedUserName, final String userName, final List<TagIndex> tagIndex, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
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
	 * TODO: remove me
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
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByConceptForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByConceptForUser", param, session);
	}

	/**
	 * XXXDZ
	 * 
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
	 * TODO get groupID of int
	 * 
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
	 * XXXDZ
	 * 
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
	 * TODO: get groupType by id => remove me
	 * 
	 * @param param
	 * @param session
	 * @return list of bookmark posts
	 */
	public List<Post<Bookmark>> getBookmarkByHash(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkByHash", param, session);
	}

	/**
	 * XXXDZ
	 * 
	 * Prepares a query which retrieves all bookmarks which are represented by
	 * the given hash. Retrieves only public bookmarks!
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
	 * XXX: requestedGroupName
	 * 
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 * 
	 * @param groupId
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bookmarks
	 */
	@Deprecated
	public List<Post<Bookmark>> getBookmarkViewable(final int groupId, final String userName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		if (GroupID.isSpecialGroupId(param.getGroupId())) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getPostsForUser(param, session);
		}
		
		return this.bookmarkList("getBookmarkViewable", param, session);
	}

	/**
	 * XXXDZ
	 * 
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

		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroup", param, session);
	}

	/**
	 * XXXDZ
	 * 
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
	 * XXXDZ
	 * 
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
	 * XXXDZ
	 * 
	 * @see BookmarkDatabaseManager#getPostsForUser(BookmarkParam, DBSession)
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
		
		return this.getPostsForUser(param, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostDelete(java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostDelete(final Integer contentId, final DBSession session) {
		this.plugins.onBookmarkDelete(contentId, session);	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostUpdate(java.lang.Integer, java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostUpdate(Integer oldContentId, Integer newContentId, DBSession session) {
		this.plugins.onBookmarkUpdate(oldContentId, newContentId, session);
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
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getNewParam()
	 */
	@Override
	protected BookmarkParam getNewParam() {
		return new BookmarkParam();
	}
}