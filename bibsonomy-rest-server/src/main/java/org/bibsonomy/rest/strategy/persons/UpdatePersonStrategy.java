package org.bibsonomy.rest.strategy.persons;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * strategy to update a person
 *
 * @author pda
 */
public class UpdatePersonStrategy extends AbstractUpdateStrategy {
	private final String personId;
	private final PersonUpdateOperation operation;

	/**
	 * @param context
	 */
	public UpdatePersonStrategy(final Context context, final String personId, final PersonUpdateOperation operation) {
		super(context);
		if (!present(personId)) {
			throw new IllegalArgumentException("No personId present.");
		}

		if (!present(operation)) {
			throw new IllegalArgumentException("No PersonUpdateOperation specified.");
		}
		this.personId = personId;
		this.operation = operation;
	}

	@Override
	protected void render(Writer writer, String personID) {
		this.getRenderer().serializePersonId(writer, personID);
	}

	@Override
	protected String update() {
		final Person person = this.getRenderer().parsePerson(this.doc);
		person.setPersonId(this.personId);
		this.getLogic().updatePerson(person, this.operation);
		return person.getPersonId();
	}
}
