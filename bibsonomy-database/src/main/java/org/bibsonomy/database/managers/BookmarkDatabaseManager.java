package org.bibsonomy.database.managers;

import java.util.List;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Used to retrieve bookmarks from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class BookmarkDatabaseManager extends AbstractDatabaseManager {

	private final static BookmarkDatabaseManager db = new BookmarkDatabaseManager();
	private final GeneralDatabaseManager gdb = GeneralDatabaseManager.getInstance();

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	private BookmarkDatabaseManager() {
	}

	public static BookmarkDatabaseManager getInstance() {
		return db;
	}

	/**
	 * Can be used to start a query that retrieves a list of bookmarks.
	 */
	@SuppressWarnings("unchecked")
	protected List<Bookmark> bookmarkList(final String query, final BookmarkParam param) {
		return (List<Bookmark>) queryForAnything(query, param, QueryFor.LIST);
	}

	// FIXME return value needs to be changed to org.bibsonomy.model.Post
	@SuppressWarnings("unchecked")
	protected List<Post<? extends Resource>> bookmarkList(final String query, final BookmarkParam param, final boolean test) {
		return (List<Post<? extends Resource>>) queryForAnything(query, param, QueryFor.LIST);
	}

	/**
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached. 
	 */
	public List<Post<? extends Resource>> getBookmarkByTagNames(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkByTagNames", param,true);
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
	public List<Post<? extends Resource>> getBookmarkByTagNamesForUser(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForUser(this.gdb, param);
		return this.bookmarkList("getBookmarkByTagNamesForUser", param,true);
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
	public List<Post<? extends Resource>> getBookmarkByConceptForUser(final BookmarkParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return this.bookmarkList("getBookmarkByConceptForUser", param,true);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have currUser as
	 * their friend.
	 */
	public List<Post<? extends Resource>> getBookmarkByUserFriends(final BookmarkParam param) {
		// groupType must be set to friends
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bookmarkList("getBookmarkByUserFriends", param,true);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<Post<? extends Resource>> getBookmarkForHomepage(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkForHomepage", param,true);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<Post<? extends Resource>> getBookmarkPopular(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkPopular", param,true);
	}

	/**
	 * Prepares a query which retrieves all bookmarks which are represented by
	 * the given hash. Retrieves only public bookmarks!
	 */
	public List<Post<? extends Resource>> getBookmarkByHash(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkByHash", param,true);
	}

	/**
	 * Retrieves the number of bookmarks represented by the given hash.
	 */
	public Integer getBookmarkByHashCount(final BookmarkParam param) {
		return (Integer) this.queryForObject("getBookmarkByHashCount", param);
	}

	/**
	 * Prepares a query which retrieves the bookmark (which is represented by
	 * the given hash) for a given user. Since user name is given, full group
	 * checking is done, i.e. everbody who may see the bookmark will see it.
	 */
	public List<Post<? extends Resource>> getBookmarkByHashForUser(final BookmarkParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return this.bookmarkList("getBookmarkByHashForUser", param,true);
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
	public List<Post<? extends Resource>> getBookmarkSearch(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkSearch", param,true);
	}

	/**
	 * Returns the number of bookmarks for a given search.
	 */
	public Integer getBookmarkSearchCount(final BookmarkParam param) {
		return (Integer) this.queryForObject("getBookmarkSearchCount", param);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 */
	public List<Post<? extends Resource>> getBookmarkViewable(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkViewable", param,true);
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
	public List<Post<? extends Resource>> getBookmarkForGroup(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.gdb, param);
		return this.bookmarkList("getBookmarkForGroup", param,true);
	}

	/**
	 * Returns the number of bookmarks belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBookmarkForGroupCount(final BookmarkParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return (Integer) this.queryForObject("getBookmarkForGroupCount", param);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBookmarkForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<? extends Resource>> getBookmarkForGroupByTag(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.gdb, param);
		return this.bookmarkList("getBookmarkForGroupByTag", param,true);
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
	public List<Post<? extends Resource>> getBookmarkForUser(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForUser(this.gdb, param);
		return this.bookmarkList("getBookmarkForUser", param, true);
	}

	/**
	 * Returns the number of bookmarks for a given user.
	 */
	public Integer getBookmarkForUserCount(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForUser(this.gdb, param);
		return (Integer) this.queryForObject("getBookmarkForUserCount", param);
	}


	
	/**
	 * This methods  are for setting functions concerning bookmark entries
	 */
	  public void insertBookmark(final BookmarkParam bookmark) {
		this.insert("insertBookmark", bookmark);
	}

	public void insertBookmarkLog(final BookmarkParam bookmark) {
		// TODO not tested
		this.insert("insertBookmarkLog", bookmark);
	}
/*
 * insert counter, hash and url of bookmark
 */
	public void insertBookmarkInc(final Bookmark param) {
		this.insert("insertBookmarkInc", param);
	}

	public void updateBookmarkHashDec(final BookmarkParam param) {
		this.insert("updateBookmarkHashDec", param);
	}

	public void updateBookmarkLog(final BookmarkParam param) {
		// TODO not tested
		this.insert("updateBookmarkLog", param);
	}

	public void deleteBookmarkByContentId(final BookmarkParam param) {
		this.insert("deleteBookmarkByContentId", param);
	}
	/**
	 * Get a current ContentID for setting a bookmark update the current
	 * ContendID for bookmark and bibtex
	 */
	
	public Integer getCurrentContentIdFromIds(final BookmarkParam param) {
		return (Integer) this.queryForObject("getNewContentID", param);
	}

	public void updateIds(final BookmarkParam param) {
		this.insert("updateIds", param);
	}
	
	public Integer getCurrentTasIdFromIds(final BookmarkParam param) {
		return (Integer) this.queryForObject("getNewTasID", param);
	}

	public Integer getContentIDForBookmark(final BookmarkParam param) {
		return (Integer) this.queryForObject("getContentIDForBookmark", param);
	}
    	
}