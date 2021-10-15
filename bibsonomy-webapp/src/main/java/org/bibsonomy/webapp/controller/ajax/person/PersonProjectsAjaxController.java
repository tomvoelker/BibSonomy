package org.bibsonomy.webapp.controller.ajax.person;

import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

public class PersonProjectsAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        return null;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }
}