package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.model.Person;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

/**
 * strategy to create a new person
 * @author pda
 */
public class PostPersonStrategy extends AbstractCreateStrategy {

	public PostPersonStrategy(Context context) {
		super(context);
	}

	@Override
	protected void render(Writer writer, String personID) {
		this.getRenderer().serializePersonId(writer, personID);
	}

	@Override
	protected String create() {
		final Person person = getRenderer().parsePerson(this.doc);
		return this.getLogic().createOrUpdatePerson(person);
	}
}
