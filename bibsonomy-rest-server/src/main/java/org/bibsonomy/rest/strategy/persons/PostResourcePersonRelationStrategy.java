package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

public class PostResourcePersonRelationStrategy extends AbstractCreateStrategy {

    /**
     * @param context
     */
    public PostResourcePersonRelationStrategy(Context context) {
        super(context);
    }

    @Override
    protected void render(Writer writer, String relationId) {
        getRenderer().serializeResourcePersonRelationId(writer, relationId);
    }

    @Override
    protected String create() {
        final ResourcePersonRelation resourcePersonRelation = getRenderer().parseResourcePersonRelation(doc);
        try {
            getLogic().addResourceRelation(resourcePersonRelation);
            final Resource resource = resourcePersonRelation.getPost().getResource();
            return resource.getInterHash() + "-" + resource.getIntraHash();
        } catch (ResourcePersonAlreadyAssignedException e) {
            throw new BadRequestOrResponseException(e);
        }
    }
}
