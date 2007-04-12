package org.bibsonomy.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Test;

/**
 * Tests related to BibTex.
 * 
 * @author mgr
 * @author Christian Schenk
 */
public class BibTexTest extends AbstractDatabaseTest {

	@Test
	public void getBibTexByHash() {
		this.bibTexDb.getBibTexByHash(this.bibtexParam);
	}

	@Test
	public void getBibTexByHashCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexByHashCount(this.bibtexParam);
		assertEquals(1, count);
	}

	@Test
	public void getBibTexByHashForUser() {
		this.bibTexDb.getBibTexByHashForUser(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setRequestedUserName("hotho");
		this.bibTexDb.getBibTexByHashForUser(this.bibtexParam);
	}

	@Test
	public void getBibTexByTagNames() {
		this.bibTexDb.getBibTexByTagNames(this.bibtexParam);
	}

	@Test
	public void getBibTexByTagNamesForUser() {
		this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam);
	}

	@Test
	public void getBibTexByConceptForUser() {
		// test it with casesensitive and caseinsensitive tagnames
		for (final boolean caseSensitive : new Boolean[] { true, false }) {
			this.bibtexParam.setCaseSensitiveTagNames(caseSensitive);
			this.bibTexDb.getBibTexByConceptForUser(this.bibtexParam);
			this.resetParameters();
			// more tags
			ParamUtils.addTagsToParam(this.bibtexParam);
			this.bibTexDb.getBibTexByConceptForUser(this.bibtexParam);
			this.resetParameters();
		}
	}

	@Test
	public void getBibTexByUserFriends() {
		this.bibTexDb.getBibTexByUserFriends(this.bibtexParam);
	}

	@Test
	public void getBibTexByDownload() {
		this.bibtexParam.setUserName("grahl");
		this.bibTexDb.getBibTexByDownload(this.bibtexParam);
	}

	@Test
	public void getBibTexForHomePage() {
		this.bibTexDb.getBibTexForHomePage(this.bibtexParam);
	}

	@Test
	public void getBibTexPopular() {
		this.bibTexDb.getBibTexPopular(this.bibtexParam);
	}

	@Test
	public void getBibTexSearch() {
		this.bibtexParam.setSearch("test");
		this.bibTexDb.getBibTexSearch(this.bibtexParam);
		this.bibtexParam.setUserName(null);
		this.bibTexDb.getBibTexSearch(this.bibtexParam);
	}

	@Test
	public void getBibTexSearchCount() {
		this.bibtexParam.setSearch("test");
		Integer count = -1;
		count = this.bibTexDb.getBibTexSearchCount(this.bibtexParam);
		assertTrue(count >= 0);

		this.bibtexParam.setUserName(null);
		count = -1;
		count = this.bibTexDb.getBibTexSearchCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexViewable() {
		this.bibTexDb.getBibTexViewable(this.bibtexParam);
	}

	@Test
	public void getBibTexDuplicate() {
		// without a friend
		this.bibTexDb.getBibTexDuplicate(this.bibtexParam);
		// with a friend
		this.bibtexParam.setRequestedUserName("grahl");
		this.bibTexDb.getBibTexDuplicate(this.bibtexParam);
	}

	@Test
	public void getBibTexDuplicateCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexDuplicateCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexForGroup() {
		this.bibTexDb.getBibTexForGroup(this.bibtexParam);
	}

	@Test
	public void getBibTexForGroupCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexForGroupCount(this.bibtexParam);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexForGroupByTag() {
		this.bibTexDb.getBibTexForGroupByTag(this.bibtexParam);
	}

	@Test
	public void getBibTexForUser() {
		this.bibTexDb.getBibTexForUser(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.bibTexDb.getBibTexForUser(this.bibtexParam);
	}

	@Test
	public void getBibTexForUserCount() {
		this.bibTexDb.getBibTexForUserCount(this.bibtexParam);
		this.resetParameters();
		this.bibtexParam.setGroupId(ConstantID.GROUP_INVALID.getId());
		this.bibTexDb.getBibTexForUserCount(this.bibtexParam);
	}

	@Test
	public void getBibTexSimHashsByContentId() {
		// TODO
		// System.out.println(this.bibTexDb.getBibTexSimHashsByContentId(this.bibtexParam));
	}

	@Test
	public void getPosts() {
		final List<Post<? extends Resource>> posts = this.bibTexDb.getPosts("jaeschke", GroupingEntity.USER, "jaeschke", null, null, false, false, 0, 19, false);
		assertEquals(19, posts.size());
	}

	@Test
	public void insertBibTex() {
		this.bibtexParam.setRequestedContentId(1234567);
		this.bibTexDb.insertBibTex(this.bibtexParam);
	}

	@Test
	public void deleteBibTex() {
		this.bibTexDb.deletePost(this.bibtexParam.getRequestedUserName(), this.bibtexParam.getHash());
	}
}