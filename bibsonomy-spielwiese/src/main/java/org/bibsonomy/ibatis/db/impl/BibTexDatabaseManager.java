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
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate this class.
	 */
	BibTexDatabaseManager(final DatabaseManager db) {
		this.db = db;
	}

	public List<BibTex> getBibTexByHash(final BibTexParam param) {
		return this.bibtexList("getBibTexByHash", param);
	}

	public List<BibTex> getBibTexByTagNames(final BibTexParam param) {
		return this.bibtexList("getBibTexByTagNames", param);
	}

	public List<BibTex> getBibTexByUserFriends(final BibTexParam param) {
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param);
	}

	public List<BibTex> getBibTexByDownload(final BibTexParam param) {
		return this.bibtexList("getBibTexByDownload", param);
	}

	public List<BibTex> getHomePageBibTex(final BibTexParam param) {
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getHomePageBibTex", param);
	}

	public List<BibTex> getBibTexPopular(final BibTexParam param) {
		return this.bibtexList("getBibTexPopular", param);
	}

	public List<BibTex> getBibTexSearch(final BibTexParam param) {
		return this.bibtexList("getBibTexSearch", param);
	}

	public int getBibTexSearchCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexSearchCount", param);
	}

	public List<BibTex> getBibTexViewable(final BibTexParam param) {
		return this.bibtexList("getBibTexViewable", param);
	}

	public List<BibTex> getBibTexDuplicate(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return this.bibtexList("getBibTexDuplicate", param);
	}

	public int getBibTexDuplicateCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexDuplicateCount", param);
	}
}