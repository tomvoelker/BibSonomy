package org.bibsonomy.webapp.command;

import java.util.List;


/**
 * @author wla
 * @version $Id$
 */
public class SyncPageCommand extends BaseCommand {

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
	
}
