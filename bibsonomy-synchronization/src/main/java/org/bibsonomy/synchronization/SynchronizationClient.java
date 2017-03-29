/**
 * BibSonomy-Synchronization - Handles user synchronization between BibSonomy authorities
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.synchronization;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.util.ResourceUtils;

/**
 * This client synchronizes PUMA with BibSonomy.
 * PUMA is the server, BibSonomy is the client.
 * 
 * 
 * @author wla
 */
public class SynchronizationClient extends AbstractSynchronizationClient {
	private static final Log log = LogFactory.getLog(SynchronizationClient.class);

	/**
	 * Synchronized the user's posts between the clientLogic and the syncServer
	 * according to the configured sync direction and resource types.
	 * 
	 * @param clientLogic
	 * @param syncServerUri
	 * @return map of synchronization data
	 */
	public Map<Class<? extends Resource>, SynchronizationData> synchronize(final LogicInterface clientLogic, final URI syncServerUri) {
		final SyncService syncServer = getServerByURI(clientLogic, syncServerUri);
		final Class<? extends Resource> resourceType = syncServer.getResourceType();
		final SynchronizationDirection direction = syncServer.getDirection();

		/*
		 * retrieve instance of server logic
		 */
		final LogicInterface serverLogic = getServerLogic(syncServer);
		final boolean isSecureSync = present(syncServer.getSecureAPI());
		
		if (!present(serverLogic)) {
			throw new IllegalArgumentException("Synchronization for " + syncServerUri + " not configured for user " + clientLogic.getAuthenticatedUser());
		}
		final String serverUserName = serverLogic.getAuthenticatedUser().getName();
		
		/*
		 * sync each configured resource type
		 */
		final Map<Class<? extends Resource>, SynchronizationData> result = new HashMap<Class<? extends Resource>, SynchronizationData>();
		
		for (final Class<? extends Resource> resource : ResourceUtils.getResourceTypesByClass(resourceType)) {
			result.put(resource, synchronize(clientLogic, serverLogic, serverUserName, resource, direction, syncServer.getStrategy(), isSecureSync));
		}
		return result;
	}

	/**
	 * Synchronizes the user's posts of the given resource type 
	 * on the client and server according to the given direction. 
	 * 
	 * @param clientLogic
	 * @param serverLogic
	 * @param serverUserName
	 * @param resourceType
	 * @param direction
	 * @param strategy 
	 * @param isSecureSync TODO
	 * @return
	 */
	protected SynchronizationData synchronize(final LogicInterface clientLogic, final LogicInterface serverLogic, final String serverUserName, final Class<? extends Resource> resourceType, final SynchronizationDirection direction, final ConflictResolutionStrategy strategy, boolean isSecureSync) {
		SynchronizationStatus newStatus;
		String info;
		try {
			/*
			 * try to synchronize
			 */
			/*
			 * get posts from client
			 */
			final List<SynchronizationPost> clientPosts = clientLogic.getSyncPosts(clientLogic.getAuthenticatedUser().getName(), resourceType);
			
			/*
			 * get synchronization actions and posts from server
			 */
			final List<SynchronizationPost> syncPlan = serverLogic.getSyncPlan(serverLogic.getAuthenticatedUser().getName(), ownUri, resourceType, clientPosts, strategy, direction);
			/*
			 * flag sync as running
			 */
			updateSyncData(serverLogic, serverUserName, resourceType, SynchronizationStatus.PLANNED, SynchronizationStatus.RUNNING, "", isSecureSync);
			/*
			 * sync
			 */
			info = synchronize(clientLogic, serverLogic, syncPlan, direction);
			newStatus = SynchronizationStatus.DONE;
		} catch (final SynchronizationRunningException e) {
			/*
			 * FIXME handling of this exception type. I think we can break "running" synchronization after timeout.
			 * Currently return only "running" status.
			 */
			throw e;
		} catch (final Exception e) {
			info = "";
			newStatus = SynchronizationStatus.ERROR;
			log.error("Error in synchronization", e);
		}
		/*
		 * store sync result
		 */
		updateSyncData(serverLogic, serverUserName, resourceType, SynchronizationStatus.RUNNING, newStatus, info, isSecureSync);
		
		/*
		 * Get synchronization data from server. Can not be constructed here 
		 * because last_sync_date is only known by the server
		 */
		return getLastSyncData(serverLogic, serverUserName, resourceType);
	}

}
