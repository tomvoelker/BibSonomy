package org.bibsonomy.search.index.update.post;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.Date;
import java.util.List;

/**
 * all neccessary methods for updating a community post index
 * @param <R>
 * @author dzo
 */
public class CommunityPostIndexUpdateLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements IndexUpdateLogic<Post<R>> {

	/**
	 * default constructor
	 *
	 * @param resourceClass the resource class
	 */
	public CommunityPostIndexUpdateLogic(Class<R> resourceClass) {
		super(resourceClass);
	}

	@Override
	public List<Post<R>> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastLogDate(lastLogDate);
			param.setLimit(size);
			param.setOffset(offset);
			return (List<Post<R>>) this.queryForList("getNew" + this.getResourceName() + "Posts", param, session);
		}
	}

	@Override
	public List<Post<R>> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return (List<Post<R>>) this.queryForList("getDeleted" + this.getResourceName() + "Posts", lastLogDate, session);
		}
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

	@Override
	public SearchIndexSyncState getDbState() {
		return null;
	}

}
