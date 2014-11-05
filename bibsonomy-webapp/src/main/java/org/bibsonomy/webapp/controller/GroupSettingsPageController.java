package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageController implements MinimalisticController<GroupSettingsPageCommand> {
	
	private LogicInterface logic;
	private RequestLogic requestLogic;
	private User user;

	@Override
	public GroupSettingsPageCommand instantiateCommand() {
		return new GroupSettingsPageCommand();
	}

	@Override
	public View workOn(GroupSettingsPageCommand command) {
		if (ValidationUtils.present(command.getRequestedGroup())
				&& ValidationUtils.present(requestLogic.getLoginUser())) {
			command.setLoggedinUser(requestLogic.getLoginUser());
			for (Group g : command.getLoggedinUser().getGroups())
				if (g.getName().equals(command.getRequestedGroup()))
					command.setGroup(g);
			
			// refresh the requested group
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

	public RequestLogic getRequestLogic() {
		return requestLogic;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
