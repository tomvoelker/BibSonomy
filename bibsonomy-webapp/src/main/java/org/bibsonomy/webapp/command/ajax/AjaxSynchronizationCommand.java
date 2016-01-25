/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.ajax;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.SyncService;


/**
 * @author wla
 */
public class AjaxSynchronizationCommand extends AjaxCommand {

	private URI serviceName;
	private List<SyncService> syncServer; // TODO: rename to syncServers
	private List<SyncService> syncClients;
	private Date syncDate;

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(final URI serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the serviceName
	 */
	public URI getServiceName() {
		return serviceName;
	}

	/**
	 * @param syncServer the syncServer to set
	 */
	public void setSyncServer(final List<SyncService> syncServer) {
		this.syncServer = syncServer;
	}

	/**
	 * @return the syncServer
	 */
	public List<SyncService> getSyncServer() {
		return syncServer;
	}

	/**
	 * @return the syncClients
	 */
	public List<SyncService> getSyncClients() {
		return this.syncClients;
	}

	/**
	 * @param syncClients the syncClients to set
	 */
	public void setSyncClients(final List<SyncService> syncClients) {
		this.syncClients = syncClients;
	}

	/**
	 * @param syncDate the syncDate to set
	 */
	public void setSyncDate(final Date syncDate) {
		this.syncDate = syncDate;
	}

	/**
	 * @return the syncDate
	 */
	public Date getSyncDate() {
		return syncDate;
	}
}
