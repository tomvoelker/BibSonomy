package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.logic.LogicInterface;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

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
		
		if (present(command.getRequestedGroup())
				&& present(command.getContext().getLoginUser())) {
			
			command.setLoggedinUser(command.getContext().getLoginUser());
			
			command.setGroup(logic.getGroupDetails(command.getRequestedGroup()));
			
			command.setUser(logic.getUserDetails(command.getRequestedGroup()));
			
			// set the GroupMembership of the logged in user.
			if (present(command.getGroup())) {
				command.setGroupMembership(command.getGroup()
						.getGroupMembershipForUser(command.getLoggedinUser()));
			}
		}
		
		switch(command.getSelTab()) {
			case 0:
				break;
			case 1:
				break;
			case 5:
				break;
			default:
				break;
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
