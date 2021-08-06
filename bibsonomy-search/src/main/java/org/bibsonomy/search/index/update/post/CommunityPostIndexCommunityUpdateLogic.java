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

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.management.database.params.SearchParam;

/**
 * all neccessary methods for updating a community post index
 * @param <R>
 * @author dzo
 */
public class CommunityPostIndexCommunityUpdateLogic<R extends Resource> extends CommunityPostIndexUpdateLogic<R> {

	/**
	 * default constructor
	 *
	 * @param resourceClass the resource class
	 */
	public CommunityPostIndexCommunityUpdateLogic(Class<R> resourceClass) {
		super(resourceClass, false);
	}

	/**
	 * returns the latest public post with the provided interhash in the system
	 * @param interHash
	 * @return
	 */
	public Post<R> getNewestPostByInterHash(final String interHash) {
		try (final DBSession session = this.openSession()) {
			return (Post<R>) this.queryForObject("getLatest" + this.getResourceName() + "Post", interHash, session);
		}
	}

	/**
	 * this method returns posts of the user that are the newest public posts (by interhash)
	 * and there is no community post in the database
	 *
	 * @param userName the name of the user
	 * @param limit how many posts should be returned
	 * @param offset how many posts should be skipped
	 * @return posts of the user
	 */
	public List<Post<R>> getPostsOfUser(final String userName, final int limit, final int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLimit(limit);
			param.setOffset(offset);
			param.setUserName(userName);
			return (List<Post<R>>) this.queryForList("get" + this.getResourceName() + "PostsForUserWithoutCommunityPost", param, session);
		}
	}

	/**
	 * @param userName
	 * @return all posts of the user
	 */
	public List<Post<R>> getAllPostsOfUser(final String userName) {
		try (final DBSession session = this.openSession()) {
			return (List<Post<R>>) this.queryForList("get" + this.getResourceName() + "PostsForUser", userName, session);
		}
	}

	/**
	 * @param lastLogDate
	 * @return all posts that were deleted by the users (for updating the all_users field)
	 */
	public List<Post<R>> getAllDeletedNormalPosts(Date lastLogDate, final int limit, final int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLimit(limit);
			param.setOffset(offset);
			param.setLastLogDate(lastLogDate);
			return (List<Post<R>>) this.queryForList("get" + this.getResourceName() + "AllDeletedPosts", param, session);
		}
	}

	/**
	 * @param lastContentId
	 * @param limit
	 * @param offset
	 * @return all posts that were added by the users (for updating the all_users field)
	 */
	public List<Post<R>> getAllNewPosts(Integer lastContentId, int limit, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLimit(limit);
			param.setOffset(offset);
			param.setLastContentId(lastContentId);

			return (List<Post<R>>) this.queryForList("get" + this.getResourceName() + "AllNewPosts", param, session);
		}
	}
}
