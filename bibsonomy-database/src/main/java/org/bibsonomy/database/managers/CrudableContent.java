package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * For every content type there should exist a separate class which implements
 * this interface. It supplies basic CRUD: create, read, update and delete.
 * 
 * @version $Id$
 */
public interface CrudableContent<T extends Resource> {
	// read
	public List<Post<T>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous, Transaction transaction);

	// read
	public Post<T> getPostDetails(String authUser, String resourceHash, String userName, Transaction transaction);

	// delete
	// FIXME why do we return a boolean here? error checking?!?
	public boolean deletePost(String userName, String resourceHash, Transaction transaction);

	// create, update
	// FIXME why do we return a boolean here? error checking?!?
	public boolean storePost(String userName, Post<T> post, String oldHash, Transaction transaction);
}