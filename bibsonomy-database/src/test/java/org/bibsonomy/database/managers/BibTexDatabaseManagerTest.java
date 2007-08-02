package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.extra.BibTexExtra;
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
		assertEquals(1, posts.get(0).getGroups().size());
		assertEquals(1, posts.size());
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

		// user == friend, existing hash and spammer
		// FIXME: Should we add Integer.MIN_VALUE to the groups? This way we could retrive spam-posts
		this.resetParameters();
		this.bibtexParam.setUserName("hotho");
		this.bibtexParam.setRequestedUserName("hotho");
		this.bibtexParam.setHash("0154d8012c1773a0a9a54576b0e317bf");
		posts = this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 0);

		// user == friend, existing hash and no spammer
		this.resetParameters();
		this.bibtexParam.setUserName("dblp");
		this.bibtexParam.setRequestedUserName("dblp");
		this.bibtexParam.setHash("546b14be1492272632ef513a1fdeee7a");
		posts = this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession);
		assertNotNull(posts);
		assertTrue(posts.size() == 1);
		assertEquals("546b14be1492272632ef513a1fdeee7a", posts.get(0).getResource().getInterHash());
		assertEquals("9ad22a9cbce2cb8c10fb5d95903ceeff", posts.get(0).getResource().getIntraHash());
		
		// nonpublic personal group
		posts = this.bibTexDb.getBibTexByHashForUser("tausendeins", "10ec64d80b0ac085328a953bb494fb89", "tausendeins", this.dbSession);
		assertEquals(1, posts.size());
	}

	@Test
	public void getBibTexByTagNames() {
		final List<Post<BibTex>> posts = this.bibTexDb.getBibTexByTagNames(this.bibtexParam, this.dbSession);
		assertEquals(this.bibtexParam.getLimit(), posts.size());
		this.assertByTagNames(posts);
	}

	@Test
	public void getBibTexByTagNamesForUser() {
		List<Post<BibTex>> posts = this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam, this.dbSession);
		this.assertByTagNames(posts);

		this.resetParameters();
		this.bibtexParam.setGroupId(GroupID.INVALID.getId());
		posts = this.bibTexDb.getBibTexByTagNamesForUser(this.bibtexParam, this.dbSession);
		this.assertByTagNames(posts);
	}

	/**
	 * Searches in a list of posts for the requested tags from the bibtexParam.
	 */
	private void assertByTagNames(final List<Post<BibTex>> posts) {
		if (posts.size() == 0) return;
		for (final TagIndex requestedTag : this.bibtexParam.getTagIndex()) {
			boolean foundTag = false;
			for (final Post<BibTex> post : posts) {
				for (final Tag tagFromOnePost : post.getTags()) {
					if (requestedTag.getTagName().equals(tagFromOnePost.getName())) {
						foundTag = true;
						break;
					}
				}
				if (foundTag) break;
			}
			assertTrue(foundTag);
		}
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
	public void getContentIdForBibTex() {
		assertEquals(925724, this.bibTexDb.getContentIdForBibTex("2313536a09d3af706469e3d2523fe7ca", "thomi", this.dbSession));

		for (final String hash : new String[] { "", " ", null }) {
			for (final String username : new String[] { "", " ", null }) {
				try {
					this.bibTexDb.getContentIdForBibTex(hash, username, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ex) {
				}
			}
		}
	}

	@Test
	public void getPosts() {
		this.bibtexParam.setHash("");
		final List<Post<BibTex>> posts = this.bibTexDb.getPosts(this.bibtexParam, this.dbSession);
		assertEquals(this.bibtexParam.getLimit(), posts.size());
		this.assertByTagNames(posts);
	}

	@Test
	public void insertBibTexPost() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);
		toInsert.setContentId(Integer.MAX_VALUE);
		this.bibTexDb.insertBibTexPost(toInsert, false, this.dbSession);
	}

	@Test
	public void deleteBibTex() {
		// deleting a bibtex post (group is public)
		this.bibtexParam.setHash("00d062518a5549c3572b26f7ce9956f3");
		this.assertDeleteBibTex();

		// deleting a bibtex post (group is private)
		this.bibtexParam.setUserName("maybe");
		this.bibtexParam.setRequestedUserName("maybe");
		this.bibtexParam.setHash("967449bcdaabceaa22cfcbe0c554356d");
		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnBibTexDelete());
		this.assertDeleteBibTex();
		assertTrue(plugin.isOnBibTexDelete());
	}

	private void assertDeleteBibTex() {
		assertEquals(1, this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession).size());
		this.bibTexDb.deletePost(this.bibtexParam.getRequestedUserName(), this.bibtexParam.getHash(), this.dbSession);
		assertEquals(0, this.bibTexDb.getBibTexByHashForUser(this.bibtexParam, this.dbSession).size());
	}

	@Test
	public void storePost() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);
		// this was a INTER-hash - we want an INTRA-hash here...
		final String BIBTEX_TEST_HASH = "41b80148937cf74ad9b07ed4b227345a"; // INTRA-hash

		this.bibTexDb.storePost(toInsert.getUser().getName(), toInsert, null, false, this.dbSession);
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, toInsert.getUser().getName(), GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { ModelUtils.class.getName(), "hurz" }), "", null, 0, 50);
		param.setRequestedSimHash(HashID.INTRA_HASH);
		final List<Post<BibTex>> posts = this.bibTexDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), Integer.MAX_VALUE, null, new String[] { "resource", "tags", "user", "date"});
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), Integer.MAX_VALUE, null);

		// post a duplicate and check whether plugins are called
		this.resetParameters();
		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnBibTexUpdate());
		this.postDuplicate(param, BIBTEX_TEST_HASH);
		assertTrue(plugin.isOnBibTexUpdate());
	}

	@Test
	public void storePostWrongUsage() {
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);

		// can't update without old hash
		try {
			this.bibTexDb.storePost(toInsert.getUser().getName(), toInsert, null, true, this.dbSession);
			fail("Should throw a throwable");
		} catch (Throwable t) {
			assertTrue(t instanceof IllegalArgumentException);
		}

		// can't create new resource with old hash
		try {
			this.bibTexDb.storePost(toInsert.getUser().getName(), toInsert, "123456789", false, this.dbSession);
			fail("Should throw a throwable");
		} catch (Throwable t) {
			assertTrue(t instanceof IllegalArgumentException);
		}
	}

	/**
	 * Makes sure that we don't lose information if we change something on an
	 * existing post.
	 */
	@Test
	public void storePostDuplicate() {
		// the first (default) hash belongs to a public post,
		// the second to a private one
		// for (final String simHash1 : new String[] { this.bibtexParam.getHash(), "b6c9a44d411bf8101abdf809d5df1431" }) {
		for (final String intraHash : new String[] {"2313536a09d3af706469e3d2523fe7ca" }) {
			this.bibtexParam.setHash(intraHash);
			this.bibtexParam.setRequestedSimHash(HashID.INTRA_HASH);
			// if (simHash1.startsWith("b6c9")) this.bibtexParam.setGroupType(GroupID.PRIVATE);
			if (intraHash.startsWith("2313")) this.bibtexParam.setGroupType(GroupID.PRIVATE);

			final Post<BibTex> originalPost = this.bibTexDb.getBibTexByHash(this.bibtexParam, this.dbSession).get(0);
			this.postDuplicate(this.bibtexParam, this.bibtexParam.getHash());
			final Post<BibTex> newPost = this.bibTexDb.getBibTexByHash(this.bibtexParam, this.dbSession).get(0);

			assertNotSame(originalPost.getContentId(), newPost.getContentId());
			assertEquals(originalPost.getDate().toString(), newPost.getDate().toString());
			assertEquals(originalPost.getDescription(), newPost.getDescription());
			assertEquals(originalPost.getGroups().size(), newPost.getGroups().size());
			assertEquals(originalPost.getTags().size(), newPost.getTags().size());
			assertEquals(originalPost.getUser().getName(), newPost.getUser().getName());
			assertEquals(originalPost.getResource().getSimHash0(), newPost.getResource().getSimHash0());
			assertEquals(originalPost.getResource().getSimHash1(), newPost.getResource().getSimHash1());
			assertEquals(originalPost.getResource().getSimHash2(), newPost.getResource().getSimHash2());
			assertEquals(originalPost.getResource().getSimHash3(), newPost.getResource().getSimHash3());
			// TODO: more tests please...
		}
	}

	private void postDuplicate(final BibTexParam param, final String hash) {
		param.setHash(hash);
		
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		someBibTexPost.getGroups().clear();
		this.bibTexDb.storePost(someBibTexPost.getUser().getName(), someBibTexPost, hash, true, this.dbSession);
	}

	@Test
	public void storePostBibTexUpdatePlugin() {
		// final String BIB_TEST_HASH = "b6c9a44d411bf8101abdf809d5df1431";
		final String BIB_TEST_HASH = "2313536a09d3af706469e3d2523fe7ca";		
		final String TEST_USER = "thomi";
		
		this.bibtexParam.setRequestedSimHash(HashID.INTRA_HASH);

		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final org.bibsonomy.database.plugin.plugins.BibTexExtra plugin = new org.bibsonomy.database.plugin.plugins.BibTexExtra();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);

		List<BibTexExtra> extras = this.bibTexExtraDb.getURL(BIB_TEST_HASH, TEST_USER, this.dbSession);
		assertEquals(2, extras.size());

		this.bibtexParam.setGroupType(GroupID.PRIVATE);
		this.postDuplicate(this.bibtexParam, BIB_TEST_HASH);

		final Post<BibTex> post = this.bibTexDb.getBibTexByHash(this.bibtexParam, this.dbSession).get(0);
		assertNotNull(post);

		extras = this.bibTexExtraDb.getURL(BIB_TEST_HASH, TEST_USER, this.dbSession);
		assertEquals(2, extras.size());
		
		this.resetParameters();
	}
}