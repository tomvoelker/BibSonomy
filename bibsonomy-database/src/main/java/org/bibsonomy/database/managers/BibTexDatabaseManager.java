package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.SimHash;

/**
 * Used to CRUD BibTexs from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class BibTexDatabaseManager extends AbstractDatabaseManager implements CrudableContent {

	/** Singleton */
	private final static BibTexDatabaseManager singleton = new BibTexDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagDatabaseManager tagDb;

	private BibTexDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
	}

	public static BibTexDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	@SuppressWarnings("unchecked")
	protected List<Post<? extends Resource>> bibtexList(final String query, final BibTexParam param) {
		return (List<Post<? extends Resource>>) queryForList(query, param, null);
	}

	/**
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all publications whose hash
	 * requestedSimHash is equal to a given hash. Only public posts are
	 * retrieved.
	 */
	public List<Post<? extends Resource>> getBibTexByHash(final BibTexParam param) {
		return this.bibtexList("getBibTexByHash", param);
	}

	/**
	 * Returns the number of publications for a given hash.
	 */
	public Integer getBibTexByHashCount(final BibTexParam param) {
		return this.queryForObject("getBibTexByHashCount", param, Integer.class, null);
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
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param);
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
		DatabaseUtils.setGroups(this.generalDb, param);
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
		return this.queryForObject("getBibTexSearchCount", param, Integer.class, null);
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
		DatabaseUtils.setGroups(this.generalDb, param);
		return this.bibtexList("getBibTexDuplicate", param);
	}

	/**
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 */
	public Integer getBibTexDuplicateCount(final BibTexParam param) {
		return this.queryForObject("getBibTexDuplicateCount", param, Integer.class, null);
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
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param);
		return this.bibtexList("getBibTexForGroup", param);
	}

	/**
	 * Returns the number of publications belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBibTexForGroupCount(final BibTexParam param) {
		DatabaseUtils.setGroups(this.generalDb, param);
		return this.queryForObject("getBibTexForGroupCount", param, Integer.class, null);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBibTexForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<? extends Resource>> getBibTexForGroupByTag(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param);
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
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param);
		return this.bibtexList("getBibTexForUser", param);
	}

	/**
	 * Returns the number of publications for a given user.
	 */
	public Integer getBibTexForUserCount(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param);
		return this.queryForObject("getBibTexForUserCount", param, Integer.class, null);
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
		DatabaseUtils.setGroups(this.generalDb, param);
		return this.bibtexList("getBibTexByHashForUser", param);
	}

	/**
	 * Inserts a publication into the database.
	 */
	public void insertBibTex(final BibTexParam param) {
		// Start transaction
		final Transaction transaction = this.getTransaction(true);

		// Insert BibTex
		this.insert("insertBibTex", param, transaction);
		// Insert/Update SimHashes
		for (final int i : new int[] { 0, 1, 2, 3 }) {
			final ConstantID simHash = ConstantID.getSimHash(i);
			param.setRequestedSimHash(simHash);
			param.setHash(SimHash.getSimHash(param.getResource(), simHash));
			this.insertBibTexHash(param);
		}

		// End transaction
		transaction.commitTransaction();
	}

	/**
	 * Inserts a BibTex-hash into the database.
	 */
	private void insertBibTexHash(final BibTexParam param) {
		this.insert("insertBibTexHash", param);
	}

	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
		return GenericChainHandler.getInstance().perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
	}

	public Post<Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		return null;
	}

	public boolean deletePost(String userName, String resourceHash) {
		// Start transaction
		final Transaction transaction = this.getTransaction(true);

		// Used for userName, hash and contentId
		final BibTexParam param = new BibTexParam();
		param.setUserName(userName);
		param.setHash(resourceHash);

		final List<Post<? extends Resource>> bibtexs = this.getBibTexByHashForUser(param);
		// BibTex doesn't exist
		if (bibtexs.size() == 0) return true;

		final Post<? extends Resource> oneBibtex = bibtexs.get(0);
		param.setRequestedContentId(oneBibtex.getContentId());

		// Delete Tas
		this.tagDb.deleteTas(param);
		// Update SimHashes
		for (final int i : new int[] { 0, 1, 2, 3 }) {
			final ConstantID simHash = ConstantID.getSimHash(i);
			param.setRequestedSimHash(simHash);
			param.setHash(SimHash.getSimHash(((BibTex) oneBibtex.getResource()), simHash));
			this.updateBibTexHash(param, transaction);
		}
		// Delete BibTex
		this.deleteBibTex(param, transaction);
		// Delete link to related document
		this.deleteBibTexDoc(param, transaction);
		// Delete id in collector table
		this.deleteBibTexCol(param, transaction);
		// Delete id in extended fields table
		this.deleteBibTexExt(param, transaction);
		// Delete id in bibtexturl table
		this.deleteBibTexUrls(param, transaction);

		// End transaction
		transaction.commitTransaction();
		return true;
	}

	private void deleteBibTex(final BibTexParam param, final Transaction transaction) {
		this.delete("deleteBibTex", param, transaction);
	}

	private void updateBibTexHash(final BibTexParam param, final Transaction transaction) {
		this.update("updateBibTexHash", param, transaction);
	}

	private void deleteBibTexDoc(final BibTexParam param, final Transaction transaction) {
		this.delete("deleteBibTexDoc", param, transaction);
	}

	private void deleteBibTexCol(final BibTexParam param, final Transaction transaction) {
		this.delete("deleteBibTexCol", param, transaction);
	}

	private void deleteBibTexExt(final BibTexParam param, final Transaction transaction) {
		this.delete("deleteBibTexExt", param, transaction);
	}

	private void deleteBibTexUrls(final BibTexParam param, final Transaction transaction) {
		this.delete("deleteBibTexUrls", param, transaction);
	}

	public boolean storePost(String userName, Post post, boolean update) {
		// Start transaction
		final Transaction transaction = this.getTransaction(true);

		// Used for userName, hash and contentId
		final BibTexParam param = new BibTexParam();
		param.setUserName(userName);
		param.setResource((BibTex) post.getResource());
		// param.setHash(post.getResource().get)

		// BibTex entry does NOT exist for this user
		final List<Post<? extends Resource>> isBibTexInDb = this.getBibTexByHashForUser(param);
		if (isBibTexInDb.size() == 0) {
			param.setId(this.generalDb.getNewContentId(param));
		} else {
			final Post oldBibTexPost = isBibTexInDb.get(0);
			param.setId(oldBibTexPost.getContentId());
			// Delete old BibTex post
			// TODO intra or inter hash ???
			this.deletePost(userName, oldBibTexPost.getResource().getIntraHash());
		}
		// Insert the new BibTex
		this.insertBibTex(param);
		// TODO: insertTags, insertRelations, update: log, doc, col, ext, url

		// End transaction
		transaction.commitTransaction();
		return true;
	}
}