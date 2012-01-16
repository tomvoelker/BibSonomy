package org.bibsonomy.webapp.command.ajax;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.SyncService;


/**
 * @author wla
 * @version $Id$
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
