package org.bibsonomy.rest.strategy.projects;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

/**
 * strategy to create a new project person relation
 *
 * @author pda
 */
public class PostProjectRelationStrategy extends AbstractCreateStrategy {

    private final String personId;

    public PostProjectRelationStrategy(Context context, String personId) {
        super(context);
        this.personId = personId;
    }

    @Override
    protected void render(Writer writer, String relationId) {
        getRenderer().serializeResourcePersonRelationId(writer, relationId);
    }

    @Override
    protected String create() {
        final Person person = getLogic().getPersonById(PersonIdType.PERSON_ID, this.personId);
        if (person.getPersonId() == null) {
            throw new BadRequestOrResponseException("Person with id " + this.personId + " doesn't exist.");
        }
//        final CRISLink crisLink = getRenderer().parseCRISLink(doc);
//        getLogic().createCRISLink(crisLink);
//        //TODO is this correct?
//        return crisLink.getSource().getLinkableId();
        return null;
    }
}
