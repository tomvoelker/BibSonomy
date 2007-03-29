package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * For every content type there should exist a separate class which implements
 * this interface. It supplies basic CRUD: create, read, update and delete.
 */
public interface CrudableContent {
	// read
	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous);

	// read
	public Post<? extends Resource> getPostDetails(String authUser, String resourceHash, String userName);

	// delete
	public boolean deletePost(String userName, String resourceHash);

	// create, update
	public boolean storePost(String userName, Post post, boolean update);
}