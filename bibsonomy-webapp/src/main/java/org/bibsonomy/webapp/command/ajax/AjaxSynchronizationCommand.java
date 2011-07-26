package org.bibsonomy.webapp.command.ajax;

import java.net.URI;
import java.util.List;


/**
 * @author wla
 * @version $Id$
 */
public class AjaxSynchronizationCommand extends AjaxCommand {

	private URI serviceName;
	private List<?> syncServices;

	/**
	 * @param syncServices the syncServices to set
	 */
	public void setSyncServices(List<?> syncServices) {
		this.syncServices = syncServices;
	}

	/**
	 * @return the syncServices
	 */
	public List<?> getSyncServices() {
		return syncServices;
	}
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
}
