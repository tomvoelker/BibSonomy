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

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

import java.util.List;

/**
 * abstract class for {@link CommunityPostIndexCommunityUpdateLogic} tests
 *
 * @param <R>
 * @author dzo
 */
public abstract class CommunityPostIndexCommunityUpdateLogicTest<R extends Resource> extends CommunityPostIndexUpdateLogicTest<R> {

	protected abstract CommunityPostIndexCommunityUpdateLogic<R> getCommunityUpdateLogic();

	@Override
	protected CommunityPostIndexUpdateLogic<R> getUpdateLogic() {
		return this.getCommunityUpdateLogic();
	}

	@Test
	public void testGetNewestPostByInterHash() {
		final Post<R> newestPostByInterHash = this.getCommunityUpdateLogic().getNewestPostByInterHash(this.getPostInterHash());
		this.testNewestPostByInterHash(newestPostByInterHash);
	}

	protected abstract void testNewestPostByInterHash(Post<R> newestPostByInterHash);

	protected abstract String getPostInterHash();

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getPostsOfUser(String, int, int)}
	 */
	@Test
	public void testGetPostsOfUser() {
		final List<Post<R>> postsOfUser = this.getCommunityUpdateLogic().getPostsOfUser("testuser1", 10, 0);
		this.testPostsOfUser1(postsOfUser);

		final List<Post<R>> testuser3Posts = this.getCommunityUpdateLogic().getPostsOfUser("testuser3", 10, 0);
		this.testPostsofUser2(testuser3Posts);
	}

	protected abstract void testPostsofUser2(List<Post<R>> testuser3Posts);

	protected abstract void testPostsOfUser1(List<Post<R>> postsOfUser);

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getAllPostsOfUser(String)}
	 */
	@Test
	public void testGetAllPostsOfUser() {
		final List<Post<R>> testspammer1Posts = this.getCommunityUpdateLogic().getAllPostsOfUser("testspammer1");
		this.testAllPostsOfSpammer1(testspammer1Posts);
	}

	protected abstract void testAllPostsOfSpammer1(List<Post<R>> testspammer1Posts);
}
