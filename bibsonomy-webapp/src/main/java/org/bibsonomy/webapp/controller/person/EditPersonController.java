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
package org.bibsonomy.webapp.controller.person;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.EditPersonCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;
import org.springframework.validation.Errors;

/**
 * Controller to edit person via AJAX.
 *
 * paths:
 * - /editPerson
 *
 * @author kchoong
 */
public class EditPersonController extends AbstractEditPersonController implements MinimalisticController<EditPersonCommand> {

    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private EditPersonDetailsController detailsController;
    private EditRelationController relationController;
    private MergePersonController mergeController;

    @Override
    public View workOn(EditPersonCommand command) {
        final RequestWrapperContext context = command.getContext();
        final PersonUpdateOperation operation = command.getUpdateOperation();
        if (!present(operation)) {
            return error(command, "person.edit.noOperation");
        }

        if (!present(command.getPersonId())) {
            return error(command, "person.edit.noPersonId");
        }

        if (!context.isValidCkey()) {
            // return error(command, "error.field.valid.ckey");
        }

        switch(operation) {
            case UPDATE_DETAILS:
                return this.detailsController.updateDetailsAction(command);
            case ADD_NAME:
                return this.detailsController.addNameAction(command);
            case DELETE_NAME:
                return this.detailsController.deleteNameAction(command);
            case SELECT_MAIN_NAME:
                return this.detailsController.setMainNameAction(command);
            case ADD_ROLE:
                this.relationController.addRoleAction(command);
            case DELETE_ROLE:
                this.relationController.deleteRoleAction(command);
            case UPDATE_ROLE:
                return this.relationController.editRoleAction(command);
            case LINK_USER:
                return this.relationController.linkAction(command);
            case UNLINK_USER:
                return this.relationController.unlinkAction(command);
            case MERGE_ACCEPT:
            case MERGE_DENIED:
                return this.mergeController.mergeAction(command);
            case MERGE_CONFLICTS:
                return this.mergeController.conflictMerge(command);
            case MERGE_GET_CONFLICTS:
                return this.mergeController.getConflicts(command);
        }

        return error(command, "No edit operation set.");
    }

    @Override
    public EditPersonCommand instantiateCommand() {
        return new EditPersonCommand();
    }

    public void setDetailsController(EditPersonDetailsController detailsController) {
        this.detailsController = detailsController;
    }

    public void setRelationController(EditRelationController relationController) {
        this.relationController = relationController;
    }

    public void setMergeController(MergePersonController mergeController) {
        this.mergeController = mergeController;
    }
}
