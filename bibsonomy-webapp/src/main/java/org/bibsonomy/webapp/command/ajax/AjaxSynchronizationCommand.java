package org.bibsonomy.webapp.command.ajax;

import java.net.URI;
import java.util.List;

import org.bibsonomy.model.sync.SyncService;


/**
 * @author wla
 * @version $Id$
 */
public class AjaxSynchronizationCommand extends AjaxCommand {

	private URI serviceName;
	private List<SyncService> syncServer;
	private Boolean resetSyncService;

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
	 * @param resetSyncService the resetSyncService to set
	 */
	public void setResetSyncService(Boolean resetSyncService) {
		this.resetSyncService = resetSyncService;
	}

	/**
	 * @return the resetSyncService
	 */
	public Boolean getResetSyncService() {
		return resetSyncService;
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
}
