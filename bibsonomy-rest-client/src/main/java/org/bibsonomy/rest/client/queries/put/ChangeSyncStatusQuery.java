/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.rest.client.queries.put;

import java.util.Date;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wla
 */
public class ChangeSyncStatusQuery extends AbstractSyncQuery<Boolean> {

	private final SynchronizationStatus status;
	private final String info;
	private final Date newDate;
	
	/**
	 * 
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 * @param status
	 * @param info
	 * @param newSyncDate
	 */
	public ChangeSyncStatusQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction, final SynchronizationStatus status, final String info, Date newSyncDate) {
		super(serviceURI, resourceType, strategy, direction);
		this.status = status;
		this.info = info;
		this.newDate = newSyncDate;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createHrefForSync(this.serviceURI, this.resourceType, this.strategy, this.direction, this.newDate, this.status);
		this.downloadedDocument = performRequest(HttpMethod.PUT, url, info);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.AbstractQuery#getResultInternal()
	 */
	@Override
	protected Boolean getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}
}
