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
	 * Add service to the database
	 * @param service service to add 
	 * @param server server/client switch
	 */
	public void createSyncService(final URI service, final boolean server);
	
	
	/**
	 * Removes service from database
	 * @param service
	 * @param server
	 */
	public void deleteSyncService(final URI service, final boolean server);
	
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
	 * @return List of synchronization server for given user 
	 */
	public List<SyncService> getSyncServerForUser(final String userName);
	
	/**
	 * 
	 * @param userName
	 * @param uri
	 * @return
	 */
	public SyncService getSyncServer(final String userName, final URI uri);
	
	/**
	 * 
	 * @param server switch between server and clients
	 * @return List of for this System allowed synchronization services
	 */
	public List<SyncService> getAvlSyncServices(boolean server);
	
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
	public void updateSyncStatus(final SynchronizationData data, final String status);
	
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
