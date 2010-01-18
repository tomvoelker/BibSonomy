package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.PostUpdateOperation;
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
	 * @param loginUserName
	 * @param resourceHash
	 * @param userName
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of posts
	 */
	public Post<T> getPostDetails(String loginUserName, String resourceHash, String userName, List<Integer> visibleGroupIDs, DBSession session);

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
	 * create
	 * 
	 * @param post
	 * @param session
	 * @return true if entry was created
	 */
	public boolean createPost(Post<T> post, DBSession session);
	
	/**
	 * update
	 * 
	 * @param post
	 * @param oldHash
	 * @param operation
	 * @param session
	 * @return true, if entry existed and was updated
	 */
	public boolean updatePost(Post<T> post, String oldHash, PostUpdateOperation operation, DBSession session);
}