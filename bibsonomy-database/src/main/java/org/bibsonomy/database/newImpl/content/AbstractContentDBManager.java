package org.bibsonomy.database.newImpl.content;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;


/*
 * for every content type there should exists a separate class which extends this class.
 */
public interface AbstractContentDBManager {

	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous);

	public Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String userName);

	public boolean deletePost(String userName, String resourceHash);
	
	public boolean storePost(String userName, Post post, boolean update);
}
