package org.bibsonomy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationActions;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManager extends AbstractDatabaseManager {
    private static final Log log = LogFactory.getLog(SynchronizationDatabaseManager.class);

	private static final SynchronizationDatabaseManager singleton = new SynchronizationDatabaseManager();
    
    private final GeneralDatabaseManager generalDb;
    
    /**
     * Singleton 
     * @return SynchronizationDatabaseManager
     */
    public static SynchronizationDatabaseManager getInstance() {
    	return singleton;
    }
    
    private SynchronizationDatabaseManager() {
    	this.generalDb = GeneralDatabaseManager.getInstance();
    }
    
    /**
     * Add a sync service. Callers should check, if a client/server with that
     * URI already exists. Otherwise, a DUPLICATE KEY exception will be thrown.
     * 
     * @param session
     * @param service - the URI of the service to be added
     * @param server - <code>true</code> if the service may act as a server, <code>false</code> if it may act as a client
     */
    public void createSyncService(final DBSession session, final URI service, final boolean server) {
    	session.beginTransaction();
    	try {
    		final SyncParam param = new SyncParam();
    		param.setService(service);
    		param.setServer(server);
    		param.setServiceId(generalDb.getNewId(ConstantID.IDS_SYNC_SERVICE, session));
	    	session.insert("insertSyncService", param);
	    	session.commitTransaction();
    	} finally {
    		session.endTransaction();
    	}
    }

    /**
     * Remove a sync service.
     * 
     * @param session
     * @param service - the URI of the service to be removed
     * @param server - <code>true</code> if the server part should be deleted, <code>false</code> if the client client part should be deleted
     */
    public void deleteSyncService(final DBSession session, final URI service, final boolean server) {
    	final SyncParam param =  new SyncParam();
    	param.setService(service);
    	param.setServer(server);
    	session.delete("deleteSyncService", param);
    }
    
    /**
     * Update the given synchronization data's status in the database.
     * 
     * @param session - the database session
     * @param status - the status to set
     * @param data SynchronizationData
     */
    public void updateSyncStatus(final DBSession session, final SynchronizationData data, final SynchronizationStatus status, final String info) {
		final SyncParam param = new SyncParam();
		param.setUserName(data.getUserName());
		param.setService(data.getService());
		param.setResourceType(data.getResourceType());
		param.setLastSyncDate(data.getLastSyncDate());
		param.setStatus(status);
		param.setInfo(info);
		param.setServer(false);
		session.update("updateSyncStatus", param);
    }

    /**
     * Insert new synchronization data for user.
     * 
     * @param session
     * @param userName
     * @param credentials
     * @param serviceId
     */
    public void createSyncServerForUser(final DBSession session, final String userName, final URI service, final Class<? extends Resource> resourceType, final Properties userCredentials, final SynchronizationDirection direction) {
    	final SyncParam param = new SyncParam();
    	param.setUserName(userName);
    	param.setCredentials(userCredentials);
    	param.setDirection(direction);
    	param.setResourceType(resourceType);
    	param.setService(service);
    	param.setServer(true);
		session.insert("insertSyncServiceForUser", param);
    }
    
    /**
     * Removes synchronization data for user.
     * @param session
     * @param userName
     * @param service
     */
    public void deleteSyncServerForUser(final DBSession session, final String userName, final URI service) {
    	final SyncParam param = new SyncParam();
    	param.setUserName(userName);
    	param.setService(service);
    	param.setServer(true);
    	session.delete("deleteSyncServerForUser", param);
    }
    
    /**
     * Updates the synchronization data for a user
     * 
     * @param session
     * @param userName
     * @param service
     * @param credentials
     * 
     */
    public void updateSyncServerForUser(final DBSession session, final String userName, final URI service, final Class<? extends Resource> resourceType, final Properties userCredentials, final SynchronizationDirection direction) {
    	final SyncParam param = new SyncParam();
    	param.setUserName(userName);
    	param.setService(service);
    	param.setDirection(direction);
    	param.setResourceType(resourceType);
    	param.setServer(true);
    	param.setCredentials(userCredentials);
    	session.update("updateSyncServerForUser", param);
    }
    
    /**
     * Returns all available synchronization services. 
     * 
     * @param session
     * @return
     */
    public List<SyncService> getSyncServices(final DBSession session, final boolean server) {
    	return this.queryForList("getSyncServices", server, SyncService.class, session);
    }
    
    /**
     * @param userName
     * @param service
     * @param contentType
     * @param session
     * @return last synchronization date for given user, content type and service 
     */
    public Date getLastSynchronizationDate(final String userName, final URI service, Class<? extends Resource> resourceType, final DBSession session) {
    	final SyncParam param = new SyncParam();
    	param.setUserName(userName);
    	param.setService(service);
    	param.setResourceType(resourceType);
    	param.setServer(false);
    	// FIXME: why don't we set the status?
    	// FIXME: couldn't we just use 'getCurrentSyncData' instead?
    	return this.queryForObject("getLastSyncDate", param , Date.class, session);
    }
    
    /**
     * Inserts synchronization data with GIVEN status into db. 
     * @param userName
     * @param service
     * @param contentType
     * @param lastSyncDate
     * @param status
     * @param session
     */
    public void insertSynchronizationData (final String userName, final URI service, Class<? extends Resource> resourceType, final Date lastSyncDate, final SynchronizationStatus status, final DBSession session) {
    	final SyncParam param = new SyncParam();
    	param.setUserName(userName);
    	param.setService(service);
    	param.setResourceType(resourceType);
    	param.setLastSyncDate(lastSyncDate);
    	param.setStatus(status);
    	param.setServer(false);
		session.insert("insertSync", param);
    }
    
    /**
     * 
     * @param userName
     * @param service
     * @param contentType
     * @param session
     * @param status - optional. If provided, only data with that state is returned.
     * @return returns last synchronization data for given user, service and content with {@link SynchronizationStatus#RUNNING}.
     */
    public SynchronizationData getLastSynchronizationData(final String userName, final URI service, final Class<? extends Resource> resourceType, final SynchronizationStatus status, final DBSession session) {
    	final SyncParam param = new SyncParam();
    	param.setUserName(userName);
    	param.setResourceType(resourceType);
    	param.setService(service);
    	param.setStatus(status);
    	param.setServer(false);
		return queryForObject("getLastSyncData", param, SynchronizationData.class, session);
    }
    
    /**
     * 
     * @param userName
     * @param session
     * @return all synchronization server for user
     */
    public List<SyncService> getSyncServersForUser(final String userName, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		return queryForList("getSyncServersForUser", param, SyncService.class, session);
    }
    
    /**
     * Computes the synchronization plan.
     * 
     * @param serverPosts
     * @param clientPosts
     * @param lastSyncDate
     * @param conflictResolutionStrategy
     * @return
     */
    public List<SynchronizationPost> getSyncPlan(final Map<String, SynchronizationPost> serverPosts, final List<SynchronizationPost> clientPosts, final Date lastSyncDate, final ConflictResolutionStrategy conflictResolutionStrategy, final SynchronizationDirection direction) {

		// is something to synchronize?
		if (!present(serverPosts) && !present(clientPosts)) {
			return clientPosts;
		}

		for (SynchronizationPost clientPost : clientPosts) {
			SynchronizationPost serverPost = serverPosts.get(clientPost.getIntraHash());
			if (!present(lastSyncDate)) {
				log.error("lastSyncDate not present");
				return null;
			}

			/* no such post on server */
			if (!present(serverPost)) {
				
				
				/*
				 * client post is older than last synchronization -> post was
				 * deleted on server
				 */
				if (clientPost.getCreateDate().compareTo(lastSyncDate) < 0) {
					setAction(clientPost, SynchronizationActions.DELETE_CLIENT, direction);					
					continue;
				} else {
					setAction(clientPost, SynchronizationActions.CREATE_SERVER, direction);
					continue;
				}
			}
			
			if (!present(serverPost.getChangeDate())) {
				log.error("post on server has no changedate");
				//FIXME what is to do in this case?
			}
			/* changed on server since last sync */
			if (serverPost.getChangeDate().compareTo(lastSyncDate) > 0) {
				if (clientPost.getChangeDate().compareTo(lastSyncDate) > 0) {
					switch (conflictResolutionStrategy) {
					case CLIENT_WINS:
						setAction(clientPost, SynchronizationActions.UPDATE_SERVER, direction);
						break;
					case SERVER_WINS:
						setAction(clientPost, SynchronizationActions.UPDATE_CLIENT, direction);
						break;
					case ASK_USER:
						clientPost.setState(SynchronizationActions.ASK);
						break;
					case FIRST_WINS:
						if (clientPost.getChangeDate().compareTo(serverPost.getChangeDate()) < 0) {
							setAction(clientPost, SynchronizationActions.UPDATE_SERVER, direction);
						} else {
							setAction(clientPost, SynchronizationActions.UPDATE_CLIENT, direction);
						}
						break;
					case LAST_WINS:
						if (clientPost.getChangeDate().compareTo(serverPost.getChangeDate()) > 0) {
							setAction(clientPost, SynchronizationActions.UPDATE_SERVER, direction);
							
						} else {
							setAction(clientPost, SynchronizationActions.UPDATE_CLIENT, direction);
							clientPost.setState(SynchronizationActions.UPDATE_CLIENT);
						}
						break;
					default:
						clientPost.setState(SynchronizationActions.UNDEFINED);
						break;
					}

				} else {
					clientPost.setState(SynchronizationActions.UPDATE_CLIENT);
				}
			} else {
				if (clientPost.getChangeDate().compareTo(lastSyncDate) > 0) {
					setAction(clientPost, SynchronizationActions.UPDATE_SERVER, direction);
				} else {
					clientPost.setState(SynchronizationActions.OK);
				}

			}
			serverPosts.remove(serverPost.getIntraHash());

		}

		/*
		 * handle post, which do not exist on client
		 */
		for (SynchronizationPost serverPost : serverPosts.values()) {

			/*
			 * post is older than lastSyncDate
			 */
			if (serverPost.getCreateDate().compareTo(lastSyncDate) < 0) {
				setAction(serverPost, SynchronizationActions.DELETE_SERVER, direction);
			} else {
				setAction(serverPost, SynchronizationActions.CREATE_CLIENT, direction);
			}
			clientPosts.add(serverPost);
		}
		
		/*
		 * FIXME posts with OK-state will be not required.
		 */
		return clientPosts;
	}
    
    /**
     * 
     * @param post
     * @param action
     * @param direction
     */
    private void setAction(SynchronizationPost post, SynchronizationActions action, SynchronizationDirection direction) {
    	boolean server = true;
    	if(direction.toString().endsWith("_CLIENT")){
    		server = false;
    	}
    	if(server && direction != SynchronizationDirection.SERVER_TO_CLIENT) {
    		post.setState(action);
    		return;
    	}
    	if(!server && direction != SynchronizationDirection.CLIENT_TO_SERVER) {
    		post.setState(action);
    		return;
    	}
    	post.setState(SynchronizationActions.OK);
    }
}
