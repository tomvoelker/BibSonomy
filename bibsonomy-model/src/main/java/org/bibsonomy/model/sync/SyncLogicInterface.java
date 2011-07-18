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
	
	/* ********************************************************************
	 * create, read, update sync services - user independent
	 */
	
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
	 * @return List of synchronization servers for given user 
	 */
	public List<SyncService> getSyncServer(final String userName);
	
	
	
	/* ********************************************************************
	 * create, read, update, delete sync services - user dependent
	 */
	
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
	 * @param server switch between server and clients
	 * @return List of for this System allowed synchronization services
	 */
	public List<SyncService> getSyncServices(final boolean server);

	
	
	/* ********************************************************************
	 * create, read, update, delete sync services - user dependent
	 */
	
	/**
	 * 
	 * @param userName
	 * @param service
	 * @param resourceType
	 * @return returns date of current running Synchronization
	 */
	public Date getLastSyncDate(final String userName, final URI service, final Class<? extends Resource> resourceType);
	
	/**
	 * 
	 * @param data
	 * @param status 
	 * @param info
	 */
	public void updateSyncStatus(final SynchronizationData data, final SynchronizationStatus status, final String info);
	
	/**
	 * 
	 * @param userName
	 * @param service
	 * @param resourceType
	 * @return Synchronization data of last successful synchronization: date and status
	 */
	public SynchronizationData getLastSyncData (final String userName, final URI service, final Class<? extends Resource> resourceType);
	
	/**
	 * 
	 * @param userName
	 * @param resourceType (e. g. Bibtex, Bookmark....) 
	 * @return List of SnchronizationPosts for given user 
	 */
	public List<SynchronizationPost> getSyncPosts (final String userName, final Class<? extends Resource> resourceType);
	
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
	public List<SynchronizationPost> getSyncPlan(final String userName, Class<? extends Resource> resourceType, final List<SynchronizationPost> clientPosts, final ConflictResolutionStrategy strategy, final URI service);
}
