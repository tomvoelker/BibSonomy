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
package org.bibsonomy.search.management.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.search.management.database.manager.PersonSearchDatabaseManager;

/**
 * TODO: change Generic
 * @author dzo
 */
@Deprecated // TODO: remove TODO_REWORK_SEARCH
public class SearchGoldStandardLogic extends SearchDBLogic<GoldStandardPublication> {

	/**
	 * @param personSearchDatabaseManager the person search database manager
	 */
	public SearchGoldStandardLogic(final PersonSearchDatabaseManager personSearchDatabaseManager) {
		super(personSearchDatabaseManager);
	}

	@Override
	protected Integer getLastTasId() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastContentId" + this.getResourceName(), Integer.class, session);
		} finally {
			session.close();
		}
	}
}