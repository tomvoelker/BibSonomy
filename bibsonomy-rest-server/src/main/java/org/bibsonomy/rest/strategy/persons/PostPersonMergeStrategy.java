package org.bibsonomy.rest.strategy.persons;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author pda
 */
public class PostPersonMergeStrategy extends AbstractUpdateStrategy {
	private final String personMergeTargetId;
	private final String personToMergeId;

	/**
	 * default constructor
	 *
	 * @param context
	 * @param personMergeTargetId
	 * @param personToMergeId
	 */
	public PostPersonMergeStrategy(final Context context, final String personMergeTargetId, final String personToMergeId) {
		super(context);
		if (!present(personMergeTargetId)) {
			throw new IllegalArgumentException("No personId given for the target person to merge.");
		}
		if (!present(personToMergeId)) {
			throw new IllegalArgumentException("No personId given for the person to merge.");
		}
		this.personMergeTargetId = personMergeTargetId;
		this.personToMergeId = personToMergeId;
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		this.getRenderer().serializePersonId(writer, resourceID);
	}

	@Override
	protected String update() {
		final Person personMergeTarget = this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.personMergeTargetId);
		if (!present(personMergeTarget)) {
			throw new BadRequestOrResponseException("No person with id " + personMergeTargetId + " as source.");
		}

		final Person personToMerge = this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.personToMergeId);
		if (!present(personToMerge)) {
			throw new BadRequestOrResponseException("No person with id " + personToMergeId + " as target.");
		}

		// FIXME Should normally be done as a api call
		personToMerge.getMainName().setMain(false);
		personToMerge.setMainName(personMergeTarget.getMainName());
		final String academicDegree = personMergeTarget.getAcademicDegree();
		if (present(academicDegree)) {
			personToMerge.setAcademicDegree(academicDegree);
			this.getLogic().updatePerson(personToMerge, PersonUpdateOperation.UPDATE_ACADEMIC_DEGREE);
		}
		this.getLogic().updatePerson(personToMerge, PersonUpdateOperation.UPDATE_NAMES);
		final PersonMatch personMatch = this.getLogic().getPersonMatches(personMergeTargetId).stream().
						filter(p -> p.getPerson2().getPersonId().equals(personToMergeId)).findAny().orElse(null);
		if (!present(personMatch)) {
			//FIXME ????????
			throw new BadRequestOrResponseException("Error in matching....");
		}
		return this.getLogic().acceptMerge(personMatch) ? this.personMergeTargetId : "no.merge";
	}
}
