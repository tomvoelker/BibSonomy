package org.bibsonomy.webapp.command.ajax;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.bibsonomy.model.sync.SyncService;


/**
 * @author wla
 * @version $Id$
 */
public class AjaxSynchronizationCommand extends AjaxCommand {

	private URI serviceName;
	private List<SyncService> syncServer;
	private Date syncDate;

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(URI serviceName) {
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
	public void setSyncServer(List<SyncService> syncServer) {
		this.syncServer = syncServer;
	}

	/**
	 * @return the syncServer
	 */
	public List<SyncService> getSyncServer() {
		return syncServer;
	}

	/**
	 * @param syncDate the syncDate to set
	 */
	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	/**
	 * @return the syncDate
	 */
	public Date getSyncDate() {
		return syncDate;
	}
}
