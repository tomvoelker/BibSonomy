package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * TODO: add documentation
 *
 * @author niebler
 */
public class GroupSettingsPageController implements MinimalisticController<GroupSettingsPageCommand> {
	private static final Log log = LogFactory.getLog(GroupSettingsPageController.class);
	
	private LogicInterface logic;

	@Override
	public GroupSettingsPageCommand instantiateCommand() {
		return new GroupSettingsPageCommand();
	}

	@Override
	public View workOn(GroupSettingsPageCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}
		
		final String requestedGroup = command.getRequestedGroup();
		if (!present(requestedGroup)) {
			throw new MalformedURLSchemeException("group settings without requested group");
		}
		
		final User loginUser = command.getContext().getLoginUser();
		command.setLoggedinUser(loginUser);
		final Group group = this.logic.getGroupDetails(requestedGroup);
		if (!present(group)) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
		
		command.setGroup(group);
		
		GroupMembership groupMembership = group.getGroupMembershipForUser(loginUser);
		switch (group.getPrivlevel()) {
			case HIDDEN:
				if (!present(groupMembership) || present(groupMembership) && !groupMembership.getGroupRole().equals(GroupRole.ADMINISTRATOR)) {
					throw new AccessDeniedException("You are not allowed to view this page");
				}
				break;
			case MEMBERS:
				if (!present(groupMembership)) {
					throw new AccessDeniedException("You are not a member of this group.");
				}
				break;
			default:
				if (!present(groupMembership)) {
					// set non-privileged user
					groupMembership = new GroupMembership(loginUser, GroupRole.USER, true);
				}
		}
		
		final GroupRole roleOfLoggedinUser = groupMembership.getGroupRole();
		command.setGroupMembership(groupMembership);
		
		// TODO: should only the admin get this information?
		final User groupUser = this.logic.getUserDetails(requestedGroup);
		command.setRealname(groupUser.getRealname());
		command.setHomepage(groupUser.getHomepage());
		if (present(command.getGroup())) {
			command.setDescription(command.getGroup().getDescription());
			command.setPrivlevel(command.getGroup().getPrivlevel().getPrivlevel());
			command.setSharedDocuments(command.getGroup().isSharedDocuments() ? 1 : 0);
		}
		command.setUser(groupUser);
		
		// determine which tabs to show based on the role of the logged in user
		switch (roleOfLoggedinUser) {
		case ADMINISTRATOR:
			command.addTab(GroupSettingsPageCommand.GROUP_SETTINGS, "navi.groupsettings");
			command.addTab(GroupSettingsPageCommand.MEMBER_LIST_IDX, "settings.group.memberList");
			// TODO: adapt cv wiki handling
			// command.addTab(CV_IDX, "navi.cvedit");
			command.setSelTab(GroupSettingsPageCommand.GROUP_SETTINGS);
			break;
		case MODERATOR:
			//$FALL-THROUGH$ all users should see the member list
		default:
			command.addTab(GroupSettingsPageCommand.MEMBER_LIST_IDX, "settings.group.memberList");
			command.setSelTab(GroupSettingsPageCommand.MEMBER_LIST_IDX);
			break;
		}
		
		
		return Views.GROUPSETTINGSPAGE;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
