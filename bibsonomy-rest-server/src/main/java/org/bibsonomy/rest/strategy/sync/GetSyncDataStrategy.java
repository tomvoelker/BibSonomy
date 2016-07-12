/**
 * BibSonomy-Rest-Server - The REST-server.
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
package org.bibsonomy.rest.strategy.sync;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author wla
 */
public class GetSyncDataStrategy extends Strategy {
	
	private static final Log log = LogFactory.getLog(GetSyncDataStrategy.class);
	
	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;

	/**
	 * @param context
	 * @param serviceURI
	 */
	public GetSyncDataStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ObjectNotFoundException {
		final LogicInterface logic = this.getLogic();
		final String userName = logic.getAuthenticatedUser().getName();
		
		final SynchronizationData lastSyncData = logic.getLastSyncData(userName, serviceURI, this.resourceType);
		if (log.isDebugEnabled()) {
			log.debug("got last sync data '" + lastSyncData + "' for user " + userName + " and sync service " + serviceURI);
		}
		this.getRenderer().serializeSynchronizationData(writer, lastSyncData);
	}

}
