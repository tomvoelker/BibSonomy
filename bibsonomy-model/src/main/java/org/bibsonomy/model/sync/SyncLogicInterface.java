package org.bibsonomy.model.sync;

import java.net.URI;
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
	 * @param service
	 * @param userCredentials
	 */
	public void createSyncServer(final String userName, final URI service, final Properties userCredentials);
	
	/**
	 * 
	 * @param userName
	 * @param service
	 */
	public void deleteSyncServer(final String userName, final URI service);
	
	/**
	 * 
	 * @param userName
	 * @param service
	 * @param userCredentials 
	 */
	public void updateSyncServer(final String userName, final URI service, final Properties userCredentials);
	
	/**
	 * 
	 * @param userName
	 * @return List of synchronization services for given user 
	 */
	public List<SyncService> getSyncServicesForUser(final String userName);
	
	/**
	 * 
	 * @param userName
	 * @param service
	 * @param resourceType
	 * @return returns date of current running Synchronization
	 */
	public Date getCurrentSyncDate(final String userName, final URI service, final Class<? extends Resource> resourceType);
	
	/**
	 * 
	 * @param data
	 */
	public void updateSyncData(final SynchronizationData data);
	
	/**
	 * 
	 * @param userName
	 * @param service
	 * @param resourceType
	 * @return Synchronization data of currently running synchronization: status is "undone"
	 */
	public SynchronizationData getCurrentSynchronizationDataForUserForServiceForContent(final String userName, final URI service, final Class<? extends Resource> resourceType);
	
	/**
	 * 
	 * @param userName
	 * @param service
	 * @param resourceType
	 * @return Synchronization data of last successful synchronization: date and status
	 */
	public SynchronizationData getLastSynchronizationDataForUserForContentType (final String userName, final URI service, final Class<? extends Resource> resourceType);
	
	/**
	 * 
	 * @param resourceType (e. g. Bibtex, Bookmark....) 
	 * @param userName
	 * @return List of SnchronizationPosts for given user 
	 */
	public List<SynchronizationPost> getSyncPostsListForUser (final Class<? extends Resource> resourceType, final String userName);
	
	/**
	 * 
	 * @param userName
	 * @param resourceType 
	 * @return map with user posts used from server!
	 */
	public Map<String, SynchronizationPost> getSyncPostsMapForUser(final String userName, Class<? extends Resource> resourceType);
	
	/**
	 * 
	 * @param userName 
	 * @param resourceType 
	 * @param clientPosts
	 * @param strategy 
	 * @param service 
	 * @return list of posts with set synchronization state
	 */
	public List<SynchronizationPost> getSynchronization(final String userName, Class<? extends Resource> resourceType, final List<SynchronizationPost> clientPosts, final ConflictResolutionStrategy strategy, final URI service);
}
