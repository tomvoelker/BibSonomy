package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.SettingsCommand;
import org.bibsonomy.webapp.util.View;

/**
 * @author Steffen
 * @version $Id$
 */
public class SettingsController {
//TODO
	private static final Log log = LogFactory.getLog(SearchPageController.class);

	/**
	 * @param command
	 * @return
	 */
	public View workOn(SettingsCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		return null;
	}
	
	/**
	 * @return
	 */
	public SettingsCommand instantiateCommand() {
		return new SettingsCommand();
	}
}
