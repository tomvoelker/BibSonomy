package org.bibsonomy.search.management;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.model.SearchIndexInfo;

/**
 * interface which describes methods for managing a resource search
 *
 * @author dzo
 * @param <R> 
 */
public interface SearchResourceManagerInterface<R extends Resource> {
	
	public void generateIndexForResource(final String containerId, final String indexId) throws ExecutionException;
	
	public List<SearchIndexInfo> getInfomationOfIndexForResource();
	
	/**
	 * updates the index, that is - adds new posts - updates posts, where tag
	 * assignments have changed - removes deleted posts
	 * 
	 * For that, we keep track of the newest tas_id seen during index update.
	 * 
	 * On each update, we query for all posts with greater tas_ids. These Posts
	 * are either new, or belong to posts, where the tag assignments have
	 * changed. We delete all those posts from the index (for implementing the
	 * tag update). Afterwards, all these posts are (re-)inserted.
	 * 
	 * To keep track of deleted posts, we further hold the last log_date t and
	 * query for all content_ids from the log_table with a change_date >=
	 * t-epsilon. These posts are removed from the index together with the
	 * updated posts.
	 * @param resourceType the resource search index to be updated
	 */
	public void updateAllIndices();
}
