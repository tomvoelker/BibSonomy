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
package org.bibsonomy.webapp.command.actions;

import java.util.List;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.webapp.command.PersonPageCommand;

/**
 * Command for editing a person.
 *
 * @author kchoong
 */
public class EditPersonCommand extends PersonPageCommand {

    private String personId;
    private boolean claimedPerson;
    private String editAction;
    private PersonUpdateOperation updateOperation;

    /** new name entry */
    private PersonName newName;
    private String firstName;
    private String lastName;
    private String selectedName;

    /** Properties if deleting or adding relations */
    private String type;
    private String interhash;
    private String index;

    @Deprecated // TODO: bind person directly
    private String formResourceHash;
    @Deprecated // TODO: bind person directly
    private PersonResourceRelationType formPersonRole;

    @Deprecated // TODO: bind person dier rectly
    private String formThesisId;
    @Deprecated // TODO: bind person directly
    private String formPersonNameId;
    @Deprecated // TODO: bind person directly
    private List<String> formPersonRoles;
    @Deprecated // TODO: bind person directly
    private String formRequestType;
    @Deprecated // TODO: bind person directly
    private String formResourcePersonRelationId;
    @Deprecated // TODO: bind person directly
    private String formInterHash;
    @Deprecated // TODO: bind person directly
    private String formIntraHash;
    @Deprecated // TODO: bind person directly
    private boolean formThatsMe;
    @Deprecated // TODO: bind person directly
    private int formPersonIndex = -1;

    private int formMatchId;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public boolean isClaimedPerson() {
        return claimedPerson;
    }

    public void setClaimedPerson(boolean claimedPerson) {
        this.claimedPerson = claimedPerson;
    }

    public PersonName getNewName() {
        return newName;
    }

    public void setNewName(PersonName newName) {
        this.newName = newName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    /**
     * @return the updateOperation
     */
    public PersonUpdateOperation getUpdateOperation() {
        return this.updateOperation;
    }

    /**
     * @param updateOperation the updateOperation to set
     */
    public void setUpdateOperation(PersonUpdateOperation updateOperation) {
        this.updateOperation = updateOperation;
    }

    /**
     * @return the editAction
     */
    public String getEditAction() {
        return editAction;
    }

    /**
     * @param editAction the editAction to set
     */
    public void setEditAction(String editAction) {
        this.editAction = editAction;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getInterhash() {
        return interhash;
    }

    /**
     * @param interhash
     */
    public void setInterhash(String interhash) {
        this.interhash = interhash;
    }

    /**
     * @return
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    public String getFormResourceHash() {
        return formResourceHash;
    }

    public void setFormResourceHash(String formResourceHash) {
        this.formResourceHash = formResourceHash;
    }

    public PersonResourceRelationType getFormPersonRole() {
        return formPersonRole;
    }

    public void setFormPersonRole(PersonResourceRelationType formPersonRole) {
        this.formPersonRole = formPersonRole;
    }

    public String getFormThesisId() {
        return formThesisId;
    }

    public void setFormThesisId(String formThesisId) {
        this.formThesisId = formThesisId;
    }

    public String getFormPersonNameId() {
        return formPersonNameId;
    }

    public void setFormPersonNameId(String formPersonNameId) {
        this.formPersonNameId = formPersonNameId;
    }

    public List<String> getFormPersonRoles() {
        return formPersonRoles;
    }

    public void setFormPersonRoles(List<String> formPersonRoles) {
        this.formPersonRoles = formPersonRoles;
    }

    public String getFormRequestType() {
        return formRequestType;
    }

    public void setFormRequestType(String formRequestType) {
        this.formRequestType = formRequestType;
    }

    public String getFormResourcePersonRelationId() {
        return formResourcePersonRelationId;
    }

    public void setFormResourcePersonRelationId(String formResourcePersonRelationId) {
        this.formResourcePersonRelationId = formResourcePersonRelationId;
    }

    public String getFormInterHash() {
        return formInterHash;
    }

    public void setFormInterHash(String formInterHash) {
        this.formInterHash = formInterHash;
    }

    public String getFormIntraHash() {
        return formIntraHash;
    }

    public void setFormIntraHash(String formIntraHash) {
        this.formIntraHash = formIntraHash;
    }

    public boolean isFormThatsMe() {
        return formThatsMe;
    }

    public void setFormThatsMe(boolean formThatsMe) {
        this.formThatsMe = formThatsMe;
    }

    public int getFormPersonIndex() {
        return formPersonIndex;
    }

    public void setFormPersonIndex(int formPersonIndex) {
        this.formPersonIndex = formPersonIndex;
    }

    public int getFormMatchId() {
        return formMatchId;
    }

    public void setFormMatchId(int formMatchId) {
        this.formMatchId = formMatchId;
    }
}
