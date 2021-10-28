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

import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;

/**
 * Controller to handle all requests to edit person details,
 * such as names, e-mail, homepage, etc.
 *
 * @author kchoong
 */
public class EditPersonDetailsController {
    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private LogicInterface logic;

    /**
     * action called when a user updates preferences of a person
     * @param command
     */
    protected View updateAction(PersonPageCommand command) {
        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getFormPersonId());

        final Person commandPerson = command.getPerson();
        if (!present(commandPerson)) {
            // FIXME: proper frontend responses in cases like this
            throw new NoSuchElementException();
        }

        final PersonUpdateOperation operation = command.getUpdateOperation();
        final JSONObject jsonResponse = new JSONObject();

        // FIXME: why do we have to copy all values from the command person to the person found in the logic?
        // set all attributes that might be updated
        person.setAcademicDegree(commandPerson.getAcademicDegree());
        person.setOrcid(commandPerson.getOrcid().replaceAll("-", ""));
        person.setResearcherid(commandPerson.getResearcherid().replaceAll("-", ""));
        person.setCollege(commandPerson.getCollege());

        // TODO only allow updates if the editor "is" this person
        person.setEmail(commandPerson.getEmail());
        person.setHomepage(commandPerson.getHomepage());

        // FIXME: write independent update method
        // FIXME: add its me action

        //command.getPerson().getMainName().setMain(false);
        //command.getPerson().setMainName(Integer.parseInt(command.getFormSelectedName()));

        try {
            this.logic.updatePerson(person, operation);
            jsonResponse.put("status", true);

        } catch (final Exception e) {
            log.error("error while updating person " + commandPerson.getPersonId(), e);
            jsonResponse.put("status", false);
            // TODO: set proper error message
            //jsonResponse.put("message", "Some error occured");
        }

        command.setResponseString(jsonResponse.toString());
        return Views.AJAX_JSON;
    }

    /**
     * Action called when a user adds an alternative name to a person
     * @param command
     */
    private View addNameAction(PersonPageCommand command) {
        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());

        final JSONObject jsonResponse = new JSONObject();

        if (!present(person) || !present(command.getNewName())) {
            jsonResponse.put("status", false);
            // TODO: set proper error message
            command.setResponseString(jsonResponse.toString());
            return Views.AJAX_JSON;
        }

        final PersonName personName = command.getNewName();
        personName.setPersonId(command.getPerson().getPersonId());
        for (PersonName otherName : person.getNames()) {
            if (personName.equals(otherName)) {
                jsonResponse.put("status", true);
                jsonResponse.put("personNameChangeId", otherName.getPersonNameChangeId());
                command.setResponseString(jsonResponse.toString());
                return Views.AJAX_JSON;
            }
        }

        try {
            this.logic.createPersonName(personName);
        } catch (Exception e) {
            jsonResponse.put("status", false);
            // TODO: set proper error message
            //jsonResponse.put("message", "Some error occured");
            command.setResponseString(jsonResponse.toString());
            return Views.AJAX_JSON;
        }

        jsonResponse.put("status", true);
        jsonResponse.put("personNameChangeId", personName.getPersonNameChangeId());
        command.setResponseString(jsonResponse.toString());
        return Views.AJAX_JSON;
    }

    /**
     * Action called when a user removes an alternative name from a person
     * @param command
     * @return
     */
    private View deleteNameAction(PersonPageCommand command) {
        final JSONObject jsonResponse = new JSONObject();
        try {
            this.logic.removePersonName(new Integer(command.getFormPersonNameId()));
        } catch (Exception e) {
            jsonResponse.put("status", false);
            // TODO: set proper error message
            //jsonResponse.put("message", "Some error occured");
            command.setResponseString(jsonResponse.toString());
            return Views.AJAX_JSON;
        }

        jsonResponse.put("status", true);
        command.setResponseString(jsonResponse.toString());
        return Views.AJAX_JSON;
    }

    private View setMainNameAction(PersonPageCommand command) {
        final Person person = logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());

        final JSONObject jsonResponse = new JSONObject();


        person.getMainName().setMain(false);
        person.setMainName(Integer.parseInt(command.getFormSelectedName()));

        // bind the new person
        command.setPerson(person);

        try {
            this.logic.updatePerson(person, PersonUpdateOperation.UPDATE_NAMES);
        } catch (final Exception e) {
            jsonResponse.put("status", false);
            // TODO: set proper error message
            //jsonResponse.put("message", "Some error occured");
            command.setResponseString(jsonResponse.toString());
            return Views.AJAX_JSON;
        }

        jsonResponse.put("status", true);
        command.setResponseString(jsonResponse.toString());

        return Views.AJAX_JSON;
    }

    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }
}
