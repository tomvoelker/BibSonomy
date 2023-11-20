/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for the community bookmarks
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicCommunityBookmarkTest extends CommunityPostIndexCommunityUpdateLogicTest<GoldStandardBookmark> {
	private static final CommunityPostIndexCommunityUpdateLogic<GoldStandardBookmark> UPDATE_LOGIC = (CommunityPostIndexCommunityUpdateLogic<GoldStandardBookmark>) SearchSpringContextWrapper.getBeanFactory().getBean("communityIndexUpdateLogicCommunityBookmark");

	@Override
	protected CommunityPostIndexCommunityUpdateLogic<GoldStandardBookmark> getCommunityUpdateLogic() {
		return UPDATE_LOGIC;
	}

	@Override
	protected void testNewestPostByInterHash(Post<GoldStandardBookmark> newestPostByInterHash) {
		assertThat(newestPostByInterHash.getResource().getTitle(), is("kde"));
	}

	@Override
	protected String getPostInterHash() {
		return "85ab919107e4cc79b345e996b3c0b097";
	}

	@Override
	protected void testNewEntities(List<Post<GoldStandardBookmark>> newCommunityPosts) {
		assertThat(newCommunityPosts.size(), is(1));
		assertThat(newCommunityPosts.get(0).getResource().getTitle(), is("Universität Kassel"));
	}

	@Override
	protected long getLastEntityIdForNewEntities() {
		return 0;
	}

	@Override
	protected void testPostsofUser2(List<Post<GoldStandardBookmark>> testuser3Posts) {
		assertThat(testuser3Posts.size(), is(1));

		assertThat(testuser3Posts.get(0).getResource().getTitle(), is("web.de"));
	}

	@Override
	protected void testPostsOfUser1(List<Post<GoldStandardBookmark>> postsOfUser) {
		assertThat(postsOfUser.size(), is(1));
	}

	@Override
	protected void testAllPostsOfSpammer1(List<Post<GoldStandardBookmark>> testspammer1Posts) {
		assertThat(testspammer1Posts.size(), is(0));
	}
}
