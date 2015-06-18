/**
 * BibSonomy-Rest-Server - The REST-server.	
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.strategy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.SynchronizationRunningException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.sync.DeleteSyncDataStrategy;
import org.bibsonomy.rest.strategy.sync.GetSyncDataStrategy;
import org.bibsonomy.rest.strategy.sync.PostSyncPlanStrategy;
import org.bibsonomy.rest.strategy.sync.PutSyncStatusStrategy;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author wla, vhem
 */
public class SynchronizationHandler implements ContextHandler {
	private static final Log log = LogFactory.getLog(SynchronizationHandler.class);
	
	@Override
	public Strategy createStrategy(final Context context, final URLDecodingPathTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countRemainingTokens();
		if (numTokensLeft != 1) {
			throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
		}
		try {
			final URI serviceURI = new URI(urlTokens.next());
			final User user = context.getLogic().getAuthenticatedUser();
			final List<SyncService> syncClient = context.getLogic().getSyncService(user.getName(), serviceURI, false);

			// check SSL for Puma instance (client has SSLDn and RestServlet set the role of the user)
			if ( ValidationUtils.present(syncClient.get(0).getSslDn()) && !user.getRole().equals(Role.SYNC) ) {
				log.debug("no sync-role was set for the user - check ssl");

				// 400er error BAD REQUEST --> CERT abgelaufen, client falsch gestellt
				throw new SynchronizationRunningException();
			}

			switch (httpMethod) {
			case GET:
				return new GetSyncDataStrategy(context, serviceURI);
			case DELETE:
				return new DeleteSyncDataStrategy(context, serviceURI);
			case PUT:
				return new PutSyncStatusStrategy(context, serviceURI);
			case POST:
				return new PostSyncPlanStrategy(context, serviceURI);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "SynchronizationStatus");
			}
		} catch (final URISyntaxException ex) {
			throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
		}
	}

}
