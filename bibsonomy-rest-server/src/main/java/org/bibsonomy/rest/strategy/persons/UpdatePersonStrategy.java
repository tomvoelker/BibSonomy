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
	protected void render(final Writer writer, final String personID) {
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
