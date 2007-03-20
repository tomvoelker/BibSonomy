package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Used to retrieve BibTexs from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class BibTexDatabaseManager extends AbstractDatabaseManager {

	private final static BibTexDatabaseManager db = new BibTexDatabaseManager();
	private final GeneralDatabaseManager gdb = GeneralDatabaseManager.getInstance();

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	private BibTexDatabaseManager() {
	}
	
	public static BibTexDatabaseManager getInstance(){
		return db;
	}
	
	
	
	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	
	@SuppressWarnings("unchecked")
     protected List<Post<? extends Resource>> bibtexList(final String query, final BibTexParam param) {
		return (List<Post<? extends Resource>>) queryForAnything(query, param, QueryFor.LIST);
	}
	
	/**
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all publications whose hash
	 * requestedSimHash is equal to a given hash. Only public posts are
	 * retrieved.
	 */
	public List<Post<? extends Resource>> getBibTexByHash(final BibTexParam param) {
		/********TODO write some Expectation values for all methods**********/
		return this.bibtexList("getBibTexByHash", param);
	}

	/**
	 * Returns the number of publications for a given hash.
	 */
	public Integer getBibTexByHashCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexByHashCount", param);
	}

	/**
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached.
	 */
	public List<Post<? extends Resource>> getBibTexByTagNames(final BibTexParam param) {
		return this.bibtexList("getBibTexByTagNames", param);
	}

	/**
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if currUser us allowed
	 * to see them.
	 */
	public List<Post<? extends Resource>> getBibTexByTagNamesForUser(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.gdb, param);
		return this.bibtexList("getBibTexByTagNamesForUser", param);
	}

	/**
	 * <em>/concept/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
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
	public List<Post<? extends Resource>> getBibTexByConceptForUser(final BibTexParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return this.bibtexList("getBibTexByConceptForUser", param);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have userName as
	 * their friend.
	 */
	public List<Post<? extends Resource>> getBibTexByUserFriends(final BibTexParam param) {
		// groupType must be set to friends
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param);
	}

	/**
	 * This method prepares a query which retrieves all publications the user
	 * has in his download list. The result is shown on the page
	 * <em>/download</em>. Since every user can only see his <em>own</em>
	 * download page, we use userName as restriction for the user name and not
	 * requestedUserName.
	 */
	public List<Post<? extends Resource>> getBibTexByDownload(final BibTexParam param) {
		return this.bibtexList("getBibTexByDownload", param);
	}

	/**
	 * This method prepares queries which retrieve all publications for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<Post<? extends Resource>> getBibTexForHomePage(final BibTexParam param) {
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexForHomePage", param);
	}

	/**
	 * This method prepares queries which retrieve all publications for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<Post<? extends Resource>> getBibTexPopular(final BibTexParam param) {
		return this.bibtexList("getBibTexPopular", param);
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
	public List<Post<? extends Resource>> getBibTexSearch(final BibTexParam param) {
		return this.bibtexList("getBibTexSearch", param);
	}

	/**
	 * Returns the number of publications for a given search.
	 */
	public Integer getBibTexSearchCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexSearchCount", param);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 */
	public List<Post<? extends Resource>> getBibTexViewable(final BibTexParam param) {
		return this.bibtexList("getBibTexViewable", param);
	}

	/**
	 * Prepares a query which returns all duplicate BibTex posts of the
	 * requested user. Duplicates are BibTex posts which have the same simhash1,
	 * but a different simhash0 (the latter is always true within the posts of a
	 * single user).
	 */
	public List<Post<? extends Resource>> getBibTexDuplicate(final BibTexParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return this.bibtexList("getBibTexDuplicate", param);
	}

	/**
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 */
	public Integer getBibTexDuplicateCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexDuplicateCount", param);
	}

	/**
	 * <em>/group/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries which show all publications of all users belonging to
	 * the group. This is an aggregated view of all posts of the group members.<br/>
	 * Full viewable-for checking is done, i.e. everybody sees everything he is
	 * allowed to see.<br/>
	 * 
	 * See also
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
	 * 92, formula (9) for formal semantics of this query.
	 */
	public List<Post<? extends Resource>> getBibTexForGroup(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.gdb, param);
		return this.bibtexList("getBibTexForGroup", param);
	}

	/**
	 * Returns the number of publications belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBibTexForGroupCount(final BibTexParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return (Integer) this.queryForObject("getBibTexForGroupCount", param);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBibTexForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<? extends Resource>> getBibTexForGroupByTag(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.gdb, param);
		return this.bibtexList("getBibTexForGroupByTag", param);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 */
	public List<Post<? extends Resource>> getBibTexForUser(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.gdb, param);
		return this.bibtexList("getBibTexForUser", param);
	}

	/**
	 * Returns the number of publications for a given user.
	 */
	public Integer getBibTexForUserCount(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.gdb, param);
		return (Integer) this.queryForObject("getBibTexForUserCount", param);
	}

	/**
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68/MaxMustermann</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all bibtex posts whose hash no. requSim
	 * is equal to requBibtex and they're owned by requUser. Full group checking
	 * is done.<br/>
	 * 
	 * Additionally, if requUser = currUser, the document table is joined so
	 * that we can present the user a link to the uploaded document.
	 */
	public List<Post<? extends Resource>> getBibTexByHashForUser(final BibTexParam param) {
		DatabaseUtils.setGroups(this.gdb, param);
		return this.bibtexList("getBibTexByHashForUser", param);
	}

	/********get a content_id by a given user and a given hash***********/
	public Integer getContentIdByUserAndHash(final BibTex bibtex){
		return (Integer)this.queryForObject("getContentIdByUserAndHash", bibtex);
	}

	/**********modify update to select, return is list of String**************/
	public String getBibTexSimHashsByContentId(final BibTex param) {
		// TODO not tested
		return (String)this.queryForObject("getBibTexSimHashsByContentId", param);
	}

	/**
	 * Inserts a publication into the database.
	 */
	public void insertBibTex(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTex", param);
	}

	public void insertBibTexLog(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexLog", param);
	}

	/**
	 * Inserts a BibTex-hash into the database.
	 */
	public void insertBibTexHash(final BibTexParam param) {
		// TODO not tested
		if (param.getHash() == null || param.getHash().equals("")) {
			throw new RuntimeException("Hash must be set");
		}
		this.insert("insertBibTexHash", param);
	}

	public void insertBibTexHash1Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHashInc", param);
	}
	
	public void insertBibTexHash2Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHash2Inc", param);
	}
	
	public void insertBibTexHash3Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHash3Inc", param);
	}
	
	public void insertBibTexHash4Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHash4Inc", param);
	}

	public void updateBibTexHash1Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHashDec", param);
	}

	public void updateBibTexHash2Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHash2Dec", param);
	}

	public void updateBibTexHash3Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHash3Dec", param);
	}

	public void updateBibTexHash4Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHash4Dec", param);
	}

	public void updateBibTexLog(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexLog", param);
	}

	public void updateBibTexDocument(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexDocument", param);
	}

	public void updateBibTexCollected(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexCollected", param);
	}

	public void updateBibTexExtended(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexExtended", param);
	}

	public void updateBibTexUrl(final BibTexParam param) {
		// TODO not tested
		this.update("updateBibTexUrl", param);
	}

	public void deleteBibTexByContentId(final BibTex param) {
		// TODO not tested
		this.update("deleteBibTexByContentId", param);
	}

	public void deleteBibTexDocumentByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexDocumentByContentId", param);
	}

	public void deleteBibTexCollectedByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexCollectedByContentId", param);
	}

	public void deleteBibTexExtendedByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexExtendedByContentId", param);
	}

	public void deleteBibTexUrlByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexUrlByContentId", param);
	}
		
}