package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.model.Person;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

public class PostPersonStrategy extends AbstractCreateStrategy {

    /**
     * @param context
     */
    public PostPersonStrategy(Context context) {
        super(context);
    }

    @Override
    protected void render(Writer writer, String personID) {
        getRenderer().serializePersonId(writer, personID);
    }

    @Override
    protected String create() {
        final Person person = getRenderer().parsePerson(doc);
        return getLogic().createPerson(person);
    }
}
