package org.bibsonomy.webapp.controller.ajax.person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.controller.ajax.PersonPublicationAjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

public class PersonProjectsAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    private static final Log LOG = LogFactory.getLog(PersonPublicationAjaxController.class);

    private LogicInterface adminLogic;

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        return null;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }
}