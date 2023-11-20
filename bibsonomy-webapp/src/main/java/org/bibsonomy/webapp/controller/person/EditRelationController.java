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

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PersonOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.webapp.command.actions.EditPersonCommand;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Controller to handle all requests to edit relations of a publication related to a given person.
 *
 * @author kchoong
 */
@Getter
@Setter
public class EditRelationController extends AbstractEditPersonController {
    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private LogicInterface logic;
    private RequestLogic requestLogic;
    private PersonRoleRenderer personRoleRenderer;
    private URLGenerator urlGenerator;
    private String crisCollege;

    /**
     * action called when a user want to link an author from a publication
     * @param command
     *
     * @return the ajax json response
     */
    protected View linkAction(final EditPersonCommand command) {
        final Person person = new Person();
        person.setPersonId(command.getPersonId());
        person.setUser(command.getContext().getLoginUser().getName());

        try {
            this.logic.updatePerson(person, PersonOperation.LINK_USER);
            return success(command, "The person has been successfully linked!");
        } catch (final Exception e) {
            log.error("error while updating person " + person.getPersonId(), e);
            return error(command, "person.error.fail.link");
        }
    }

    /**
     * action called when a user want to unlink an author from a publication
     * @param command
     *
     * @return the ajax json response
     */
    protected View unlinkAction(final EditPersonCommand command) {
        Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPersonId());

        try {
            this.logic.updatePerson(person, PersonOperation.UNLINK_USER);
            return success(command, "The person has been successfully unlinked!");
        } catch (final Exception e) {
            log.error("error while updating person " + person.getPersonId(), e);
            return error(command, "person.error.fail.unlink");
        }
    }

    /**
     * Action called when a user wants to add a person role to a thesis
     * @param command
     *
     * @return the ajax json response
     */
    protected View addRoleAction(final EditPersonCommand command) {
        final JSONObject jsonResponse = new JSONObject();
        final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
        final Post<BibTex> post = new Post<>();
        post.setResource(new BibTex());
        post.getResource().setInterHash(command.getFormInterHash());
        resourcePersonRelation.setPost(post);

        try {
            final Person person = new Person();
            if (present(command.getPersonId())) {
                person.setPersonId(command.getPersonId());
            } else {
                final PersonName mainName = command.getPersonName();
                mainName.setMain(true);
                person.setMainName(mainName);
                this.logic.createPerson(person);
            }
            resourcePersonRelation.setPerson(person);
            resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
            resourcePersonRelation.setRelationType(command.getFormPersonRole());

            this.logic.createResourceRelation(resourcePersonRelation);
        } catch (LogicException e) {
            command.getLogicExceptions().add(e);
            jsonResponse.put("exception", e.getClass().getSimpleName());
        }

        jsonResponse.put("personId", resourcePersonRelation.getPerson().getPersonId());
        jsonResponse.put("resourcePersonRelationId", resourcePersonRelation.getPersonRelChangeId() + "");
        jsonResponse.put("personUrl", this.urlGenerator.getPersonUrl(resourcePersonRelation.getPerson().getPersonId()));
        command.setResponseString(jsonResponse.toJSONString());

        return Views.AJAX_JSON;
    }

    /**
     * Action called when a user wants to delete the role of a person in a thesis
     * @param command
     *
     * @return the ajax json response
     */
    protected View deleteRoleAction(final EditPersonCommand command) {
        this.logic.removeResourceRelation(command.getPersonId(), command.getFormInterHash(), command.getFormPersonIndex(), command.getFormPersonRole());

        return Views.AJAX_TEXT;
    }

    /**
     * Action called when searching for persons...
     * @param command
     *
     * @return the ajax json response
     */
    @SuppressWarnings("unchecked")
    protected View searchAction(EditPersonCommand command) {
        final PersonQuery query = new PersonQuery(command.getSelectedName());
        query.setUsePrefixMatch(true);

        /*
         * query the persons and get the publication that should be displayed alongside the person
         */
        final List<Person> persons = this.logic.getPersons(query);
        final JSONArray array = new JSONArray();
        for (final Person person : persons) {
            final JSONObject jsonPersonName = new JSONObject();
            jsonPersonName.put("personId", person.getPersonId());
            final String personName = BibTexUtils.cleanBibTex(person.getMainName().toString());
            jsonPersonName.put("personName", personName);
            jsonPersonName.put("extendedPersonName", personName); // FIXME: this.personRoleRenderer.getExtendedPersonName(rel, this.requestLogic.getLocale(), false));

            array.add(jsonPersonName);
        }

        command.setResponseString(array.toJSONString());

        return Views.AJAX_JSON;
    }

    /**
     * Action called when user searches for a publication...
     * @param command
     *
     * @return the ajax json response
     */
    protected View searchPubAction(EditPersonCommand command) {
        final List<Post<GoldStandardPublication>> suggestions = this.getSuggestionPub(command.getSelectedName());
        final JSONArray array = this.buildupPubResponseArray(suggestions);
        command.setResponseString(array.toJSONString());

        return Views.AJAX_JSON;
    }

    protected List<Post<GoldStandardPublication>> getSuggestionPub(final String search) {
        // TODO limit searches to thesis
        final PostQuery<GoldStandardPublication> postQuery = new PostQueryBuilder()
                .search(search).
                createPostQuery(GoldStandardPublication.class);

        return this.logic.getPosts(postQuery);
    }

    /**
     * Combined publication and author search action. This search is in particular necessary
     * when someone want's to find unrelated (no role associated to authors) documents.
     * @param command
     *
     * @return the ajax json response
     */
    protected View searchPubAuthorAction(final EditPersonCommand command) {
        final List<Post<GoldStandardPublication>> suggestionsPub = this.getSuggestionPub(command.getSelectedName());

        final JSONArray array = new JSONArray();

        array.addAll(buildupPubResponseArray(suggestionsPub));  // Publications (not associated to Persons) oriented search return
        command.setResponseString(array.toJSONString());

        return Views.AJAX_JSON;
    }

    /**
     * This is a helper function adds to an JSONArray Publications form a suggestions list.
     * @param posts
     *
     * @return JSONArray
     */
    private JSONArray buildupPubResponseArray(final List<Post<GoldStandardPublication>> posts) {
        final JSONArray array = new JSONArray();
        for (final Post<GoldStandardPublication> post : posts) {
            final JSONObject jsonPersonName = new JSONObject();
            final BibTex publication = post.getResource();
            jsonPersonName.put("interhash", publication.getInterHash());
            jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(publication, this.requestLogic.getLocale(), false));
            array.add(jsonPersonName);
        }
        return array;
    }

}