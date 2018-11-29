/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.search.index.generator.person;

import org.bibsonomy.model.Person;
import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.List;

/**
 * logic to retriev all data from the database
 * @author dzo
 */
public class PersonIndexGenerationLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Person> {

	@Override
	public int getNumberOfEntities() {
		return 0;
	}

	@Override
	public SearchIndexSyncState getDbState() {
		return null;
	}

	@Override
	public List<Person> getEntites(int lastContenId, int limit) {
		return null;
	}
}
