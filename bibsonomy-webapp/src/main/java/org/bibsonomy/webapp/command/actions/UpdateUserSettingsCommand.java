package org.bibsonomy.webapp.command.actions;


/**
 * @author cvo
 * @version $Id$
 */
public class UpdateUserSettingsCommand extends SettingsCommand {

	/**
	 * action can be logging, api or layoutTagPost
	 * this three types determine the different possible actions which will be handled 
	 * by this controller for the settings.settings site
	 */
	private String action;

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
}
