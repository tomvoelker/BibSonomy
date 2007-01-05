package org.bibsonomy.ibatis.db.impl;

import java.util.List;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;

/**
 * Used to retrieve BibTexs from the database.
 * 
 * @author Christian Schenk
 */
public class BibTexDatabaseManager extends AbstractDatabaseManager {

	private final DatabaseManager db;

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	BibTexDatabaseManager(final DatabaseManager db) {
		this.db = db;
	}

	public List<BibTex> getBibTexByHash(final BibTexParam param) {
		return this.bibtexList("getBibTexByHash", param);
	}

	public Integer getBibTexByHashCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexByHashCount", param);
	}

	public List<BibTex> getBibTexByTagNames(final BibTexParam param) {
		return this.bibtexList("getBibTexByTagNames", param);
	}

	/**
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all BibTexs for a given user
	 * name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if currUser us allowed
	 * to see them.
	 */
	public List<BibTex> getBibTexByTagNamesForUser(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.db, param);
		return this.bibtexList("getBibTexByTagNamesForUser", param);
	}

	/**
	 * <em>/concept/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all BibTexs for a given user
	 * name (requestedUser) and given tags. The tags are interpreted as
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
	public List<BibTex> getBibTexByConceptForUser(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return this.bibtexList("getBibTexByConceptForUser", param);
	}

	public List<BibTex> getBibTexByUserFriends(final BibTexParam param) {
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param);
	}

	public List<BibTex> getBibTexByDownload(final BibTexParam param) {
		return this.bibtexList("getBibTexByDownload", param);
	}

	public List<BibTex> getBibTexForHomePage(final BibTexParam param) {
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexForHomePage", param);
	}

	public List<BibTex> getBibTexPopular(final BibTexParam param) {
		return this.bibtexList("getBibTexPopular", param);
	}

	public List<BibTex> getBibTexSearch(final BibTexParam param) {
		return this.bibtexList("getBibTexSearch", param);
	}

	public Integer getBibTexSearchCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexSearchCount", param);
	}

	public List<BibTex> getBibTexViewable(final BibTexParam param) {
		return this.bibtexList("getBibTexViewable", param);
	}

	/**
	 * Prepares a query which returns all duplicate BibTex posts of the
	 * requested user. Duplicates are BibTex posts which have the same simhash1,
	 * but a different simhash0 (the latter is always true within the posts of a
	 * single user).
	 */
	public List<BibTex> getBibTexDuplicate(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
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
	 * Prepares queries which show all BibTexs of all users belonging to the
	 * group. This is an aggregated view of all posts of the group members.<br/>
	 * Full viewable-for checking is done, i.e. everybody sees everything he is
	 * allowed to see.<br/>
	 * 
	 * See also
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
	 * 92, formula (9) for formal semantics of this query.
	 */
	public List<BibTex> getBibTexForGroup(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.db, param);
		return this.bibtexList("getBibTexForGroup", param);
	}

	/**
	 * Returns the number of BibTexs belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBibTexForGroupCount(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return (Integer) this.queryForObject("getBibTexForGroupCount", param);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBibTexForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<BibTex> getBibTexForGroupByTag(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.db, param);
		return this.bibtexList("getBibTexForGroupByTag", param);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all BibTexs for a given user
	 * name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 */
	public List<BibTex> getBibTexForUser(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.db, param);
		return this.bibtexList("getBibTexForUser", param);
	}

	/**
	 * Returns the number of BibTexs for a given user.
	 */
	public Integer getBibTexForUserCount(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.db, param);
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
	public List<BibTex> getBibTexByHashForUser(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return this.bibtexList("getBibTexByHashForUser", param);
	}
}