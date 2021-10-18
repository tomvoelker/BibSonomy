package org.bibsonomy.webapp.controller.ajax.person;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class PersonSimilarAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());
        final PostQuery<GoldStandardPublication> personNameQuery = new PostQueryBuilder()
                .setPersonNames(person.getNames())
                .setOnlyIncludeAuthorsWithoutPersonId(true)
                .end(20) // get 20 "recommendations"
                .createPostQuery(GoldStandardPublication.class);
        final List<Post<GoldStandardPublication>> similarPosts = this.logic.getPosts(personNameQuery);

        final List<ResourcePersonRelation> similarAuthorRelations = new ArrayList<>();
        for (final Post<GoldStandardPublication> post : similarPosts) {
            final ResourcePersonRelation relation = new ResourcePersonRelation();
            relation.setPost(post);
            relation.setPersonIndex(PersonUtils.findIndexOfPerson(person, post.getResource()));
            relation.setRelationType(PersonUtils.getRelationType(person, post.getResource()));
            similarAuthorRelations.add(relation);
        }

        command.setSimilarAuthorPubs(similarAuthorRelations);

        return Views.AJAX_PERSON_PUBLICATIONS;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }
}
