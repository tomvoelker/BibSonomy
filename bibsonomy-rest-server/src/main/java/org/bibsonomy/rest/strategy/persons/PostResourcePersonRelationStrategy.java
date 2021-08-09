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

	/**
	 * default construtor
	 * @param context
	 * @param personId
	 */
	public PostResourcePersonRelationStrategy(final Context context, final String personId) {
		super(context);
		this.personId = personId;
	}

	@Override
	protected void render(final Writer writer, final String relationId) {
		this.getRenderer().serializeResourceHash(writer, relationId);
	}

	@Override
	protected String create() {
		final Person person = this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.personId);
		if (!present(person)) {
			throw new BadRequestOrResponseException("Person with id " + this.personId + " doesn't exist.");
		}

		final ResourcePersonRelation resourcePersonRelation = this.getRenderer().parseResourcePersonRelation(this.doc);
		resourcePersonRelation.setPerson(person);

		try {
			this.getLogic().createResourceRelation(resourcePersonRelation);
			final Resource resource = resourcePersonRelation.getPost().getResource();
			return resource.getInterHash();
		} catch (final ResourcePersonAlreadyAssignedException e) {
			throw new BadRequestOrResponseException(e);
		}
	}
}
