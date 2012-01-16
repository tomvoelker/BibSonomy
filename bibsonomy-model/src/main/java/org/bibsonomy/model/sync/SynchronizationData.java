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
import java.util.Date;

import org.bibsonomy.model.Resource;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationData {

	private URI service;
	private Class<? extends Resource> resourceType;
	private Date lastSyncDate;
	private SynchronizationStatus status;
	private String deviceInfo;
	private String deviceId;
	private String info;
	
	/**
	 * @return the lastSyncDate
	 */
	public Date getLastSyncDate() {
		return this.lastSyncDate;
	}
	
	/**
	 * @param lastSyncDate the lastSyncDate to set
	 */
	public void setLastSyncDate(final Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
	/**
	 * @return the status
	 */
	public SynchronizationStatus getStatus() {
		return this.status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(final SynchronizationStatus status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @return URI
	 */
	public URI getService() {
		return this.service;
	}
	
	/**
	 * 
	 * @param service
	 */
	public void setService(final URI service) {
		this.service = service;
	}
	
	/**
	 * 
	 * @return class of the resource
	 */
	public Class<? extends Resource> getResourceType() {
		return this.resourceType;
	}
	
	/**
	 * 
	 * @param resourceType
	 */
	public void setResourceType(final Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	
	/**
	 * @return the info
	 */
	public String getInfo() {
		return this.info;
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(final String info) {
		this.info = info;
	}
	
	/**
	 * @return the deviceInfo
	 */
	public String getDeviceInfo() {
		return this.deviceInfo;
	}
	
	/**
	 * @param deviceInfo the deviceInfo to set
	 */
	public void setDeviceInfo(final String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return this.deviceId;
	}
	
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(final String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Override
	public String toString() {
		return "@" + service + " for " + resourceType.getSimpleName() + " in status '" + status + "' (lastSyncDate=" + lastSyncDate + ")"; 
	}
}
