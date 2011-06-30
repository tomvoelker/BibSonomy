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
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStates;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationDatabaseManager extends AbstractDatabaseManager {
    private static final SynchronizationDatabaseManager singelton = new SynchronizationDatabaseManager();
    
    private static final Log log = LogFactory.getLog(SynchronizationDatabaseManager.class);
    
    /**
     * Singleton 
     * @return SynchronizationDatabaseManager
     */
    public static SynchronizationDatabaseManager getInstance() {
    	return singelton;
    }
    
    private SynchronizationDatabaseManager() {
    }
    
    /**
     * Updates given synchronization data in db. This method will be used from SynchronizationServer!
     * @param session database session
     * @param data SynchronizationData
     */
    public void updateSyncData(final DBSession session, final SynchronizationData data) {
    	int serviceId = this.getServiceId(session, data.getService());
		SyncParam param = new SyncParam(data.getUserName(), data.getService(),serviceId, data.getResourceType(), data.getLastSyncDate(), data.getStatus(), null);
		session.update("updateSyncStatus", param);
    }

    /**
     * Insert new synchronization SERVER into db.
     * @param session
     * @param userName
     * @param serviceId
     * @param credentials
     */
    public void createSyncServerForUser(final DBSession session, final String userName, final URI service, final Properties credentials) {
    	int serviceId = this.getServiceId(session, service);
    	session.insert("insertSyncServiceForUser", new SyncParam(userName, service, serviceId, null, null, null, credentials));
    }
    
    /**
     * Removes a synchronization Server from db
     * @param session
     * @param userName
     * @param service
     */
    public void deleteSyncServerForUser(final DBSession session, final String userName, final URI service) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param = new SyncParam(userName, service, serviceId, null, null, null, null);
    	session.delete("deleteSyncServer", param);
    }
    
    /**
     * Updates a synchronization Server
     * @param session
     * @param userName
     * @param service
     * @param credentials
     */
    public void updateSyncServerForUser(final DBSession session, final String userName, final URI service, final Properties credentials) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param = new SyncParam(userName, service, serviceId, null, null, null, credentials);
    	session.update("updateSyncServer", param);
    }
    
    /**
     * 
     * @param session
     * @return
     */
    public List<SyncService> getAvlSyncServer(final DBSession session) {
    	return this.queryForList("getAvlSyncServer", null, SyncService.class, session);
    }
    
    /**
     * @param userName
     * @param service
     * @param contentType
     * @param session
     * @return last synchronization date for given user, content type and service 
     */
    public Date getLastSynchronizationDate(final String userName, final URI service, Class<? extends Resource> resourceType, final DBSession session) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param =  new SyncParam(userName, service, serviceId, resourceType, null, null, null);
    	return this.queryForObject("getLastSyncDateForUserForServiceForContent", param , Date.class, session);
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
    public void insertSyncronizationData (final String userName, final URI service, Class<? extends Resource> resourceType, final Date lastSyncDate, final String status, final DBSession session) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param = new SyncParam(userName, service, serviceId, resourceType, lastSyncDate, status, null);
		session.insert("insertSync", param);
    }
    
    /**
     * 
     * @param userName
     * @param service
     * @param contentType
     * @param session
     * @return returns last synchronization data for given user, service and content with status="undone"
     */
    public SynchronizationData getCurrentSynchronizationData (final String userName, final URI service, final Class<? extends Resource> resourceType, final DBSession session) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param = new SyncParam(userName, service, serviceId, resourceType, null, null, null);
		return queryForObject("getCurrentSyncData", param, SynchronizationData.class, session);
    }
    
    /**
     * 
     * @param userName
     * @param service
     * @param contentType
     * @param session
     * @return list of synchronizationData for given user, service and contentType
     */
    public List<SynchronizationData> getSynchronizationData (final String userName, final URI service, Class<? extends Resource> resourceType, final DBSession session) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param = new SyncParam(userName, service, serviceId, resourceType, null, null, null);
		return this.queryForList("getSyncData", param, SynchronizationData.class, session);
    }
    
    /**
     * 
     * @param userName
     * @param session
     * @return all synchronization server for user
     */
    public List<SyncService> getSyncServerForUser(final String userName, final DBSession session) {
		SyncParam param = new SyncParam(userName, null, 0, null, null, null, null);
		return queryForList("getSyncServerForUser", param, SyncService.class, session);
    }
    
    /**
     * 
     * @param userName
     * @param service
     * @param session
     * @return sync service for user for given URI
     */
    public SyncService getSyncServer(final String userName, final URI service, final DBSession session) {
    	int serviceId = this.getServiceId(session, service);
    	SyncParam param = new SyncParam(userName, service, serviceId, null, null, null, null);
    	return queryForObject("getSyncServerForUserByUri", param, SyncService.class, session);
    }
    
    /**
     * 
     * @param session
     * @param service
     * @return intern id of service
     */
    private int getServiceId(final DBSession session, final URI service) {
    	Integer test = queryForObject("getSyncServiceByURI", new SyncParam(null, service, 0, null, null, null, null), Integer.class, session);
    	return test.intValue();
    }
    
    public List<SynchronizationPost> synchronize(Map<String, SynchronizationPost> serverPosts, List<SynchronizationPost> clientPosts, Date lastSyncDate, ConflictResolutionStrategy conflictStrategy) {

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
				 * clientpost is older than last synchronization -> post was
				 * deleted on server
				 */
				if (clientPost.getCreateDate().compareTo(lastSyncDate) < 0) {
					clientPost.setState(SynchronizationStates.DELETE_CLIENT);
					continue;
				} else {
					clientPost.setState(SynchronizationStates.CREATE);
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
					switch (conflictStrategy) {
					case CLIENT_WINS:
						clientPost.setState(SynchronizationStates.UPDATE);
						break;
					case SERVER_WINS:
						clientPost.setState(SynchronizationStates.UPDATE_CLIENT);
						break;
					case ASK_USER:
						clientPost.setState(SynchronizationStates.ASK);
						break;
					case FIRST_WINS:
						if (clientPost.getChangeDate().compareTo(serverPost.getChangeDate()) < 0) {
							clientPost.setState(SynchronizationStates.UPDATE);
						} else {
							clientPost.setState(SynchronizationStates.UPDATE_CLIENT);
						}
						break;
					case LAST_WINS:
						if (clientPost.getChangeDate().compareTo(serverPost.getChangeDate()) > 0) {
							clientPost.setState(SynchronizationStates.UPDATE);
						} else {
							clientPost.setState(SynchronizationStates.UPDATE_CLIENT);
						}
						break;
					default:
						clientPost.setState(SynchronizationStates.UNDEFINED);
						break;
					}

				} else {
					clientPost.setState(SynchronizationStates.UPDATE_CLIENT);
				}
			} else {
				if (clientPost.getChangeDate().compareTo(lastSyncDate) > 0) {
					clientPost.setState(SynchronizationStates.UPDATE);
				} else {
					clientPost.setState(SynchronizationStates.OK);
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
				serverPost.setState(SynchronizationStates.DELETE);
			} else {
				serverPost.setState(SynchronizationStates.CREATE_CLIENT);
			}
			clientPosts.add(serverPost);
		}


		return clientPosts;
	}
}
