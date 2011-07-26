package org.bibsonomy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.util.IbatisSyncDBSessionFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * This client synchronizes PUMA with BibSonomy.
 * PUMA is the server, BibSonomy is the client.
 * 
 * 
 * @author wla
 * @version $Id$
 */
public class SynchronizationClient {
	private static final Log log = LogFactory.getLog(SynchronizationClient.class);
	
	/*
	 * own URI
	 */
	private URI ownUri;
	
	/*
	 * FIXME make ConflictResolutionStrategy configurable by user 
	 */
	private final ConflictResolutionStrategy strategy = ConflictResolutionStrategy.LAST_WINS;
	/*
	 * FIXME: must be a different one for different servers 
	 */
	private final DBLogicUserInterfaceFactory serverLogicFactory;

	
	public SynchronizationClient() {
		this(new IbatisSyncDBSessionFactory());
	}
	
	public SynchronizationClient(final DBSessionFactory dbSessionFactory) {
		this.serverLogicFactory = new DBLogicApiInterfaceFactory();
		this.serverLogicFactory.setDbSessionFactory(dbSessionFactory);	
	}
	
	


	/**
	 * Looks up the credentials for the given syncServer. If no credentials
	 * could be found, <code>null</code> is returned.
	 *  
	 * @param clientLogic
	 * @param syncServer
	 * @return syncService
	 */
	private SyncService getServerByURI(final LogicInterface clientLogic, final URI syncServer) {
		final List<SyncService> syncServers = ((SyncLogicInterface)clientLogic).getSyncServer(clientLogic.getAuthenticatedUser().getName());
		
		for (final SyncService syncService : syncServers) {
			if (syncServer.equals(syncService.getService())) {
				return syncService;
			}
		}
		return null;
	}

	/**
	 * Creates an instance of the LogicInterface for the given syncService
	 * 
	 * @param syncService
	 * @return
	 */
	private LogicInterface getServerLogic(final SyncService syncService) {
		final Properties serverUser = syncService.getServerUser();
		return serverLogicFactory.getLogicAccess(serverUser.getProperty("userName"), serverUser.getProperty("apiKey"));
	}
	

	/**
	 * Used in a synchronization process, in case that server logic already created 
	 * @param userName
	 * @param contentType
	 * @param serverLogic
	 * @return
	 */
	public SynchronizationData getLastSyncData(final String userName, Class<? extends Resource> resourceType, final LogicInterface serverLogic) {
		/*
		 * FIXME: errorhandling
		 */
		return ((SyncLogicInterface) serverLogic).getLastSyncData(userName, ownUri, resourceType);
	}
	
	/**
	 * Used in SettingsPageController, to show syncData, gets own logic
	 * @param syncService
	 * @param contentType
	 * @return
	 */
	public SynchronizationData getLastSyncData(final SyncService syncService, final Class<? extends Resource> resourceType) {
		/*
		 * FIXME errorhandling
		 */
		
		final LogicInterface serverLogic = getServerLogic(syncService);
		
		return getLastSyncData(serverLogic.getAuthenticatedUser().getName(), resourceType, serverLogic);
	}
	

