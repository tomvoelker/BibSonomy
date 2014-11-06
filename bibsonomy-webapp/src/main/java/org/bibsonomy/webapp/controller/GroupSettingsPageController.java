package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.ValidationUtils;
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
			
			for (Group g : command.getLoggedinUser().getGroups())
				if (g.getName().equals(command.getRequestedGroup()))
					command.setGroup(g);
			
			// refresh the requested group
			if (present(command.getGroup()))
				command.getGroup().setUsers(this.logic.getUsers(null, GroupingEntity.GROUP,
						command.getGroup().getName(), null, null, null, null, null, 0, Integer.MAX_VALUE));
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
