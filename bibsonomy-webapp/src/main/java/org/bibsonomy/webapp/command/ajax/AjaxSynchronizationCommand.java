package org.bibsonomy.webapp.command.ajax;

import java.util.List;


/**
 * @author wla
 * @version $Id$
 */
public class AjaxSynchronizationCommand extends AjaxCommand{

	private String serviceName;
	// FIXME: use Map<Class<? extends Resource>, Boolean> instead?
	private boolean syncBookmarks;
	private boolean syncPublications;
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
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @return <code>true</code>, if bookmarks shall be synchronized.
	 */
	public boolean getSyncBookmarks() {
		return this.syncBookmarks;
	}

	/**
	 * @param syncBookmarks
	 */
	public void setSyncBookmarks(boolean syncBookmarks) {
		this.syncBookmarks = syncBookmarks;
	}

	/**
	 * @return <code>true</code>, if publications shall be synchronized.
	 */
	public boolean getSyncPublications() {
		return this.syncPublications;
	}

	/**
	 * @param syncPublications
	 */
	public void setSyncPublications(boolean syncPublications) {
		this.syncPublications = syncPublications;
	}
	
}
