package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author wla
 * @version $Id$
 */
public class SyncSettingsCommand extends BaseCommand {
	
	private SyncService syncService = new SyncService();

	/**
	 * @return the service
	 */
	public SyncService getSyncService() {
		return this.syncService;
	}
	/**
	 * @param syncService the service to set
	 */
	public void setSyncService(final SyncService syncService) {
		this.syncService = syncService;
	}

}
