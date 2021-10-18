package org.bibsonomy.webapp.controller.ajax.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class PersonThesesAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    public static final Set<PersonResourceRelationType> PUBLICATION_RELATED_RELATION_TYPES = Sets.asSet(PersonResourceRelationType.AUTHOR, PersonResourceRelationType.EDITOR);

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
                .byPersonId(command.getRequestedPersonId())
                .withPosts(true)
                .withPersonsOfPosts(true)
                .onlyTheses(true)
                .groupByInterhash(true)
                .orderBy(PersonResourceRelationOrder.PublicationYear)
                .fromTo(0, Integer.MAX_VALUE);

        final List<ResourcePersonRelation> thesesRelations = logic.getResourceRelations(queryBuilder.build());
        final List<ResourcePersonRelation> authorEditorRelations = new ArrayList<>();
        final List<ResourcePersonRelation> advisorRelations = new ArrayList<>();

        for (ResourcePersonRelation thesis : thesesRelations) {
            final boolean isAuthorEditor = PUBLICATION_RELATED_RELATION_TYPES.contains(thesis.getRelationType());
            if (isAuthorEditor) {
                authorEditorRelations.add(thesis);
            } else {
                advisorRelations.add(thesis);
            }
        }

        command.setThesis(authorEditorRelations);
        command.setAdvisedThesis(advisorRelations);

        return Views.AJAX_PERSON_THESES;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }

}
