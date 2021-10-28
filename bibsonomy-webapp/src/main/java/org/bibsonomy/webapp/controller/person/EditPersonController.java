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
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;
import org.springframework.validation.Errors;

public class EditPersonController implements MinimalisticController<PersonPageCommand>, ErrorAware {

    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private LogicInterface logic;
    private Errors errors;

    private EditPersonDetailsController detailsController;
    private EditRelationController relationController;
    private MergePersonController mergeController;

    @Override
    public View workOn(PersonPageCommand command) {

        final RequestWrapperContext context = command.getContext();
        final String formAction = command.getFormAction();
        final boolean action = present(formAction);
        if (!action && !present(command.getRequestedPersonId())) {
            throw new MalformedURLSchemeException("The person page was requested without a person in the request.");
        }


        if (present(formAction)) {
            if (!context.isValidCkey()) {
                errors.reject("error.field.valid.ckey");
            }

            switch(formAction) {
                case "update":
                    return this.detailsController.updateAction(command);
                case "link":
                    return this.relationController.linkAction(command);
                case "unlink":
                    return this.relationController.unlinkAction(command);
                case "addRole":
                    this.relationController.addRoleAction(command);
                case "addThesis":
                    this.relationController.addRoleAction(command);
                case "editRole":
                    this.relationController.editRoleAction(command);
                case "deleteRole":
                    this.relationController.deleteRoleAction(command);
                case "search":
                    this.relationController.searchAction(command);
                case "searchPub":
                    this.relationController.searchPubAction(command);
                case "searchPubAuthor":
                    this.relationController.searchPubAuthorAction(command);
                case "merge":
                    this.mergeController.mergeAction(command);
                case "conflictMerge":
                    this.mergeController.conflictMerge(command);
                case "getConflict":
                    this.mergeController.getConflicts(command);
            }
        }

        final JSONObject jsonResponse = new JSONObject();
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

    public void setLogic(LogicInterface logic) {
        this.logic = logic;
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
