package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Used to CRUD bookmarks from the database.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BookmarkDatabaseManager extends AbstractDatabaseManager implements CrudableContent<Bookmark, BookmarkParam> {

	/** Singleton */
	private final static BookmarkDatabaseManager singleton = new BookmarkDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagDatabaseManager tagDb;
	private static final BookmarkChain chain = new BookmarkChain();

	private BookmarkDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
	}

	public static BookmarkDatabaseManager getInstance() {
		return singleton;
	}

	// FIXME return value needs to be changed to org.bibsonomy.model.Post
	@SuppressWarnings("unchecked")
	protected List<Post<Bookmark>> bookmarkList(final String query, final BookmarkParam param, final boolean test, final DBSession session) {
		return (List<Post<Bookmark>>) queryForList(query, param, session);
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
		return this.bookmarkList("getBookmarkByTagNames", param,true, session);
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
		return this.bookmarkList("getBookmarkByTagNamesForUser", param, true, session);
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
		return this.bookmarkList("getBookmarkByConceptForUser", param, true, session);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have currUser as
	 * their friend.
	 */
	public List<Post<Bookmark>> getBookmarkByUserFriends(final BookmarkParam param, final DBSession session) {
		// groupType must be set to friends
		param.setGroupType(GroupID.FRIENDS);
		return this.bookmarkList("getBookmarkByUserFriends", param, true, session);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<Post<Bookmark>> getBookmarkForHomepage(final BookmarkParam param, final DBSession session) {
		param.setLimit(15);
		param.setOffset(0);
		return this.bookmarkList("getBookmarkForHomepage", param, true, session);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<Post<Bookmark>> getBookmarkPopular(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkPopular", param, true, session);
	}

	/**
	 * Prepares a query which retrieves all bookmarks which are represented by
	 * the given hash. Retrieves only public bookmarks!
	 */
	public List<Post<Bookmark>> getBookmarkByHash(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkByHash", param, true, session);
	}

	/**
	 * Retrieves the number of bookmarks represented by the given hash.
	 */
	public Integer getBookmarkByHashCount(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getBookmarkByHashCount", param, Integer.class, session);
	}

	/**
	 * Prepares a query which retrieves the bookmark (which is represented by
	 * the given hash) for a given user. Since user name is given, full group
	 * checking is done, i.e. everbody who may see the bookmark will see it.
	 */
	public List<Post<Bookmark>> getBookmarkByHashForUser(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkByHashForUser", param, true, session);
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
		return this.bookmarkList("getBookmarkSearch", param, true, session);
	}

	/**
	 * Returns the number of bookmarks for a given search.
	 */
	public Integer getBookmarkSearchCount(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getBookmarkSearchCount", param, Integer.class, session);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 */
	public List<Post<Bookmark>> getBookmarkViewable(final BookmarkParam param, final DBSession session) {
		return this.bookmarkList("getBookmarkViewable", param, true, session);
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
		return this.bookmarkList("getBookmarkForGroup", param, true, session);
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
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBookmarkForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<Bookmark>> getBookmarkForGroupByTag(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bookmarkList("getBookmarkForGroupByTag", param, true, session);
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
		return this.bookmarkList("getBookmarkForUser", param, true, session);
	}

	/**
	 * Returns the number of bookmarks for a given user.
	 */
	public Integer getBookmarkForUserCount(final BookmarkParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForObject("getBookmarkForUserCount", param, Integer.class, session);
	}


	
	/**
	 * This methods  are for setting functions concerning bookmark entries
	 */
	  public void insertBookmark(final BookmarkParam bookmark, final DBSession session) {
		this.insert("insertBookmark", bookmark, session);
	}

	public void insertBookmarkLog(final BookmarkParam bookmark, final DBSession session) {
		// TODO not tested
		this.insert("insertBookmarkLog", bookmark, session);
	}

	// insert counter, hash and url of bookmark
	public void insertBookmarkInc(final Bookmark param, final DBSession session) {
		this.insert("insertBookmarkInc", param, session);
	}

	public void updateBookmarkHashDec(final BookmarkParam param, final DBSession session) {
		this.insert("updateBookmarkHashDec", param, session);
	}

	public void updateBookmarkLog(final BookmarkParam param, final DBSession session) {
		// TODO not tested
		this.insert("updateBookmarkLog", param, session);
	}

	public void deleteBookmarkByContentId(final BookmarkParam param, final DBSession session) {
		this.insert("deleteBookmarkByContentId", param, session);
	}

	public Integer getContentIDForBookmark(final BookmarkParam param, final DBSession session) {
		return this.queryForObject("getContentIDForBookmark", param, Integer.class, session);
	}

	public List<Post<Bookmark>> getPosts(final BookmarkParam param, final DBSession session) {
		return chain.getFirstElement().perform(param, session);
	}
	
	public Post<Bookmark> getPostDetails(String authUser, String resourceHash, String userName, final DBSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deletePost(String userName, String resourceHash, final DBSession session) {
		// TODO: test for removal (tas, bookmark, ...)
		final BookmarkParam paramDelete = new BookmarkParam();
		paramDelete.setUserName(userName);
		paramDelete.setHash(resourceHash);	

		// return a bookmark object for current hash value
		final List<Post<Bookmark>> storeTemp = this.getBookmarkByHashForUser(paramDelete, session);
		// bookmark DOESN'T EXIST
		if (storeTemp.size() == 0) return false;

		final Post<Bookmark> provePost = storeTemp.get(0);
	    paramDelete.setRequestedContentId(provePost.getContentId());

        // counter in urls table is decremented (-1)
		this.updateBookmarkHashDec(paramDelete, session);
		// delete the selected bookmark (by given contentId) from current database table
	    this.deleteBookmarkByContentId(paramDelete, session);
	    // deleting tas
	    this.tagDb.deleteTags(provePost, session);

		return true;
	}

	// TODO: this method belongs to the logic-layer not database-layer. anyway, i would appreciate a rewrite of this copy'n'paste mess
	@SuppressWarnings("unchecked")
	public boolean storePost(String userName, Post<Bookmark> post, final String oldIntraHash, final DBSession session) {
		// TODO: implement correctly if it only had been copy'n'pasted it would have been ok, but it used contentids as tasids, hardcoded hashes and so on
		throw new UnsupportedOperationException();
	}
}