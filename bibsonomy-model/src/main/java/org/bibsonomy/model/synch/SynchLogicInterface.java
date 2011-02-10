package org.bibsonomy.model.synch;

import java.util.List;

import org.bibsonomy.model.Resource;


/**
 * @author wla
 * @version $Id$
 */
public interface SynchLogicInterface {

	
	/**
	 * 
	 * @param <T> Resource type (Bibtex, Bookmark....) 
	 * @param resourceType
	 * @param userName
	 * @return List of SnchronizationPosts for given user 
	 */
	public <T extends SynchronizationResource> List<SynchronizationPost> getSynchPosts (Class<? extends Resource> resourceType, String userName);
}
