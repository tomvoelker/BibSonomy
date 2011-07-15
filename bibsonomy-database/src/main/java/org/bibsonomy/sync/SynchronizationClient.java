package org.bibsonomy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;
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

/**
 * This client is only for BibSonomy
 * @author wla
 * @version $Id$
 */
public class SynchronizationClient {
	private static final Log log = LogFactory.getLog(SynchronizationClient.class);
	
	/*
	 * own URI as String and as java.net.URI
	 */
	private String ownUri;
	private URI uri;
	
	/*
	 *	FIXME make ConficResolutionStrategy configurable by user 
	 */
	private final ConflictResolutionStrategy strategy;
	private DBLogicApiInterfaceFactory serverLogicFactory;
	private DBSessionFactory dbSessionFactory = new IbatisSyncDBSessionFactory();
	
	public SynchronizationClient() {
		//FIXME get strategy form DB or elsewhere
		this.strategy = ConflictResolutionStrategy.LAST_WINS;
	}
	
	/**
	 * 
	 * @param userName
	 * @param apiKey
	 * @return Logic with access to database on server-service   
	 */
	private SyncLogicInterface createServerLogic(String userName, String apiKey) {
	
		//FIXME get correct DBSessionFactory for each service
		serverLogicFactory.setDbSessionFactory(this.dbSessionFactory);
		
		//FIXME remove cast after integration
		SyncLogicInterface serverLogic = (SyncLogicInterface) serverLogicFactory.getLogicAccess(userName, apiKey);
		
		return serverLogic;
	}
	
	/**
	 * Creates User-object from java.util.Properties stroed in database
	 * @param userProperties
	 * @return user from userProperties
	 */
	private User getUserFromProperties(Properties userProperties) {
		String userName = userProperties.getProperty("userName");
		String apiKey = userProperties.getProperty("apiKey");
		
		if(!present(userName) || !present(apiKey)){
			throw new IllegalStateException();
		}
		
		User user = new User();
		user.setName(userName);
		user.setApiKey(apiKey);
		return user;
	}
	
	/**
	 * Used in a synchronization process, in case that server logic already created 
	 * @param userName
	 * @param contentType
	 * @param serverLogic
	 * @return
	 */
	public SynchronizationData getLastSyncData(final String userName, Class<? extends Resource> resourceType, final SyncLogicInterface serverLogic) {
		//FIXME errorhandling
		return serverLogic.getLastSynchronizationDataForUserForContentType(userName, uri, resourceType);
	}
	
	/**
	 * Used in SettingsPageController, to show syncData, gets own logic
	 * @param syncService
	 * @param contentType
	 * @return
	 */
	public SynchronizationData getLastSyncData(final SyncService syncService, final Class<? extends Resource> resourceType) {
		// FIXME errorhandling
		final User serverUser = getUserFromProperties(syncService.getServerUser());
		final String userName = serverUser.getName();
		
		final SyncLogicInterface serverLogic = createServerLogic(userName, serverUser.getApiKey());
		
		return getLastSyncData(userName, resourceType, serverLogic);
	}
	

	/**
	 * handles synchronization of a resourceType 
	 * @param clientLogic
	 * @param resourceType
	 * @param clientUser
	 * @param server
	 * @return
	 */
	public SynchronizationData synchronize(LogicInterface clientLogic, Class<? extends Resource> resourceType, User clientUser, SyncService server) {
		//get server user
		User serverUser = getUserFromProperties(server.getServerUser());
		
		//get server logic
		SyncLogicInterface serverSyncLogic = createServerLogic(serverUser.getName(), serverUser.getApiKey());
		
		//set default result to "error"
		String result = "error";
		
		try {
			//try to synchronize resource
			result = synchronizeResource(resourceType, serverUser, clientUser, serverSyncLogic, clientLogic);
		} catch (SynchronizationRunningException e) {
			/*
			 * FIXME handling of this exception type. I think we can break "running" synchronization after timeout.
			 * Currently return only "running" status.
			 */
			SynchronizationData data = new SynchronizationData();
			data.setStatus("running");
			return data;
		} catch (Exception e) {
			//in case of an error, store syncdate as not successful, result stay "error"
			result = "error";
			log.error("ERROR OCCURRED", e);
		}
		//after successful synchronization, store sync result.
		storeSyncResult(result, resourceType, serverSyncLogic, serverUser.getName());
		
		//Get synchronization data from server. Can't construct here, because last_sync_date only known by server
		return getLastSyncData(serverUser.getName(), resourceType, serverSyncLogic);
	}	
	
	/**
	 * Stores result of synchronization on server
	 * @param result
	 * @param resourceType
	 * @param serverLogic
	 * @param serverUserName
	 */
	private void storeSyncResult(String result, Class<? extends Resource> resourceType, SyncLogicInterface serverLogic, String serverUserName) {
		SynchronizationData data = serverLogic.getCurrentSynchronizationDataForUserForServiceForContent(serverUserName, uri, resourceType);
		if(!present(data)) {
			//started more than one sync process per second -> do nothing
			return;
		}
		if (data.getStatus().equals("undone")) {
			data.setStatus(result);
			serverLogic.updateSyncData(data);
		} else {
			log.error("Error no running synchronization dound, to store result");
		}
	}

	/**
	 * main synchronization method
	 * @param resourceType
	 * @param serverUser
	 * @param clientUser
	 * @param syncServerLogic
	 * @param clientLogic
	 * @return synchronization result
	 */
	private String synchronizeResource(final Class<? extends Resource> resourceType, User serverUser, User clientUser, SyncLogicInterface syncServerLogic, LogicInterface clientLogic) {
		/*
		 * TODO remove syncServerLogic and syncClientLogic after integration of
		 * SyncLogicInterface into LogicInterface
		 */
		SyncLogicInterface syncClientLogic = (SyncLogicInterface)clientLogic;
		LogicInterface serverLogic = (LogicInterface)syncServerLogic;
		
		//Add sync acces to both users
		User serverAuthenticatedUser = serverLogic.getAuthenticatedUser();
		serverAuthenticatedUser.setRole(Role.SYNC);
		
		User clientAuthenticatedUser = clientLogic.getAuthenticatedUser();
		clientAuthenticatedUser.setRole(Role.SYNC);
	
		//get posts from client system
		List<SynchronizationPost> clientPosts = syncClientLogic.getSyncPostsListForUser(resourceType, clientUser.getName());
		
		//get synchronization states and posts from server
		clientPosts = syncServerLogic.getSynchronization(serverUser.getName(), resourceType, clientPosts, strategy, uri);

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
	public void setOwnUri(String ownUri) {
		this.ownUri = ownUri;
		try {
			uri = new URI(ownUri);
		} catch (URISyntaxException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	/**
	 * @return the ownUri
	 */
	public String getOwnUri() {
		return ownUri;
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
