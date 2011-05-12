package org.bibsonomy.model.sync;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bibsonomy.model.Resource;


/**
 * @author wla
 * @version $Id$
 */
public interface SyncLogicInterface {
	
	/**
	 * 
	 * @param userName
	 * @param serviceId
	 * @param userCredentials
	 */
	public void storeNewClientForUser(final String userName, final int serviceId, final Properties userCredentials);
	
	/**
	 * 
	 * @param user
	 * @return 
	 */
	public List<SyncService> getSyncServicesForUser(String user);
	
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
	 * @return Synchronization data of currently running synchronization: status is "undone"
	 */
	public SynchronizationData getCurrentSynchronizationDataForUserForServiceForContent (String userName, int serviceId, int contentType);
	
	/**
	 * 
	 * @param userName
	 * @param serviceId
	 * @param contentType
	 * @return Synchronization data of last successful synchronization: date and status
	 */
	public SynchronizationData getLastSynchronizationDataForUserForContentType (String userName, int serviceId, int contentType);
	
	/**
	 * 
	 * @param resourceType (e. g. Bibtex, Bookmark....) 
	 * @param userName
	 * @return List of SnchronizationPosts for given user 
	 */
	public List<SynchronizationPost> getSyncPostsListForUser (Class<? extends Resource> resourceType, String userName);
	
	/**
	 * 
	 * @param userName
	 * @return map with user posts used from server!
	 */
	public Map<String, SynchronizationPost> getSyncPostsMapForUser(String userName);
	
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
