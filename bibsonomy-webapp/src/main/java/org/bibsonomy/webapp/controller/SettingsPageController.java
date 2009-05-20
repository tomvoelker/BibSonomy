package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.AdminViewCommand;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Steffen
 * @version $Id$
 */
public class SettingsPageController implements MinimalisticController<SettingsViewCommand> {
//TODO
	private static final Log log = LogFactory.getLog(SearchPageController.class);

	private LogicInterface logic;

	private UserSettings userSettings;
	/**
	 * @param command
	 * @return the view
	 */
	public View workOn(SettingsViewCommand command) {
		log.info("WorkOn called");
		log.debug(this.getClass().getSimpleName());
		// TODO i18n
		command.setPageTitle("settings");
		log.info("Command getGrouping: "+command.getGrouping());
		log.info("importType: "+command.getImportType());
		log.info("overwrite: " + command.getOverwrite());
		return Views.SETTINGSPAGE;
	}
	
	/**
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param userSettings
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}
	
	/**
	 * @return
	 */
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setGrouping(GroupUtils.getPublicGroup().getName());
		return command;
	}
}
