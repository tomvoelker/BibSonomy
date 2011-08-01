package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Date;

import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;



/**
 * @author dzo
 * @version $Id$
 */
public class GoldStandardBookmarkDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static final GoldStandardBookmarkDatabaseManager manager = GoldStandardBookmarkDatabaseManager.getInstance();
	
	private static final String GOLD_BOOKMARK_INTERHASH = "20592a292e53843965c1bb42bfd51876";
	
	@Test
	public void getGoldStandardBookmark() {
		final Post<GoldStandardBookmark> post = manager.getPostDetails("", GOLD_BOOKMARK_INTERHASH, "", Collections.<Integer>emptyList(), this.dbSession);
		final GoldStandardBookmark bookmark = post.getResource();
		assertEquals("http://www.uni-kassel.de", bookmark.getUrl());
	}
	
	@Test
	public void createBookmark() {
		final String interhash = this.createGoldStandardBookmark();
		// clear database
		this.deletePost(interhash);
	}
	
	@Test
	public void createDuplicate() {
		final String interhash = this.createGoldStandardBookmark();
		try {
			this.createGoldStandardBookmark();
			fail("duplicate missing database exception");
		} catch (final DatabaseException ex) {
			// ignore
		}
		
		this.deletePost(interhash);
	}

	protected String createGoldStandardBookmark() {
		this.pluginMock.reset();
		assertFalse(this.pluginMock.isOnGoldStandardCreate());
		
		// create post
		final Post<GoldStandardBookmark> post = this.generateGoldBookmark();
		assertTrue(manager.createPost(post, this.dbSession));
		
		final String interhash = post.getResource().getInterHash();
		assertNotNull(manager.getPostDetails("", interhash, "", null, this.dbSession).getResource());
		
		assertTrue(this.pluginMock.isOnGoldStandardCreate());
		return interhash;
	}
	
	private void deletePost(final String interhash) {
		this.pluginMock.reset();
		assertFalse(this.pluginMock.isOnGoldStandardDelete());
		
		// delete post
		manager.deletePost("", interhash, this.dbSession);
		assertNull(manager.getPostDetails("", interhash, "", null, this.dbSession));
		
		assertTrue(this.pluginMock.isOnGoldStandardDelete());
	}	
	
	// TODO: add a builder for posts!
	private Post<GoldStandardBookmark> generateGoldBookmark() {
		final Post<GoldStandardBookmark> post = new Post<GoldStandardBookmark>();

		// groups
		final Group group = GroupUtils.getPublicGroup();
		post.getGroups().add(group);
		
		post.setDescription("trallalla");
		post.setDate(new Date());
		post.setUser(ModelUtils.getUser());
		final GoldStandardBookmark bookmark = new GoldStandardBookmark();
		bookmark.setUrl("http://www.bibsonomy.org");
		bookmark.setTitle("BibSonomy :: home");
		bookmark.recalculateHashes();
		
		post.setResource(bookmark);
		
		return post;
	}
}
