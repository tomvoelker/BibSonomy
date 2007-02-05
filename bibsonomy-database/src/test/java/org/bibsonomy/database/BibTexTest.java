package org.bibsonomy.database;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.util.ParamUtils;
import org.junit.Test;

/**
 * Tests related to BibTex.
 * 
 * @author mgr
 * @author Christian Schenk
 */
public class BibTexTest extends AbstractSqlMapTest {

	@Test
	public void getBibTexByHash() {
		this.db.getBibTex().getBibTexByHash(this.bibtexParam);
	}

	@Test
	public void getBibTexByHashCount() {
		Integer count = -1;
		count = this.db.getBibTex().getBibTexByHashCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexByHashForUser() {
		this.db.getBibTex().getBibTexByHashForUser(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setRequestedUserName("hotho");
		this.db.getBibTex().getBibTexByHashForUser(this.bibtexParam);
	}

	@Test
	public void getBibTexByTagNames() {
		this.db.getBibTex().getBibTexByTagNames(this.bibtexParam);
	}

	@Test
	public void getBibTexByTagNamesForUser() {
		this.db.getBibTex().getBibTexByTagNamesForUser(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.db.getBibTex().getBibTexByTagNamesForUser(this.bibtexParam);
	}

	@Test
	public void getBibTexByConceptForUser() {
		// test it with casesensitive and caseinsensitive tagnames
		for (final boolean caseSensitive : new Boolean[] { true, false }) {
			this.bibtexParam.setCaseSensitiveTagNames(caseSensitive);
			this.db.getBibTex().getBibTexByConceptForUser(this.bibtexParam);
			this.resetParameters();
			// more tags
			ParamUtils.addTagsToParam(this.bibtexParam);
			this.db.getBibTex().getBibTexByConceptForUser(this.bibtexParam);
			this.resetParameters();
		}
	}

	@Test
	public void getBibTexByUserFriends() {
		this.db.getBibTex().getBibTexByUserFriends(this.bibtexParam);
	}

	@Test
	public void getBibTexByDownload() {
		this.bibtexParam.setUserName("grahl");
		this.db.getBibTex().getBibTexByDownload(this.bibtexParam);
	}

	@Test
	public void getBibTexForHomePage() {
		this.db.getBibTex().getBibTexForHomePage(this.bibtexParam);
	}

	@Test
	public void getBibTexPopular() {
		this.db.getBibTex().getBibTexPopular(this.bibtexParam);
	}

	@Test
	public void getBibTexSearch() {
		this.bibtexParam.setSearch("test");
		this.db.getBibTex().getBibTexSearch(this.bibtexParam);
		this.bibtexParam.setUserName(null);
		this.db.getBibTex().getBibTexSearch(this.bibtexParam);
	}

	@Test
	public void getBibTexSearchCount() {
		this.bibtexParam.setSearch("test");
		Integer count = -1;
		count = this.db.getBibTex().getBibTexSearchCount(this.bibtexParam);
		assertTrue(count >= 0);

		this.bibtexParam.setUserName(null);
		count = -1;
		count = this.db.getBibTex().getBibTexSearchCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexViewable() {
		this.db.getBibTex().getBibTexViewable(this.bibtexParam);
	}

	@Test
	public void getBibTexDuplicate() {
		// without a friend
		this.db.getBibTex().getBibTexDuplicate(this.bibtexParam);
		// with a friend
		this.bibtexParam.setRequestedUserName("grahl");
		this.db.getBibTex().getBibTexDuplicate(this.bibtexParam);
	}

	@Test
	public void getBibTexDuplicateCount() {
		Integer count = -1;
		count = this.db.getBibTex().getBibTexDuplicateCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexForGroup() {
		this.db.getBibTex().getBibTexForGroup(this.bibtexParam);
	}

	@Test
	public void getBibTexForGroupCount() {
		Integer count = -1;
		count = this.db.getBibTex().getBibTexForGroupCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexForGroupByTag() {
		this.db.getBibTex().getBibTexForGroupByTag(this.bibtexParam);
	}

	@Test
	public void getBibTexForUser() {
		this.db.getBibTex().getBibTexForUser(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.db.getBibTex().getBibTexForUser(this.bibtexParam);
	}

	@Test
	public void getBibTexForUserCount() {
		this.db.getBibTex().getBibTexForUserCount(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.db.getBibTex().getBibTexForUserCount(this.bibtexParam);
	}
}