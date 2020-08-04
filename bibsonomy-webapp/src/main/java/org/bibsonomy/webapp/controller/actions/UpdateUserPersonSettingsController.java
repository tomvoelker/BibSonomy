/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;

/**
 * @author kchoong
 */
public class UpdateUserPersonSettingsController extends SettingsPageController {
	private static final Log log = LogFactory.getLog(UpdateUserPersonSettingsController.class);

	private int maxQuerySize;

	@Override
	public View workOn(final SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}

		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}

		if (errors.hasErrors()) {
			super.workOn(command);
		}

		final User user = context.getLoginUser();
		updatePersonSettings(command.getUser().getSettings(), user);

		final ExtendedRedirectViewWithAttributes redirectView = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getSettingsUrlWithSelectedTab(SettingsViewCommand.PERSON_IDX));
		if (this.errors.hasErrors()) {
			redirectView.addAttribute(ExtendedRedirectViewWithAttributes.ERRORS_KEY, this.errors);
		} else {
			redirectView.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "settings.user.settings.update.success");
		}
		return redirectView;
	}

	private void updatePersonSettings(final UserSettings commandSettings, final User user) {
		final UserSettings userSettings = user.getSettings();
		userSettings.setPersonPostsStyle(commandSettings.getPersonPostsStyle());
		userSettings.setPersonPostsLayout(commandSettings.getPersonPostsLayout());
		userSettings.setPersonPostsPerPage(commandSettings.getPersonPostsPerPage());
		final String updatedUser = logic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		log.debug("person settings of user " + updatedUser + " has been changed successfully");
	}

	/**
	 * @param maxQuerySize the maxQuerySize to set
	 */
	public void setMaxQuerySize(int maxQuerySize) {
		this.maxQuerySize = maxQuerySize;
	}

}
