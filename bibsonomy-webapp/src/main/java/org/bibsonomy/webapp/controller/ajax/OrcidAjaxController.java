/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.importer.orcid.OrcidRestClient;
import org.bibsonomy.webapp.command.ajax.OrcidAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;

@Getter
@Setter
public class OrcidAjaxController extends AjaxController implements MinimalisticController<OrcidAjaxCommand> {

    private OrcidRestClient client;
    private boolean orcidImportEnabled;

    public OrcidAjaxController() {
        this.client = new OrcidRestClient();
    }

    @Override
    public OrcidAjaxCommand instantiateCommand() {
        return new OrcidAjaxCommand();
    }

    @Override
    public View workOn(OrcidAjaxCommand command) {
        if (!orcidImportEnabled) {
            return this.error(command, "error.503");
        }

        final String orcidId = command.getOrcidId();
        final String workId = command.getWorkId();
        final List<String> workIds = command.getWorkIds();

        if (!present(orcidId)) {
            return this.error(command, "post_bibtex.orcid.action.error.invalidId");
        }

        // Get details for a single work
        if (present(workId)) {
            return this.handleWorkDetails(command, orcidId, workId);
        }

        // Get details for multiple works
        if (present(workIds)) {
            return this.handleWorkDetailsBulk(command, orcidId, workIds);
        }

        // Default: Get list of work summary
        return this.handleWorks(command, orcidId);
    }

    private View handleWorks(final OrcidAjaxCommand command, final String orcidId) {
        String response = this.client.getWorks(orcidId);
        return this.checkAndRespond(command, response);
    }

    private View handleWorkDetails(final OrcidAjaxCommand command, final String orcidId, final String workId) {
        String response = this.client.getWorkDetails(orcidId, workId);
        return this.checkAndRespond(command, response);
    }

    private View handleWorkDetailsBulk(final OrcidAjaxCommand command, final String orcidId, final List<String> workIds) {
        String response = this.client.getWorkDetailsBulk(orcidId, workIds);
        return this.checkAndRespond(command, response);
    }

    private View checkAndRespond(final OrcidAjaxCommand command, final String response) {
        if (present(response)) {
            return this.success(command, response);
        }

        return this.error(command, "post_bibtex.orcid.action.error.connection");
    }

    private View success(final OrcidAjaxCommand command, final String successMsg) {
        final JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", successMsg);

        command.setResponseString(response.toString());
        return Views.AJAX_JSON;
    }

    private View error(final OrcidAjaxCommand command, final String errorMsg) {
        final JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("error", errorMsg);

        command.setResponseString(response.toString());
        return Views.AJAX_JSON;
    }

}
