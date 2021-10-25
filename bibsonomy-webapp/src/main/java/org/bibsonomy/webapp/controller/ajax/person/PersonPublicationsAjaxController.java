package org.bibsonomy.webapp.controller.ajax.person;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.ValidationUtils;
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

        if (ValidationUtils.present(user) && user.getSettings().getPersonPostsStyle() == PersonPostsStyle.MYOWN) {
            return workOnMyOwnPosts(command, user);
        } else {
            return workOnPublications(command, person);
        }
    }

    public View workOnPublications(AjaxPersonPageCommand command, Person person) {
        final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
                .byPersonId(command.getRequestedPersonId())
                .withPosts(true)
                .withPersonsOfPosts(true)
                .excludeTheses(true)
                .groupByInterhash(true)
                .orderBy(PersonResourceRelationOrder.PublicationYear)
                .fromTo(command.getStart(), command.getEnd());

        final List<ResourcePersonRelation> publications = logic.getResourceRelations(queryBuilder.build());

        command.setOtherPubs(publications);

        return Views.AJAX_PERSON_PUBLICATIONS;
    }

    public View workOnMyOwnPosts(AjaxPersonPageCommand command, User user) {
        // Get 'myown' posts of the user
        final PostQueryBuilder queryBuilder = new PostQueryBuilder()
                .setTags(Collections.singletonList("myown"))
                .setGrouping(GroupingEntity.USER)
                .setGroupingName(user.getName())
                .search(command.getSearch())
                .entriesStartingAt(command.getPageSize(), command.getStart());

        final List<Post<BibTex>> posts = logic.getPosts(queryBuilder.createPostQuery(BibTex.class));
        command.setMyownPosts(posts);

        return Views.AJAX_PERSON_POSTS;
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