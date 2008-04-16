package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * For every content type there should exist a separate class which implements
 * this interface. It supplies basic CRUD: create, read, update and delete.
 * 
 * @author Christian Schenk
 * @version $Id$
 * @param <T> extends Resource
 * @param <P> extends GenericParam
 */
public interface CrudableContent<T extends Resource, P extends GenericParam> {
	/**
	 * Read
	 * 
	 * @param param
	 * @param session
	 * @return list of posts
	 */
	public List<Post<T>> getPosts(P param, DBSession session);

	/**
	 * Read
	 * 
	 * @param authUser
	 * @param resourceHash
	 * @param userName
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of posts
	 */
	public Post<T> getPostDetails(String authUser, String resourceHash, String userName, List<Integer> visibleGroupIDs, DBSession session);

	/**
	 * Delete
	 * 
	 * @param userName 
	 * @param resourceHash 
	 * @param session 
	 * 
	 * @return true, if entry existed and was deleted
	 */
	public boolean deletePost(String userName, String resourceHash, DBSession session);

	/**
	 * Create and update
	 * 
	 * @param userName 
	 * @param post 
	 * @param oldHash 
	 * @param update 
	 * @param session 
	 * 
	 * @return true, if entry existed and was updated
	 */
	public boolean storePost(String userName, Post<T> post, String oldHash, boolean update, DBSession session);
}