package org.bibsonomy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.util.IbatisSyncDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
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

	
	private String ownUri;
	private URI uri;
	
	private final ConflictResolutionStrategy strategy;
	private DBLogicApiInterfaceFactory serverLogicFactory;
	
	public SynchronizationClient() {
		//FIXME get strategy form DB or elsewhere
		this.strategy = ConflictResolutionStrategy.LAST_WINS;
	}
	
	private SyncLogicInterface createServerLogic(String userName, String apiKey) {
	
		//FIXME get correct DBSessionFactory for each service
		serverLogicFactory.setDbSessionFactory(new IbatisSyncDBSessionFactory());
		
		SyncLogicInterface serverLogic = (SyncLogicInterface) serverLogicFactory.getLogicAccess(userName, apiKey);
		
		return serverLogic;
	}
	
	private void createClientLogic() {
		
	}
	
	/**
	 * 
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
	
	public Map<String, SynchronizationData> getLastSyncData(SyncService syncService, ConstantID contentType) {
		//FIXME errorhandling
		User serverUser = getUserFromProperties(syncService.getServerUser());
		String userName = serverUser.getName();
		SyncLogicInterface serverLogic = createServerLogic(userName, serverUser.getApiKey());
		
		Map<String, SynchronizationData> result = new HashMap<String, SynchronizationData>();
		
		if(contentType.equals(ConstantID.BOOKMARK_CONTENT_TYPE) || contentType.equals(ConstantID.ALL_CONTENT_TYPE)) {
			result.put(Bookmark.class.getSimpleName(), serverLogic.getLastSynchronizationDataForUserForContentType(userName, uri, Bookmark.class));
		}
		if(contentType.equals(ConstantID.BIBTEX_CONTENT_TYPE) || contentType.equals(ConstantID.ALL_CONTENT_TYPE)) {
			result.put(BibTex.class.getSimpleName(), serverLogic.getLastSynchronizationDataForUserForContentType(userName, uri, BibTex.class));
		}
		return result;
	}
	

	
	public SynchronizationData synchronize(LogicInterface clientLogic, Class<? extends Resource> resourceType, User clientUser, SyncService server) {
		User serverUser = getUserFromProperties(server.getServerUser());
		
		SyncLogicInterface serverSyncLogic = createServerLogic(serverUser.getName(), serverUser.getApiKey());
		
		String result = synchronizeResource(resourceType, serverUser, clientUser, serverSyncLogic, clientLogic);
		
		storeSyncResult(result, resourceType, serverSyncLogic, serverUser.getName());
		
		SynchronizationData data = getLastSyncData(server, ConstantID.getContentTypeByClass(resourceType)).get(resourceType.getSimpleName());

		return data;
	}

	private void storeSyncResult(String result, Class<? extends Resource> resourceType, SyncLogicInterface serverLogic, String serverUserName) {
		SyncLogicInterface syncServerLogic = serverLogic;
		SynchronizationData data = syncServerLogic.getCurrentSynchronizationDataForUserForServiceForContent(serverUserName, uri, resourceType);
		if (data.getStatus().equals("undone")) {
			data.setStatus(result);
			syncServerLogic.updateSyncData(data);
		} else {
			// ERROR
		}
	}

	private String synchronizeResource(final Class<? extends Resource> resourceType, User serverUser, User clientUser, SyncLogicInterface syncServerLogic, LogicInterface clientLogic) {
		/*
		 * TODO remove syncServerLogic and syncClientLogic after integration of
		 * SyncLogicInterface into LogicInterface
		 */
		SyncLogicInterface syncClientLogic = (SyncLogicInterface)clientLogic;
		LogicInterface serverLogic = (LogicInterface)syncServerLogic;

		// TODO replace this with correct cast
		// int serverServiceId = Integer.parseInt(serviceIdentifier);

		@SuppressWarnings("unused")
		int contentType = 0;
		if (BibTex.class.equals(resourceType)) {
			contentType = ConstantID.BIBTEX_CONTENT_TYPE.getId();
		} else if (Bookmark.class.equals(resourceType)) {
			contentType = ConstantID.BOOKMARK_CONTENT_TYPE.getId();
		} else {
			// TODO unknown resource Type
		}

		List<SynchronizationPost> clientPosts = syncClientLogic.getSyncPostsListForUser(resourceType, clientUser.getName());

		syncServerLogic.getSynchronization(serverUser.getName(), resourceType, clientPosts, strategy, uri);

		/*
		 * target lists
		 */
		List<Post<?>> createOnClientList = new ArrayList<Post<?>>();
		List<Post<?>> createOnServerList = new ArrayList<Post<?>>();
		List<String> deleteOnServerList = new ArrayList<String>();
		List<String> deleteOnClientList = new ArrayList<String>();
		List<Post<?>> updateOnClientList = new ArrayList<Post<?>>();
		List<Post<?>> updateOnServerList = new ArrayList<Post<?>>();

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

		// Apply changes
		StringBuilder answer = new StringBuilder();
		if (!createOnClientList.isEmpty()) {
			clientLogic.createPosts(createOnClientList);
			answer.append("created on client: " + createOnClientList.size() + ", ");
		}

		if (!createOnServerList.isEmpty()) {
			serverLogic.createPosts(createOnServerList);
			answer.append("created on server: " + createOnServerList.size() + ", ");
		}

		if (!updateOnClientList.isEmpty()) {
			// TODO other operation?
			clientLogic.updatePosts(updateOnClientList, PostUpdateOperation.UPDATE_ALL);
			answer.append("updated on client: " + updateOnClientList.size() + ", ");
		}

		if (!updateOnServerList.isEmpty()) {
			serverLogic.updatePosts(updateOnServerList, PostUpdateOperation.UPDATE_ALL);
			answer.append("updated on server: " + updateOnServerList.size() + ", ");
		}

		if (!deleteOnClientList.isEmpty()) {
			clientLogic.deletePosts(clientUser.getName(), deleteOnClientList);
			answer.append("deleted on client: " + deleteOnClientList.size() + ", ");
		}

		if (!deleteOnServerList.isEmpty()) {
			serverLogic.deletePosts(serverUser.getName(), deleteOnServerList);
			answer.append("deleted on server: " + deleteOnServerList.size());
		}

		answer.insert(0, "done, ");
		int length = answer.length();
		if (length == 6) {
			answer.append("no changes");
		} else if (answer.lastIndexOf(", ") == length - 2) {
			answer.delete(length - 2, length);
		}

		return answer.toString();
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


}
