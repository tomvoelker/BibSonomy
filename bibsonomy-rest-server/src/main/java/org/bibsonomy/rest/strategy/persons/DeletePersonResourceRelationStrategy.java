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
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * stragegy for deleting a person resource relation
 *
 * @author dzo
 */
public class DeletePersonResourceRelationStrategy extends AbstractDeleteStrategy {
	private final String personId;
	private final String interHash;
	private final int index;
	private final PersonResourceRelationType type;

	/**
	 * inits a delete strategy for a {@link org.bibsonomy.model.ResourcePersonRelation}
	 * @param context
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 */
	public DeletePersonResourceRelationStrategy(final Context context, final String personId, final String interHash, final int index, final PersonResourceRelationType type) {
		super(context);
		this.personId = personId;
		this.interHash = interHash;
		this.index = index;
		this.type = type;
	}

	@Override
	protected boolean delete() {
		this.getLogic().removeResourceRelation(this.personId, this.interHash, this.index, this.type);
		return true;
	}
}
