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

@Getter
@Setter
public class OrcidAjaxController extends AjaxController implements MinimalisticController<OrcidAjaxCommand> {

    private OrcidRestClient client;

    public OrcidAjaxController() {
        this.client = new OrcidRestClient();
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
        } else if (present(workIds)) {
            response = this.client.getWorkDetailsBulk(orcidId, workIds);
        } else {
            response = this.client.getWorks(orcidId);
        }

        command.setResponseString(response);

        return Views.AJAX_JSON;
    }

}
