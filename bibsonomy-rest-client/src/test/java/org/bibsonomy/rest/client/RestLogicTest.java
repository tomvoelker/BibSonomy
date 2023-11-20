/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.SortUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RestLogicTest extends TestCase {

	// TODO probably move somewhere git ignored
	private static String API_URL = RestLogicFactory.BIBSONOMY_API_URL;
	private static String LOGIN_NAME = "";
	private static String API_KEY = "";
	private static boolean PRINT_SORTING = true;

	private RestLogic logic;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.logic = (RestLogic) new RestLogicFactory(API_URL).getLogicAccess(LOGIN_NAME, API_KEY);
	}

	@Test
	public void testSearch() {
		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.search("test")
				.end(100)
				.createPostQuery(BibTex.class);
		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testSearchSorted() {
		SortKey sortKey = SortKey.TITLE;
		SortOrder sortOrder = SortOrder.ASC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.search("test")
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	@Test
	public void testGetPosts() {
		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.end(100)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testGetPostsSorted() {
		SortKey sortKey = SortKey.TITLE;
		SortOrder sortOrder = SortOrder.ASC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.setScope(QueryScope.SEARCHINDEX)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	@Test
	public void testGetPostsSortedByAuthor() {
		SortKey sortKey = SortKey.AUTHOR;
		SortOrder sortOrder = SortOrder.ASC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.setScope(QueryScope.SEARCHINDEX)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	@Test
	public void testGetPostsSortedByPubdate() {
		SortKey sortKey = SortKey.PUBDATE;
		SortOrder sortOrder = SortOrder.DESC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.setScope(QueryScope.SEARCHINDEX)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	@Test
	public void testGetPostsForUser() {
		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.USER)
				.setGroupingName("hotho")
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(SortKey.TITLE, SortOrder.ASC))
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testGetPostsForUserSorted() {
		SortKey sortKey = SortKey.TITLE;
		SortOrder sortOrder = SortOrder.ASC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.USER)
				.setGroupingName("hotho")
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.setScope(QueryScope.SEARCHINDEX)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	@Test
	public void testGetPostsForGroup() {
		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.GROUP)
				.setGroupingName("kde")
				.end(100)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testGetBookmarksForUser() {
		PostQuery<Bookmark> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.USER)
				.setGroupingName("hotho")
				.end(100)
				.createPostQuery(Bookmark.class);

		List<Post<Bookmark>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testGetPublicationsForPerson() {
		PostQuery<GoldStandardPublication> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.PERSON)
				.setGroupingName("a.hotho")
				.end(100)
				.createPostQuery(GoldStandardPublication.class);

		List<Post<GoldStandardPublication>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testGetPostsForGroupSorted() {
		SortKey sortKey = SortKey.TITLE;
		SortOrder sortOrder = SortOrder.ASC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.GROUP)
				.setGroupingName("kde")
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.setScope(QueryScope.SEARCHINDEX)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	@Test
	public void testGetPostsForTag() {
		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.setTags(Arrays.asList("myown"))
				.end(100)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);
	}

	@Test
	public void testGetPostsForTagSorted() {
		SortKey sortKey = SortKey.TITLE;
		SortOrder sortOrder = SortOrder.ASC;

		PostQuery<BibTex> query = new PostQueryBuilder()
				.setGrouping(GroupingEntity.ALL)
				.setTags(Arrays.asList("myown"))
				.end(100)
				.setSortCriteria(SortUtils.singletonSortCriteria(sortKey, sortOrder))
				.setScope(QueryScope.SEARCHINDEX)
				.createPostQuery(BibTex.class);

		List<Post<BibTex>> posts = this.logic.getPosts(query);
		Assert.assertNotEquals(posts.size(), 0);

		RestLogicTest.printSorting(posts, sortKey);
		Assert.assertEquals(true, testSorting(posts, sortKey, sortOrder));
	}

	private static boolean testSorting(List<Post<BibTex>> posts, SortKey sortKey, SortOrder sortOrder) {
		// TODO need ascii folding first
		return true;
	}

	private static void printSorting(List<Post<BibTex>> posts, SortKey sortKey) {
		if (PRINT_SORTING) {
			switch(sortKey) {
				case PUBDATE:
					for (Post<BibTex> post : posts) {
						System.out.println(post.getResource().getDay() + " " + post.getResource().getMonth() + " " + post.getResource().getYear());
					}
					break;
				case AUTHOR:
					for (Post<BibTex> post : posts) {
						System.out.println(post.getResource().getAuthor().toString());
					}
					break;
				case TITLE:
				default:
					for (Post<BibTex> post : posts) {
						System.out.println(post.getResource().getTitle());
					}
					break;
			}
		}
	}

}