	/**
	 * Synchronized the user's posts between the clientLogic and the syncServer.  
	 * 
	 * @param clientLogic
	 * @param syncServerUri
	 * @param resourceType
	 * @return
	 */
	public SynchronizationData synchronize(final LogicInterface clientLogic, final URI syncServerUri) {
		
		final SyncService syncServer = getServerByURI(clientLogic, syncServerUri);
		final Class<? extends Resource> resourceType = syncServer.getResourceType();

		/*
		 * retrieve instance of server logic
		 */
		final LogicInterface serverLogic = getServerLogic(syncServer);
		
		if (!present(serverLogic)) {
			throw new IllegalArgumentException("Synchronization for " + syncServerUri + " not configured for user " + clientLogic.getAuthenticatedUser());
		}
		final String serverUserName = serverLogic.getAuthenticatedUser().getName();
		
		SynchronizationStatus result;
		String info;
		try {
			/*
			 * try to synchronize
			 */
			info = synchronize(clientLogic, serverLogic, resourceType, syncServer.getDirection());
			result = SynchronizationStatus.DONE;
		} catch (final SynchronizationRunningException e) {
			/*
			 * FIXME handling of this exception type. I think we can break "running" synchronization after timeout.
			 * Currently return only "running" status.
			 */
			final SynchronizationData data = new SynchronizationData();
			data.setStatus(SynchronizationStatus.RUNNING);
			return data;
		} catch (final Exception e) {
			info = "";
			result = SynchronizationStatus.ERROR;
			log.error("Error in synchronization", e);
		}
		/*
		 * store sync result
		 */
		storeSyncResult(result, info, resourceType, serverLogic, serverUserName);
		
		// Get synchronization data from server. Can't construct here, because last_sync_date only known by server
		return getLastSyncData(serverUserName, resourceType, serverLogic);
	}
	
	/**
	 * Stores result of synchronization on server
	 * @param result
	 * @param resourceType
	 * @param serverLogic
	 * @param serverUserName
	 */
	private void storeSyncResult(final SynchronizationStatus status, final String info, final Class<? extends Resource> resourceType, final LogicInterface serverLogic, final String serverUserName) {
		final SyncLogicInterface syncLogicInterface = (SyncLogicInterface) serverLogic;
		final SynchronizationData data = syncLogicInterface.getLastSyncData(serverUserName, ownUri, resourceType);
		if (!present(data)) {
			/*
			 * sync data seems not to have been stored --> error!
			 */
			throw new RuntimeException("No sync data found for " + serverUserName + " on " + ownUri);
		}
		if (SynchronizationStatus.RUNNING.equals(data.getStatus())) {
			syncLogicInterface.updateSyncStatus(data, status, info);
		} else {
			throw new RuntimeException("no running synchronization found for " + serverUserName + " on " + ownUri + " to store result");
		}
	}

