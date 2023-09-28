/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * @author dzo
 */
public class GoldStandardBookmarkDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static final GoldStandardBookmarkDatabaseManager manager = GoldStandardBookmarkDatabaseManager.getInstance();
	
	private static final String GOLD_BOOKMARK_INTERHASH = "20592a292e53843965c1bb42bfd51876";
	
	@Test
	public void getGoldStandardBookmark() {
		final Post<GoldStandardBookmark> post = manager.getPostDetails("", GOLD_BOOKMARK_INTERHASH, "", Collections.emptyList(), this.dbSession);
		final GoldStandardBookmark bookmark = post.getResource();
		assertEquals("http://www.uni-kassel.de", bookmark.getUrl());
		assertEquals(1025, post.getContentId().intValue());
	}

	@Test
	public void testGetGoldStandardsAfterChangeDate() throws ParseException {
		String changeDateString = "2008-05-01";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date changeDate = formatter.parse(changeDateString);

		List<Post<GoldStandardBookmark>> posts = manager.getGoldStandardPostsAfterChangeDate(changeDate, this.dbSession);
		assertThat(posts.size(), equalTo(1));
	}
	
	@Test
	public void createBookmark() {
		this.createGoldStandardBookmark();
	}
	
	@Test
	public void createDuplicate() {
		this.createGoldStandardBookmark();
		try {
			this.createGoldStandardBookmark();
			fail("duplicate missing database exception");
		} catch (final DatabaseException ex) {
			// ignore
		}
	}

	protected void createGoldStandardBookmark() {
		this.pluginMock.reset();
		assertFalse(this.pluginMock.isOnGoldStandardCreate());
		
		// create post
		final Post<GoldStandardBookmark> post = this.generateGoldBookmark();
		final JobResult jobResult = manager.createPost(post, null, this.dbSession);
		assertThat(jobResult.getStatus(), is(Status.OK));
		
		final String interhash = post.getResource().getInterHash();
		assertNotNull(manager.getPostDetails("", interhash, "", null, this.dbSession).getResource());
		
		assertTrue(this.pluginMock.isOnGoldStandardCreate());
	}

	@Test
	public void testDeletePost() {
		this.pluginMock.reset();
		assertFalse(this.pluginMock.isOnGoldStandardDelete());
		
		// delete post
		manager.deletePost("", GOLD_BOOKMARK_INTERHASH, new User("testuser1"), this.dbSession);
		assertNull(manager.getPostDetails("", GOLD_BOOKMARK_INTERHASH, "", null, this.dbSession));
		
		assertTrue(this.pluginMock.isOnGoldStandardDelete());
	}
	
	// TODO: add a builder for posts!
	private Post<GoldStandardBookmark> generateGoldBookmark() {
		final Post<GoldStandardBookmark> post = new Post<>();

		// groups
		final Group group = GroupUtils.buildPublicGroup();
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
