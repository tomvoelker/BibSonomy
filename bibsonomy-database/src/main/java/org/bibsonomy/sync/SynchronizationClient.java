package org.bibsonomy.sync;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SynchronizationClients;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationClient {

	private User serverUser;
	private User clientUser;
	private LogicInterface serverLogic;
	private LogicInterface clientLogic;
	private ConflictResolutionStrategy strategy;
	private URI serviceIdentifier;

	public synchronized boolean synchronize(User serverUser, User clientUser, LogicInterface serverLogic, LogicInterface clientLogic, URI serviceIdentifier, String ServerServiceIdentifier) {

		this.serverUser = serverUser;
		this.clientUser = clientUser;
		this.serverLogic = serverLogic;
		this.clientLogic = clientLogic;
		this.serviceIdentifier = serviceIdentifier;

		// TODO get strategy form db
		this.strategy = ConflictResolutionStrategy.LAST_WINS;

		// TODO correct cast form serviceIdentifier
		SynchronizationClients client = SynchronizationClients.getById(Integer.parseInt(serviceIdentifier));

		switch (client) {
		case BIBSONOMY:
		case PUMA:
		case LOCAL:
		case BIBLICIOUS:
			
			String result = synchronizeResource(BibTex.class);
			storeSyncResult(result, client.getId(), BibTex.class);

			result = synchronizeResource(Bookmark.class);
			storeSyncResult(result, client.getId(), Bookmark.class);
			break;

		default:
			break;
		}

		return true;
	}

	private void storeSyncResult(final String result, final URI service, final Class<? extends Resource> resourceType) {
		final SyncLogicInterface syncServerLogic = (SyncLogicInterface) serverLogic;
		final SynchronizationData data = syncServerLogic.getCurrentSynchronizationDataForUserForServiceForContent(serverUser.getName(), service, resourceType);
		if (data.getStatus().equals("undone")) {
			data.setStatus(result);
			syncServerLogic.updateSyncData(data);
		} else {
			// ERROR
		}
	}

	public String synchronizeResource(final Class<? extends Resource> resourceType) {
		/*
		 * TODO remove syncServerLogic and syncClientLogic after integration of
		 * SyncLogicInterface into LogicInterface
		 */
		final SyncLogicInterface syncServerLogic = (SyncLogicInterface) serverLogic;
		final SyncLogicInterface syncClientLogic = (SyncLogicInterface) clientLogic;

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

		syncServerLogic.getSynchronization(serverUser.getName(), resourceType, clientPosts, strategy, serviceIdentifier);

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

}
