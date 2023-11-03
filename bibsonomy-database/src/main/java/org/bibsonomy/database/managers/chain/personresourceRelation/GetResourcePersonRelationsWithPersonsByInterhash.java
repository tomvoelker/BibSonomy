/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain.personresourceRelation;


import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

import java.util.List;

/**
 * @author ada
 */
public class GetResourcePersonRelationsWithPersonsByInterhash extends ResourcePersonRelationChainElement {

	/**
	 * Creates an instance with the person database manager set.
	 *
	 * @param personDatabaseManager an instance.
	 */
	public GetResourcePersonRelationsWithPersonsByInterhash(final PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<ResourcePersonRelation> handle(final QueryAdapter<ResourcePersonRelationQuery> adapter, final DBSession session) {
		return this.getPersonDatabaseManager().getResourcePersonRelationsWithPersonsByInterhash(adapter.getQuery().getInterhash(), session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ResourcePersonRelationQuery> adapter) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return present(query.getInterhash()) &&
						!query.isWithPosts() &&
						!present(query.getPersonId()) &&
						!present(query.getRelationType());
	}
}
