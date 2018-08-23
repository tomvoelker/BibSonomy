package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

/**
 * strategy to create a new post person relation
 *
 * @author pda
 */
public class PostResourcePersonRelationStrategy extends AbstractCreateStrategy {

	private final String personId;

	public PostResourcePersonRelationStrategy(Context context, String personId) {
		super(context);
		this.personId = personId;
	}

	@Override
	protected void render(Writer writer, String relationId) {
		this.getRenderer().serializeResourcePersonRelationId(writer, relationId);
	}

	@Override
	protected String create() {
		final Person person = this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.personId);
		if (person.getPersonId() == null) {
			throw new BadRequestOrResponseException("Person with id " + this.personId + " doesn't exist.");
		}

		final ResourcePersonRelation resourcePersonRelation = getRenderer().parseResourcePersonRelation(doc);
		try {
			this.getLogic().addResourceRelation(resourcePersonRelation);
			final Resource resource = resourcePersonRelation.getPost().getResource();
			return resource.getInterHash() + "-" + resource.getIntraHash();
		} catch (final ResourcePersonAlreadyAssignedException e) {
			throw new BadRequestOrResponseException(e);
		}
	}
}
