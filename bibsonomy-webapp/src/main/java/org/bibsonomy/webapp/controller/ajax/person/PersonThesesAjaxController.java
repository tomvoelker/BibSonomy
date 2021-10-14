package org.bibsonomy.webapp.controller.ajax.person;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

public class PersonThesesAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    private LogicInterface logic;

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        return null;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }

    @Override
    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }
}
