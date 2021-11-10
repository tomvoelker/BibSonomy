package org.bibsonomy.webapp.controller.person;

import org.bibsonomy.webapp.command.actions.EditPersonCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;

/**
 * @author kchoong
 */
public class AbstractEditPersonController {

    protected View success(EditPersonCommand command, String successMsg) {
        final JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", successMsg);

        command.setResponseString(response.toString());
        return Views.AJAX_JSON;
    }

    protected View error(EditPersonCommand command, String errorMsg) {
        final JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("error", errorMsg);

        command.setResponseString(response.toString());
        return Views.AJAX_JSON;
    }

    protected View errorPersonNotFound(EditPersonCommand command) {
        return error(command, "Person with ID: " + command.getPersonId() + "not found.");
    }

}
