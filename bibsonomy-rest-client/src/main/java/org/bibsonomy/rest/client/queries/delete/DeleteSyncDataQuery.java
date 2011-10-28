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
package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.utils.RestSyncUtils;
import org.bibsonomy.util.UrlUtils;

/**
 * @author wla
 * @version $Id$
 */
/**
 * @author wla
 *
 */
public class DeleteSyncDataQuery extends AbstractSyncQuery<String> {

	final Date syncDate;
	
	public DeleteSyncDataQuery(final String serviceURI, final Class<? extends Resource> resourceType, final Date syncDate, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		super(serviceURI, resourceType, strategy, direction);
		this.syncDate = syncDate;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		String url = generateURL("data");
		
		if (present(syncDate)) {
			url = UrlUtils.setParam(url, "date", UrlUtils.safeURIEncode(RestSyncUtils.serializeDate(syncDate)));
		}
		performRequest(HttpMethod.DELETE, url, "");
		return null;
	}

}
