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

package org.bibsonomy.rest.client;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.util.UrlBuilder;
import org.bibsonomy.util.UrlUtils;


/**
 * @author wla
 * @version $Id$
 * @param <T> 
 */
public abstract class AbstractSyncQuery<T> extends AbstractQuery<T> {

	private final String serviceURI;
	private final ConflictResolutionStrategy strategy;
	private final SynchronizationDirection direction;
	private final Class<? extends Resource> resourceType;
	
	/**
	 * 
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 */
	public AbstractSyncQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		this.serviceURI = serviceURI;
		this.resourceType = resourceType;
		this.strategy = strategy;
		this.direction = direction;
	}
	
	/**
	 * @return the sync url
	 */
	protected String getSyncURL() {
		final UrlBuilder urlBuilder = new UrlBuilder(RESTConfig.SYNC_URL + "/" + UrlUtils.safeURIEncode(serviceURI));
		/*
		 * FIXME: resourceType=all not supported - where to block?
		 */
		if (present(resourceType)) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}
		if (present(strategy)) {
			urlBuilder.addParameter(RESTConfig.SYNC_STRATEGY_PARAM, strategy.getConflictResolutionStrategy());
		}
		if (present(direction)) {
			urlBuilder.addParameter(RESTConfig.SYNC_DIRECTION_PARAM, direction.getSynchronizationDirection());
		}
		
		return urlBuilder.asString();
	}
}
