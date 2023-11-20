/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.persons;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;

import org.bibsonomy.common.enums.PersonOperation;
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
			this.getLogic().updatePerson(personToMerge, PersonOperation.UPDATE_DETAILS);
		}
		this.getLogic().updatePerson(personToMerge, PersonOperation.UPDATE_NAMES);
		final PersonMatch personMatch = this.getLogic().getPersonMatches(personMergeTargetId).stream().
						filter(p -> p.getPerson2().getPersonId().equals(personToMergeId)).findAny().orElse(null);
		if (!present(personMatch)) {
			//FIXME ????????
			throw new BadRequestOrResponseException("Error in matching....");
		}
		return this.getLogic().acceptMerge(personMatch) ? this.personMergeTargetId : "no.merge";
	}
}
