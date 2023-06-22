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

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * strategy for deleting a person resource relation
 *
 * @author dzo
 */
public class DeletePersonRelationStrategy extends AbstractDeleteStrategy {
	private final String personId;
	private final String interHash;
	private final PersonResourceRelationType type;
	private final int index;

	/**
	 * inits a delete strategy for a {@link org.bibsonomy.model.ResourcePersonRelation}
	 * @param context
	 */
	public DeletePersonRelationStrategy(final Context context) {
		super(context);

		this.personId = context.getStringAttribute(RESTConfig.PERSON_ID_PARAM, null);
		this.interHash = context.getStringAttribute(RESTConfig.INTERHASH_PARAM, null);
		this.type = PersonResourceRelationType.getByRelatorCode(context.getStringAttribute(RESTConfig.RELATION_TYPE_PARAM, null));
		this.index = context.getIntAttribute(RESTConfig.RELATION_INDEX_PARAM, 0);
	}

	@Override
	protected boolean delete() {
		this.getLogic().removeResourceRelation(this.personId, this.interHash, this.index, this.type);
		return true;
	}
}
