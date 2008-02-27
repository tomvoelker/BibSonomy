package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.Order;
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

	private static final BibTexDatabaseManager singleton = new BibTexDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final PermissionDatabaseManager permissionDb;
	private final DocumentDatabaseManager docDb;
	private final TagDatabaseManager tagDb;
	private final DatabasePluginRegistry plugins;
	private static final BibTexChain chain = new BibTexChain();

	private BibTexDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
		this.permissionDb = PermissionDatabaseManager.getInstance();
		this.docDb = DocumentDatabaseManager.getInstance();
	}

	public static BibTexDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	@SuppressWarnings("unchecked")
	protected List<Post<BibTex>> bibtexList(final String query, final BibTexParam param, final DBSession session) {
		return queryForList(query, param, session);
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
	 * wrapper for {@see getBibTexByHash(final BibTexParam param, final DBSession session)}
	 */
	public List<Post<BibTex>> getBibTexByHash(final String hash, final HashID hashId, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setRequestedSimHash(hashId);
		param.setSimHash(hashId);
		param.setHash(hash);
		param.setGroupType(GroupID.PUBLIC);
		return this.getBibTexByHash(param, session);
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
		if (Order.FOLKRANK.equals(param.getOrder())){
			param.setGroupId(GroupID.PUBLIC.getId());
			return this.bibtexList("getBibTexByTagNamesAndFolkrank", param, session);
		}
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
		param.setGroupType(GroupID.PUBLIC);
		param.setSimHash(HashID.INTER_HASH);
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
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBibTexForUser(param, session);
		}
		return this.bibtexList("getBibTexViewable", param, session);
	}

	public List<Post<BibTex>> getBibTexViewableByTag(final BibTexParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBibTexByTagNamesForUser(param, session);
		}
		return this.bibtexList("getBibTexViewableByTag", param, session);
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
	 * Returns the number of publications
	 * 
	 * @param groupID
	 * @param loginUserName
	 * @param session
	 * @return the (approximated) number of resources posted to the group with the given groupID
	 */
	public Integer getBibTexForGroupCount(final int groupID, final String loginUserName, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setGroupId(groupID);
		return this.getBibTexForGroupCount(param, session);
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
	 * Returns the number of publications for a given user
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param session
	 * @return the number of publications of the requested user which the logged in user is allowed to see
	 */
	public Integer getBibTexForUserCount(final String requestedUserName, final String loginUserName, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setRequestedUserName(requestedUserName);
		return this.getBibTexForUserCount(param, session);
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

	/**
	 * Returns a list containg BibTeX posts by INTER-Hash for a user
	 * 
	 * @param loginUserName
	 * @param interHash
	 * @param requestedUserName
	 * @param session
	 * @return List<Post<BibTex>> a list of BibTeX posts
	 */
	public List<Post<BibTex>> getBibTexByHashForUser(final String loginUserName, final String interHash, final String requestedUserName, final DBSession session) {
		return this.getBibTexByHashForUser(loginUserName, interHash, requestedUserName, session, HashID.INTER_HASH);
	}

	public List<Post<BibTex>> getBibTexByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final DBSession session, final HashID hashType) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setRequestedUserName(requestedUserName);
		param.setHash(intraHash);
		param.setRequestedSimHash(hashType);
		return this.getBibTexByHashForUser(param, session);
	}

	public int getContentIdForBibTex(final String hash, final String userName, final DBSession session) {
		if (present(hash) == false || present(userName) == false) {
			throw new RuntimeException("Hash and user name must be set");
		}
		final BibTexParam param = new BibTexParam();
		param.setHash(hash);
		param.setRequestedUserName(userName);
		param.setSimHash(HashID.INTRA_HASH);
		return this.queryForObject("getContentIdForBibTex", param, Integer.class, session);
	}

	public List<Post<BibTex>> getPosts(final BibTexParam param, final DBSession session) {
		return this.getPostsWithPrivnote(param, session);
	}

	/**
	 * Iterates over a list of bibtex posts and inserts private notes, if existent and if
	 * post belongs to the logged-in user
	 * 
	 * @param param a bibtex parameter object
	 * @param session a database session
	 * @return a list of bibtex posts, with private notes inserted at the appropriate places
	 */
	public List<Post<BibTex>> getPostsWithPrivnote(final BibTexParam param, final DBSession session) {
		// start the chain
		List<Post<BibTex>> posts = chain.getFirstElement().perform(param, session);
		// insert the private notes
		final BibTexExtraDatabaseManager bibtexExtraDb = BibTexExtraDatabaseManager.getInstance();
		for (final Iterator<Post<BibTex>> postsIterator = posts.iterator(); postsIterator.hasNext();) {
			final Post<BibTex> post = postsIterator.next();
			if (post.getUser().getName().equals(param.getUserName())) {
				post.getResource().setPrivnote(bibtexExtraDb.getBibTexPrivnoteForUser(post.getResource().getIntraHash(), param.getUserName(), session));
			}
		}
		return posts;
	} 

	/**
	 * Gets the details of a post, including all extra data like (TODO!) 
	 * given the INTRA-HASH of the post and the user name.
	 * 
	 * <ul>
	 * <li>extra URLs</li>
	 * <li>private notes (if userName = loginUserName)</li>
	 * <li>private PDFs (if requirements are met)<li>
	 * </ul>
	 * 
	 */
	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final DBSession session) {
		/*
		 * get post from database
		 */
		final List<Post<BibTex>> list = getBibTexByHashForUser(authUser, resourceHash, userName, session, HashID.INTRA_HASH);
		if (list.size() >= 1) {
			if (list.size() > 1) {
				/*
				 * user has multiple posts with the same hash
				 */
				log.warn("multiple BibTeX-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			/*
			 * just take first post
			 */
			final Post<BibTex> post = list.get(0);
			/*
			 * attach document hash 
			 */
			if (permissionDb.isAllowedToAccessPostsDocuments(userName, post, session)) {
				post.getResource().setDocuments(docDb.getDocuments(userName, resourceHash, session));
			}
			return post;
		}

		log.debug("BibTex-post from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' not found");
		return null;
	}

	/**
	 * Inserts a publication into the database.
	 */
	private void insertBibTex(final BibTexParam param, final DBSession session) {
		session.beginTransaction();
		try {
			// Insert BibTex
			this.insert("insertBibTex", param, session);
			// Insert/Update SimHashes
			this.insertUpdateSimHashes(param.getResource(), false, session);
			// add private note, if exists
			if (param.getResource().getPrivnote() != null) {
				final BibTexExtraDatabaseManager bibtexExtraDb = BibTexExtraDatabaseManager.getInstance();
				bibtexExtraDb.updateBibTexPrivnoteForUser(param.getResource().getIntraHash(), param.getUserName(), param.getResource().getPrivnote(), session);
			}			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Inserts and updates simHashes.
	 */
	private void insertUpdateSimHashes(final BibTex bibtex, final boolean delete, final DBSession session) {
		for (final int hashId : HashID.getHashRange()) {
			final HashID simHash = HashID.getSimHash(hashId);
			final String hash = SimHash.getSimHash(bibtex, simHash);
			// no action on an empty hash
			if (present(hash) == false) continue;

			final BibTexParam param = new BibTexParam();
			param.setRequestedSimHash(simHash);
			param.setHash(hash);

			if (delete == true) {
				// decrement counter
				this.updateBibTexHash(param, session);
			}
			else {
				// insert new hash or increment its counter, if it already exists
				this.insertBibTexHash(param, session);  
			} 				
		}
	}

	/**
	 * Inserts a post with a publication into the database.
	 */
	protected void insertBibTexPost(final Post<BibTex> post, final DBSession session) {
		if (present(post.getResource()) == false) throw new InvalidModelException("There is no resource for this post.");
		if (present(post.getGroups()) == false) throw new InvalidModelException("There are no groups for this post.");

		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(post.getContentId());
		param.setDescription(post.getDescription());
		param.setDate(post.getDate());
		param.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
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

	@SuppressWarnings("null") // because eclipse's checks are not intelligent enough for checks in methods
	public boolean storePost(final String userName, final Post<BibTex> post, final String oldIntraHash, boolean update, final DBSession session) {
		// TODO: test insertion (tas, bibtex, ...)
		session.beginTransaction();
		try {
			// the bibtex with the "old" intrahash, i.e. the one that was sent
			// within the create/update bibtex request
			final List<Post<BibTex>> isOldBibTexInDb;

			// the bookmark with the "new" intrahash, i.e. the one that was recalculated
			// based on the bookmark's fields			
			final List<Post<BibTex>> isNewBibTexInDb;
									
			// check if a user is trying to create a bibtex that already exists
			isNewBibTexInDb = this.getBibTexByHashForUser(userName, post.getResource().getIntraHash(), userName, session, HashID.INTRA_HASH);
			if (isNewBibTexInDb != null && isNewBibTexInDb.size() > 0 && update == false) {
				throw new IllegalArgumentException("Could not create new bibtex entry: This bibtex entry already exists in your collection (intrahash: " + post.getResource().getIntraHash() + ")");
			}			
			
			if (oldIntraHash != null) {
				if ((update == false) && (oldIntraHash.equals(post.getResource().getIntraHash()) == false)) {
					throw new IllegalArgumentException(
							"Could not create new bibtex: The requested intrahash " 
							+ oldIntraHash + " is not correct for this bibtex (correct intrahash is " 
							+ post.getResource().getIntraHash() + ")."
					);					
				}
				isOldBibTexInDb = this.getBibTexByHashForUser(userName, oldIntraHash, userName, session, HashID.INTRA_HASH);
			} else {
				if (update == true) {
					throw new IllegalArgumentException("Could not update bibtex entry: no intrahash specified.");
				}
				isOldBibTexInDb = null;
			}

			// ALWAYS get a new contentId
			post.setContentId(this.generalDb.getNewContentId(ConstantID.IDS_CONTENT_ID, session));

			if (present(isOldBibTexInDb)) {
				// BibTex entry DOES EXIST for this user -> delete old BibTex post
				final Post<BibTex> oldBibTexPost = isOldBibTexInDb.get(0);
				
				// if no groups are specified for an existing bibtex when updating -> take over existing groups
				// this is kind of a hack, as the JabRef-Client does not store group information so far :(
				// dbe, 2007/07/27
//				if (update == true && !present(post.getGroups())) {										
//					post.setGroups(this.groupDb.getGroupsForContentId(oldBibTexPost.getContentId(), session));
//				}				
				
				/*TODO nicht fertig*/
				/** insert addition bibtex attributes into the search table*/
				//this.plugins.onBibTexInsertintoSearch(post.getContentId(), session);
				
				this.plugins.onBibTexUpdate(post.getContentId(), oldBibTexPost.getContentId(), session);
				this.deletePost(userName, oldBibTexPost.getResource().getIntraHash(), update, session);
												
			} else {
				if (update == true) {
					log.warn("Bibtex with intrahash " + oldIntraHash + " does not exist for user " + userName);
					throw new ResourceNotFoundException(oldIntraHash);	
				}
				update = false;
				
				// if no groups are specified when inserting a new bibtex -> post it as public
				// this is kind of a hack, as the JabRef-Client does not store group information so far :(
				// dbe, 2007/08/02
//				if (!present(post.getGroups())) {
//					List<Group> groups = new ArrayList<Group>();
//					Group pub = new Group();
//					pub.setGroupId(GroupID.PUBLIC.getId());
//					pub.setName("public");
//					groups.add(pub);
//					post.setGroups(groups);
//				}				
			}
								
			this.insertBibTexPost(post, session);			
			// add the tags
			this.tagDb.insertTags(post, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return update;
	}

	public boolean deletePost(final String userName, final String resourceHash, final DBSession session) {
		return this.deletePost(userName, resourceHash, false, session);
	}

	private boolean deletePost(final String userName, final String resourceHash, final boolean update, final DBSession session) {
		// TODO: test removal (tas and bibtex ...)
		session.beginTransaction();
		try {
			final List<Post<BibTex>> bibtexs = this.getBibTexByHashForUser(userName, resourceHash, userName, session, HashID.INTRA_HASH);
			if (bibtexs.size() == 0) {
				// BibTex doesn't exist
				log.debug("post not found");
				return false;
			}

			final Post<? extends Resource> oneBibtex = bibtexs.get(0);
			final BibTexParam param = new BibTexParam();
			param.setRequestedContentId(oneBibtex.getContentId());
			if (update == false) this.plugins.onBibTexDelete(param.getRequestedContentId(), session);
			this.tagDb.deleteTags(oneBibtex, session);
			this.insertUpdateSimHashes(((BibTex) oneBibtex.getResource()), true, session);
			this.deleteBibTex(param, session);

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
	
	/**
	 * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name (restricted by group public).
	 * @param param 
	 * @param session 
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getBibTexByAuthor(final BibTexParam param, final DBSession session){
		return this.bibtexList("getBibTexByAuthor",param,session);
	}
	/**
	 * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name and TagName(restricted by group public).
	 * @param param 
	 * @param session 
	 * @return list of bibtex entries
	 */
    public List<Post<BibTex>> getBibTexByAuthorAndTag(final BibTexParam param, final DBSession session){
		return this.bibtexList("getBibTexByAuthorAndTag",param,session);
	}
    
    /**
     * <em>/concept/tag/TAGNAME</em>
   	 * --
     * @param param
     * @param session
     * @return
     */
	public List<Post<BibTex>> getBibTexByConceptByTag(final BibTexParam param, final DBSession session){
		return this.bibtexList("getBibTexByConceptByTag",param,session);
	}
}