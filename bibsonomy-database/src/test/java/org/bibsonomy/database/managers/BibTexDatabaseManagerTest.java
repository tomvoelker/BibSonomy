package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Test;

/**
 * Tests related to BibTex.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getBibTexByHash() {
		this.bibTexDb.getBibTexByHash(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexByHashCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexByHashCount(this.bibtexParam, this.dbSession);
		assertEquals(1, count);
	}

	@Test
	public void getBibTexByHashForUser() {
		this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		this.resetParameters();
		this.bibtexParam.setRequestedUserName("hotho");
		this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexByTagNames() {
		this.bibTexDb.getBibTexByTagNames(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexByTagNamesForUser() {
		this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam, this.dbSession);
		this.resetParameters();
		this.bibtexParam.setGroupId(GroupID.GROUP_INVALID.getId());
		this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexByConceptForUser() {
		// test it with casesensitive and caseinsensitive tagnames
		for (final boolean caseSensitive : new Boolean[] { true, false }) {
			this.bibtexParam.setCaseSensitiveTagNames(caseSensitive);
			this.bibTexDb.getBibTexByConceptForUser(this.bibtexParam, this.dbSession);
			this.resetParameters();
			// more tags
			ParamUtils.addTagsToParam(this.bibtexParam);
			this.bibTexDb.getBibTexByConceptForUser(this.bibtexParam, this.dbSession);
			this.resetParameters();
		}
	}

	@Test
	public void getBibTexByUserFriends() {
		this.bibTexDb.getBibTexByUserFriends(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexByDownload() {
		this.bibtexParam.setUserName("grahl");
		this.bibTexDb.getBibTexByDownload(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexForHomePage() {
		this.bibTexDb.getBibTexForHomePage(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexPopular() {
		this.bibTexDb.getBibTexPopular(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexSearch() {
		this.bibtexParam.setSearch("test");
		this.bibTexDb.getBibTexSearch(this.bibtexParam, this.dbSession);
		this.bibtexParam.setUserName(null);
		this.bibTexDb.getBibTexSearch(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexSearchCount() {
		this.bibtexParam.setSearch("test");
		Integer count = -1;
		count = this.bibTexDb.getBibTexSearchCount(this.bibtexParam, this.dbSession);
		assertTrue(count >= 0);

		this.bibtexParam.setUserName(null);
		count = -1;
		count = this.bibTexDb.getBibTexSearchCount(this.bibtexParam, this.dbSession);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexViewable() {
		this.bibTexDb.getBibTexViewable(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexDuplicate() {
		// without a friend
		this.bibTexDb.getBibTexDuplicate(this.bibtexParam, this.dbSession);
		// with a friend
		this.bibtexParam.setRequestedUserName("grahl");
		this.bibTexDb.getBibTexDuplicate(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexDuplicateCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexDuplicateCount(this.bibtexParam, this.dbSession);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexForGroup() {
		this.bibTexDb.getBibTexForGroup(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexForGroupCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexForGroupCount(this.bibtexParam, this.dbSession);
		assertTrue(count >= 0);
	}

	@Test
	public void getBibTexForGroupByTag() {
		this.bibTexDb.getBibTexForGroupByTag(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexForUser() {
		this.bibTexDb.getBibTexForUser(this.bibtexParam, this.dbSession);
		this.resetParameters();
		this.bibtexParam.setGroupId(GroupID.GROUP_INVALID.getId());
		this.bibTexDb.getBibTexForUser(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexForUserCount() {
		this.bibTexDb.getBibTexForUserCount(this.bibtexParam, this.dbSession);
		this.resetParameters();
		this.bibtexParam.setGroupId(GroupID.GROUP_INVALID.getId());
		this.bibTexDb.getBibTexForUserCount(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getPosts() {
//		final List<Post<BibTex>> posts = this.bibTexDb.getPosts(this.bibtexParam, this.dbSession);
//		assertEquals(19, posts.size());
	}

	@Test
	public void insertBibTex() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);
		toInsert.setContentId(Integer.MAX_VALUE);
		this.bibTexDb.insertBibTex(toInsert, this.dbSession);
	}

	@Test
	public void deleteBibTex() {
		this.bibTexDb.deletePost(this.bibtexParam.getRequestedUserName(), this.bibtexParam.getHash(), this.dbSession);
	}

	@Test
	public void storePost() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);
		
		this.bibTexDb.storePost(toInsert.getUser().getName(), toInsert, null, this.dbSession);
		
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, toInsert.getUser().getName(), GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { this.getClass().getName(), "hurz" }), "", false, false, 0, 50);
		final List<Post<BibTex>> posts = this.bibTexDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		final HashSet<String> skip = new HashSet<String>();
		skip.addAll(Arrays.asList(new String[] {"resource", "tags"}));
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), skip);
		skip.clear();
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), skip);
	}
}