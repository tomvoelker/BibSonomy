package org.bibsonomy.database.newImpl.content;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;


/*
 * for every content type there should exists a separate class which extends this class.
 */
public abstract class AbstractContentDBManager {

	public abstract List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous);

	public abstract Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String userName);

	public abstract boolean deletePost(String userName, String resourceHash);
	
	public abstract boolean storePost(String userName, Post post, boolean update);
}
