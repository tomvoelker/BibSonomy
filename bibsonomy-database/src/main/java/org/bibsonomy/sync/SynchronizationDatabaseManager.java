package org.bibsonomy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;
import java.util.Iterator;
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
import org.bibsonomy.model.sync.SynchronizationAction;
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
	 * @param service - the URI of the service to be added
	 * @param server - <code>true</code> if the service may act as a server, <code>false</code> if it may act as a client
	 * @param sslDn 
	 * @param secureAPI 
	 * @param session
	 */
	public void createSyncService(final URI service, final boolean server, final String sslDn, final URI secureAPI, final DBSession session) {
		session.beginTransaction();
		try {
			final SyncParam param = new SyncParam();
			param.setService(service);
			param.setServer(server);
			param.setSslDn(sslDn);
			param.setSecureAPI(secureAPI);
			param.setServiceId(generalDb.getNewId(ConstantID.IDS_SYNC_SERVICE, session));
			session.insert("insertSyncService", param);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Remove a sync service.
	 * @param service - the URI of the service to be removed
	 * @param server - <code>true</code> if the server part should be deleted, <code>false</code> if the client client part should be deleted
	 * @param session
	 */
	public void deleteSyncService(final URI service, final boolean server, final DBSession session) {
		final SyncParam param =  new SyncParam();
		param.setService(service);
		param.setServer(server);
		session.delete("deleteSyncService", param);
	}

	/**
	 * Update the synchronization status in the database.
	 * @param userName - identifies 
	 * @param service - identifies the status
	 * @param resourceType - identifies the status
	 * @param syncDate - identifies the status
	 * @param status - the new status
	 * @param info - some additional information to be stored
	 * @param session - the database session
	 */
	public void updateSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date syncDate, final SynchronizationStatus status, final String info, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		param.setService(service);
		param.setResourceType(resourceType);
		param.setLastSyncDate(syncDate);
		param.setStatus(status); // this is changed
		param.setInfo(info); // and this is changed
		param.setServer(false);
		session.update("updateSyncStatus", param);
	}

	/**
	 * Delete the given synchronization data's status in the database. If syncDate is null, delete all sync data, which matches other parameters
	 * @param userName 
	 * @param service 
	 * @param resourceType 
	 * @param syncDate 
	 * @param session - the database session
	 */
	public void deleteSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date syncDate, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		param.setService(service);
		param.setResourceType(resourceType);
		param.setLastSyncDate(syncDate);
		param.setServer(false);
		session.update("deleteSyncStatus", param);
	}
	
	/**
	 * Insert new synchronization data for user.
	 * @param userName
	 * @param service 
	 * @param resourceType 
	 * @param userCredentials 
	 * @param direction 
	 * @param strategy 
	 * @param session
	 */
	public void createSyncServerForUser(final String userName, final URI service, final Class<? extends Resource> resourceType, final Properties userCredentials, final SynchronizationDirection direction, final ConflictResolutionStrategy strategy, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		param.setCredentials(userCredentials);
		param.setDirection(direction);
		param.setStrategy(strategy);
		param.setResourceType(resourceType);
		param.setService(service);
		param.setServer(true);
		session.insert("insertSyncServiceForUser", param);
	}

	/**
	 * Removes synchronization data for user.
	 * @param userName
	 * @param service
	 * @param session
	 */
	public void deleteSyncServerForUser(final String userName, final URI service, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		param.setService(service);
		param.setServer(true);
		session.delete("deleteSyncServerForUser", param);
	}

	/**
	 * Updates the synchronization data for a user
	 * @param userName
	 * @param service
	 * @param resourceType 
	 * @param userCredentials 
	 * @param direction 
	 * @param strategy 
	 * @param session
	 * 
	 */
	public void updateSyncServerForUser(final String userName, final URI service, final Class<? extends Resource> resourceType, final Properties userCredentials, final SynchronizationDirection direction, final ConflictResolutionStrategy strategy, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		param.setService(service);
		param.setDirection(direction);
		param.setResourceType(resourceType);
		param.setServer(true);
		param.setCredentials(userCredentials);
		param.setStrategy(strategy);
		session.update("updateSyncServerForUser", param);
	}

	/**
	 * 
	 * @param server 
	 * @param session
	 * @return all available synchronization services. if server <true> sync server
	 * otherwise sync clients
	 */
	public List<URI> getSyncServices(final boolean server, final DBSession session) {
		return this.queryForList("getSyncServices", server, URI.class, session);
	}
	
	/**
	 * 
	 * @param server
	 * @param session
	 * @return
	 */
	public List<SyncService> getAllSyncServices(final boolean server, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setServer(server);
		return this.queryForList("getAllSyncServices", param, SyncService.class, session);
	}

	/**
	 * Inserts synchronization data with GIVEN status into db. 
	 * @param userName
	 * @param service
	 * @param resourceType
	 * @param lastSyncDate
	 * @param status
	 * @param session
	 */
	public void insertSynchronizationData(final String userName, final URI service, final Class<? extends Resource> resourceType, final Date lastSyncDate, final SynchronizationStatus status, final DBSession session) {
		final SyncParam param = new SyncParam();
		log.debug("user name: " + userName + 
				", service: " + service.toString() + 
				", resource type: " + resourceType.getSimpleName() + 
				", date: " + lastSyncDate +
				", status: " + status);
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
	 * @param resourceType
	 * @param status - optional. If provided, only data with that state is returned.
	 * @param session
	 * @return returns last synchronization data for given user, service and content with {@link SynchronizationStatus#RUNNING}.
	 */
	public SynchronizationData getLastSyncData(final String userName, final URI service, final Class<? extends Resource> resourceType, final SynchronizationStatus status, final DBSession session) {
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
	 * @param userName - the user's name
	 * @param service - select a particular service
	 * @param server - set to <code>true</code>, if queried for a server, otherwise client.
	 * @param session
	 * @return all synchronization server for user
	 */
	public List<SyncService> getSyncServersForUser(final String userName, final URI service, final boolean server, final DBSession session) {
		final SyncParam param = new SyncParam();
		param.setUserName(userName);
		param.setServer(server);
		param.setService(service);
		return queryForList("getSyncServersForUser", param, SyncService.class, session);
	}

	/**
	 * Computes the synchronization plan.
	 * 
	 * @param serverPosts - Note: this map is modified by this method - posts are removed.
	 * @param clientPosts - Note: this list is modified by this method - posts are added. It's the same list that is returned by this method.
	 * @param lastSyncDate
	 * @param conflictResolutionStrategy
	 * @param direction 
	 * @return The clientPosts with {@link SynchronizationAction}'s and posts from the server added.
	 */
	public List<SynchronizationPost> getSyncPlan(final Map<String, SynchronizationPost> serverPosts, final List<SynchronizationPost> clientPosts, final Date lastSyncDate, final ConflictResolutionStrategy conflictResolutionStrategy, final SynchronizationDirection direction) {

		// is there something to synchronize? (we can't use present() on this place, because it's possible to have empty list or map)
		if (serverPosts == null && clientPosts == null) {
			throw new IllegalArgumentException("client posts and server posts can't be null!");
		}

		if (!present(lastSyncDate)) {
			throw new IllegalArgumentException("lastSyncDate not present");
		}

		/*
		 * check all client posts
		 */
		final Iterator<SynchronizationPost> iterator = clientPosts.iterator();
		
		while (iterator.hasNext()) {
			final SynchronizationPost clientPost = iterator.next();
			final SynchronizationPost serverPost = serverPosts.get(clientPost.getIntraHash());

			if (!present(serverPost)) {
				/*  
				 * no such post on server 
				 */
				if (clientPost.getCreateDate().before(lastSyncDate)) {
				    /*
				     * post was created before last sync, but when was it changed?
				     */
					if (clientPost.getChangeDate().before(lastSyncDate)) {
						/*
						 * client post was created and last changed before last synchronization 
						 * -> post was deleted on server
						 */
						if (!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction))
							clientPost.setAction(SynchronizationAction.DELETE_CLIENT);
						else
							clientPost.setAction(SynchronizationAction.OK);
					} else {
						/*
						 * CONFLICT! (we can't solve, currently :-(
						 * 
						 * Post was changed after last sync but does not exist on server
						 * --> either it was deleted on server, or it's hash has changed
						 * Since it is neither simple to find out if the post has been deleted
						 * or its hash has changed, we create the post on the server.   
						 * FIXME: This can result in 
						 * a) a duplicate post (if the hash has changed on the client but the
						 * post still exists on the server), or 
						 * b) an unwanted post (if the post has been deleted on the server, but
						 * according to the strategy this deletion should be carried out on
						 * the client, too).
						 */
						if (!SynchronizationDirection.SERVER_TO_CLIENT.equals(direction)) {
							clientPost.setAction(SynchronizationAction.CREATE_SERVER);
						} else { 
							clientPost.setAction(SynchronizationAction.OK);
						}
					}
				} else {
					/*
					 * post was created on client after last sync
					 */
					if (!SynchronizationDirection.SERVER_TO_CLIENT.equals(direction)) {
						clientPost.setAction(SynchronizationAction.CREATE_SERVER);
					} else { 
						clientPost.setAction(SynchronizationAction.OK);
					}
				}
				continue;
			}

			if (!present(serverPost.getChangeDate())) {
				log.error("post " + serverPost.getIntraHash() + " on server has no changedate");
				// FIXME what can we do in this case?
			}

			if (serverPost.getChangeDate().after(lastSyncDate)) {
				/*  
				 * changed on server since last sync 
				 */
				if (clientPost.getChangeDate().after(lastSyncDate)) {
					
					if (clientPost.getChangeDate().equals(serverPost.getChangeDate())) {
						/*
						 * both have the same change date -> do nothing
						 */
						clientPost.setAction(SynchronizationAction.OK);
					} else {
						/*
						 * changed on client, too -> conflict!
						 */
						resolveConflict(clientPost, serverPost, conflictResolutionStrategy, direction);
					}
				} else {
					/*
					 * must be updated on client
					 */
					if (!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction)) {
						clientPost.setAction(SynchronizationAction.UPDATE_CLIENT);
					} else {
						clientPost.setAction(SynchronizationAction.OK);
					}
				}
			} else {
				/*
				 * post is in sync on the server
				 */
				if (clientPost.getChangeDate().after(lastSyncDate) && !SynchronizationDirection.SERVER_TO_CLIENT.equals(direction)) {
					/*
					 * ... but not on the client -> update
					 */
					clientPost.setAction(SynchronizationAction.UPDATE_SERVER);
				} else {
					clientPost.setAction(SynchronizationAction.OK);
				}

			}
			/*
			 * to reduce data and loop count on the client side we remove
			 * the client post is the action is ok
			 */
			if (SynchronizationAction.OK.equals(clientPost.getAction())) {
				iterator.remove();
			}
			
			/*
			 * In the next loop we go over all *remaining* server posts and
			 * compare them. To not handle this post twice, we remove it from
			 * the server posts list.
			 */
			serverPosts.remove(clientPost.getIntraHash());
		}
		
		/*
		 * handle the remaining posts that do not exist on the client
		 */
		for (final SynchronizationPost serverPost : serverPosts.values()) {
			if (serverPost.getCreateDate().before(lastSyncDate)) {
				/*
				 * post is older than lastSyncDate but does not exist on client
				 */
				if (serverPost.getChangeDate().before(lastSyncDate)) {
					/*
					 * post was deleted on client and must now be deleted on server
					 */
					if (!SynchronizationDirection.SERVER_TO_CLIENT.equals(direction)) {
						serverPost.setAction(SynchronizationAction.DELETE_SERVER);
					} else { 
						serverPost.setAction(SynchronizationAction.OK);
					}
				} else {
					/*
					 * CONFLICT (see above! FIXME: currently, we can't resolve this)
					 * 
					 * we create the post on the client
					 */
					if (!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction)) {
						serverPost.setAction(SynchronizationAction.CREATE_CLIENT);
					} else { 
						serverPost.setAction(SynchronizationAction.OK);
					}
				}
			} else {
				/*
				 * post was created after last sync -> create on client
				 */
				if (!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction)) {
					serverPost.setAction(SynchronizationAction.CREATE_CLIENT);
				} else { 
					serverPost.setAction(SynchronizationAction.OK);
				}
			}
			
			/*
			 * add post to list of client posts if not OK
			 */
			if (!SynchronizationAction.OK.equals(serverPost.getAction())) {
				clientPosts.add(serverPost);
			}
		}
		
		return clientPosts;
	}
	

	/**
	 * When a post was changed on both the server and the client /after/ 
	 * synchronization, this method resolved the corresponding conflict.
	 * 
	 * @param clientPost
	 * @param serverPost
	 * @param conflictResolutionStrategy
	 * @param direction
	 */
	private void resolveConflict(final SynchronizationPost clientPost, final SynchronizationPost serverPost, final ConflictResolutionStrategy conflictResolutionStrategy, final SynchronizationDirection direction) {
		switch (conflictResolutionStrategy) {
		case CLIENT_WINS:
			if (!SynchronizationDirection.SERVER_TO_CLIENT.equals(direction)) {
				clientPost.setAction(SynchronizationAction.UPDATE_SERVER);
			} else {
				clientPost.setAction(SynchronizationAction.OK);
			}
			break;
		case SERVER_WINS:
			if (!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction)) {
				clientPost.setAction(SynchronizationAction.UPDATE_CLIENT);
			} else {
				clientPost.setAction(SynchronizationAction.OK);
			}
			break;
			/*
			 * TODO: document why this was disabled!
			 * temporary disabled
			 */
//		case ASK_USER:
//			clientPost.setAction(SynchronizationAction.ASK);
//			break;
		case FIRST_WINS:
			if (clientPost.getChangeDate().before(serverPost.getChangeDate())) {
				if(!SynchronizationDirection.SERVER_TO_CLIENT.equals(direction))
					clientPost.setAction(SynchronizationAction.UPDATE_SERVER);
				else 
					clientPost.setAction(SynchronizationAction.OK);
			} else {
				if(!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction))
					clientPost.setAction(SynchronizationAction.UPDATE_CLIENT);
				else
					clientPost.setAction(SynchronizationAction.OK);
			}
			break;
		case LAST_WINS:
			if (clientPost.getChangeDate().after(serverPost.getChangeDate())) {
				if (!SynchronizationDirection.SERVER_TO_CLIENT.equals(direction)) {
					clientPost.setAction(SynchronizationAction.UPDATE_SERVER);
				} else { 
					clientPost.setAction(SynchronizationAction.OK);
				}
			} else {
				if (!SynchronizationDirection.CLIENT_TO_SERVER.equals(direction)) {
					clientPost.setAction(SynchronizationAction.UPDATE_CLIENT);
				} else {
					clientPost.setAction(SynchronizationAction.OK);
				}
			}
			break;
		default:
			clientPost.setAction(SynchronizationAction.UNDEFINED);
			break;
		}
	}

}
