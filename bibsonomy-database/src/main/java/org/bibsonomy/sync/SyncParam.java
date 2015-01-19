/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.sync;

import java.net.URI;
import java.util.Date;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.model.sync.SynchronizationData;

/**
 * @author wla
 */
public class SyncParam {

	private SynchronizationData data;
	private Date newDate;
	private SyncService syncService;

	private String userName;
	private URI service;
	private int serviceId;
	private boolean server;

	/**
	 * @return the newDate
	 */
	public Date getNewDate() {
		return this.newDate;
	}

	/**
	 * @param newDate the newDate to set
	 */
	public void setNewDate(Date newDate) {
		this.newDate = newDate;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the serviceId
	 */
	public URI getService() {
		return service;
	}

	/**
	 * @return the serviceId
	 */
	public int getServiceId() {
		return serviceId;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setService(final URI service) {
		this.service = service;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(final int serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the server
	 */
	public boolean isServer() {
		return this.server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(final boolean server) {
		this.server = server;
	}

	/**
	 * @return the data
	 */
	public SynchronizationData getData() {
		return this.data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(final SynchronizationData data) {
		this.data = data;
	}
	
	/**
	 * @return the syncService
	 */
	public SyncService getSyncService() {
		return this.syncService;
	}

	/**
	 * @param syncService the syncService to set
	 */
	public void setSyncService(final SyncService syncService) {
		this.syncService = syncService;
	}
}
