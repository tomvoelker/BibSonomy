package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author wla
 * @version $Id$
 */
public class SyncSettingsCommand extends BaseCommand {
	
	private SyncService syncService = new SyncService();
	private String action;

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

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
}
