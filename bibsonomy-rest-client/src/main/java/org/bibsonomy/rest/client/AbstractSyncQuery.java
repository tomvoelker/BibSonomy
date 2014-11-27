/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;


/**
 * @author wla
 * @param <T> 
 */
public abstract class AbstractSyncQuery<T> extends AbstractQuery<T> {
	/** the service uri of the sync */
	protected final String serviceURI;
	/** the strategy of the sync */
	protected final ConflictResolutionStrategy strategy;
	/** the sync direction */
	protected final SynchronizationDirection direction;
	/** the resource type */
	protected final Class<? extends Resource> resourceType;
	
	/**
	 * 
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 */
	public AbstractSyncQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		/*
		 * XXX: currently we only can sync one resource type one after another
		 */
		if (resourceType == Resource.class) {
			throw new IllegalArgumentException(Resource.class + " not supported. Please use specific resource types.");
		}
		this.serviceURI = serviceURI;
		this.resourceType = resourceType;
		this.strategy = strategy;
		this.direction = direction;
	}
}
