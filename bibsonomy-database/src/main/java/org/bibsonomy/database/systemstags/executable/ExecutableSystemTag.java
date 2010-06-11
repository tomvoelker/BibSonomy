package org.bibsonomy.database.systemstags.executable;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author sdo
 * @version $Id$
 */
public interface ExecutableSystemTag extends SystemTag{

	/**
	 * Action to perform before the creation of a post
	 * 
	 * @param <T> = Resource Type of the post
	 * @param post = a VALID post for which action should be performed
	 * @param session 
	 */
	public <T extends Resource> void performBeforeCreate(Post<T> post, final DBSession session);

	/**
	 * Action to perform before the update of a post
	 * 
	 * @param <T> Resource Type of the post
	 * @param newPost = updated post
	 * @param oldPost = post to be updated If operation is not UPDATE_TAGS the post MUST be VALID
	 * @param operation = type of UpdateOperation
	 * @param session 
	 */
	public <T extends Resource> void performBeforeUpdate(Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session);

	/**
	 * Action to perform after the creation of a post
	 * 
	 * @param <T> = Resource Type of the post
	 * @param post = a VALID post for which action should be performed
	 * @param session 
	 */
	public <T extends Resource> void performAfterCreate(Post<T> post, final DBSession session);
	
	/**
	 * Action to perform after the update of a post
	 * 
	 * @param <T> Resource Type of the post
	 * @param newPost = updated post
	 * @param oldPost = post to be updated If operation is not UPDATE_TAGS the post MUST be VALID
	 * @param operation = type of UpdateOperation
	 * @param session 
	 */
	public <T extends Resource> void performAfterUpdate(Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session);

	/**
	 * Creates a new instance of this kind of ExecutableSystemTag
	 * @return
	 */
	public ExecutableSystemTag newInstance();


}
