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
package org.bibsonomy.search.index.update.project;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Date;
import java.util.List;

/**
 * TODO: merge with {@link org.bibsonomy.search.index.update.person.PersonIndexUpdateLogic}
 *
 * update logic for {@link Project}s
 *
 * @author dzo
 */
public class ProjectIndexUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<Project> {

	@Override
	public List<Project> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);

			return this.queryForList("getUpdatedAndNewProjects", param, Project.class, session);
		}
	}

	@Override
	public List<Project> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeletedProjects", lastLogDate, Project.class, session);
		}
	}
}
