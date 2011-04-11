package org.bibsonomy.model.sync;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.Resource;


/**
 * @author wla
 * @version $Id$
 */
public interface SyncLogicInterface {

	/**
	 * 
	 * @param userName
	 * @return
	 */
	public Map<String, SynchronizationPost> getSyncPostsMapForUser(String userName);
	
	/**
	 * 
	 * @param userName
	 * @param serviceId
	 * @param contentType
	 * @return
	 */
	public Date getCurrentSyncDate(String userName, int serviceId, int contentType);
	
	/**
	 * 
	 * @param data
	 */
	public void setCurrentSyncDone(SynchronizationData data);
	
	/**
	 * 
	 * @param userName
	 * @param serviceId
	 * @param contentType
	 * @return
	 */
	public SynchronizationData getCurrentSynchronizationData (String userName, int serviceId, int contentType);
	
	/**
	 * 
	 * @param userName
	 * @param serviceId
	 * @param contentType
	 * @return
	 */
	public SynchronizationData getLastSynchronizationData (String userName, int serviceId, int contentType);
	
	/**
	 * 
	 * @param resourceType (e. g. Bibtex, Bookmark....) 
	 * @param userName
	 * @return List of SnchronizationPosts for given user 
	 */
	public List<SynchronizationPost> getPostsForSync (Class<? extends Resource> resourceType, String userName);
	
	/**
	 * 
	 * @param userName 
	 * @param resourceType 
	 * @param clientPosts
	 * @param strategy 
	 * @param serviceIdentifier 
	 * @return list of posts with set synchronization state
	 */
	public List<SynchronizationPost> getSynchronization(final String userName, Class<? extends Resource> resourceType, final List<SynchronizationPost> clientPosts, final ConflictResolutionStrategy strategy, final String serviceIdentifier);
}
