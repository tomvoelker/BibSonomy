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

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Handles cases, where an interhash, an index and a type is set.
 *
 * @author ada
 */
public class GetResourcePersonRelationsByInterhashIndexAndType extends ResourcePersonRelationChainElement {

	/**
	 * Creates an instance with the person database manager set.
	 *
	 * @param personDatabaseManager an instance.
	 */
	public GetResourcePersonRelationsByInterhashIndexAndType(final PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<ResourcePersonRelation> handle(final QueryAdapter<ResourcePersonRelationQuery> adapter, final DBSession session) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return this.getPersonDatabaseManager().getResourcePersonRelations(query.getInterhash(), query.getAuthorIndex(), query.getRelationType(), session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ResourcePersonRelationQuery> adapter) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return present(query.getInterhash()) &&
						present(query.getAuthorIndex()) &&
						present(query.getRelationType()) &&
						!query.isWithPosts() &&
						!query.isWithPersons() &&
						!present(query.getPersonId());
	}
}
