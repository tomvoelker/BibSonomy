package org.bibsonomy.webapp.controller.person;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.springframework.validation.Errors;

public class EditPersonController implements MinimalisticController<PersonPageCommand>, ErrorAware {

    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private LogicInterface logic;
    private Errors errors;

    @Override
    public View workOn(PersonPageCommand command) {

        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());

        final JSONObject jsonResponse = new JSONObject();

        try {
            this.logic.updatePerson(person, PersonUpdateOperation.UPDATE_ALL);
            jsonResponse.put("status", true);

        } catch (final Exception e) {
            log.error("error while updating person " + person.getPersonId(), e);
            jsonResponse.put("status", false);
            // TODO: set proper error message
            //jsonResponse.put("message", "Some error occured");
        }

        command.setResponseString(jsonResponse.toString());
        return Views.AJAX_JSON;
    }

    @Override
    public PersonPageCommand instantiateCommand() {
        return new PersonPageCommand();
    }

    @Override
    public Errors getErrors() {
        return errors;
    }

    @Override
    public void setErrors(Errors errors) {
        this.errors = errors;
    }
}
