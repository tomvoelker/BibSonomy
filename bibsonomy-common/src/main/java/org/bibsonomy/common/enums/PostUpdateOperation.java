package org.bibsonomy.common.enums;

/**
 * Depicts which party of a post should be updated when calling 
 * the <code>update(...)</code> method in the LogicInterface.
 * 
 * @author rja
 * @version $Id$
 */
public enum PostUpdateOperation {
	/**
	 * Update all parts of the entity.
	 */
	UPDATE_ALL(0),
	/**
	 * Update only the tags of the post.
	 */
	UPDATE_TAGS(1),
	/**
	 * Update only the documents attached to the post.
	 */
	UPDATE_DOCUMENTS(2);
	
	private int id;
	
	private PostUpdateOperation(final int postUpdateOperation) {
		this.id = postUpdateOperation;
	}
	
}
