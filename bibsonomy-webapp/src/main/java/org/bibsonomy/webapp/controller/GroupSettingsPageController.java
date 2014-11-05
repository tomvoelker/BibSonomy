package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.logic.LogicInterface;
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

	@Override
	public GroupSettingsPageCommand instantiateCommand() {
		return new GroupSettingsPageCommand();
	}

	@Override
	public View workOn(GroupSettingsPageCommand command) {
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

}
