package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.DatabasePluginMock;
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
		final List<Post<BibTex>> posts = this.bibTexDb.getBibTexByHash(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 1);
		assertEquals("0000175071e6141a7d36835489f922ef", posts.get(0).getResource().getInterHash());
		assertEquals("43ef2a4cc61e40a8999b132631e63bc4", posts.get(0).getResource().getIntraHash());
	}

	@Test
	public void getBibTexByHashCount() {
		Integer count = -1;
		count = this.bibTexDb.getBibTexByHashCount(this.bibtexParam, this.dbSession);
		assertNotNull(count);
		assertEquals(1, count);
	}

	@Test
	public void getBibTexByHashForUser() {
		List<Post<BibTex>> posts;

		// user != friend
		this.bibtexParam.setRequestedUserName("dblp");
		this.bibtexParam.setHash("");
		posts = this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 0);

		// user != friend and existing hash
		this.resetParameters();
		this.bibtexParam.setRequestedUserName("dblp");
		posts = this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 1);
		assertEquals("0000175071e6141a7d36835489f922ef", posts.get(0).getResource().getInterHash());
		assertEquals("43ef2a4cc61e40a8999b132631e63bc4", posts.get(0).getResource().getIntraHash());

		// user == friend
		this.resetParameters();
		this.bibtexParam.setUserName("hotho");
		this.bibtexParam.setRequestedUserName("hotho");
		posts = this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 0);

		// user == friend and existing hash
		this.resetParameters();
		this.bibtexParam.setUserName("hotho");
		this.bibtexParam.setRequestedUserName("hotho");
		this.bibtexParam.setHash("0154d8012c1773a0a9a54576b0e317bf");
		posts = this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 1);
		assertEquals("0154d8012c1773a0a9a54576b0e317bf", posts.get(0).getResource().getInterHash());
		assertEquals("3e8c52949336171a6c316ccfe9c5e581", posts.get(0).getResource().getIntraHash());
		
		// nonpublic personal group
		posts = this.bibTexDb.getBibTexByHashForUser("tausendeins", "10ec64d80b0ac085328a953bb494fb89", "tausendeins", this.dbSession);
		assertEquals(1, posts.size());
	}

	@Test
	public void getBibTexByTagNames() {
		this.bibTexDb.getBibTexByTagNames(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexByTagNamesForUser() {
		this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam, this.dbSession);
		this.resetParameters();
		this.bibtexParam.setGroupId(GroupID.INVALID.getId());
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
		List<Post<BibTex>> l = this.bibTexDb.getBibTexPopular(this.bibtexParam, this.dbSession);
		assertEquals(this.bibtexParam.getLimit() , l.size());
		this.bibtexParam.setLimit(1);
		this.bibtexParam.setOffset(25);
		l = this.bibTexDb.getBibTexPopular(this.bibtexParam, this.dbSession);
		// FIXME: db inconsistency: assertEquals(1, l.size());
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
	public void getBibTexForUsersInGroup() {
		this.bibTexDb.getBibTexForUsersInGroup(this.bibtexParam, this.dbSession);
		this.bibTexDb.getBibTexForUsersInGroup("jaeschke", GroupID.KDE.getId(), this.dbSession);
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
		this.bibtexParam.setGroupId(GroupID.INVALID.getId());
		this.bibTexDb.getBibTexForUser(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getBibTexForUserCount() {
		this.bibTexDb.getBibTexForUserCount(this.bibtexParam, this.dbSession);
		this.resetParameters();
		this.bibtexParam.setGroupId(GroupID.INVALID.getId());
		this.bibTexDb.getBibTexForUserCount(this.bibtexParam, this.dbSession);
	}

	@Test
	public void getPosts() {
		this.bibtexParam.setHash("");
		final List<Post<BibTex>> posts = this.bibTexDb.getPosts(this.bibtexParam, this.dbSession);
		assertEquals(this.bibtexParam.getLimit(), posts.size());
	}

	@Test
	public void insertBibTexPost() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);
		toInsert.setContentId(Integer.MAX_VALUE);
		this.bibTexDb.insertBibTexPost(toInsert, this.dbSession);
	}

	@Test
	public void deleteBibTex() {
		this.bibTexDb.deletePost(this.bibtexParam.getRequestedUserName(), this.bibtexParam.getHash(), this.dbSession);
	}

	@Test
	public void storePost() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);

		this.bibTexDb.storePost(toInsert.getUser().getName(), toInsert, null, this.dbSession);

		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, toInsert.getUser().getName(), GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { ModelUtils.class.getName(), "hurz" }), "", null, 0, 50);
		final List<Post<BibTex>> posts = this.bibTexDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), new String[] { "resource", "tags" });
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), "");

		// Duplicate post and check whether plugins are called
		this.resetParameters();
		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnBibTexUpdate());
		param.setHash("06aef6e5439298f27dc5aee82c4293d6");
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		this.bibTexDb.storePost(someBibTexPost.getUser().getName(), someBibTexPost, "06aef6e5439298f27dc5aee82c4293d6", this.dbSession);
		assertTrue(plugin.isOnBibTexUpdate());
	}
}
