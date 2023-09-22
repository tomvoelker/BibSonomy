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
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * strategy to update a target person by merging with a source person
 * @author pda
 */
public class PostPersonMergeStrategy extends AbstractUpdateStrategy {
	private final String mergeSourceId;
	private final String mergeTargetId;

	/**
	 * default constructor
	 *
	 * @param context
	 */
	public PostPersonMergeStrategy(final Context context) {
		super(context);

		this.mergeSourceId = context.getStringAttribute(RESTConfig.SOURCE_ID_PARAM, null);
		this.mergeTargetId = context.getStringAttribute(RESTConfig.TARGET_ID_PARAM, null);

		if (!present(this.mergeSourceId)) {
			throw new IllegalArgumentException("No personId given for the source person to merge.");
		}

		if (!present(this.mergeTargetId)) {
			throw new IllegalArgumentException("No personId given for the target person to merge.");
		}
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		this.getRenderer().serializePersonId(writer, resourceID);
	}

	@Override
	protected String update() {
		final Person sourcePerson = this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.mergeSourceId);
		if (!present(sourcePerson)) {
			throw new BadRequestOrResponseException("No person with id " + this.mergeSourceId + " as source.");
		}

		final Person targetPerson = this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.mergeTargetId);
		if (!present(targetPerson)) {
			throw new BadRequestOrResponseException("No person with id " + this.mergeTargetId + " as target.");
		}

		// FIXME Should normally be done as a api call
		targetPerson.getMainName().setMain(false);
		targetPerson.setMainName(sourcePerson.getMainName());
		final String academicDegree = sourcePerson.getAcademicDegree();
		if (present(academicDegree)) {
			targetPerson.setAcademicDegree(academicDegree);
			this.getLogic().updatePerson(targetPerson, PersonOperation.UPDATE_DETAILS);
		}
		this.getLogic().updatePerson(targetPerson, PersonOperation.UPDATE_NAMES);
		final PersonMatch personMatch = this.getLogic().getPersonMatches(mergeSourceId).stream().
						filter(p -> p.getPerson2().getPersonId().equals(mergeTargetId)).findAny().orElse(null);
		if (!present(personMatch)) {
			//FIXME ????????
			throw new BadRequestOrResponseException("Error in matching....");
		}
		return this.getLogic().acceptMerge(personMatch) ? this.mergeSourceId : "no.merge";
	}
}
