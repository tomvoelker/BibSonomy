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
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;

/**
 * This client is only for BibSonomy
 * 
 * @author wla
 * @version $Id$
 */
public class SynchronizationClient {
	private static final Log log = LogFactory.getLog(SynchronizationClient.class);
	
	/*
	 * own URI as String and as java.net.URI
	 */
	private URI uri;
	
	/*
	 *	FIXME make ConflictResolutionStrategy configurable by user 
	 */
	private final ConflictResolutionStrategy strategy;
	private DBLogicApiInterfaceFactory serverLogicFactory;
	private DBSessionFactory dbSessionFactory = new IbatisSyncDBSessionFactory();
	
	public SynchronizationClient() {
		//FIXME get strategy form DB or elsewhere
		this.strategy = ConflictResolutionStrategy.LAST_WINS;
	}
	
	/**
	 * Looks up the credentials for the given syncServer and creates an 
	 * instance of the a LogicInterface on the syncServer. If no credentials
	 * could be found, <code>null</code> is returned.
	 *  
	 * 
	 * @param userName
	 * @param apiKey
	 * @return Logic with access to database on server-service   
	 */
	private LogicInterface getServerLogic(final LogicInterface clientLogic, final URI syncServer) {
		final List<SyncService> syncServers = ((SyncLogicInterface)clientLogic).getSyncServer(clientLogic.getAuthenticatedUser().getName());
		
		for (final SyncService syncService : syncServers) {
			if (syncServer.equals(syncService.getService())) {
				return getServerLogic(syncService);
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
		/*
		 * FIXME: get correct DBSessionFactory for each service
		 */
		serverLogicFactory.setDbSessionFactory(this.dbSessionFactory);
		
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
		return ((SyncLogicInterface) serverLogic).getLastSyncData(userName, uri, resourceType);
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
	 * @param syncServer
	 * @param resourceType
	 * @return
	 */
	public SynchronizationData synchronize(final LogicInterface clientLogic, final URI syncServer, final Class<? extends Resource> resourceType) {
		/*
		 * retrieve instance of server logic
		 */
		final LogicInterface serverLogic = getServerLogic(clientLogic, syncServer);
		if (!present(serverLogic)) {
			throw new IllegalArgumentException("Synchronization for " + syncServer + " not configured for user " + clientLogic.getAuthenticatedUser());
		}
		final String serverUserName = serverLogic.getAuthenticatedUser().getName();
		
		/*
		 * set default result to "error"
		 */
		SynchronizationStatus result;
		String info;
		try {
			// try to synchronize resource
			info = synchronize(clientLogic, serverLogic, resourceType);
			result = SynchronizationStatus.DONE;
		} catch (final SynchronizationRunningException e) {
			/*
			 * FIXME handling of this exception type. I think we can break "running" synchronization after timeout.
			 * Currently return only "running" status.
			 */
			final SynchronizationData data = new SynchronizationData();
			data.setStatus(SynchronizationStatus.RUNNING); // FIXME: we had "running" here, in contrast to "done" elsewhere
			return data;
		} catch (final Exception e) {
			info = "";
			result = SynchronizationStatus.ERROR;
			log.error("Error in synchronization", e);
		}
		// after successful synchronization, store sync result.
		storeSyncResult(result, info, resourceType, serverLogic, serverUserName);
		
		//Get synchronization data from server. Can't construct here, because last_sync_date only known by server
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
		final SynchronizationData data = ((SyncLogicInterface) serverLogic).getLastSyncData(serverUserName, uri, resourceType);
		if (!present(data)) {
			// started more than one sync process per second -> do nothing
			return;
		}
		if (SynchronizationStatus.RUNNING.equals(data.getStatus())) {
			((SyncLogicInterface) serverLogic).updateSyncStatus(data, status, info);
		} else {
			log.error("Error no running synchronization dound, to store result");
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
	private String synchronize(final LogicInterface clientLogic, final LogicInterface serverLogic, final Class<? extends Resource> resourceType) {
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
		 * get posts from client system
		 */
		List<SynchronizationPost> clientPosts = ((SyncLogicInterface)clientLogic).getSyncPosts(clientUser.getName(), resourceType);
		
		/*
		 * get synchronization states and posts from server
		 */
		clientPosts = ((SyncLogicInterface)serverLogic).getSyncPlan(serverUser.getName(), resourceType, clientPosts, strategy, uri);

		/*
		 * create target lists
		 */
		List<Post<?>> createOnClientList = new ArrayList<Post<?>>();
		List<Post<?>> createOnServerList = new ArrayList<Post<?>>();
		List<String> deleteOnServerList = new ArrayList<String>();
		List<String> deleteOnClientList = new ArrayList<String>();
		List<Post<?>> updateOnClientList = new ArrayList<Post<?>>();
		List<Post<?>> updateOnServerList = new ArrayList<Post<?>>();

		/*
		 * iterate over all posts and put each post to the target list
		 */
		for (SynchronizationPost post : clientPosts) {
			Post<? extends Resource> postToHandle;
			switch (post.getState()) {
			case CREATE:
				postToHandle = clientLogic.getPostDetails(post.getIntraHash(), clientUser.getName());
				postToHandle.setUser(serverUser);
				createOnServerList.add(postToHandle);
				break;
			case CREATE_CLIENT:
				postToHandle = serverLogic.getPostDetails(post.getIntraHash(), serverUser.getName());
				postToHandle.setUser(clientUser);
				createOnClientList.add(postToHandle);
				break;
			case DELETE:
				deleteOnServerList.add(post.getIntraHash());
				break;
			case DELETE_CLIENT:
				deleteOnClientList.add(post.getIntraHash());
				break;
			case UPDATE:
				postToHandle = clientLogic.getPostDetails(post.getIntraHash(), clientUser.getName());
				postToHandle.setUser(serverUser);
				updateOnServerList.add(postToHandle);
				break;
			case UPDATE_CLIENT:
				postToHandle = serverLogic.getPostDetails(post.getIntraHash(), serverUser.getName());
				postToHandle.setUser(clientUser);
				updateOnClientList.add(postToHandle);
				break;
			default:
				break;
			}
		}

		boolean duplicates = false;
		
		/*
		 *  Apply changes to both systems.
		 */
		StringBuilder result = new StringBuilder();
		
		/*
		 * create posts on client 
		 */
		if (!createOnClientList.isEmpty()) {
			try {
				clientLogic.createPosts(createOnClientList);
				result.append("created on client: " + createOnClientList.size() + ", ");
			} catch (DatabaseException e) {
				/*
				 *  this can happen if some duplicate posts exists
				 *  FIXME: check oder possibilities to throw Database Exception
				 */
				log.error("database exception catched during creation on client", e);
				duplicates = true;
			}
			
		}

		/*
		 * create posts on server
		 */
		if (!createOnServerList.isEmpty()) {
			try {
				serverLogic.createPosts(createOnServerList);
				result.append("created on server: " + createOnServerList.size() + ", ");
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
		if (!updateOnClientList.isEmpty()) {
			clientLogic.updatePosts(updateOnClientList, PostUpdateOperation.UPDATE_ALL);
			result.append("updated on client: " + updateOnClientList.size() + ", ");
		}

		/*
		 * update posts on server
		 */
		if (!updateOnServerList.isEmpty()) {
			serverLogic.updatePosts(updateOnServerList, PostUpdateOperation.UPDATE_ALL);
			result.append("updated on server: " + updateOnServerList.size() + ", ");
		}

		/*
		 * delete posts on client
		 */
		if (!deleteOnClientList.isEmpty()) {
			clientLogic.deletePosts(clientUser.getName(), deleteOnClientList);
			result.append("deleted on client: " + deleteOnClientList.size() + ", ");
		}

		/*
		 * delete posts no server
		 */
		if (!deleteOnServerList.isEmpty()) {
			serverLogic.deletePosts(serverUser.getName(), deleteOnServerList);
			result.append("deleted on server: " + deleteOnServerList.size());
		}

		/*
		 * generate result string
		 */
		if (duplicates) {
			result.insert(0, "duplicates detected, ");
		} else {
			result.insert(0, "done, ");
		}
		
	
		int length = result.length();
		if (length == 6) {
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
		this.uri = ownUri;
	}

	/**
	 * @return the ownUri
	 */
	public URI getOwnUri() {
		return uri;
	}

	/**
	 * @param serverLogicFactory the serverLogicFactory to set
	 */
	public void setServerLogicFactory(DBLogicApiInterfaceFactory serverLogicFactory) {
		this.serverLogicFactory = serverLogicFactory;
	}

	/**
	 * @return the serverLogicFactory
	 */
	public DBLogicApiInterfaceFactory getServerLogicFactory() {
		return serverLogicFactory;
	}

	/**
	 * @param factory the factory to set
	 */
	public void setDBSessionFactory(DBSessionFactory factory) {
		this.dbSessionFactory = factory;
	}

}
