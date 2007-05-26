package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * For every content type there should exist a separate class which implements
 * this interface. It supplies basic CRUD: create, read, update and delete.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface CrudableContent<T extends Resource, P extends GenericParam> {
	// read
	public List<Post<T>> getPosts(P param, Transaction session);

	// read
	public Post<T> getPostDetails(String authUser, String resourceHash, String userName, Transaction session);

	/**
	 *  delete
	 *  @return true, if entry existed and was deleted
	 */
	public boolean deletePost(String userName, String resourceHash, Transaction session);

	/**
	 *  create, update
	 *  @return true, if entry existed and was updated
	 */
	public boolean storePost(String userName, Post<T> post, String oldHash, Transaction session);
}