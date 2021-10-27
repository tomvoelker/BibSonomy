package org.bibsonomy.webapp.controller.ajax.person;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.webapp.controller.PersonPageController.NO_THESIS_SEARCH;

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

public class PersonPublicationsAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    private AdhocRenderer renderer;
    private CSLFilesManager cslFilesManager;
    private URLGenerator urlGenerator;

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

        // exclude theses, when no search set
        if (!present(command.getSearch())) {
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
}