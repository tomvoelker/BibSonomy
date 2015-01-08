package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupRole;
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
 *
 * @author niebler
 */
public class GroupSettingsPageController implements MinimalisticController<GroupSettingsPageCommand> {
	
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
		
		if (!present(command.getRequestedGroup())) {
			throw new MalformedURLSchemeException("group settings without requested group");
		}
		
		final User loginUser = command.getContext().getLoginUser();
		command.setLoggedinUser(loginUser);
		final Group group = logic.getGroupDetails(command.getRequestedGroup());
		if (!present(group)) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
		
		command.setGroup(group);
		
		final GroupMembership groupMembership = group.getGroupMembershipForUser(loginUser);
		GroupRole roleOfLoggedinUser = groupMembership.getGroupRole();
		if (!present(groupMembership) || roleOfLoggedinUser.isPendingRole()) {
			throw new AccessDeniedException("You are not a member of this group.");
		}
		command.setGroupMembership(groupMembership);
		
		// TODO: should only the admin get this information?
		final User groupUser = this.logic.getUserDetails(command.getRequestedGroup());
		command.setRealname(groupUser.getRealname());
		command.setHomepage(groupUser.getHomepage());
		if (present(command.getGroup())) {
			command.setDescription(command.getGroup().getDescription());
			command.setPrivlevel(command.getGroup().getPrivlevel().getPrivlevel());
			command.setSharedDocuments(command.getGroup().isSharedDocuments() ? 1 : 0);
		}
		command.setUser(groupUser);
		
		switch (roleOfLoggedinUser) {
		case ADMINISTRATOR:
			command.addTab(GroupSettingsPageCommand.USERS_IDX, "navi.groupsettings");
			//$FALL-THROUGH$ admin should also be able to config profile
		case MODERATOR:
			command.addTab(GroupSettingsPageCommand.MY_PROFILE_IDX, "navi.myprofile");
			// TODO: adapt cv wiki handling
			// command.addTab(CV_IDX, "navi.cvedit");
			//$FALL-THROUGH$ all users should see the member list
		default:
			command.addTab(GroupSettingsPageCommand.MEMBER_LIST_IDX, "settings.group.memberList");
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
