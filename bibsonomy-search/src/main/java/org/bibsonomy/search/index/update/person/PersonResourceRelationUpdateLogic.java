/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.index.update.person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

/**
 * implementation to get the new person resource relations and the deleted ones
 *
 * @author dzo
 */
public class PersonResourceRelationUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<ResourcePersonRelation> {

	private final boolean includeRelatedEntityUpdates;

	/**
	 * constructor that sets if the related entity updates should be considered by returning new entities
	 * @param includeRelatedEntityUpdates
	 */
	public PersonResourceRelationUpdateLogic(final boolean includeRelatedEntityUpdates) {
		this.includeRelatedEntityUpdates = includeRelatedEntityUpdates;
	}

	@Override
	public List<ResourcePersonRelation> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);
			param.setIncludeRelatedEntityUpdates(this.includeRelatedEntityUpdates);
			return this.queryForList("getUpdatedOrNewPersonRelations", param, ResourcePersonRelation.class, session);
		}
	}

	@Override
	public List<ResourcePersonRelation> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeletedPersonResourceRelations", lastLogDate, ResourcePersonRelation.class, session);
		}
	}
}
