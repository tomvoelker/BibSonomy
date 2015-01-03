package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import static org.bibsonomy.webapp.command.GroupSettingsPageCommand.CV_IDX;
import static org.bibsonomy.webapp.command.GroupSettingsPageCommand.MY_PROFILE_IDX;
import static org.bibsonomy.webapp.command.GroupSettingsPageCommand.USERS_IDX;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.view.Views;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageController implements MinimalisticController<GroupSettingsPageCommand> {
	
	private LogicInterface logic;

	@Override
	public GroupSettingsPageCommand instantiateCommand() {
		GroupSettingsPageCommand c = new GroupSettingsPageCommand();
		return c;
	}

	@Override
	public View workOn(GroupSettingsPageCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}
		
		if (present(command.getRequestedGroup())
				&& present(command.getContext().getLoginUser())) {
			
			command.setLoggedinUser(command.getContext().getLoginUser());
			
			command.setGroup(logic.getGroupDetails(command.getRequestedGroup()));
			User groupUser = logic.getUserDetails(command.getRequestedGroup());
			command.setRealname(groupUser.getRealname());
			command.setHomepage(groupUser.getHomepage());
			command.setDescription(command.getGroup().getDescription());
			command.setPrivlevel(command.getGroup().getPrivlevel().getPrivlevel());
			command.setSharedDocuments(command.getGroup().isSharedDocuments() ? 1 : 0);
			
			command.setUser(groupUser);
			
			// set the GroupMembership of the logged in user.
			if (present(command.getGroup())) {
				GroupMembership m = command.getGroup()
						.getGroupMembershipForUser(command.getLoggedinUser());
				command.setGroupMembership(m);
				if (m.getGroupRole() == GroupRole.ADMINISTRATOR)
					command.addTab(USERS_IDX, "navi.groupsettings");
				command.addTab( MY_PROFILE_IDX, "navi.myprofile");
//				if (m.getGroupRole() == GroupRole.ADMINISTRATOR || m.getGroupRole() == GroupRole.MODERATOR)
//					command.addTab(CV_IDX, "navi.cvedit");
			}
		}
		
		return Views.GROUPSETTINGSPAGE;
	}

	public LogicInterface getLogic() {
		return logic;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

}
