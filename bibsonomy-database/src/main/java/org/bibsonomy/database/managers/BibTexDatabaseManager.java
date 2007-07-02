package org.bibsonomy.database.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.SimHash;

/**
 * Used to CRUD BibTexs from the database.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexDatabaseManager extends AbstractDatabaseManager implements CrudableContent<BibTex, BibTexParam> {
	private static final Logger log = Logger.getLogger(BibTexDatabaseManager.class);
	
	/** Singleton */
	private final static BibTexDatabaseManager singleton = new BibTexDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagDatabaseManager tagDb;
	private final DatabasePluginRegistry plugins;
	private static final BibTexChain chain = new BibTexChain();

	private BibTexDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	public static BibTexDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	@SuppressWarnings("unchecked")
	protected List<Post<BibTex>> bibtexList(final String query, final BibTexParam param, final DBSession session) {
		return (List<Post<BibTex>>) queryForList(query, param, session);
	}

	/**
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all publications whose hash
	 * requestedSimHash is equal to a given hash. Only public posts are
	 * retrieved.
	 */
	public List<Post<BibTex>> getBibTexByHash(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexByHash", param, session);
	}

	/**
	 * Returns the number of publications for a given hash.
	 */
	public Integer getBibTexByHashCount(final BibTexParam param, final DBSession session) {
		return this.queryForObject("getBibTexByHashCount", param, Integer.class, session);
	}

	/**
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached.
	 */
	public List<Post<BibTex>> getBibTexByTagNames(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexByTagNames", param, session);
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
	public List<Post<BibTex>> getBibTexByTagNamesForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		HashID.getSimHash(param.getSimHash()); // ensures correct simHash is set (exception would be thrown otherwise)
		return this.bibtexList("getBibTexByTagNamesForUser", param, session);
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
	public List<Post<BibTex>> getBibTexByConceptForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.bibtexList("getBibTexByConceptForUser", param, session);
	}
	
	public List<Post<BibTex>> getBibTexByConceptForUser(final String loginUser, final String conceptName, final String requestedUser, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUser);
		param.setRequestedUserName(requestedUser);
		param.addSimpleConceptName(conceptName);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.getBibTexByConceptForUser(param, session);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have userName as
	 * their friend.
	 */
	public List<Post<BibTex>> getBibTexByUserFriends(final BibTexParam param, final DBSession session) {
		// groupType must be set to friends
		param.setGroupType(GroupID.FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param, session);
	}

	/**
	 * This method prepares a query which retrieves all publications the user
	 * has in his download list. The result is shown on the page
	 * <em>/download</em>. Since every user can only see his <em>own</em>
	 * download page, we use userName as restriction for the user name and not
	 * requestedUserName.
	 */
	public List<Post<BibTex>> getBibTexByDownload(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexByDownload", param, session);
	}

	/**
	 * This method prepares queries which retrieve all publications for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<Post<BibTex>> getBibTexForHomePage(final BibTexParam param, final DBSession session) {
		param.setGroupType(GroupID.FRIENDS);
		param.setLimit(15);
		param.setOffset(0);
		return this.bibtexList("getBibTexForHomePage", param, session);
	}

	/**
	 * This method prepares queries which retrieve all publications for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<Post<BibTex>> getBibTexPopular(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexPopular", param, session);
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
	public List<Post<BibTex>> getBibTexSearch(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexSearch", param, session);
	}

	/**
	 * Returns the number of publications for a given search.
	 */
	public Integer getBibTexSearchCount(final BibTexParam param, final DBSession session) {
		return this.queryForObject("getBibTexSearchCount", param, Integer.class, session);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 */
	public List<Post<BibTex>> getBibTexViewable(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexViewable", param, session);
	}

	/**
	 * Prepares a query which returns all duplicate BibTex posts of the
	 * requested user. Duplicates are BibTex posts which have the same simhash1,
	 * but a different simhash0 (the latter is always true within the posts of a
	 * single user).
	 */
	public List<Post<BibTex>> getBibTexDuplicate(final BibTexParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.bibtexList("getBibTexDuplicate", param, session);
	}

	/**
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 */
	public Integer getBibTexDuplicateCount(final BibTexParam param, final DBSession session) {
		return this.queryForObject("getBibTexDuplicateCount", param, Integer.class, session);
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
	public List<Post<BibTex>> getBibTexForUsersInGroup(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexForUsersInGroup", param, session);
	}
	
	public List<Post<BibTex>> getBibTexForUsersInGroup(final String loginUserName, final Integer groupId, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setGroupId(groupId);
		return getBibTexForUsersInGroup(param, session);
	}

	/**
	 * Returns the number of publications belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBibTexForGroupCount(final BibTexParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.queryForObject("getBibTexForGroupCount", param, Integer.class, session);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBibTexForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<BibTex>> getBibTexForGroupByTag(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexForGroupByTag", param, session);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 * 
	 * ATTENTION! in case of a given groupId it is NOT checked if the user actually
	 * belongs to this group.
	 */
	public List<Post<BibTex>> getBibTexForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bibtexList("getBibTexForUser", param, session);
	}

	/**
	 * Returns the number of publications for a given user.
	 */
	public Integer getBibTexForUserCount(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForObject("getBibTexForUserCount", param, Integer.class, session);
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
	public List<Post<BibTex>> getBibTexByHashForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.setGroups(this.generalDb, param, session);
		return this.bibtexList("getBibTexByHashForUser", param, session);
	}
	
	public List<Post<BibTex>> getBibTexByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final DBSession session) {
		return getBibTexByHashForUser(loginUserName, intraHash, requestedUserName, session, HashID.INTER_HASH);
	}
	
	public List<Post<BibTex>> getBibTexByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final DBSession session, final HashID hashType) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setRequestedUserName(requestedUserName);
		param.setHash(intraHash);
		param.setRequestedSimHash(hashType);
		return getBibTexByHashForUser(param, session);
	}

	public List<Post<BibTex>> getPosts(final BibTexParam param, final DBSession session) {
		return chain.getFirstElement().perform(param, session);
	}

	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final DBSession session) {
		final List<Post<BibTex>> list = getBibTexByHashForUser(authUser, resourceHash, userName, session, HashID.INTRA_HASH);
		if (list.size() >= 1) {
			if (list.size() > 1) {
				log.warn("multiple BibTex-post from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			return list.get(0);
		} else {
			log.debug("BibTex-post from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' not found");
			return null;
		}
	}

	/**
	 * Inserts a publication into the database.
	 */
	private void insertBibTex(final BibTexParam param, final DBSession session) {
		// Start transaction
		session.beginTransaction();
		try {
			// Insert BibTex
			this.insert("insertBibTex", param, session);
			// Insert/Update SimHashes
			for (final int i : new int[] { 0, 1, 2, 3 }) {
				final HashID simHash = HashID.getSimHash(i);
				param.setRequestedSimHash(simHash);
				param.setHash(SimHash.getSimHash(param.getResource(), simHash));
				this.insertBibTexHash(param, session);
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Inserts a post with a publication into the database.
	 */
	protected void insertBibTexPost(final Post<BibTex> post, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(post.getContentId());
		param.setDescription(post.getDescription());
		for (final Group group : post.getGroups()) {
			param.setGroupId(group.getGroupId());
			this.insertBibTex(param, session);
		}
	}

	/**
	 * Inserts a BibTex-hash into the database.
	 */
	private void insertBibTexHash(final BibTexParam param, final DBSession session) {
		this.insert("insertBibTexHash", param, session);
	}

	public boolean storePost(final String userName, final Post<BibTex> post, final String oldIntraHash,  boolean update, final DBSession session) {
		// TODO: test insertion (tas, bibtex, ...)
		session.beginTransaction();
		try {
			final List<Post<BibTex>> isBibTexInDb;
			if (oldIntraHash != null) {
				if ((update == false) && (oldIntraHash.equals(post.getResource().getIntraHash()) == false)) {
					throw new IllegalArgumentException("cannot create new resource with an old hash value");
				}
				isBibTexInDb = this.getBibTexByHashForUser(userName, oldIntraHash, userName, session);
			} else {
				if (update == true) {
					throw new IllegalArgumentException("cannot update without old hash value");
				}
				isBibTexInDb = null;
			}
			// ALWAYS get a new contentId
			post.setContentId(this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, session));

			if ((isBibTexInDb != null) && (isBibTexInDb.size() > 0)) {
				// BibTex entry DOES EXIST for this user -> delete old BibTex post
				final Post oldBibTexPost = isBibTexInDb.get(0);
				this.plugins.onBibTexUpdate(post.getContentId(), oldBibTexPost.getContentId(), session);
				this.deletePost(userName, oldBibTexPost.getResource().getIntraHash(), session);
			} else {
				if (update == true) {
					final String errorMsg = "cannot update nonexisting BibTex-post with intrahash " + oldIntraHash + " for user " + userName;
					log.warn(errorMsg);
					throw new ValidationException(errorMsg);
				}
				update = false;
			}
			this.insertBibTexPost(post, session);
			// add the tags
			this.tagDb.insertTags(post, session);
			
			// TODO: update: log, doc, col, ext, url

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return update;
	}

	public boolean deletePost(final String userName, final String resourceHash, final DBSession session) {
		// TODO: test removal (tas and bibtex ...)
		session.beginTransaction();
		try {
			// Used for userName, hash and contentId
			final BibTexParam param = new BibTexParam();
			param.setUserName(userName);
			param.setHash(resourceHash);

			final List<Post<BibTex>> bibtexs = this.getBibTexByHashForUser(param, session);
			if (bibtexs.size() == 0) {
				// BibTex doesn't exist
				return false;
			}

			final Post<? extends Resource> oneBibtex = bibtexs.get(0);
			param.setRequestedContentId(oneBibtex.getContentId());

			this.tagDb.deleteTags(oneBibtex, session);
			// Update SimHashes
			for (final int i : new int[] { 0, 1, 2, 3 }) {
				final HashID simHash = HashID.getSimHash(i);
				param.setRequestedSimHash(simHash);
				param.setHash(SimHash.getSimHash(((BibTex) oneBibtex.getResource()), simHash));
				this.updateBibTexHash(param, session);
			}
			this.deleteBibTex(param, session);
			// Delete link to related document
			this.deleteBibTexDoc(param, session);
			// Delete id in collector table
			this.deleteBibTexCol(param, session);
			// Delete id in extended fields table
			this.deleteBibTexExt(param, session);
			// Delete id in bibtexturl table
			this.deleteBibTexUrls(param, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return true;
	}

	private void deleteBibTex(final BibTexParam param, final DBSession session) {
		this.delete("deleteBibTex", param, session);
	}

	private void updateBibTexHash(final BibTexParam param, final DBSession session) {
		this.update("updateBibTexHash", param, session);
	}

	private void deleteBibTexDoc(final BibTexParam param, final DBSession session) {
		this.delete("deleteBibTexDoc", param, session);
	}

	private void deleteBibTexCol(final BibTexParam param, final DBSession session) {
		this.delete("deleteBibTexCol", param, session);
	}

	private void deleteBibTexExt(final BibTexParam param, final DBSession session) {
		this.delete("deleteBibTexExt", param, session);
	}

	private void deleteBibTexUrls(final BibTexParam param, final DBSession session) {
		this.delete("deleteBibTexUrls", param, session);
	}
}