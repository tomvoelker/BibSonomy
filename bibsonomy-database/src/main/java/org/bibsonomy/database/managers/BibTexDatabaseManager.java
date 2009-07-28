package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.lucene.LuceneSearchBibTex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.SimHash;

/**
 * Used to create, read, update and delete BibTexs from the database.
 * 
 * FIXME: why do some methods use loginUserName and some methods not? Shouldn't all methods
 * need loginUserName?
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexDatabaseManager extends AbstractDatabaseManager implements CrudableContent<BibTex, BibTexParam> {

	private static final Logger log = Logger.getLogger(BibTexDatabaseManager.class);

	private static final BibTexDatabaseManager singleton = new BibTexDatabaseManager();
	private final BibTexExtraDatabaseManager extraDb;
	private final GeneralDatabaseManager generalDb;
	private final PermissionDatabaseManager permissionDb;
	private final DocumentDatabaseManager docDb;
	private final TagDatabaseManager tagDb;
	private final DatabasePluginRegistry plugins;
	private static final BibTexChain chain = new BibTexChain();
	//private static final BibTexHashingManager hashelements = new BibTexHashingManager();
	
	private BibTexDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
		this.permissionDb = PermissionDatabaseManager.getInstance();
		this.docDb = DocumentDatabaseManager.getInstance();
		this.extraDb = BibTexExtraDatabaseManager.getInstance();
	}

	/**
	 * @return BibTexDatabaseManager
	 */
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
	 * simHash is equal to a given hash. Only public posts are
	 * retrieved.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByHash(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexByHash", param, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexByHash(BibTexParam, DBSession)
	 * 
	 * @param hash
	 * @param hashId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
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
	 * Returns the number of publications for a given hash.
	 * 
	 * @param param
	 * @param session
	 * @return number of publications for a given hash
	 */
	public Integer getBibTexByHashCount(final BibTexParam param, final DBSession session) {
		return this.queryForObject("getBibTexByHashCount", param, Integer.class, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexByHashCount(BibTexParam, DBSession)
	 * 
	 * @param requHash
	 * @param simHash
	 * @param session
	 * @return number of publications for a given hash
	 */
	public Integer getBibTexByHashCount(final String requHash, final HashID simHash, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setHash(requHash);
		param.setSimHash(simHash);
		return this.queryForObject("getBibTexByHashCount", param, Integer.class, session);
	}
	
	/** 
	 * @param requHash
	 * @param simHash
	 * @param userName
	 * @param session
	 * @return number of publications for a given hash and a user
	 */
	public Integer getBibTexByHashAndUserCount(final String requHash, final HashID simHash, final String userName, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setHash(requHash);
		param.setSimHash(simHash);
		param.setUserName(userName);
		return this.queryForObject("getBibTexByHashAndUserCount", param, Integer.class, session);
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
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByTagNames(final BibTexParam param, final DBSession session) {
		if (Order.FOLKRANK.equals(param.getOrder())){
			param.setGroupId(GroupID.PUBLIC.getId());
			return this.bibtexList("getBibTexByTagNamesAndFolkrank", param, session);
		}
		return this.bibtexList("getBibTexByTagNames", param, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexByTagNames(BibTexParam, DBSession)
	 * 
	 * @param groupId
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByTagNames(final Integer groupId, final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		BibTexParam param = new BibTexParam();
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
	 * Counts the number of visible bibtex entries for a given list of tags
	 * 
	 * @param tagIndex a list of tags
	 * @param session DB session
	 * @param groupId a group id
	 * @return the number of visible bibtex entries
	 */
	public Integer getBibtexByTagNamesCount(final List<TagIndex> tagIndex, final int groupId, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setTagIndex(tagIndex);		
		return this.queryForObject("getBibTexByTagNamesCount", param, Integer.class, session);
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
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
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
	 * Retrieves the number of bibtex items tagged by the tags present in tagIndex by user requestedUserName
	 * being visible to the logged in user
	 * 
	 * @param requestedUserName
	 * 			owner of the bibtex items
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
	public Integer getBibTexByTagNamesForUserCount(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setTagIndex(tagIndex);
		return this.queryForObject("getBibTexByTagNamesForUserCount", param, Integer.class, session);
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
	 * <em>/concept/group/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * This method retrieves all bibtex of all group members of the given group
	 * which are tagged at least with one of the concept tags or its subtags.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByConceptForGroup(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexByConceptForGroup", param, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexByConceptForGroup(BibTexParam, DBSession)
	 * 
	 * Returns a BibTex for a given concept and a user.
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
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have userName as
	 * their friend.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByUserFriends(final BibTexParam param, final DBSession session) {
		// groupType must be set to friends
		param.setGroupType(GroupID.FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexByUserFriends(BibTexParam, DBSession)
	 * 
	 * @param loginUser
	 * @param limit
	 * @param offset
	 * @param simHash
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByUserFriends(final String loginUser, final HashID simHash, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUser);
		param.setSimHash(simHash);
		param.setLimit(limit);
		param.setOffset(offset);
		// groupType must be set to friends
		param.setGroupType(GroupID.FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param, session);
	}

	/**
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
		return this.bibtexList("getBibTexForHomePage", param, session);
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
		return this.bibtexList("getBibTexForHomePage", param, session);
	}

	/**
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
	 * @see BibTexDatabaseManager#getBibTexPopular(BibTexParam, DBSession)
	 * 
	 * @param limit
	 * @param offset
	 * @param simHash
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexPopular(final int limit, final int offset, final HashID simHash, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(simHash);
		return this.bibtexList("getBibTexPopular", param, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexPopular(BibTexParam, DBSession)
	 * 
	 * @param days
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexPopular(final int days, final int limit, final int offset, final HashID simHash, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setDays(days);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setSimHash(simHash);
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
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexSearch(final BibTexParam param, final DBSession session) {
		
		return this.bibtexList("getBibTexSearch", param, session);
		
	}
	
	
	/**
	 * @see BibTexDatabaseManager#getBibTexSearchForGroup(BibTexParam, DBSession)
	 * 
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param search
	 * @param userName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getBibTexSearchForGroup(final int groupId, final List<Integer> visibleGroupIDs, final String search, final String userName, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexSearchForGroup", param, session);
	}
	
	
	/**
	 * <em>/search/ein+lustiger+satz+group%3AmyGroup</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table with the requested group<br/>
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexSearchForGroup(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexSearchForGroup", param, session);
		
	}



	/**
	 * @see BibTexDatabaseManager#getBibTexSearch(GroupID, String, String, int, int, DBSession)
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 * @throws IOException 
	 * /
	public List<Post<BibTex>> getBibTexSearchLucene(final BibTexParam param, final DBSession session) throws IOException {

		return this.getBibTexSearchLucene(param.getGroupId(), param.getSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), session);
		
	}
*/	
	
	/**
	 * @see BibTexDatabaseManager#getBibTexSearch(BibTexParam, DBSession)
	 * 
	 * @param groupType
	 * @param search
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */

	public List<Post<BibTex>> getBibTexSearch(final int groupId, final String search, final String requestedUserName, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bibtexList("getBibTexSearch", param, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexSearch(BibTexParam, DBSession)
	 * 
	 * @param groupType
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
		return this.bibtexList("getBibTexSearch", param, session);
	}

	/**
	 * Prepares queries to retrieve posts which match a fulltext search in lucene index <br/>
	 * @param groupId 
	 * @param search
	 * @param requestedUserName
	 * @param UserName 
	 * @param GroupNames 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */

	public ResultList<Post<BibTex>> getBibTexSearchLucene(final int groupId, final String search, final String requestedUserName, final String UserName, final Set<String> GroupNames, final int limit, final int offset, final DBSession session) {
		final Logger LOGGER = Logger.getLogger(BibTexDatabaseManager.class);
		ResultList<Post<BibTex>> postBibtexList = new ResultList<Post<BibTex>>();
/*
		final BibTexParam param = new BibTexParam();
		param.setGroupId(groupId);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
*/
		// get list of ids from lucene

		// get groupname by group 
		GroupDatabaseManager groupDb;
		groupDb = GroupDatabaseManager.getInstance();
		String group = groupDb.getGroupNameByGroupId(groupId, session);
		
		final LuceneSearchBibTex lucene = LuceneSearchBibTex.getInstance();


//		ArrayList<Integer> contentIds = new ArrayList<Integer>();
		long starttimeQuery = System.currentTimeMillis();

//		contentIds = lucene.searchLucene(groupId, search, requestedUserName, limit, offset);
		postBibtexList = lucene.search(group, search, requestedUserName, UserName, GroupNames, limit, offset);

		long endtimeQuery = System.currentTimeMillis();
		LOGGER.debug("LuceneBibTex complete query time: " + (endtimeQuery-starttimeQuery) + "ms");

		

/*
		long starttimeTable = System.currentTimeMillis();
		LuceneHelper luceneTTable = new LuceneHelper();
		// create temp. table
		luceneTTable.createTTable(session);

		// delete all content in temp. table
		luceneTTable.truncateTTable(session);

		// store content ids in temp. table
		luceneTTable.fillTTable(contentIds, session);
		long endtimeTable = System.currentTimeMillis();
		LOGGER.debug("LuceneBibTex: filled temp. table with requested lucene ids in " + (endtimeTable-starttimeTable) + "ms");
*/

//		return this.bibtexList("getBibTexSearchLucene", param, session);
		return postBibtexList;

	}

	
	/**
	 * Returns the number of publications for a given search.
	 * 
	 * @param param
	 * @param session
	 * @return number of publications for a given search
	 */
	public Integer getBibTexSearchCount(final BibTexParam param, final DBSession session) {
		return this.queryForObject("getBibTexSearchCount", param, Integer.class, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexSearchCount(BibTexParam, DBSession)
	 * 
	 * @param groupType
	 * @param search 
	 * @param requestedUserName
	 * @param session
	 * @return number of publications for a given search
	 */
	public Integer getBibTexSearchCount(final GroupID groupType, final String search, final String requestedUserName, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setGroupType(groupType);
		param.setSearch(search);
		param.setRequestedUserName(requestedUserName);
		return this.queryForObject("getBibTexSearchCount", param, Integer.class, session);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexViewable(final BibTexParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBibTexForUser(param, session);
		}
		return this.bibtexList("getBibTexViewable", param, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexViewable(BibTexParam, DBSession)
	 * 
	 * @param requestedGroupName
	 * @param loginUserName 
	 * @param groupId
	 * @param simHash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexViewable(final String requestedGroupName, final String loginUserName, int groupId, final HashID simHash, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedGroupName(requestedGroupName);
		param.setUserName(loginUserName);
		param.setGroupId(groupId);
		param.setSimHash(simHash);
		param.setLimit(limit);
		param.setOffset(offset);
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBibTexForUser(param, session);
		}
		return this.bibtexList("getBibTexViewable", param, session);
	}

	/**
	 * Returns viewable BibTexs for a given tag.
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	// TODO: check method
	public List<Post<BibTex>> getBibTexViewableByTag(final BibTexParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return getBibTexByTagNamesForUser(param, session);
		}
		return this.bibtexList("getBibTexViewableByTag", param, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexViewableByTag(BibTexParam, DBSession)
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
	public List<Post<BibTex>> getBibTexDuplicate(final BibTexParam param, final DBSession session) {
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
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexDuplicate", param, session);
	}

	/**
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 * 
	 * @param param
	 * @param session
	 * @return number of duplicates
	 */
	public Integer getBibTexDuplicateCount(final BibTexParam param, final DBSession session) {
		return this.queryForObject("getBibTexDuplicateCount", param, Integer.class, session);
	}
	
	/**
	 * @see BibTexDatabaseManager#getBibTexDuplicateCount(BibTexParam, DBSession)
	 * 
	 * @param requestedUserName
	 * @param session
	 * @return number of duplicates
	 */
	// TODO: check method
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
	 * Returns the number of publications belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications).
	 * 
	 * @param param
	 * @param session
	 * @return number of publications belonging to the given group
	 */
	public Integer getBibTexForGroupCount(final BibTexParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.queryForObject("getBibTexForGroupCount", param, Integer.class, session);
	}
	
	/**
	 * @param requestedUserName 
	 * @param userName 
	 * @see BibTexDatabaseManager#getBibTexForGroupCount(BibTexParam, DBSession)
	 * 
	 * Returns the number of publications
	 * 
	 * @param groupId
	 * @param visibleGroupIDs 
	 * @param session
	 * @return the (approximated) number of resources posted to the group with the given groupID
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 */
	public Integer getBibTexForGroupCount(final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setGroups(visibleGroupIDs);
		param.setGroupId(groupId);
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.queryForObject("getBibTexForGroupCount", param, Integer.class, session);
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
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 * 
	 * ATTENTION! in case of a given groupId it is NOT checked if the user
	 * actually belongs to this group.
	 * 
	 * TODO: which of the two methods {@link #getBibTexForUser(BibTexParam, DBSession)} and {@link #getBibTexForUser(String, HashID, int, int, int, DBSession)} is deprecated? 
	 * Mark it with the appropriate tag! 
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForUser(final BibTexParam param, final DBSession session) {
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

	/**
	 * @see BibTexDatabaseManager#getBibTexForUser(BibTexParam, DBSession)
	 * 
	 * @param requestedUserName
	 * @param limit
	 * @param offset
	 * @param simHash TODO: what is this hash good for?
	 * @param groupId
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForUser(final String requestedUserName, final HashID simHash, final int groupId, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setSimHash(simHash);
		param.setGroupId(groupId);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.bibtexList("getBibTexForUser", param, session);
	}

	/**
	 * Returns the number of publications for a given user.
	 * 
	 * @param param
	 * @param session
	 * @return number of publications for a given user
	 */
	public Integer getBibTexForUserCount(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForObject("getBibTexForUserCount", param, Integer.class, session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexForUserCount(BibTexParam, DBSession)
	 * 
	 * Returns the number of publications for a given user
	 * 
	 * @param requestedUserName
	 * @param userName 
	 * @param groupId 
	 * @param visibleGroupIDs 
	 * @param session
	 * @return the number of publications of the requested user which the logged in user is allowed to see
	 * 
	 * groupId or
	 * visibleGroupIDs && userName && (userName != requestedUserName)
	 */
	public Integer getBibTexForUserCount(final String requestedUserName, final String userName, final int groupId, final List<Integer> visibleGroupIDs, final DBSession session) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(userName);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
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
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByHashForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexByHashForUser", param, session);
	}

	/**
	 * Returns a list containg BibTeX posts by INTER-Hash for a user
	 * 
	 * @param loginUserName
	 * @param interHash
	 * @param requestedUserName
	 * @param visibleGroupIDs 
	 * @param session
	 * @return List<Post<BibTex>> a list of BibTeX posts
	 */
	public List<Post<BibTex>> getBibTexByHashForUser(final String loginUserName, final String interHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session) {
		return this.getBibTexByHashForUser(loginUserName, interHash, requestedUserName, visibleGroupIDs, session, HashID.INTER_HASH);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexByHashForUser(BibTexParam, DBSession)
	 * 
	 * Returns a list containg BibTeX posts by hash for a user
	 * 
	 * @param loginUserName
	 * @param intraHash
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @param hashType
	 * @return List<Post<BibTex>> a list of BibTeX posts
	 */
	public List<Post<BibTex>> getBibTexByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session, final HashID hashType) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setHash(intraHash);
		param.setSimHash(hashType);
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.bibtexList("getBibTexByHashForUser", param, session);
	}
	
	private List<Post<BibTex>> getLoggedBibTexByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session, final HashID hashType) {
		final BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setHash(intraHash);
		param.setSimHash(hashType);
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		
		final List<Post<BibTex>> bibtexList = this.bibtexList("getLoggedHashesByHashForUser", param, session);
		
		return bibtexList;
	}

	/**
	 * Return a contentId for a BibTex with a given hash and for a given user.
	 * 
	 * @param hash
	 * @param userName
	 * @param session
	 * @return contentId
	 */
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
	
	// TODO: check if its correct / is this method special ? (no javadoc)
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
		final List<Post<BibTex>> posts = chain.getFirstElement().perform(param, session);
		
		
//		final List<Post<BibTex>> posts = hashelements.getMapping(param).perform(param, session);
		
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
	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		// get post from database
		final List<Post<BibTex>> list = this.getBibTexByHashForUser(authUser, resourceHash, userName, visibleGroupIDs, session, HashID.INTRA_HASH);
		if (list.size() >= 1) {
			if (list.size() > 1) {
				// user has multiple posts with the same hash
				log.warn("multiple BibTeX-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			// just take first post
			final Post<BibTex> post = list.get(0);
			// attach document hash 
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
		 * second try: look into logging table
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
			throw new ResourceMovedException(resourceHash, loggedList.get(0).getResource().getIntraHash(), userName);
		}
		log.debug("BibTeX-post from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' not found");
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
				// FIXME: singletonitis
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
			param.setSimHash(simHash);
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
	 * Inserts a BibTex-hash into the database.
	 */
	private void insertBibTexHash(final BibTexParam param, final DBSession session) {
		this.insert("insertBibTexHash", param, session);
	}

	/**
	 * Inserts a post with a publication into the database.
	 */
	protected void insertBibTexPost(final Post<BibTex> post, final DBSession session) {
		if (present(post.getResource()) == false) throw new InvalidModelException("There is no resource for this post.");
		if (present(post.getGroups()) == false) throw new InvalidModelException("There are no groups for this post.");
		/*if (post.getGroups().contains(GroupID.PUBLIC) && post.getGroups().size() > 1) throw new InvalidModelException("Invalid constilation of groups for this post.");
		if (post.getGroups().contains(GroupID.PRIVATE) && post.getGroups().size() > 1) throw new InvalidModelException("Invalid constilation of groups for this post.");*/
		
		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(post.getContentId());
		param.setDescription(post.getDescription());
		param.setDate(post.getDate());
		param.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		
		/* nur ein eintrag pro post in der bibtex tabelle*/
		 for (final Group group : post.getGroups()) {
	 		param.setGroupId(group.getGroupId());
			this.insertBibTex(param, session);
		 }
		
		
		/*
		MULTIPLE GROUPS FOR A POST
		if(post.getGroups().size() > 1){
			param.setGroupId(GroupID.MULTIPLE.getId());
		}else if(param.getGroups().contains(GroupID.PUBLIC)){
			param.setGroupId(GroupID.PUBLIC.getId());
		}else{
			param.setGroupId(GroupID.PRIVATE.getId());
		}
		this.insertBibTex(param, session);*/
	}

	// TODO: test insertion (tas, bibtex, ...)
	public boolean storePost(final String userName, final Post<BibTex> post, final String oldIntraHash, boolean update, final DBSession session) {
		session.beginTransaction();
		try {
			// the bibtex with the "old" intrahash, i.e. the one that was sent
			// within the create/update bibtex request
			final List<Post<BibTex>> isOldBibTexInDb;

			// the bookmark with the "new" intrahash, i.e. the one that was recalculated
			// based on the bibtex's fields			
			final List<Post<BibTex>> isNewBibTexInDb;

			// check if a user is trying to create a bibtex that already exists
			isNewBibTexInDb = this.getBibTexByHashForUser(userName, post.getResource().getIntraHash(), userName, new ArrayList<Integer>(), session, HashID.INTRA_HASH);
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
				isOldBibTexInDb = this.getBibTexByHashForUser(userName, oldIntraHash, userName, new ArrayList<Integer>(), session, HashID.INTRA_HASH);
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
			/*FIXME
			 * Warum wird insertTags nicht in insertBibTexPost aufgerufen?
			 * in deletePost wird ja auch deleteTags aufgerufen
			 */
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
			final List<Post<BibTex>> bibtexs = this.getBibTexByHashForUser(userName, resourceHash, userName, new ArrayList<Integer>(), session, HashID.INTRA_HASH);
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
	 * @see BibTexDatabaseManager#getBibTexByAuthor(BibTexParam, DBSession)
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
	public List<Post<BibTex>> getBibTexByAuthor(final String search, final GroupID groupType, final String requestedUserName, final String requestedGroupName, final String year, final String firstYear, final String lastYear, final int limit, final int offset, final DBSession session){
		BibTexParam param = new BibTexParam();
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
		
		return this.bibtexList("getBibTexByAuthor",param,session);
	}

	/**
	 * @see BibTexDatabaseManager#getBibTexByAuthor(BibTexParam, DBSession)
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
	public List<Post<BibTex>> getBibTexByAuthorLucene(String search, int groupType, String requestedUserName, String requestedGroupName, String year, String firstYear, String lastYear, final int limit, final int offset, final int simHash, final List<String> tagIndex, final DBSession session){
		final Logger LOGGER = Logger.getLogger(BibTexDatabaseManager.class);
		ResultList<Post<BibTex>> postBibtexList = new ResultList<Post<BibTex>>();
		
		/*
		BibTexParam param = new BibTexParam();
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
		*/

		// TODO Lucene
		
		GroupDatabaseManager groupDb;
		groupDb = GroupDatabaseManager.getInstance();
		String group = groupDb.getGroupNameByGroupId(groupType, session);

		final LuceneSearchBibTex lucene = LuceneSearchBibTex.getInstance();


//		ArrayList<Integer> contentIds = new ArrayList<Integer>();
		long starttimeQuery = System.currentTimeMillis();
		
//		contentIds = lucene.searchLucene(groupId, search, requestedUserName, limit, offset);
		//(Resource resourceType, GroupingEntity groupingEntity, String groupingName, ArrayList<String> tags, String hash, Order order, FilterEntity filter, int offset, int limit, String search)
		postBibtexList = lucene.searchAuthor(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagIndex, limit, offset);
		
		long endtimeQuery = System.currentTimeMillis();
		LOGGER.debug("LuceneBibTex complete query time: " + (endtimeQuery-starttimeQuery) + "ms");

		return postBibtexList;
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
     * @see BibTexDatabaseManager#getBibTexByAuthorAndTag(BibTexParam, DBSession)
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
    public List<Post<BibTex>> getBibTexByAuthorAndTag(final String search, final GroupID groupType, final String requestedUserName, final String requestedGroupName, final List<TagIndex> tagIndex, final String year, final String firstYear, final String lastYear, final int limit, final int offset, final DBSession session){
		BibTexParam param = new BibTexParam();
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
	 * <em>/concept/tag/TAGNAME</em> --
	 * 
	 * @param param
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexByConceptByTag(final BibTexParam param, final DBSession session) {
		return this.bibtexList("getBibTexByConceptByTag", param, session);
	}

	/**
	 * <em>/bibtexkey/KEY</em> Returns a list of bibtex entries for a given
	 * bibtexKey
	 * 
	 * @param param
	 *            a bibtex parameter object
	 * @param session
	 *            a database session
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getBibTexByKey(BibTexParam param, DBSession session) {
		return this.bibtexList("getBibTexByKey",param,session);
	}
	
	/**
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of publications that are available for some groups
	 */
	public int getGroupBibtexCount(final String requestedUserName, final String loginUserName, final List<Integer> visibleGroupIDs, final DBSession session){
		BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);
		
		return (Integer) this.queryForObject("getGroupBibtexCount", param, session);
	}
	
	/**
	 * @param requestedUserName
	 * @param loginUserName
	 * @param tagIndex
	 * @param visibleGroupIDs
	 * @param session
	 * @return number of publications that are available for some groups and tagged by a tag of the tagIndex
	 */
	public int getGroupBibtexCountByTag(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, final DBSession session){
		BibTexParam param = new BibTexParam();
		param.setTagIndex(tagIndex);
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);
		
		return (Integer) this.queryForObject("getGroupBibtexCountByTag", param, session);
	}
	
	/**
	 * 
	 * @param requestedUserName
	 * @param loginUserName
	 * @param limit 
	 * @param offset 
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForMyGroupPosts(final String requestedUserName, final String loginUserName, final int limit, final int offset, final List<Integer> visibleGroupIDs, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);
		
		return this.bibtexList("getBibtexForMyGroupPosts",param,session);
	}
	
	/**
	 * @param requestedUserName
	 * @param loginUserName
	 * @param tagIndex
	 * @param limit
	 * @param offset
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getBibTexForMyGroupPostsByTag(final String requestedUserName, final String loginUserName, final List<TagIndex> tagIndex, final int limit, final int offset, final List<Integer> visibleGroupIDs, final DBSession session){
		BibTexParam param = new BibTexParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		param.setGroups(visibleGroupIDs);
		
		return this.bibtexList("getBibtexForMyGroupPostsByTag",param,session);
	}
	
	/**
	 * @param days
	 * @param session
	 * @return the number of days when a publication was popular
	 */
	public int getBibTexPopularDays(final int days, final DBSession session){
		Integer result;
		BibTexParam param = new BibTexParam();
		param.setDays(days);
		
		result = this.queryForObject("getBibTexPopularDays", param, Integer.class, session);
		if (result != null) {
			return result;
		}
		return 0;
	}
	
	
	/**
	 * Get bibtex by followed users.
	 * 
	 * @param loginUserName
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @param session
	 * @return
	 */
	public List<Post<BibTex>> getBibTexByFollowedUsers(final String loginUserName, final List<Integer> visibleGroupIDs, final int limit, final int offset, final DBSession session) {
		BibTexParam param = new BibTexParam();
		param.setUserName(loginUserName);
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.bibtexList("getBibTexByFollowedUsers",param,session);
	} 
	
	
}