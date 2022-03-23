package org.bibsonomy.webapp.controller.ajax;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.importer.orcid.RestClient;
import org.bibsonomy.webapp.command.ajax.OrcidAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

@Getter
@Setter
public class OrcidAjaxController extends AjaxController implements MinimalisticController<OrcidAjaxCommand> {

    private RestClient client;

    public OrcidAjaxController() {
        this.client = new RestClient();
    }

    @Override
    public OrcidAjaxCommand instantiateCommand() {
        return new OrcidAjaxCommand();
    }

    @Override
    public View workOn(OrcidAjaxCommand command) {
        final String orcidId = command.getOrcidId();
        final String workId = command.getWorkId();
        final List<String> workIds = command.getWorkIds();

        if (!present(orcidId)) {
            return Views.ERROR;
        }

        String response = "";

        if (present(workId)) {
            response = this.client.getWorkDetails(orcidId, workId);
        }
        else if (present(workIds)) {
            response = this.client.getWorkDetailsBulk(orcidId, workIds);
        }
        else {
            response = this.client.getWorks(orcidId);
        }

        command.setResponseString(response);

        return Views.AJAX_JSON;
    }

}
