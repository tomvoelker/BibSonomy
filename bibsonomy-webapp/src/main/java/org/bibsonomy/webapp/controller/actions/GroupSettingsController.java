/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.GroupPublicationReportingSettings;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SearchPageController;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * FIXME: refactor as subclass of {@link SettingsPageController}.
 * 
 * @author ema
 */
@Deprecated // TODO_GROUPS as of 29.12.2014
public class GroupSettingsController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(SearchPageController.class);

	/**
	 * hold current errors
	 */
	private Errors errors;
	private LogicInterface logic;

	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		final Group group = new Group();
		group.setPublicationReportingSettings(new GroupPublicationReportingSettings());
		command.setGroup(group);
		return command;
	}

	@Override
	public View workOn(final SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		final User loginUser = context.getLoginUser();
		command.setUser(loginUser);
		
		// used to set the user specific value of maxCount/minFreq 
		command.setChangeTo((loginUser.getSettings().getIsMaxCount() ? loginUser.getSettings().getTagboxMaxCount() : loginUser.getSettings().getTagboxMinfreq()));
		// show sync tab only for non-spammers
		command.showSyncTab(!loginUser.isSpammer());
		
		// check whether the user is a group
		if (UserUtils.userIsGroup(loginUser)) {
			command.setHasOwnGroup(true);
		} else {
			// if he is not, an error message is shown, because this controller
			// can only be called from a group admin page.
			command.setSelTab(SettingsViewCommand.GROUP_IDX);
			this.errors.reject("settings.group.error.groupDoesNotExist");
			return Views.SETTINGSPAGE;
		}
		
		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		log.debug("User is logged in, ckey is valid");
		// the group properties to update
		final Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
		final boolean sharedDocs = command.getSharedDocuments() == 1;
		
		// the group to update
		final Group groupToUpdate = this.logic.getGroupDetails(loginUser.getName(), false);
		if (!present(groupToUpdate)) {
			throw new AccessDeniedException("please login as group");
		}
		// this should be always the same as loginUser.getName()
		final String groupName = groupToUpdate.getName();

		if ("updateGroupReportingSettings".equals(command.getAction())) {
			groupToUpdate.setPublicationReportingSettings(command.getGroup().getPublicationReportingSettings());
			this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_GROUP_REPORTING_SETTINGS, null);
			return returnSettingsView(command, groupToUpdate, groupName);
		}
		
		// update the bean
		groupToUpdate.setPrivlevel(priv);
		groupToUpdate.setSharedDocuments(sharedDocs);
		
		// do ADD_NEW_USER on addUserToGroup != null
		final String username = command.getUsername();
		if (present(username)) {
			try {
				// since now only one user can be added to a group at once
				// TODO: When are we getting here?
				final GroupMembership ms = new GroupMembership(new User(username), GroupRole.USER, false);
				this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ADD_MEMBER, ms);
			} catch (final Exception ex) {
				log.error("error while adding user '" + username + "' to group '" + groupName + "'", ex);
				// if a user can't be added to a group, this exception is thrown
				this.errors.reject("settings.group.error.addUserToGroupFailed", new Object[]{username, groupName},
						"The User {0} couldn't be added to the Group {1}.");
			}
		} else {
			try {
				this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_SETTINGS, null);
			} catch (final Exception ex) {
				log.error("error while updating settings for group '" + groupName + "'", ex);
				// TODO: what exceptions can be thrown?!
			}
		}
		return returnSettingsView(command, groupToUpdate, groupName);
	}

	protected View returnSettingsView(final SettingsViewCommand command, final Group groupToUpdate, final String groupName) {
		/*
		 * we have to re-fetch the group details
		 */
		command.setGroup(groupToUpdate);
		/*
		 * choose correct tab and return
		 */
		command.setSelTab(SettingsViewCommand.GROUP_IDX);
		return Views.SETTINGSPAGE;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

}
