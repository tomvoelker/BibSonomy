package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Steffen
 * @version $Id$
 */
public class SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(SearchPageController.class);

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	private LogicInterface logic;

	/**
	 * @param command
	 * @return the view
	 */
	public View workOn(final SettingsViewCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			return new ExtendedRedirectView("/login");
		}

		command.setPageTitle("settings");
		
		final User loginUser = command.getContext().getLoginUser();
		command.setUser(loginUser);
		
		//used to set the user specific value of maxCount/minFreq 
		command.setChangeTo((loginUser.getSettings().getIsMaxCount() ? 
				loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));
		
		//check whether the user is a group		
		if (UserUtils.userIsGroup(loginUser)) {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		}

		switch (command.getSelTab()) {
		case 0: {
			// called by the my profile tab
			workOnMyProfileTab(command);
			break;
		}
		case 1: {
			// called by the setting tab
			workOnSettingsTab(command);
			break;
		}
		case 2: {

			checkInstalledJabrefLayout(command);
			break;
		}
		case 3: {
			//do nothing
			break;
		}
		default: {
			errors.reject("error.settings.tab");
			break;
		}
		}

		if (errors.hasErrors()) {

			if (errors.hasFieldErrors("error.general.login")) {
				return Views.SETTINGSPAGE;
			}

			return Views.ERROR;
		}

		return Views.SETTINGSPAGE;
	}

	private void workOnMyProfileTab(final SettingsViewCommand command) {
		// retrieve friend list of the user
		command.setUserFriends(logic.getUserFriends(command.getUser()));
		command.setFriendsOfUser(logic.getFriendsOfUser(command.getUser()));
		// retrieve profile privacy level setting
		command.setGroup(command.getUser().getSettings().getProfilePrivlevel().name().toLowerCase());
	}

	/**
	 * checks whether the user has already uploaded jabref layout definitions
	 * 
	 * @param command
	 */
	private void checkInstalledJabrefLayout(final SettingsViewCommand command) {

		final LayoutPart[] values = LayoutPart.values();

		for (final LayoutPart layoutpart : values) {

			final String fileHash = JabrefLayoutUtils.userLayoutHash(command
					.getContext().getLoginUser().getName(), layoutpart);

			final Document document = this.logic.getDocument(command.getContext()
					.getLoginUser().getName(), fileHash);

			if (document != null) {
				if ("begin".equals(layoutpart.getName())) {
					command.setBeginHash(fileHash);
					command.setBeginName(document.getFileName());
				} else if ("end".equals(layoutpart.getName())) {
					command.setEndHash(fileHash);
					command.setEndName(document.getFileName());
				} else if ("item".equals(layoutpart.getName())) {
					command.setItemHash(fileHash);
					command.setItemName(document.getFileName());
				}
			}
		}
	}

	private void workOnSettingsTab(final SettingsViewCommand command) {
		// no work to do
	}

	/**
	 * @return the current command
	 */
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		return command;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
