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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.layout.citeproc.CSLUtils;
import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.citeproc.renderer.LanguageFile;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.layout.csl.CSLStyle;
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
@Setter
public class PersonPublicationsAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    private static final Log log = LogFactory.getLog(PersonPublicationsAjaxController.class);

    private AdhocRenderer renderer;
    private CSLFilesManager cslFilesManager;
    private URLGenerator urlGenerator;
    private boolean crisEnabled;

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());
        final User user = this.logic.getUserDetails(person.getUser());

        // user settings, or default if no user linked
        final PersonPostsStyle personPostsStyle = user.getSettings().getPersonPostsStyle();
        final String personPostsLayout = user.getSettings().getPersonPostsLayout();
        final String locale = user.getSettings().getDefaultLanguage();
        command.setPersonPostsLayout(personPostsLayout);
        command.setPersonPostsStyle(personPostsStyle);

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

        if (personPostsStyle.equals(PersonPostsStyle.MYOWN)) {
            this.workOnMyOwnPosts(command, user);
        } else {
            this.workOnPublications(command, person);
        }

        // rendered posts
        // IMPORTANT: Experimental feature, TODO rework whole feature

        if (personPostsLayout.startsWith("CUSTOM/")) {
            this.renderPosts(command, personPostsStyle, personPostsLayout, locale);
        }

        return Views.AJAX_PERSON_PUBLICATIONS;
    }

    public void workOnPublications(AjaxPersonPageCommand command, Person person) {
        final PostQueryBuilder queryBuilder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.PERSON)
                .setGroupingName(person.getPersonId())
                .entriesStartingAt(command.getPageSize(), command.getStart())
                .search(command.getSearch())
                .setSortCriteria(command.getSortCriteria());

        final List<Post<GoldStandardPublication>> publications = this.logic.getPosts(queryBuilder.createPostQuery(GoldStandardPublication.class));
        final List<ResourcePersonRelation> relations = PersonUtils.convertToRelations(publications, person);
        command.setOtherPubs(relations);
    }

    public void workOnMyOwnPosts(AjaxPersonPageCommand command, User user) {
        StringBuilder searchSB = new StringBuilder("tags:myown");
        if (present(command.getSearch())) {
            searchSB.append(" AND ");
            searchSB.append(command.getSearch());
        }

        // Get 'myown' posts of the user
        final PostQueryBuilder queryBuilder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.USER)
                .setGroupingName(user.getName())
                .search(searchSB.toString())
                .setSortCriteria(command.getSortCriteria())
                .entriesStartingAt(command.getPageSize(), command.getStart());

        final List<Post<BibTex>> posts = logic.getPosts(queryBuilder.createPostQuery(BibTex.class));
        command.setMyownPosts(posts);
    }

    public void renderPosts(AjaxPersonPageCommand command, PersonPostsStyle personPostsStyle, String layout, String locale) {
        // the prefix is set in settings->person
        String prefix = "CUSTOM/";
        CSLStyle cslStyle;

        if (layout.startsWith(prefix)) {
            cslStyle = cslFilesManager.getStyleByName(layout.substring(prefix.length()));
        } else {
            cslStyle = cslFilesManager.getStyleByName(layout);
        }

        if (!present(cslStyle)) {
            // style not found? reset to default rendering
            command.setPersonPostsLayout("DEFAULT");
        } else {
            // convert posts
            List<Post<? extends BibTex>> postsToConvert = new ArrayList<>();
            if (personPostsStyle.equals(PersonPostsStyle.MYOWN)) {
                postsToConvert.addAll(command.getMyownPosts());
            } else {
                postsToConvert.addAll(command.getOtherPubs().stream()
                        .map(ResourcePersonRelation::getPost)
                        .collect(Collectors.toList()));
            }

            LanguageFile localeProvider = new LanguageFile();
            switch (locale) {
                case "de":
                    localeProvider.setLocale(cslFilesManager.getLocaleFile("de-DE"));
                    break;
                case "en":
                default:
                    localeProvider.setLocale(cslFilesManager.getLocaleFile("en-US"));
                    break;
            }

            Map<String, String> renderedPosts;
            try {
                renderedPosts = AdhocRenderer.renderPosts(postsToConvert , cslStyle.getContent(), localeProvider, true);
            } catch (Exception e) {
                log.error("error while rendering posts", e);
                renderedPosts = new HashMap<>();
            }

            command.setRenderedPosts(renderedPosts);

            if (personPostsStyle.equals(PersonPostsStyle.MYOWN)) {
                Map<String, String> myOwnPostsRendered = new HashMap<>();
                for (Post<BibTex> post : command.getMyownPosts()) {
                    String renderedPost = renderedPosts.get(post.getResource().getIntraHash());

                    // CSL replacements
                    renderedPost = CSLUtils.replacePlaceholdersFromCSLRendering(renderedPost, post, urlGenerator);

                    myOwnPostsRendered.put(post.getResource().getIntraHash(), renderedPost);
                }
                command.setMyownPostsRendered(myOwnPostsRendered);
            } else {
                for (ResourcePersonRelation resourcePersonRelation: command.getOtherPubs()) {
                    String renderedPost = renderedPosts.get(resourcePersonRelation.getPost().getResource().getIntraHash());

                    // CSL replacements
                    renderedPost = CSLUtils.replacePlaceholdersFromCSLRendering(renderedPost, resourcePersonRelation.getPost(), urlGenerator);

                    resourcePersonRelation.setRenderedPost(renderedPost);
                }
            }
        }
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }

}