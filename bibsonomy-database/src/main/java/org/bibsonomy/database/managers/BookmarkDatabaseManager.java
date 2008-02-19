package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.Order;
import org.bibsonomy.model.util.SimHash;

/**
 * Used to CRUD bookmarks from the database.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkDatabaseManager extends AbstractDatabaseManager implements CrudableContent<Bookmark, BookmarkParam> {

	private static final Logger log = Logger.getLogger(BookmarkDatabaseManager.class);

	private final static BookmarkDatabaseManager singleton = new BookmarkDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagDatabaseManager tagDb;
	private final DatabasePluginRegistry plugins;
	private static final BookmarkChain chain = new BookmarkChain();

	private BookmarkDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	public static BookmarkDatabaseManager getInstance() {
		return singleton;
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
	 */
	public List<Post<Bookmark>> getBookmarkByTagNames(final BookmarkParam param, final DBSession session) {
		if (Order.FOLKRANK.equals(param.getOrder())){
			param.setGroupId(GroupID.PUBLIC.getId());
			return this.bookmarkList("getBookmarkByTagNamesAndFolkrank", param, session);
		}
		return this.bookmarkList("getBookmarkByTagNames", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager.getBookmarkByTagNames
	 * 
	 * @param groupType
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkByTagNames(final GroupID groupType, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkByTagNames(param, session);
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
	 */
	public List<Post<Bookmark>> getBookmarkByTagNamesForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByTagNamesForUser", param, session);
	}

	/**
	 * @see BookmarkDatabaseManager.getBookmarkByTagNamesForUser
	 * 
	 * @param requestedUserName
	 * @param userName
	 * @param tagIndex
	 * @param groupId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkByTagNamesForUser(final String requestedUserName, final String userName, final List<TagIndex> tagIndex, final int groupId, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setTagIndex(tagIndex);
		param.setGroupId(groupId);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkByTagNamesForUser(param, session);
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
	 */
	public List<Post<Bookmark>> getBookmarkByConceptForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByConceptForUser", param, session);
	}

	/**
	 * @param loginUser
	 * @param requestedUserName
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkByConceptForUser(final String loginUser, final String requestedUserName, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(loginUser);
		param.setRequestedUserName(requestedUserName);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkByConceptForUser(param, session);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have currUser as
	 * their friend.
	 */
	public List<Post<Bookmark>> getBookmarkByUserFriends(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkByUserFriends", param, session);
	}

	/**
	 * @param user
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkByUserFriends(final String user, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(user);
		param.setGroupType(GroupID.FRIENDS); // groupType must be set to friends
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkByUserFriends(param, session);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<Post<Bookmark>> getBookmarkForHomepage(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkForHomepage", param, session);
	}

	/**
	 * @param groupType
	 * @param limit
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkForHomepage(final GroupID groupType, final int limit, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setLimit(limit);
		return getBookmarkForHomepage(param, session);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<Post<Bookmark>> getBookmarkPopular(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkPopular", param, session);
	}

	/**
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkPopular(final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		return getBookmarkPopular(param, session);
	}
	
	/**
	 * Prepares a query which retrieves all bookmarks which are represented by
	 * the given hash. Retrieves only public bookmarks!
	 */
	public List<Post<Bookmark>> getBookmarkByHash(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkByHash", param, session);
	}

	/**
	 * @param requBibtex
	 * @param groupType
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkByHash(final String requBibtex, final GroupID groupType, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requBibtex);
		param.setGroupType(groupType);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkByHash(param, session);
	}
	
	/**
	 * Retrieves the number of bookmarks represented by the given hash.
	 */
	public Integer getBookmarkByHashCount(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getBookmarkByHashCount", param, Integer.class, session);
	}

	/**
	 * @param requBibtex
	 * @param groupType
	 * @param session
	 * @return see at param method
	 */
	public Integer getBookmarkByHashCount(final String requBibtex, final GroupID groupType, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requBibtex);
		param.setGroupType(groupType);
		return getBookmarkByHashCount(param, session);
	}

	/**
	 * Prepares a query which retrieves the bookmark (which is represented by
	 * the given hash) for a given user. Since user name is given, full group
	 * checking is done, i.e. everbody who may see the bookmark will see it.
	 */
	public List<Post<Bookmark>> getBookmarkByHashForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByHashForUser", param, session);
	}

	/**
	 * @param userName
	 * @param requBibtex
	 * @param requestedUserName
	 * @param session
	 * @param hashType
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkByHashForUser(final String userName, final String requBibtex, final String requestedUserName, final DBSession session, final HashID hashType) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(userName);
		param.setHash(requBibtex);
		param.setRequestedUserName(requestedUserName);
		param.setRequestedSimHash(hashType);
		return getBookmarkByHashForUser(param, session);
	}

	/**
	 * Returns a list with bookmark posts identified by INTER-hash for a given user
	 */
	/**
	 * @param userName
	 * @param requBibtex
	 * @param requestedUserName
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkHashForUser(final String userName, final String requBibtex, final String requestedUserName, final DBSession session) {
		return getBookmarkByHashForUser(userName, requBibtex, requestedUserName, session, HashID.INTER_HASH);
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
	 */
	public List<Post<Bookmark>> getBookmarkSearch(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkSearch", param, session);
	}

	/**
	 * @param groupType
	 * @param search
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkSearch(final GroupID groupType, final String search, final String requestedUserName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkSearch(param, session);
	}

	/**
	 * Returns the number of bookmarks for a given search.
	 */
	public Integer getBookmarkSearchCount(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getBookmarkSearchCount", param, Integer.class, session);
	}

	/**
	 * @param groupType
	 * @param search
	 * @param requestedUserName
	 * @param session
	 * @return see at param method
	 */
	public Integer getBookmarkSearchCount(final GroupID groupType, final String search, final String requestedUserName, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupType(groupType);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		return getBookmarkSearchCount(param, session);
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
	 * @param groupId
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkViewable(final int groupId, final String userName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkViewable(param, session);
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
	 */
	public List<Post<Bookmark>> getBookmarkForGroup(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroup", param, session);
	}

	/**
	 * @param groupId
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkForGroup(final int groupId, final String userName, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkForGroup(param, session);
	}

	/**
	 * Returns the number of bookmarks belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBookmarkForGroupCount(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.queryForObject("getBookmarkForGroupCount", param, Integer.class, session);
	}
	
	/**
	 * Returns the number of bookmarks belonging to this group
	 * 
	 * @see BookmarkDatabaseManager.getBookmarkForGroupCount
	 * 
	 * @param groupId
	 * @param loginUserName
	 * @param session
	 * @return the (approximated) number of bookmarks for the given group, see method above
	 */

	/**
	 * @param groupId
	 * @param userName
	 * @param session
	 * @return see at param method
	 */
	public Integer getBookmarkForGroupCount(final int groupId, final String userName, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setUserName(userName);
		param.setGroupId(groupId);
		return this.getBookmarkForGroupCount(param, session);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBookmarkForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<Bookmark>> getBookmarkForGroupByTag(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroupByTag", param, session);
	}

	/**
	 * @param groupId
	 * @param userName
	 * @param tagIndex
	 * @param session
	 * @return see at param method
	 */
	public List<Post<Bookmark>> getBookmarkForGroupByTag(final int groupId, final String userName,  List<TagIndex> tagIndex, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setGroupId(groupId); 
		param.setUserName(userName);
		param.setTagIndex(tagIndex);
		return getBookmarkForGroupByTag(param, session);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 */
	public List<Post<Bookmark>> getBookmarkForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForUser", param, session);
	}

	/**
	 * @param userName
	 * @param requestedUserName
	 * @param groupId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return  see at param method
	 */
	public List<Post<Bookmark>> getBookmarkForUser(final String userName, final String requestedUserName, final int groupId, final int limit, final int offset, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setUserName(userName);
		param.setRequestedUserName(requestedUserName);
		param.setGroupId(groupId);
		param.setLimit(limit);
		param.setOffset(offset);
		return getBookmarkForUser(param, session);
	}

	/**
	 * Returns the number of bookmarks for a given user.
	 */
	public Integer getBookmarkForUserCount(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session); // set groups
		return this.queryForObject("getBookmarkForUserCount", param, Integer.class, session);
	}
	
	/**
	 * returns the number of bookmarks for a given user
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param session
	 * @return the number of bookmarks of the requested User which the logged in user is allowed to see
	 */
	public Integer getBookmarkForUserCount(final String requestedUserName, final String userName, final DBSession session) {
		BookmarkParam param = new BookmarkParam();
		param.setUserName(userName);
		param.setRequestedUserName(requestedUserName);
		return this.getBookmarkForUserCount(param, session);
	}

	/**
	 * Inserts a bookmark into the database.
	 */
	private void insertBookmark(final BookmarkParam param, final DBSession session) {
		// Start transaction
		session.beginTransaction();
		try {
			// Insert bookmark
			this.insert("insertBookmark", param, session);
			// compute only a single simHash, as all simHashes are equal for bookmarks
			final HashID simHash = HashID.getSimHash(0);
			param.setRequestedSimHash(simHash);
			param.setHash(SimHash.getSimHash(param.getResource(),simHash));
			this.insertBookmarkHash(param, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Inserts a post with a bookmark into the database.
	 */
	private void insertBookmarkPost(final Post<Bookmark> post, final DBSession session) {
		if (present(post.getResource()) == false) throw new InvalidModelException("There is no resource for this post.");
		if (present(post.getGroups()) == false) throw new InvalidModelException("There are no groups for this post.");
		
		final BookmarkParam param = new BookmarkParam();
		param.setResource(post.getResource());
		param.setDate(post.getDate());
		param.setRequestedContentId(post.getContentId());
		param.setHash(post.getResource().getIntraHash());
		param.setDescription(post.getDescription());
		param.setUserName(post.getUser().getName());
		param.setUrl(post.getResource().getUrl());
		for (final Group group : post.getGroups()) {
			param.setGroupId(group.getGroupId());
			this.insertBookmark(param, session);
		}
	}

	// insert counter, hash and url of bookmark
	private void insertBookmarkHash(final BookmarkParam param, final DBSession session) {
		this.insert("insertBookmarkHash", param, session);
	}

   	// decrements one count in url table after deleting
	private void updateBookmarkHash(final BookmarkParam param, final DBSession session) {
		this.insert("updateBookmarkHash", param, session);
	}

	public void deleteBookmark(final BookmarkParam param, final DBSession session) {
		this.delete("deleteBookmark", param, session);
	}

	public Integer getContentIDForBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getContentIDForBookmark", param, Integer.class, session);
	}
	//checkme (OK)
	public Integer getContentIDForBookmark(final String requBibtex, final String userName, final DBSession session) {
		final BookmarkParam param = new BookmarkParam();
		param.setHash(requBibtex);
		param.setUserName(userName);
		return getContentIDForBookmark(param, session);
	}

	public List<Post<Bookmark>> getPosts(final BookmarkParam param, final DBSession session) {
		return chain.getFirstElement().perform(param, session);
	}

	public Post<Bookmark> getPostDetails(String authUser, String resourceHash, String userName, final DBSession session) {
		final List<Post<Bookmark>> list = getBookmarkByHashForUser(authUser, resourceHash, userName, session, HashID.INTRA_HASH);
		if (list.size() >= 1) {
			if (list.size() > 1) {
				log.warn("multiple Bookmark-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			return list.get(0);
		}

		log.debug("Bookmark-post from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' not found");
		return null;
	}

	public boolean deletePost(final String userName, final String resourceHash, final DBSession session) {
		return this.deletePost(userName, resourceHash, false, session);
	}

	private boolean deletePost(final String userName, final String resourceHash, boolean update, final DBSession session) {
		// TODO: test removal (tas and bibtex ...)
		session.beginTransaction();
		try {
			// Used for userName, hash and contentId
			final BookmarkParam param = new BookmarkParam();
			param.setRequestedUserName(userName);
			param.setHash(resourceHash);
			
			final List<Post<Bookmark>> bookmarks = this.getBookmarkByHashForUser(param, session);
			if (bookmarks.size() == 0) {
				// Bookmark doesn't exist
				return false;
			}
			
			final Post<? extends Resource> oneBookmark = bookmarks.get(0);
			param.setRequestedContentId(oneBookmark.getContentId());
			
			if (update == false) {
				this.plugins.onBookmarkDelete(param.getRequestedContentId(), session);
			}
			// Delete al tags according bookmark
			this.tagDb.deleteTags(oneBookmark, session);
			// Update SimHashes - as for bookmarks currently all simhashes are the same, 
			// we only update one and are done		
			// TODO: isn't it better to replace this zero by a constant?
			final HashID simHash = HashID.getSimHash(0);
			param.setRequestedSimHash(simHash);
			param.setHash(SimHash.getSimHash(((Bookmark) oneBookmark.getResource()), simHash));
			// Decrement counter in url table
			this.updateBookmarkHash(param, session);
			// Delete entry from table bookmark
			this.deleteBookmark(param, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.CrudableContent#storePost(java.lang.String, org.bibsonomy.model.Post, java.lang.String, boolean, org.bibsonomy.database.util.DBSession)
	 */
	public boolean storePost(final String userName, final Post<Bookmark> post, final String oldIntraHash, boolean update, final DBSession session)  {
		session.beginTransaction();
		try {
			// the bookmark with the "old" intrahash, i.e. the one that was sent
			// within the create/update bookmark request
			final List<Post<Bookmark>> isOldBookmarkInDb;
			
			// the bookmark with the "new" intrahash, i.e. the one that was recalculated
			// based on the bookmark's fields
			final List<Post<Bookmark>> isNewBookmarkInDb;
			
			// check if user is trying to create a bookmark that already exists
			isNewBookmarkInDb = this.getBookmarkByHashForUser(userName, post.getResource().getIntraHash(), userName, session, HashID.INTRA_HASH);
			if ((isNewBookmarkInDb != null) && (isNewBookmarkInDb.size() > 0) && update == false) {
				throw new IllegalArgumentException("Could not create new bookmark: This bookmark already exists in your collection (intrahash: " + post.getResource().getIntraHash() + ")");
			}
			
			if (oldIntraHash != null) {
				// check if the hash sent within the request is correct
				if ((update == false) && (oldIntraHash.equals(post.getResource().getIntraHash()) == false)) {
					throw new IllegalArgumentException(
							"Could not create new bookmark: The requested intrahash " 
							+ oldIntraHash + " is not correct for this bookmark (correct intrahash is " 
							+ post.getResource().getIntraHash() + ")."
					);
				}
				// if yes, check if a bookmark exists with the old intrahash				
				isOldBookmarkInDb = this.getBookmarkByHashForUser(userName, oldIntraHash, userName, session, HashID.INTRA_HASH);
			} else {
				if (update == true) {
					throw new IllegalArgumentException("Could not update bookmark: no intrahash specified.");
				}
				isOldBookmarkInDb = null;
			}
						
			// ALWAYS get a new contentId
			post.setContentId(this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, session));
			if ((isOldBookmarkInDb != null) && (isOldBookmarkInDb.size() > 0)) {
				update = true;
				// Bookmark entry DOES EXIST for this user -> delete old Bookmark post
				final Post<?> oldBookmarkPost = isOldBookmarkInDb.get(0);
				this.plugins.onBookmarkUpdate(post.getContentId(), oldBookmarkPost.getContentId(), session);
				this.deletePost(userName, oldBookmarkPost.getResource().getIntraHash(), true, session);
			} else {
				if (update == true) {
					log.warn("Bookmark with hash " + oldIntraHash + " does not exist for user " + userName);
					throw new ResourceNotFoundException(oldIntraHash);
				}
				update = false;
			}

			this.insertBookmarkPost(post, session);

			// add the tags
			this.tagDb.insertTags(post, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return update;
	}
}