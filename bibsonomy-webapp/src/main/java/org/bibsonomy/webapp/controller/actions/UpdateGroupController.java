/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.GroupValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * TODO: add documentation
 * 
 * @author tni
 */
public class UpdateGroupController implements ValidationAwareController<GroupSettingsPageCommand>, ErrorAware, RequestAware {
	private static final Log log = LogFactory.getLog(UpdateGroupController.class);

	private static final String SETTINGS_GROUP_TAB_REDIRECT = "/settings?selTab=3";

	private Errors errors = null;

	private LogicInterface logic;
	private LogicInterface adminLogic;

	private RequestLogic requestLogic;
	private MailUtils mailUtils;

	@Override
	public GroupSettingsPageCommand instantiateCommand() {
		final GroupSettingsPageCommand command = new GroupSettingsPageCommand();
		command.setGroup(new Group());
		return command;
	}

	@Override
	public View workOn(final GroupSettingsPageCommand command) {
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
			this.errors.reject("error.field.valid.ckey");
		}

		Group groupToUpdate = null;
		// since before requesting a group, it must not exist, we cannot check
		// for it, either.
		groupToUpdate = this.logic.getGroupDetails(command.getGroupname());

		final GroupUpdateOperation operation = command.getOperation();
		Integer selTab = null;
		if (present(operation)) {
			final User loginUser = context.getLoginUser();
			switch (operation) {
			case ADD_INVITED: {
				// sent an invite
				final String username = command.getUsername();
				if (present(username) && !username.equals(groupToUpdate.getName())) {
					// get user details with an admin logic to get the mail
					// address
					final User invitedUser = this.adminLogic.getUserDetails(username);
					if (present(invitedUser.getName())) {
						final GroupMembership membership = groupToUpdate.getGroupMembershipForUser(invitedUser.getName());
						if (!present(membership)) {
							final GroupMembership ms = new GroupMembership(invitedUser, null, false);
							try {
								// since now only one user can be invited to a
								// group at once
								this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ADD_INVITED, ms);
								this.mailUtils.sendGroupInvite(groupToUpdate.getName(), loginUser, invitedUser, this.requestLogic.getLocale());
							} catch (final Exception ex) {
								log.error("error while inviting user '" + username + "' to group '" + groupToUpdate + "'", ex);
								// if a user can't be added to a group, this
								// exception is thrown
								this.errors.rejectValue("username", "settings.group.error.inviteUserToGroupFailed", new Object[] { username, groupToUpdate },
										"The User {0} couldn't be invited to the Group {1}.");
							}
						} else {
							// TODO: handle case of already invited user
						}
					} else {
						// TODO: handle case of non existing user!
					}
				}
				selTab = Integer.valueOf(GroupSettingsPageCommand.MEMBER_LIST_IDX);
				break;
			}
			case ADD_MEMBER: {
				/*
				 * add a new user to the group
				 * this handles a join request by the user
				 */
				final String username = command.getUsername();
				if (present(username) && !username.equals(groupToUpdate.getName())) {
					final GroupMembership ms = new GroupMembership(new User(username), null, false);
					try {
						this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ADD_MEMBER, ms);
					} catch (final Exception ex) {
						log.error("error while adding user '" + username + "' to group '" + groupToUpdate + "'", ex);
						// if a user can't be added to a group, this exception
						// is thrown
						this.errors.rejectValue("username", "settings.group.error.addUserToGroupFailed", new Object[] { username, groupToUpdate },
								"The User {0} couldn't be added to the Group {1}.");
					}
				}
				selTab = Integer.valueOf(GroupSettingsPageCommand.MEMBER_LIST_IDX);
				break;
			}
			case REMOVE_MEMBER: {
				/*
				 * remove the user from the group
				 */
				final String username = command.getUsername();
				if (present(username) && !username.equals(groupToUpdate.getName())) {
					final GroupMembership ms = new GroupMembership(new User(username), null, false);
					try {
						this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.REMOVE_MEMBER, ms);

						// if we removed ourselves from the group, return the
						// homepage.
						if (loginUser.getName().equals(username)) {
							return new ExtendedRedirectView(SETTINGS_GROUP_TAB_REDIRECT);
						}
					} catch (final Exception ex) {
						log.error("error while removing user '" + username + "' from group '" + groupToUpdate + "'", ex);
						// if a user can't be added to a group, this exception
						// is thrown
						this.errors.reject("settings.group.error.removeUserFromGroupFailed", new Object[] { username, groupToUpdate },
								"The User {0} couldn't be removed from the Group {1}.");
					}
				}
				selTab = Integer.valueOf(GroupSettingsPageCommand.MEMBER_LIST_IDX);
				break;
			}
			case UPDATE_GROUP_REPORTING_SETTINGS: {
				/*
				 * update the reporting settings
				 */
				// the group to update
				groupToUpdate.setPublicationReportingSettings(command.getGroup().getPublicationReportingSettings());
				this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_GROUP_REPORTING_SETTINGS, null);
				break;
			}
			case UPDATE_SETTINGS: {
				/*
				 * the group properties to update
				 */
				final Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
				final boolean sharedDocs = command.getSharedDocuments() == 1;
				final String realname = command.getRealname();
				final URL homepage = command.getHomepage();

				final User groupUserToUpdate = this.logic.getUserDetails(groupToUpdate.getName());
				groupUserToUpdate.setEmail("nomail"); // TODO: adapt to the
														// notion that admins
														// should receive mails.
				// the group to update
				try {
					groupToUpdate.setPrivlevel(priv);
					groupToUpdate.setSharedDocuments(sharedDocs);

					if (present(realname)) {
						groupUserToUpdate.setRealname(realname);
					}
					if (present(homepage)) {
						groupUserToUpdate.setHomepage(homepage);
					}

					this.logic.updateUser(groupUserToUpdate, UserUpdateOperation.UPDATE_CORE);
					this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_SETTINGS, null);
				} catch (final Exception ex) {
					log.error("error while updating settings for group '" + groupToUpdate + "'", ex);
					// TODO: what exceptions can be thrown?!
				}
				break;
			}
			case DECLINE_JOIN_REQUEST: {
				final String username = command.getUsername();
				if (present(username)) {
					// the group to update
					final User declineUser = this.adminLogic.getUserDetails(username);
					final GroupMembership ms = new GroupMembership(declineUser, null, false);
					try {
						this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.DECLINE_JOIN_REQUEST, ms);
						// TODO: I18N
						this.mailUtils.sendJoinGroupDenied(groupToUpdate.getName(), username, declineUser.getEmail(), "Your group join request was denied.", this.requestLogic.getLocale());
					} catch (final Exception ex) {
						log.error("error while declining the join request of user '" + username + "' from group '" + groupToUpdate + "'", ex);
						this.errors.rejectValue("username", "settings.group.error.declineJoinRequestFailed", new Object[] { username },
								"The request of User {0} couldn't be removed.");
					}
				}
				selTab = Integer.valueOf(GroupSettingsPageCommand.MEMBER_LIST_IDX);
				break;
			}
			case REMOVE_INVITED: {
				final String username = command.getUsername();
				if (present(username)) {
					final GroupMembership ms = new GroupMembership(new User(username), null, false);
					try {
						this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.REMOVE_INVITED, ms);
						if (loginUser.getName().equals(username)) {
							return new ExtendedRedirectView(SETTINGS_GROUP_TAB_REDIRECT);
						}
						selTab = Integer.valueOf(GroupSettingsPageCommand.MEMBER_LIST_IDX);
					} catch (final Exception ex) {
						log.error("error while removing the invite of user '" + username + "' from group '" + groupToUpdate + "'", ex);
						this.errors.rejectValue("username", "settings.group.error.removeInviteFailed", new Object[] { username },
								"The invite of User {0} couldn't be removed.");
					}
				}
				break;
			}
			case UPDATE_GROUPROLE: {
				final String username = command.getUsername();
				if (!present(command.getGroup())) {
					this.errors.reject("settings.group.error.changeGroupRoleFailed", username);
				}
				if (!this.errors.hasErrors()) {
					final GroupMembership ms = new GroupMembership(new User(username), command.getGroupRole(), false);
					try {
						this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_GROUPROLE, ms);
					} catch (final Exception ex) {
						log.error("error while changing the the role of user '" + username + "' from group '" + groupToUpdate + "'", ex);
					}
				}
				selTab = Integer.valueOf(GroupSettingsPageCommand.MEMBER_LIST_IDX);
				break;
			}
			default:
				this.errors.reject("error.invalid_parameter");
				break;
			}
		} else {
			this.errors.reject("error.invalid_parameter");
		}

		if (this.errors.hasErrors()) {
			// command.setSelTab(SettingsViewCommand.GROUP_IDX);
			// return super.workOn(command);
		}

		// success: go back where you've come from
		// TODO: inform the user about the success!
		// TODO: use url generator
		final String settingsPage = "/settings/group/" + groupToUpdate.getName() + (present(selTab) ? "?selTab=" + selTab : "");
		return new ExtendedRedirectView(settingsPage);
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

	public void setMailUtils(final MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public boolean isValidationRequired(final GroupSettingsPageCommand command) {
		// FIXME: why?
		return command.getContext().getLoginUser().isSpammer()
				&& (command.getContext().getLoginUser().getToClassify() == 0);
	}

	@Override
	public Validator<GroupSettingsPageCommand> getValidator() {
		return new Validator<GroupSettingsPageCommand>() {

			@Override
			public boolean supports(final Class<?> clazz) {
				return SettingsViewCommand.class.equals(clazz);
			}

			@Override
			public void validate(final Object target, final Errors errors) {
				Assert.notNull(target);
				final SettingsViewCommand command = (SettingsViewCommand) target;

				ValidationUtils.invokeValidator(new GroupValidator(), command.getGroup(), errors);
			}
		};
	}

}
