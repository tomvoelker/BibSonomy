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