	/**
	 * Synchronizes clientLogic with serverLogic.
	 * 
	 * @param clientLogic
	 * @param serverLogic
	 * @param resourceType
	 * @return synchronization result
	 */
	private String synchronize(final LogicInterface clientLogic, final LogicInterface serverLogic, final Class<? extends Resource> resourceType, SynchronizationDirection direction) {
		/*
		 * add sync access to both users = allow users to modify the dates of
		 * posts
		 * FIXME: must be secured using crypto
		 */
		final User serverUser = serverLogic.getAuthenticatedUser();
		serverUser.setRole(Role.SYNC);
		
		final User clientUser = clientLogic.getAuthenticatedUser();
		clientUser.setRole(Role.SYNC);
	
		/*
		 * get posts from client
		 */
		final List<SynchronizationPost> clientPosts = ((SyncLogicInterface)clientLogic).getSyncPosts(clientUser.getName(), resourceType);
		
		/*
		 * get synchronization actions and posts from server
		 */
		final List<SynchronizationPost> syncPlan = ((SyncLogicInterface)serverLogic).getSyncPlan(serverUser.getName(), resourceType, clientPosts, strategy, ownUri, direction);

		/*
		 * create target lists
		 */
		final List<Post<? extends Resource>> createOnClient = new ArrayList<Post<?>>();
		final List<Post<? extends Resource>> createOnServer = new ArrayList<Post<?>>();
		final List<Post<? extends Resource>> updateOnClient = new ArrayList<Post<?>>();
		final List<Post<? extends Resource>> updateOnServer = new ArrayList<Post<?>>();
		final List<String> deleteOnServer = new ArrayList<String>();
		final List<String> deleteOnClient = new ArrayList<String>();

		/*
		 * iterate over all posts and put each post into the target list
		 */
		for (final SynchronizationPost post: syncPlan) {
			final String postIntraHash = post.getIntraHash();
			
			final Post<? extends Resource> postToHandle;
			switch (post.getState()) {
			case CREATE_SERVER:
				postToHandle = clientLogic.getPostDetails(postIntraHash, clientUser.getName());
				postToHandle.setUser(serverUser);
				createOnServer.add(postToHandle);
				break;
			case CREATE_CLIENT:
				postToHandle = post.getPost();
				postToHandle.setUser(clientUser);
				createOnClient.add(postToHandle);
				break;
			case DELETE_SERVER:
				deleteOnServer.add(postIntraHash);
				break;
			case DELETE_CLIENT:
				deleteOnClient.add(postIntraHash);
				break;
			case UPDATE_SERVER:
				postToHandle = clientLogic.getPostDetails(postIntraHash, clientUser.getName());
				postToHandle.setUser(serverUser);
				updateOnServer.add(postToHandle);
				break;
			case UPDATE_CLIENT:
				postToHandle = post.getPost();
				postToHandle.setUser(clientUser);
				updateOnClient.add(postToHandle);
				break;
			default:
				break;
			}
		}

		boolean duplicates = false;
		
		/*
		 *  Apply changes to both systems.
		 */
		final StringBuilder result = new StringBuilder();
		
		/*
		 * create posts on client 
		 */
		if (!createOnClient.isEmpty()) {
			try {
				clientLogic.createPosts(createOnClient);
				result.append("created on client: " + createOnClient.size() + ", ");
			} catch (final DatabaseException e) {
				/*
				 *  this can happen if some duplicate posts exists
				 *  FIXME: check other possibilities to throw Database Exception
				 *  
				 *  
				 *  FIXME: check for duplicate error messages
				 *  
				 */
				duplicates = true;
			}
			
		}

		/*
		 * create posts on server
		 */
		if (!createOnServer.isEmpty()) {
			try {
				serverLogic.createPosts(createOnServer);
				result.append("created on server: " + createOnServer.size() + ", ");
			} catch (DatabaseException e) {
				/*
				 *  this can happen if some duplicate posts exists
				 *  FIXME: check oder possibilities to throw Database Exception
				 */
				log.error("database exception catched during creation on server", e);
				duplicates = true;
			}
		}

		/*
		 * update posts on client 
		 */
		if (!updateOnClient.isEmpty()) {
			clientLogic.updatePosts(updateOnClient, PostUpdateOperation.UPDATE_ALL);
			result.append("updated on client: " + updateOnClient.size() + ", ");
		}

		/*
		 * update posts on server
		 */
		if (!updateOnServer.isEmpty()) {
			serverLogic.updatePosts(updateOnServer, PostUpdateOperation.UPDATE_ALL);
			result.append("updated on server: " + updateOnServer.size() + ", ");
		}

		/*
		 * delete posts on client
		 */
		if (!deleteOnClient.isEmpty()) {
			clientLogic.deletePosts(clientUser.getName(), deleteOnClient);
			result.append("deleted on client: " + deleteOnClient.size() + ", ");
		}

		/*
		 * delete posts no server
		 */
		if (!deleteOnServer.isEmpty()) {
			serverLogic.deletePosts(serverUser.getName(), deleteOnServer);
			result.append("deleted on server: " + deleteOnServer.size());
		}

		/*
		 * generate result string
		 */
		if (duplicates) {
			result.insert(0, "duplicates detected, ");
		}
		
	
		int length = result.length();
		if (length == 0) {
			result.append("no changes");
		} else if (result.lastIndexOf(", ") == length - 2) {
			result.delete(length - 2, length);
		}

		return result.toString();
	}
	
	/**
	 * @param ownUri the ownUri to set
	 */
	public void setOwnUri(final URI ownUri) {
		this.ownUri = ownUri;
	}

	/**
	 * @return the ownUri
	 */
	public URI getOwnUri() {
		return ownUri;
	}
}
