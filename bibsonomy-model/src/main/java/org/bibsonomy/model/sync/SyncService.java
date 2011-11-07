/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model.sync;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.bibsonomy.model.Resource;

/** 
 * @author wla
 * @version $Id$
 */
public class SyncService {
	
	private Properties user;
	private URI service; // FIXME: rename to "uri" or "serviceUri"
	private URI secureAPI;
	private Map <String, SynchronizationData> lastSyncData;
	private Class<? extends Resource> resourceType;
	private SynchronizationDirection direction;
	private ConflictResolutionStrategy strategy;
	private Map<Class<? extends Resource>, Map<String, String>> plan;
	private String sslDn;
	
	/**
	 * @return the clientUser
	 */
	public Properties getServerUser() {
		return this.user;
	}
	
	/**
	 * @param serverUser the clientUser to set
	 */
	public void setServerUser(final Properties serverUser) {
		this.user = serverUser;
	}

	/**
	 * @return the service
	 */
	public URI getService() {
		return this.service;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setService(final URI service) {
		this.service = service;
	}
	
	/**
	 * @return the secureAPI
	 */
	public URI getSecureAPI() {
		return secureAPI;
	}
	
	/**
	 * @param secureAPI the secureAPI to set
	 */
	public void setSecureAPI(final URI secureAPI) {
		this.secureAPI = secureAPI;
	}
	
	/**
	 * @return the lastSyncData
	 */
	public Map <String, SynchronizationData> getLastSyncData() {
		return lastSyncData;
	}
	
	/**
	 * 
	 * @param lastSyncData the lastSyncData to set
	 */
	public void setLastSyncData(final Map<String, SynchronizationData> lastSyncData) {
		this.lastSyncData = lastSyncData;
	}
	
	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(final Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	
	/**
	 * @return the resourceType
	 */
	public Class<? extends Resource> getResourceType() {
		return resourceType;
	}
	
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(final SynchronizationDirection direction) {
		this.direction = direction;
	}
	
	/**
	 * @return the direction
	 */
	public SynchronizationDirection getDirection() {
		return direction;
	}
	
	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(final ConflictResolutionStrategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * @return the strategy
	 */
	public ConflictResolutionStrategy getStrategy() {
		return strategy;
	}
	
	@Override
	public String toString() {
		return service != null ? service.toString() : null;
	}
	
	/**
	 * @param plan the plan to set
	 */
	public void setPlan(final Map<Class<? extends Resource>, Map<String, String>> plan) {
		this.plan = plan;
	}
	
	/**
	 * @return the plan
	 */
	public Map<Class<? extends Resource>, Map<String, String>> getPlan() {
		return plan;
	}
	
	/**
	 * @return the sslDn
	 */
	public String getSslDn() {
		return sslDn;
	}
	
	/**
	 * @param sslDn the sslDn to set
	 */
	public void setSslDn(final String sslDn) {
		this.sslDn = sslDn;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.service == null) ? 0 : this.service.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SyncService)) {
			return false;
		}
		final SyncService other = (SyncService) obj;
		if (this.service == null) {
			if (other.service != null) {
				return false;
			}
		} else if (!this.service.equals(other.service)) {
			return false;
		}
		return true;
	}
}
