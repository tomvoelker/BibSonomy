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
import org.bibsonomy.common.enums.PersonOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.EditPersonCommand;
import org.bibsonomy.webapp.util.View;

/**
 * Controller to handle all requests to edit person details,
 * such as names, e-mail, homepage, etc.
 *
 * @author kchoong
 */
public class EditPersonDetailsController extends AbstractEditPersonController {
    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private LogicInterface logic;

    /**
     * action called when a user updates preferences of a person
     * @param command
     *
     * @return the ajax json response
     */
    protected View updateDetailsAction(final EditPersonCommand command) {
        Person updatedPerson = command.getPerson();
        if (!present(updatedPerson)) {
            return error(command, "person.error.noUpdates");
        }

        Person person = fillPersonFromDB(command, updatedPerson);

        // Check if person exists
        if (!present(person)) {
            return errorPersonNotFound(command);
        }

        try {
            this.logic.updatePerson(person, PersonOperation.UPDATE_DETAILS);
            return success(command, "The person has been successfully updated!");
        } catch (final Exception e) {
            log.error("error while updating person " + updatedPerson.getPersonId(), e);
            return error(command, "person.error.fail.details");
        }
    }

    /**
     * Action called when a user adds an alternative name to a person
     * @param command
     *
     * @return the ajax json response
     */
    protected View addNameAction(final EditPersonCommand command) {
        Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPersonId());

        // Check if person exists
        if (!present(person)) {
            return errorPersonNotFound(command);
        }

        // Check if a person name is given
        if (!present(command.getPersonName())) {
            return errorPersonNameNotFound(command);
        }

        // Adding name to person
        try {
            if (person.addName(command.getPersonName())) {
                this.logic.updatePerson(person, PersonOperation.UPDATE_NAMES);
                return success(command, "New name has been added!");
            } else {
                return error(command, "person.error.nameExists");
            }
        } catch (Exception e) {
            return error(command, "person.error.fail.names");
        }
    }

    /**
     * Action called when a user removes an alternative name from a person
     * @param command
     *
     * @return the ajax json response
     */
    protected View deleteNameAction(final EditPersonCommand command) {
        Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPersonId());

        // Check if person exists
        if (!present(person)) {
            return errorPersonNotFound(command);
        }

        // Check if a person name is given
        if (!present(command.getPersonName())) {
            return errorPersonNameNotFound(command);
        }

        try {
            if (person.removeName(command.getPersonName())) {
                this.logic.updatePerson(person, PersonOperation.UPDATE_NAMES);
                return success(command, "The name has been successfully deleted!");
            } else {
                return error(command, "person.error.nameNotFound");
            }
        } catch (Exception e) {
            return error(command, "person.error.fail.deleteName");
        }
    }

    /**
     * Action called when a user sets a new main name for a person
     * @param command
     *
     * @return the ajax json response
     */
    protected View setMainNameAction(final EditPersonCommand command) {
        Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPersonId());

        // Check if person exists
        if (!present(person)) {
            return errorPersonNotFound(command);
        }

        // Check if a person name is given
        if (!present(command.getPersonName())) {
            return errorPersonNameNotFound(command);
        }

        if (present(person.getMainName())) {
            person.getMainName().setMain(false);
        }
        person.setMainName(command.getPersonName());

        try {
            this.logic.updatePerson(person, PersonOperation.UPDATE_NAMES);
            return success(command, "Main name has been successfully updated!");
        } catch (final Exception e) {
            return error(command, "person.error.fail.mainName");
        }
    }

    /**
     * Gets the person from the database and replaces their fields with the updated version.
     * (Not updated in the Database yet though, just object)
     *
     * @param command
     * @param updatedPerson
     * @return person from the database with updated fields
     */
    private Person fillPersonFromDB(final EditPersonCommand command, final Person updatedPerson) {
        Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPersonId());

        if (!present(person)) {
            return null;
        }

        // set all attributes that might be updated
        person.setAcademicDegree(updatedPerson.getAcademicDegree());
        if (present(updatedPerson.getOrcid())) {
            person.setOrcid(updatedPerson.getOrcid().replaceAll("-", ""));
        }
        if (present(updatedPerson.getResearcherid())) {
            person.setResearcherid(updatedPerson.getResearcherid().replaceAll("-", ""));
        }
        person.setCollege(updatedPerson.getCollege());

        // TODO only allow updates if the editor "is" this person
        person.setEmail(updatedPerson.getEmail());
        person.setHomepage(updatedPerson.getHomepage());

        return person;
    }

    protected View errorPersonNameNotFound(final EditPersonCommand command) {
        return error(command, "person.error.noName");
    }

    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }
}
