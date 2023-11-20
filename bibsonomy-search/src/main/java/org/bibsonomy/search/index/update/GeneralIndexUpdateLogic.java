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
package org.bibsonomy.search.index.update;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.model.SearchParam;

/**
 * general index update logic
 *
 * @author dzo
 */
public class GeneralIndexUpdateLogic<T> extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<T> {

	private final Class<T> entityClass;

	/**
	 * default constructor
	 * @param entityClass
	 */
	public GeneralIndexUpdateLogic(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	private String getEntityName() {
		return this.entityClass.getSimpleName();
	}

	@Override
	public List<T> getNewerEntities(final long lastEntityId, final Date lastLogDate, final int size, final int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);
			return this.queryForList("getUpdatedAndNew" + this.getEntityName() + "s", param, this.entityClass, session);
		}
	}

	@Override
	public List<T> getDeletedEntities(final Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeleted" + this.getEntityName() + "s", lastLogDate, this.entityClass, session);
		}
	}
}
