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
package org.bibsonomy.webapp.controller.ajax.person;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.webapp.controller.person.PersonPageController.NO_THESIS_SEARCH;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller to handle AJAX requests for the publications of a given person.
 *
 * @author kchoong
 */
public class PersonPublicationsAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    private AdhocRenderer renderer;
    private CSLFilesManager cslFilesManager;
    private URLGenerator urlGenerator;
    private boolean crisEnabled;

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());
        final User user = this.logic.getUserDetails(person.getUser());

        // start + end
        final int postsPerPage = command.getPageSize();
        final int start = postsPerPage * command.getPage();
        command.setStart(start);
        command.setEnd(start + postsPerPage);

        // sort criteria
        List<SortCriteria> sortCriteria = SortUtils.generateSortCriteria(SortUtils.parseSortKeys(command.getSortPage()), SortUtils.parseSortOrders(command.getSortPageOrder()));
        command.setSortCriteria(sortCriteria);

        // exclude theses, when no search set and CRIS disabled
        if (!present(command.getSearch()) && !crisEnabled) {
            command.setSearch(NO_THESIS_SEARCH);
        }

        if (present(user) && user.getSettings().getPersonPostsStyle() == PersonPostsStyle.MYOWN) {
            return workOnMyOwnPosts(command, user);
        } else {
            return workOnPublications(command, person);
        }
    }

    public View workOnPublications(AjaxPersonPageCommand command, Person person) {
        final PostQueryBuilder queryBuilder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.PERSON)
                .setGroupingName(person.getPersonId())
                .entriesStartingAt(command.getPageSize(), command.getStart())
                .search(command.getSearch())
                .setSortCriteria(command.getSortCriteria());

        final List<Post<GoldStandardPublication>> publications = this.logic.getPosts(queryBuilder.createPostQuery(GoldStandardPublication.class));
        final List<ResourcePersonRelation> relations = PersonUtils.convertToRelations(publications, person);
        command.setOtherPubs(relations);

        return Views.AJAX_PERSON_PUBLICATIONS;
    }

    public View workOnMyOwnPosts(AjaxPersonPageCommand command, User user) {
        // Get 'myown' posts of the user
        final PostQueryBuilder queryBuilder = new PostQueryBuilder()
                .setTags(Collections.singletonList("myown"))
                .setGrouping(GroupingEntity.USER)
                .setGroupingName(user.getName())
                .search(command.getSearch())
                .setSortCriteria(command.getSortCriteria())
                .entriesStartingAt(command.getPageSize(), command.getStart());

        final List<Post<BibTex>> posts = logic.getPosts(queryBuilder.createPostQuery(BibTex.class));
        command.setMyownPosts(posts);

        return Views.AJAX_PERSON_PUBLICATIONS;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }

    public void setRenderer(AdhocRenderer renderer) {
        this.renderer = renderer;
    }

    public void setCslFilesManager(CSLFilesManager cslFilesManager) {
        this.cslFilesManager = cslFilesManager;
    }

    public void setUrlGenerator(URLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    public void setCrisEnabled(boolean crisEnabled) {
        this.crisEnabled = crisEnabled;
    }
}