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
			errors.reject("error.field.valid.ckey");
		}

		Group groupToUpdate = null;
		// since before requesting a group, it must not exist, we cannot check for it, either.
		groupToUpdate = this.logic.getGroupDetails(command.getGroupname());
		
		// TODO: Clean this up.
		final GroupUpdateOperation operation = command.getOperation();
		if (present(operation)) {
			final User loginUser = context.getLoginUser();
			switch (operation) {
				case ADD_INVITED: {
					// sent an invite
					final String username = command.getUsername();
					if (present(username) && !username.equals(groupToUpdate.getName())) {
						
						// TODO: inform the user about the invite
						final User invitedUser = this.logic.getUserDetails(username);
						final GroupMembership ms = new GroupMembership(invitedUser, null, false);
						try {
							// since now only one user can be invited to a group at once
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.ADD_INVITED, ms);
							mailUtils.sendGroupInvite(groupToUpdate.getName(), command.getLoggedinUser(), invitedUser, requestLogic.getLocale());
						} catch (final Exception ex) {
							log.error("error while inviting user '" + username + "' to group '" + groupToUpdate + "'", ex);
							// if a user can't be added to a group, this exception is thrown
							this.errors.rejectValue("username", "settings.group.error.inviteUserToGroupFailed", new Object[]{username, groupToUpdate},
									"The User {0} couldn't be invited to the Group {1}.");
						}
					}
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
							// if a user can't be added to a group, this exception is thrown
							this.errors.rejectValue("username", "settings.group.error.addUserToGroupFailed", new Object[]{username, groupToUpdate},
									"The User {0} couldn't be added to the Group {1}.");
						}
					}
					break;
				}
				case REMOVE_MEMBER: {
					/*
					 * remove the user from the group
					 */
					final String username = command.getUsername();
					if (present(username) && !username.equals(groupToUpdate)) {
						final GroupMembership ms = new GroupMembership(new User(username), null, false);
						try {
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.REMOVE_MEMBER, ms);
							
							// if we removed ourselves from the group, return the homepage.
							if (loginUser.getName().equals(username)) {
								return new ExtendedRedirectView(SETTINGS_GROUP_TAB_REDIRECT);
							}
						} catch (final Exception ex) {
							log.error("error while removing user '" + username + "' from group '" + groupToUpdate + "'", ex);
							// if a user can't be added to a group, this exception is thrown
							this.errors.reject("settings.group.error.removeUserFromGroupFailed", new Object[]{username, groupToUpdate},
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
					groupToUpdate.setPublicationReportingSettings(command.getGroup().getPublicationReportingSettings());
					this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.UPDATE_GROUP_REPORTING_SETTINGS, null);
					break;
				}
				case UPDATE_SETTINGS: {
					/*
					 *  the group properties to update
					 */
//					throw new UnsupportedOperationException("not yet implemented");
					final Privlevel priv = Privlevel.getPrivlevel(command.getPrivlevel());
					final boolean sharedDocs = command.getSharedDocuments() == 1;
					final String realname = command.getRealname();
					final URL homepage = command.getHomepage();
//					final String description = command.getDescription();
//					log.error(realname + " " + description);
					
					User groupUserToUpdate = this.logic.getUserDetails(groupToUpdate.getName());
					groupUserToUpdate.setEmail("nomail"); // TODO: remove
					// the group to update
					try {
						groupToUpdate.setPrivlevel(priv);
						groupToUpdate.setSharedDocuments(sharedDocs);
						
						if (present(realname))
							groupUserToUpdate.setRealname(realname);
						if (present(homepage))
							groupUserToUpdate.setHomepage(homepage);
//						groupToUpdate.setDescription(description);
						
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
						final User declineUser = this.logic.getUserDetails(username);
						final GroupMembership ms = new GroupMembership(declineUser, null, false);
						try {
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.DECLINE_JOIN_REQUEST, ms);
							// TODO: I18N
							mailUtils.sendJoinGroupDenied(groupToUpdate.getName(), username, declineUser.getEmail(), "Your group join request was denied.", requestLogic.getLocale());
						} catch (final Exception ex) {
							log.error("error while declining the join request of user '" + username + "' from group '" + groupToUpdate + "'", ex);
							this.errors.rejectValue("username", "settings.group.error.declineJoinRequestFailed", new Object[]{username},
									"The request of User {0} couldn't be removed.");
						}
					}
					break;
				}
				case REMOVE_INVITED: {
					final String username = command.getUsername();
					if (present(username)) {
						final GroupMembership ms = new GroupMembership(new User(username), null, false);
						try {
							this.logic.updateGroup(groupToUpdate, GroupUpdateOperation.REMOVE_INVITED, ms);
							return new ExtendedRedirectView(SETTINGS_GROUP_TAB_REDIRECT);
						} catch (final Exception ex) {
							log.error("error while removing the invite of user '" + username + "' from group '" + groupToUpdate + "'", ex);
							this.errors.rejectValue("username", "settings.group.error.removeInviteFailed", new Object[]{username},
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
		// TODO: use url generator
		return new ExtendedRedirectView("/settings/group/" + groupToUpdate.getName());
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	public void setMailUtils(MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	@Override
	public boolean isValidationRequired(GroupSettingsPageCommand command) {
		// FIXME: why?
		return command.getContext().getLoginUser().isSpammer()
				&& command.getContext().getLoginUser().getToClassify() == 0;
	}

	@Override
	public Validator<GroupSettingsPageCommand> getValidator() {
		return new Validator<GroupSettingsPageCommand>() {

			@Override
			public boolean supports(Class<?> clazz) {
				return SettingsViewCommand.class.equals(clazz);
			}

			@Override
			public void validate(Object target, Errors errors) {
				Assert.notNull(target);
				final SettingsViewCommand command = (SettingsViewCommand) target;
				
				ValidationUtils.invokeValidator(new GroupValidator(), command.getGroup(), errors);
			}
		};
	}

}
