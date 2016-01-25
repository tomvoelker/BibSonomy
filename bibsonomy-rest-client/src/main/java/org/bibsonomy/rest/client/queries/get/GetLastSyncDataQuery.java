/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wla
 */
public class GetLastSyncDataQuery extends AbstractSyncQuery<SynchronizationData> {
	
	/**
	 * @see AbstractSyncQuery#AbstractSyncQuery(String, Class, ConflictResolutionStrategy, SynchronizationDirection)
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 */
	public GetLastSyncDataQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		super(serviceURI, resourceType, strategy, direction);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(this.getUrlRenderer().createHrefForSync(this.serviceURI, this.resourceType, this.strategy, this.direction, null, null));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.AbstractQuery#getResultInternal()
	 */
	@Override
	protected SynchronizationData getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseSynchronizationData(this.downloadedDocument);
	}

}
