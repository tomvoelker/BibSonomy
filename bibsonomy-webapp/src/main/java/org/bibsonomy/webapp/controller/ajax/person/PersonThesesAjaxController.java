package org.bibsonomy.webapp.controller.ajax.person;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

public class PersonThesesAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

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

        return null;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }

}
