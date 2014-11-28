package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.UpdateGroupCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 *
 * @author ema
 */
public class UpdateGroupController implements MinimalisticController<UpdateGroupCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(UpdateGroupController.class);
	private Errors errors = null;
	private LogicInterface logic;

	@Override
	public UpdateGroupCommand instantiateCommand() {
		return new UpdateGroupCommand();
	}

	@Override
	public View workOn(final UpdateGroupCommand command) {
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

		final String groupName = command.getGroupName();

		if (present(command.getOperation())) {
			switch (command.getOperation()) {
				case REQUEST: {
					// get the request
					final Group requestedGroup = command.getGroup();
					if (present(requestedGroup)) {
						if (!present(requestedGroup.getName())) {
							this.errors.reject("settings.group.error.requestGroupFailed");
						}
						if (!present(requestedGroup.getDescription())) {
							this.errors.reject("settings.group.error.requestGroupFailed");
						}
						if (!present(requestedGroup.getGroupRequest().getReason())) {
							this.errors.reject("settings.group.error.requestGroupFailed");
						}
						if (!this.errors.hasErrors()) {
							// set the username and create the request
							requestedGroup.getGroupRequest().setUserName(command.getContext().getLoginUser().getName());
							this.logic.createGroup(requestedGroup);
						}
					}
					// do set new settings here
					break;
				}
				case ADD_INVITED: {
					// sent an invite
					final String username = command.getUsername();
					if (present(username) && !username.equals(groupName)) {
						// the group
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						try {
							// since now only one user can be invited to a group at once
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ADD_INVITED);
						} catch (final Exception ex) {
							log.error("error while inviting user '" + username + "' to group '" + groupName + "'", ex);
							// if a user can't be added to a group, this exception is thrown
							this.errors.rejectValue("username", "settings.group.error.inviteUserToGroupFailed", new Object[]{username, groupName},
									"The User {0} couldn't be invited to the Group {1}.");
						}
					}
					break;
				}
				case ADD_NEW_USER: {
					/*
					 * add a new user to the group
					 */
					final String username = command.getUsername();
					if (present(username) && !username.equals(groupName)) {
						// the group to update
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						try {
							// since now only one user can be added to a group at once
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ADD_NEW_USER);
						} catch (final Exception ex) {
							log.error("error while adding user '" + username + "' to group '" + groupName + "'", ex);
							// if a user can't be added to a group, this exception is thrown
							this.errors.rejectValue("username", "settings.group.error.addUserToGroupFailed", new Object[]{username, groupName},
									"The User {0} couldn't be added to the Group {1}.");
						}
					}
					break;
				}
				case REMOVE_USER: {
					/*
					 * remove the user from the group
					 *
					 * TODO: not fully migrated yet, see {@link SettingsHandler}
					 */
					final String username = command.getUsername();
					if (present(username) && !username.equals(groupName)) {
						// the group to update
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						try {
							// since now only one user can be added to a group at once
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.REMOVE_USER);
						} catch (final Exception ex) {
							log.error("error while removing user '" + username + "' from group '" + groupName + "'", ex);
							// if a user can't be added to a group, this exception is thrown
							this.errors.reject("settings.group.error.removeUserFromGroupFailed", new Object[]{username, groupName},
									"The User {0} couldn't be removed from the Group {1}.");
						}
					}
					break;
				}
				case UPDATE_GROUP_REPORTING_SETTINGS: {
					/*
					 * update the reporting settings
					 */
					// the group to update
					final Group groupToUpdate = this.logic.getGroupDetails(groupName);
					groupToUpdate.setPublicationReportingSettings(command.getGroup().getPublicationReportingSettings());
					this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_GROUP_REPORTING_SETTINGS);
					break;
				}
				case UPDATE_SETTINGS: {
					/*
					 *  the group properties to update
					 */
					final Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
					final boolean sharedDocs = command.getSharedDocuments() == 1;
					// the group to update
					final Group groupToUpdate = this.logic.getGroupDetails(groupName);
					groupToUpdate.setPrivlevel(priv);
					groupToUpdate.setSharedDocuments(sharedDocs);
					try {
						this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_SETTINGS);
					} catch (final Exception ex) {
						log.error("error while updating settings for group '" + groupName + "'", ex);
						// TODO: what exceptions can be thrown?!
					}
					break;
				}
				case ACCEPT_JOIN_REQUEST: {
					final String username = command.getUsername();
					if (present(username)) {
						// the group to update
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						try {
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ACCEPT_JOIN_REQUEST);
						} catch (final Exception ex) {
							log.error("error while accepting the join request of user '" + username + "' from group '" + groupName + "'", ex);
							this.errors.rejectValue("username", "settings.group.error.acceptJoinRequestFailed", new Object[]{username, groupName},
									"The User {0} couldn't be added to the Group {1}.");
						}
					}
					break;
				}
				case DECLINE_JOIN_REQUEST: {
					final String username = command.getUsername();
					if (present(username)) {
						// the group to update
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						try {
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.DECLINE_JOIN_REQUEST);
						} catch (final Exception ex) {
							log.error("error while accepting the join request of user '" + username + "' from group '" + groupName + "'", ex);
							this.errors.rejectValue("username", "settings.group.error.declineJoinRequestFailed", new Object[]{username},
									"The request of User {0} couldn't be removed.");
						}
					}
					break;
				}
				case UPDATE_GROUPROLE: {
					final String username = command.getUsername();
					if (!present(command.getGroup()) || !present(command.getGroup().getGroupRole())) {
						this.errors.reject("settings.group.error.changeGroupRoleFailed", username);						
					}
					if (!this.errors.hasErrors()) {
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						groupToUpdate.setGroupRole(command.getGroup().getGroupRole());
						try {
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_GROUPROLE);
						} catch (final Exception ex) {
							log.error("error while changing the the role of user '" + username + "' from group '" + groupName + "'", ex);
						}
					}					
					break;
				}
				case REMOVE_INVITED: {
					final String username = command.getUsername();
					if (present(username)) {
						// the group to update
						final Group groupToUpdate = this.logic.getGroupDetails(groupName);
						try {
							groupToUpdate.setUsers(Collections.singletonList(new User(username)));
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.REMOVE_INVITED);
						} catch (final Exception ex) {
							log.error("error while removing the invite of user '" + username + "' from group '" + groupName + "'", ex);
							this.errors.rejectValue("username", "settings.group.error.removeInviteFailed", new Object[]{username},
									"The invite of User {0} couldn't be removed.");
						}
					}
					break;
				}
				default:
					errors.reject("error.invalid_parameter");
					break;
			}
		} else {
			this.errors.reject("error.invalid_parameter");
		}

		if (errors.hasErrors()) {
//			command.setSelTab(SettingsViewCommand.GROUP_IDX);
//			return super.workOn(command);
		}

		// success: go back where you've come from
		// TODO: inform the user about the success!
		return Views.GROUPSETTINGSPAGE;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public LogicInterface getLogic() {
		return logic;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
