/**
 * BibSonomy-Rest-Client - The REST-client.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.rest.client.AbstractDeleteQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * delete query for a {@link org.bibsonomy.model.ResourcePersonRelation}
 *
 * @author dzo
 */
public class DeleteResourcePersonRelationQuery extends AbstractDeleteQuery {
	private final String personId;
	private final String interHash;
	private final int index;
	private final PersonResourceRelationType type;

	/**
	 * default constructor with all required fields
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 */
	public DeleteResourcePersonRelationQuery(String personId, String interHash, int index, PersonResourceRelationType type) {
		if (!present(personId)) {
			throw new IllegalArgumentException("no person id given");
		}

		if (!present(interHash)) {
			throw new IllegalArgumentException("no interhash given");
		}

		if (!present(type)) {
			throw new IllegalArgumentException("no type given");
		}
		this.personId = personId;
		this.interHash = interHash;
		this.index = index;
		this.type = type;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String personResourceRelationUrl = this.getUrlRenderer().createUrlBuilderForPersonResourceRelation(this.personId, this.interHash, this.index, this.type).asString();
		this.downloadedDocument = performRequest(HttpMethod.DELETE, personResourceRelationUrl, null);
	}
}
