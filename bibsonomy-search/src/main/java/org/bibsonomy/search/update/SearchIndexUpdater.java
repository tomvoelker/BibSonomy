package org.bibsonomy.search.update;

import java.io.IOException;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;

/**
 * search index update
 * @author dzo
 * @param <R> 
 */
public interface SearchIndexUpdater<R extends Resource> {

	/**
	 * @param contentIdToDelete
	 */
	public void deletePostWithContentId(final int contentIdToDelete);

	/**
	 * @param userName
	 */
	public void removeAllPostsOfUser(String userName);

	/**
	 * @param post
	 */
	public void insertPost(SearchPost<R> post);

	/**
	 * @param newState
	 */
	public void updateIndexState(SearchIndexState newState);

	/**
	 * TODO: move?
	 * @throws IOException TODO
	 */
	public void createEmptyIndex() throws IOException;

}
