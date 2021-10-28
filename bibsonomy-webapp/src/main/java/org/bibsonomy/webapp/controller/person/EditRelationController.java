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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.webapp.command.PersonPageCommand;
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
public class EditRelationController {
    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private LogicInterface logic;
    private RequestLogic requestLogic;
    private PersonRoleRenderer personRoleRenderer;
    private URLGenerator urlGenerator;
    private String crisCollege;

    /**
     * action called when a user want to link an author from a publication
     * @param command
     * @return the ajax text view
     */
    protected View linkAction(final PersonPageCommand command) {
        final Person person = new Person();
        person.setPersonId(command.getFormPersonId());
        person.setUser(command.getContext().getLoginUser().getName());
        this.logic.updatePerson(person, PersonUpdateOperation.LINK_USER);
        return Views.AJAX_TEXT;
    }

    /**
     * action called when a user want to unlink an author from a publication
     * @param command
     * @return the ajax text view
     */
    protected View unlinkAction(PersonPageCommand command) {
        this.logic.unlinkUser(this.logic.getAuthenticatedUser().getName());
        return Views.AJAX_TEXT;
    }

    /**
     * Action called when a user wants to add a person role to a thesis
     * @param command
     * @return
     */
    protected View addRoleAction(PersonPageCommand command) {
        final JSONObject jsonResponse = new JSONObject();
        final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
        final Post<BibTex> post = new Post<>();
        post.setResource(new BibTex());
        post.getResource().setInterHash(command.getFormInterHash());
        resourcePersonRelation.setPost(post);

        try {
            final Person person = new Person();
            if (present(command.getFormPersonId())) {
                person.setPersonId(command.getFormPersonId());
            } else {
                final PersonName mainName = command.getNewName();
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
        jsonResponse.put("resourcePersonRelationid", resourcePersonRelation.getPersonRelChangeId() + "");
        jsonResponse.put("personUrl", this.urlGenerator.getPersonUrl(resourcePersonRelation.getPerson().getPersonId()));
        command.setResponseString(jsonResponse.toJSONString());

        return Views.AJAX_JSON;
    }

    /**
     * Action called when a user wants to edit the role of a person in a thesis
     * @param command
     * @return
     */
    protected View editRoleAction(PersonPageCommand command) {
        // TODO not used? remove?
        for (String role : command.getFormPersonRoles()) {
            final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
            Post<BibTex> post = new Post<>();
            post.setResource(new BibTex());
            post.getResource().setInterHash(command.getFormInterHash());
            resourcePersonRelation.setPost(post);
            resourcePersonRelation.setPerson(new Person());
            resourcePersonRelation.getPerson().setPersonId(command.getFormPersonId());
            resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
            final PersonResourceRelationType relationType = PersonResourceRelationType.valueOf(StringUtils.upperCase(role));
            resourcePersonRelation.setRelationType(relationType);
            try {
                this.logic.createResourceRelation(resourcePersonRelation);
            } catch (LogicException e) {
                command.getLogicExceptions().add(e);
            }
        }

        return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(command.getPerson().getPersonId()));
    }

    protected View deleteRoleAction(PersonPageCommand command) {
        this.logic.removeResourceRelation(null, null, -1, null); // FIXME: change

        return Views.AJAX_TEXT;
    }

    /**
     * @param command
     * @return
     */
    protected View searchPubAction(PersonPageCommand command) {
        final List<Post<GoldStandardPublication>> suggestions = this.getSuggestionPub(command.getFormSelectedName());
        final JSONArray array = this.buildupPubResponseArray(suggestions);
        command.setResponseString(array.toJSONString());

        return Views.AJAX_JSON;
    }

    protected List<Post<GoldStandardPublication>> getSuggestionPub(final String search) {
        final PostQuery<GoldStandardPublication> postQuery = new PostQueryBuilder().search(search).
                createPostQuery(GoldStandardPublication.class);
        // TODO limit searches to thesis
        return this.logic.getPosts(postQuery);
    }

    /**
     * Combined publication and author search action. This search is in particular necessary
     * when someone want's to find unrelated (no role associated to authors) documents.
     * @param command
     * @return
     */
    protected View searchPubAuthorAction(final PersonPageCommand command) {
        final List<Post<GoldStandardPublication>> suggestionsPub = this.getSuggestionPub(command.getFormSelectedName());

        final JSONArray array = new JSONArray();

        array.addAll(buildupPubResponseArray(suggestionsPub));  // Publications (not associated to Persons) oriented search return
        command.setResponseString(array.toJSONString());

        return Views.AJAX_JSON;
    }

    /**
     * @param command
     * @return
     */
    @SuppressWarnings("unchecked")
    protected View searchAction(PersonPageCommand command) {
        final PersonQuery query = new PersonQuery(command.getFormSelectedName());
        query.setUsePrefixMatch(true);
        if (command.isLimitResultsToCRISCollege() && present(this.crisCollege)) {
            query.setCollege(this.crisCollege);
        }

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
     * This is a helper function adds to an JSONarray Publications form a sugesstions list.
     * @param posts
     * @return
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

    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }

    public void setRequestLogic(RequestLogic requestLogic) {
        this.requestLogic = requestLogic;
    }

    public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
        this.personRoleRenderer = personRoleRenderer;
    }

    public void setCrisCollege(String crisCollege) {
        this.crisCollege = crisCollege;
    }

    public void setUrlGenerator(URLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }
}