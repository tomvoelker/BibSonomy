package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

import static org.bibsonomy.util.ValidationUtils.present;

public class PostPersonMergeStrategy extends AbstractUpdateStrategy {
    private final String personIdTarget, personIdSource;

    public PostPersonMergeStrategy(Context context, String personIdSource, String personIdTarget) {
        super(context);
        if (!present(personIdSource)) throw new IllegalArgumentException("No personId given for the source.");
        if (!present(personIdTarget)) throw new IllegalArgumentException("No personId given for the target.");
        this.personIdSource = personIdSource;
        this.personIdTarget = personIdTarget;
    }

    @Override
    protected void render(Writer writer, String resourceID) {
        this.getRenderer().serializePersonId(writer, resourceID);
    }

    @Override
    protected String update() {
        final Person personSource = this.getLogic().getPersonById(PersonIdType.PERSON_ID, personIdSource);
        if (!present(personSource)) {
            throw new BadRequestOrResponseException("No person with id " + personIdSource + " as source.");
        }
        personSource.setPersonId(personIdSource);
        final Person personTarget = this.getLogic().getPersonById(PersonIdType.PERSON_ID, personIdTarget);
        if (!present(personTarget)) {
            throw new BadRequestOrResponseException("No person with id " + personIdTarget + " as target.");
        }
        personTarget.setPersonId(personIdTarget);
        //FIXME Should normally be done as a api call
        personSource.setMainName(personTarget.getMainName());
        this.getLogic().updatePerson(personSource, PersonUpdateOperation.UPDATE_NAMES);
        PersonMatch personMatch = this.getLogic().getPersonMatches(personIdTarget).stream().
                filter(p -> p.getPerson2().getPersonId().equals(personIdSource)).findAny().orElse(null);
        if (!present(personMatch)) {
            //FIXME ????????
            throw new BadRequestOrResponseException("Error in matching....");
        }
        return this.getLogic().acceptMerge(personMatch) ? personTarget.getPersonId() : "no.merge";
    }
}
