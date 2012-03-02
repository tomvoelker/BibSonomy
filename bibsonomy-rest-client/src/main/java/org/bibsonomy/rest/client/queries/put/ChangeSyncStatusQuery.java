/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client.queries.put;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.UrlUtils;

/**
 * @author wla
 * @version $Id$
 */
public class ChangeSyncStatusQuery extends AbstractSyncQuery<Boolean> {

	private final SynchronizationStatus status;
	private final String info;
	
	public ChangeSyncStatusQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction, final SynchronizationStatus status, final String info) {
		super(serviceURI, resourceType, strategy, direction);
		this.status = status;
		this.info = info;
	}

	@Override
	protected Boolean doExecute() throws ErrorPerformingRequestException {
		final String url = UrlUtils.setParam(getSyncURL(), RESTConfig.SYNC_STATUS, status.toString());
		performRequest(HttpMethod.PUT, url, info);
		return true;
	}
}
