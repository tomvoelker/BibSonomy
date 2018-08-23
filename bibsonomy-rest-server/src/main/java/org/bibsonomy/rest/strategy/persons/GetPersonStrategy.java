package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

import java.io.ByteArrayOutputStream;

/**
 * strategy to get a person by his/her person id
 *
 * @author pda
 */
public class GetPersonStrategy extends Strategy {

	private final String personId;

	public GetPersonStrategy(Context context, String personId) {
		super(context);
		this.personId = personId;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ObjectNotFoundException {
		final Person person = getLogic().getPersonById(PersonIdType.PERSON_ID, personId);
		if (person.getPersonId() == null) {
			throw new NoSuchResourceException("The requested person with id '" + personId + "' does not exist.");
		}
		getRenderer().serializePerson(writer, person, new ViewModel());
	}
}
