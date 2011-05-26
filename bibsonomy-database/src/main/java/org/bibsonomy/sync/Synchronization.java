package org.bibsonomy.sync;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStates;

/**
 * @author wla
 * @version $Id$
 */
public class Synchronization {

	/*
	 * private final HashMap<String, SynchronizationPost> serverPosts; private
	 * final List<SynchronizationPost> clientPosts; private final Date
	 * lastSyncDate; boolean synchronizationSucsessful = false;
	 * 
	 * ConflictResolutionStrategy conflicStrategy;
	 */

	/*
	 * public Synchronization(HashMap<String, SynchronizationPost> serverPosts,
	 * List<SynchronizationPost> clientPosts, Date lastSynchronizationDate,
	 * ConflictResolutionStrategy conflictStrategy) { this.serverPosts =
	 * serverPosts; this.clientPosts = clientPosts; this.lastSync =
	 * lastSynchronizationDate; this.conflicStrategy = conflictStrategy; }
	 */

	public List<SynchronizationPost> synchronize(Map<String, SynchronizationPost> serverPosts, List<SynchronizationPost> clientPosts, Date lastSyncDate, ConflictResolutionStrategy conflictStrategy) {
		for (SynchronizationPost clientPost : clientPosts) {
			SynchronizationPost serverPost = serverPosts.get(clientPost.getIntraHash());
			if (lastSyncDate == null) {
				System.err.println("last sync date not present");
			}

			/* no such post on server */
			if (serverPost == null) {
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
