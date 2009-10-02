package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * Used to create, read, update and delete BibTexs from the database.
 * 
 * FIXME: why do some methods use loginUserName and some methods not? Shouldn't all methods
 * need loginUserName?
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Daniel Zoller
 * 
 * @version $Id$
 */
public class BibTexDatabaseManager extends PostDatabaseManager<BibTex, BibTexParam> {
	private static final Log log = LogFactory.getLog(BibTexDatabaseManager.class);
	
	private static final BibTexDatabaseManager singleton = new BibTexDatabaseManager();
	
	private static final HashID[] hashRange = HashID.getAllHashIDs();
	private static final FirstChainElement<Post<BibTex>, BibTexParam> chain = new BibTexChain();
	
	/**
	 * @return BibTexDatabaseManager
	 */
	public static BibTexDatabaseManager getInstance() {
		return singleton;
	}
	
	
	private final BibTexExtraDatabaseManager extraDb;
	private final DocumentDatabaseManager docDb;
	
	private BibTexDatabaseManager() {
		super();
		
		this.docDb = DocumentDatabaseManager.getInstance();
		this.extraDb = BibTexExtraDatabaseManager.getInstance();
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	@SuppressWarnings("unchecked")
	protected List<Post<BibTex>> bibtexList(final String query, final BibTexParam param, final DBSession session) {
		return queryForList(query, param, session);
	}

	/**
	 * XXXDZ
	 * 
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all publications whose hash
	 * simHash is equal to a given hash. Only public posts are
	 * retrieved.
	 * 
	 * @param hash
	 * @param hashId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	@Deprecated
	public List<Post<BibTex>> getBibTexByHash(final String hash, final HashID hashId, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setSimHash(hashId);
		param.setHash(hash);
		param.setGroupType(GroupID.PUBLIC);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bibtexList("getBibTexByHash", param, session);
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
	 * @param groupId
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByTagNames(final Integer groupId, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setTagIndex(tagIndex);
		if (Order.FOLKRANK.equals(param.getOrder())){
			param.setGroupId(GroupID.PUBLIC.getId());
			return this.bibtexList("getBibTexByTagNamesAndFolkrank", param, session);
		}
		return this.bibtexList("getBibTexByTagNames", param, session);
	}

	/**
	 * TODO: remove me!!!
	 * 
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if currUser us allowed
	 * to see them.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	@Deprecated
	public List<Post<BibTex>> getBibTexByTagNamesForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		HashID.getSimHash(param.getSimHash()); // ensures correct simHash is set (exception would be thrown otherwise)
		
		// if user wants to retrieve documents
		if (present(param.getFilter())) {
			if (param.isDocumentsAttached() && param.getFilter().equals(FilterEntity.JUST_PDF)) {
				return this.bibtexList("getJustBibTexByTagNamesForUserWithPDF", param, session);
			}
			throw new IllegalArgumentException("Filter " + param.getFilter().name() + " not supported");
		}
		
		// posts including documents
		if (param.isDocumentsAttached()) {
			return this.bibtexList("getBibTexByTagNamesForUserWithPDF", param, session);
		}
		// posts only
		return this.bibtexList("getBibTexByTagNamesForUser", param, session);
	}
	
	/**
	 * XXXDZ
	 * 
	 * @see BibTexDatabaseManager#getBibTexByTagNamesForUser(BibTexParam, DBSession)
	 * 
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param tagIndex
	 * @param groupId
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByTagNamesForUser(final String requestedUserName, final int limit, final int offset, final List<TagIndex> tagIndex, final int groupId, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setTagIndex(tagIndex);
		param.setGroupId(groupId);
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		HashID.getSimHash(param.getSimHash()); // ensures correct simHash is set (exception would be thrown otherwise)
		return this.bibtexList("getBibTexByTagNamesForUser", param, session);
	}

	/**
	 * TODO: remove me
	 * 
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
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByConceptForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexByConceptForUser", param, session);
	}
	
	/**
	 * XXXDZ
	 * 
	 * @see BibTexDatabaseManager#getBibTexByConceptForUser(BibTexParam, DBSession)
	 * 
	 * @param loginUser
	 * @param requestedUserName
	 * @param tagIndex
	 * @param visibleGroupIDs 
	 * @param caseSensitive
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByConceptForUser(final String loginUser, final  String requestedUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final boolean caseSensitive, final int limit, final int offset, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setUserName(loginUser); // original parameter: userName
		param.setRequestedUserName(requestedUserName);
		param.setGroups(visibleGroupIDs);
		param.setTagIndex(tagIndex);
		param.setCaseSensitiveTagNames(caseSensitive);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexByConceptForUser", param, session);
	}

	/**
	 * XXXDZ
	 * 
	 * <em>/concept/group/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * This method retrieves all bibtex of all group members of the given group
	 * which are tagged at least with one of the concept tags or its subtags.
	 * 
	 * @param loginUser
	 * @param conceptName
	 * @param requestedUser
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByConceptForGroup(final String loginUser, final String conceptName, final String requestedUser, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUser);
		param.setRequestedUserName(requestedUser);
		param.addSimpleConceptName(conceptName);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexByConceptForGroup", param, session);
	}

	/**
	 * TODO: remove me!!!
	 * 
	 * This method prepares a query which retrieves all publications the user
	 * has in his basket list. The result is shown on the page
	 * <em>/basket</em>. Since every user can only see his <em>own</em>
	 * basket page, we use userName as restriction for the user name and not
	 * requestedUserName.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexFromBasketForUser(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexFromBasketForUser", param, session);
	}

	/**
	 * XXXDZ
	 * 
	 * @see BibTexDatabaseManager#getBibTexFromBasketForUser(BibTexParam, DBSession)
	 * 
	 * @param loginUser
	 * @param limit TODO
	 * @param offset TODO
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexFromBasketForUser(final String loginUser, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUser);
		param.setSimHash(HashID.INTER_HASH);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bibtexList("getBibTexFromBasketForUser", param, session);
	}

	/**
	 * TODO: remove!!
	 * 
	 * This method prepares queries which retrieve all publications for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForHomePage(final BibTexParam param, final DBSession session) {
		param.setGroupType(GroupID.PUBLIC);
		param.setSimHash(HashID.INTER_HASH);
		return this.bibtexList("getBibTexForHomepage", param, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexForHomePage(BibTexParam, DBSession)
	 * 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForHomePage(final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroupType(GroupID.PUBLIC);
		param.setSimHash(HashID.INTER_HASH);
		return this.bibtexList("getBibTexForHomepage", param, session);
	}

	/**
	 * TODO: remove me
	 * 
	 * This method prepares queries which retrieve all publications for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexPopular(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexPopular", param, session);
	}
	
	/**
	 * TODO: remove me !
	 * 
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
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexSearch(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexSearch", param, session);
	}
	
	/**
	 * TODO: DatabaseUtils call missing!
	 * <em>/search/ein+lustiger+satz+group%3AmyGroup</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table with the requested group<br/>
	 * 
	 * @param groupId
	 * @param search
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexSearchForGroup(final int groupId, final String search, final String requestedUserName, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return this.bibtexList("getBibTexSearch", param, session); // FIXME: why not SearchForGroup (was in param method)
	}

	/**
	 * TODO param get requested group name => remove me
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexViewable(final BibTexParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getPostsForUser(param, session);
		}
		return this.bibtexList("getBibTexViewable", param, session);
	}
	
	/**
	 * XXXDZ
	 * 
	 * @see BibTexDatabaseManager#getPostsViewableByTag(GenericParam, DBSession)
	 * 
	 * @param groupId
	 * @param tagIndex
	 * @param simHash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	// TODO: check method
	public List<Post<BibTex>> getBibTexViewableByTag(final int groupId, final List<TagIndex> tagIndex, final HashID simHash, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setTagIndex(tagIndex);
		param.setSimHash(simHash);
		param.setLimit(limit);
		param.setOffset(offset);
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
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	private List<Post<BibTex>> getBibTexDuplicate(final BibTexParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexDuplicate", param, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexDuplicate(BibTexParam, DBSession)
	 * 
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param simHash
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexDuplicate(final String requestedUserName, final List<Integer> visibleGroupIDs, final HashID simHash, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setGroups(visibleGroupIDs);
		param.setSimHash(simHash);
		
		return this.getBibTexDuplicate(param, session);
	}
	
	/**
	 * TODO: move to PostDatabaseManager ??
	 * TODO: check method
	 * 
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 * 
	 * @param requestedUserName
	 * @param session
	 * @return number of duplicates
	 */
	public Integer getBibTexDuplicateCount(final String requestedUserName, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
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
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForUsersInGroup(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		// document retrieval
		if ( present(param.getFilter()) ) {
			// just entries with document attached
			if (param.isDocumentsAttached() && param.getFilter().equals(FilterEntity.JUST_PDF)) {
				return this.bibtexList("getJustBibTexForGroupWithPDF", param, session);
			}
			throw new IllegalArgumentException("Filter " + param.getFilter().name() + " not supported");
		}

		// posts including documents
		if (param.isDocumentsAttached()) {
			return this.bibtexList("getBibTexForGroupWithPDF", param, session);
		}
		// posts only
		return this.bibtexList("getBibTexForUsersInGroup", param, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexForUsersInGroup(BibTexParam, DBSession)
	 * 
	 * @param loginUserName
	 * @param groupId
	 * @param groups 
	 * @param limit
	 * @param offset
	 * @param simHash
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForUsersInGroup(final String loginUserName, final Integer groupId, final List<Integer> groups, final HashID simHash, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setGroupId(groupId);
		param.setGroups(groups);
		param.setSimHash(simHash);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		// document retrieval
		if ( present(param.getFilter()) ) {
			// just entries with document attached
			if (param.isDocumentsAttached() && param.getFilter().equals(FilterEntity.JUST_PDF)) {
				return this.bibtexList("getJustBibTexForGroupWithPDF", param, session);
			}
			throw new IllegalArgumentException("Filter " + param.getFilter().name() + " not supported");
		}

		// posts including documents
		if (param.isDocumentsAttached()) {
			return this.bibtexList("getBibTexForGroupWithPDF", param, session);
		}
		// posts only
		return this.bibtexList("getBibTexForUsersInGroup", param, session);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBibTexForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForGroupByTag(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		
		// if user wants to retrieve documents
		if ( present(param.getFilter()) ) {
			if (param.isDocumentsAttached() && param.getFilter().equals(FilterEntity.JUST_PDF)) {
				return this.bibtexList("getJustBibTexForGroupByTagWithPDF", param, session);
			}
			throw new IllegalArgumentException("Filter " + param.getFilter().name() + " not supported");
		}
		// posts including documents
		if (param.isDocumentsAttached()) {
			return this.bibtexList("getBibTexForGroupByTagWithPDF", param, session);
		}
		// posts only
		return this.bibtexList("getBibTexForGroupByTag", param, session);
	}

	/**
	 * XXXDZ
	 * 
	 * @see BibTexDatabaseManager#getBibTexForGroupByTag(BibTexParam, DBSession)
	 * 
	 * @param tagIndex
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param loginUser
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 * 
	 * groupType is always FRIEND (repareGetPostForGroup())
	 */
	public List<Post<BibTex>> getBibTexForGroupByTag(final List<TagIndex> tagIndex, final int groupId, final List<Integer> visibleGroupIDs, final String loginUser, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setTagIndex(tagIndex);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.setUserName(loginUser);
		param.setLimit(limit);
		param.setOffset(offset);
		
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		
		// if user wants to retrieve documents
		if (present(param.getFilter())) {
			if (param.isDocumentsAttached() && param.getFilter().equals(FilterEntity.JUST_PDF)) {
				return this.bibtexList("getJustBibTexForGroupByTagWithPDF", param, session);
			}
			throw new IllegalArgumentException("Filter " + param.getFilter().name() + " not supported");
		}

		// posts including documents
		if (param.isDocumentsAttached()) {
			return this.bibtexList("getBibTexForGroupByTagWithPDF", param, session);
		}
		// posts only
		return this.bibtexList("getBibTexForGroupByTag", param, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getPostsForUser(org.bibsonomy.database.params.ResourcesParam, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	@Deprecated
	public List<Post<BibTex>> getPostsForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		
		// document retrieval
		if (present(param.getFilter())) {
			// retrieve only entries with a document attached
			if (param.isDocumentsAttached() && param.getFilter().equals(FilterEntity.JUST_PDF)) {
				return this.bibtexList("getJustBibTexForUserWithPDF", param, session);
			}			
			// retrieve duplicate entries
			if (param.getFilter().equals(FilterEntity.DUPLICATES)) {
				return this.getBibTexDuplicate(param, session);
			}
			
			log.warn("Filter " + param.getFilter().name() + " not supported");
		}
		
		// posts including documents
		if (param.isDocumentsAttached()) {
			return this.bibtexList("getBibTexForUserWithPDF", param, session);
		}
		// posts only
		return this.bibtexList("getBibTexForUser", param, session);
	}

	/*
	 * TODO: move code from param to here; add Filter to getPostsForUser???
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getPostsForUser(java.lang.String, java.lang.String, org.bibsonomy.common.enums.HashID, int, java.util.List, int, int, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public List<Post<BibTex>> getPostsForUser(final String userName, final String requestedUserName, final HashID simHash, final int groupId, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setSimHash(simHash);
		param.setGroupId(groupId);
		param.setLimit(limit);
		param.setOffset(offset);
		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bibtexList("getBibTexForUser", param, session);
		// FIXME: with groupId = -1 test failed
//		return super.getPostsForUser(userName, requestedUserName, simHash, groupId, visibleGroupIDs, limit, offset, session);
	}
	
	private List<Post<BibTex>> getLoggedBibTexByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session, final HashID hashType) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setHash(intraHash);
		param.setSimHash(hashType);
		
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);		
		return this.bibtexList("getLoggedHashesByHashForUser", param, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getPosts(org.bibsonomy.database.params.ResourcesParam, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public List<Post<BibTex>> getPosts(final BibTexParam param, final DBSession session) {
		final List<Post<BibTex>> posts = super.getPosts(param, session);
		
		if (posts != null) {
			for (final Post<BibTex> post : posts) {
				if (post.getUser().getName().equals(param.getUserName())) {
					post.getResource().setPrivnote(extraDb.getBibTexPrivnoteForUser(post.getResource().getIntraHash(), param.getUserName(), session));
				}
			}
		}
		
		return posts;
	}
	
	/**
	 * TODO: param.getGroup() : String
	 * 
	 * @param param 
	 * @param session 
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getPostsByAuthor(final BibTexParam param, final DBSession session){
		return this.bibtexList("getBibTexByAuthor",param,session);
	}
	
	/**
	 * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name (restricted by group public).
	 * 
	 * @param search
	 * @param groupType
	 * @param requestedUserName
	 * @param requestedGroupName 
	 * @param year 
	 * @param firstYear 
	 * @param lastYear 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getPostsByAuthor(final String search, final GroupID groupType, final String requestedUserName, final String requestedGroupName, final String year, final String firstYear, final String lastYear, final int limit, final int offset, final DBSession session){
		final BibTexParam param = new BibTexParam();
		param.setSearch(search);
		param.setGroupType(groupType);
		param.setRequestedUserName(requestedUserName);
		param.setRequestedGroupName(requestedGroupName);
		param.setYear(year);
		param.setFirstYear(firstYear);
		param.setLastYear(lastYear);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(HashID.INTER_HASH);
		
		return this.bibtexList("get" + this.resourceClassName + "ByAuthor",param,session);
	}

	/**
	 * TODO: improve doc
	 * FIXME: check method
	 * TODO: move time logging into Lucene!? @see {@link PostDatabaseManager#getPostsSearchLucene(int, String, String, String, java.util.Set, int, int, DBSession)}
	 * 
	 * @param search
	 * @param groupType
	 * @param requestedUserName
	 * @param requestedGroupName 
	 * @param year 
	 * @param firstYear 
	 * @param lastYear 
	 * @param limit
	 * @param offset
	 * @param simHash 
	 * @param tagIndex 
	 * @param session
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getPostsByAuthorLucene(String search, int groupType, String requestedUserName, String requestedGroupName, String year, String firstYear, String lastYear, final int limit, final int offset, final int simHash, final List<String> tagIndex, final DBSession session){
		final ResultList<Post<BibTex>> postBibtexList;
		final ResourceSearch<BibTex> resourceSearch = getResourceSearch();
		if (present(resourceSearch)) {
			final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
			String group = groupDb.getGroupNameByGroupId(groupType, session);
			
			final long starttimeQuery = System.currentTimeMillis();

			postBibtexList = resourceSearch.searchAuthor(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagIndex, limit, offset);

			final long endtimeQuery = System.currentTimeMillis();
			log.debug("LuceneBibTex complete query time: " + (endtimeQuery-starttimeQuery) + "ms");
		} else {
			postBibtexList = new ResultList<Post<BibTex>>();
			log.error("No resource searcher available.");
		}
			
		return postBibtexList;
	}
	
	
	/**
	 * TODO GroupID in getPostsByAuthorAndTag; remove me
	 * 
	 * @param param 
	 * @param session 
	 * @return list of bibtex entries
	 */
    public List<Post<BibTex>> getBibTexByAuthorAndTag(final BibTexParam param, final DBSession session){
		return this.bibtexList("getBibTexByAuthorAndTag",param,session);
	}
    
    /**
     * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name and TagName(restricted by group public).
	 * 
     * @param search
     * @param groupType
     * @param requestedUserName 
     * @param requestedGroupName 
     * @param tagIndex
     * @param year 
     * @param firstYear 
     * @param lastYear 
     * @param limit
     * @param offset
     * @param session
     * @return list of bibtex entries
     */
    public List<Post<BibTex>> getPostsByAuthorAndTag(final String search, final GroupID groupType, final String requestedUserName, final String requestedGroupName, final List<TagIndex> tagIndex, final String year, final String firstYear, final String lastYear, final int limit, final int offset, final DBSession session){
		final BibTexParam param = this.getNewParam();
		param.setSearch(search);
		param.setGroupType(groupType);
		param.setRequestedUserName(requestedUserName);
		param.setRequestedGroupName(requestedGroupName);
		param.setTagIndex(tagIndex);
		param.setYear(year);
		param.setFirstYear(firstYear);
		param.setLastYear(lastYear);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(HashID.INTER_HASH);
		
		return this.bibtexList("getBibTexByAuthorAndTag",param,session);
	}

	/**
	 * <em>/bibtexkey/KEY</em> Returns a list of bibtex posts for a given
	 * bibtexKey
	 * 
	 * @param param		a bibtex parameter object
	 * @param session	a database session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsByKey(BibTexParam param, DBSession session) {
		return this.bibtexList("getBibTexByKey",param,session);
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
	@Override
	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		// get post from database
		final Post<BibTex> post = super.getPostDetails(authUser, resourceHash, userName, visibleGroupIDs, session);
		
		if (post != null) {
			if (this.permissionDb.isAllowedToAccessPostsDocuments(userName, post, session)) {
				post.getResource().setDocuments(this.docDb.getDocumentsForPost(userName, resourceHash, session));
			}
			
			// add private note
			if (authUser != null && authUser.equalsIgnoreCase(userName)) {
				post.getResource().setPrivnote(extraDb.getBibTexPrivnoteForUser(resourceHash, userName, session));
			}
			
			// add extra URLs
			post.getResource().setExtraUrls(extraDb.getURL(resourceHash, userName, session));
			
			return post;
		}
		
		/*
		 * post null => not found => second try: look into logging table
		 */
		final List<Post<BibTex>> loggedList = this.getLoggedBibTexByHashForUser(authUser, resourceHash, userName, visibleGroupIDs, session, HashID.INTRA_HASH);
		if (loggedList.size() >= 1) {
			if (loggedList.size() > 1) {
				// user has multiple posts with the same hash
				log.warn("multiple logged BibTeX-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			/*
			 * Resource has been changed and thus could be found in logging table. We send back the new resource hash. 
			 */
			final Post<BibTex> loggedPost = loggedList.get(0);
			if (!resourceHash.equals(loggedPost.getResource().getIntraHash())) {
				/*
				 * TODO: quick fix to break loops when the hash has not changed.
				 * This does not help in the case of more complex change patterns 
				 * (e.g., A -> B -> A). To fix that, we have to respect the 
				 * date given to the exception and implement a query which 
				 * returns the latest post with given hash+username whose 
				 * posting date is smaller or equal to the given date.  
				 */
				throw new ResourceMovedException(resourceHash, loggedPost.getResource().getIntraHash(), userName, loggedPost.getDate());
			}
		}
		
		return null;
	}
	
	/*	XXX: check implementation
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#insertPost(org.bibsonomy.database.params.ResourcesParam, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void insertPost(BibTexParam param, DBSession session) {
		super.insertPost(param, session); // insert post and update/insert hashes
		session.beginTransaction();
		
		try {
			if (param.getResource().getPrivnote() != null) {
				extraDb.updateBibTexPrivnoteForUser(param.getResource().getIntraHash(), param.getUserName(), param.getResource().getPrivnote(), session);
			}
			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostUpdate(java.lang.Integer, java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostUpdate(Integer oldContentId, Integer newContentId, DBSession session) {
		this.plugins.onBibTexUpdate(oldContentId, newContentId, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostDelete(java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostDelete(Integer contentId, DBSession session) {
		this.plugins.onBibTexDelete(contentId, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getChain()
	 */
	@Override
	protected FirstChainElement<Post<BibTex>, BibTexParam> getChain() {
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
	protected BibTexParam getInsertParam(Post<BibTex> post, DBSession session) {
		final BibTexParam insert = this.getNewParam();
		insert.setResource(post.getResource());
		insert.setRequestedContentId(post.getContentId());
		insert.setDescription(post.getDescription());
		insert.setDate(post.getDate());
		insert.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		
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
	protected BibTexParam getNewParam() {
		return new BibTexParam();
	}
}