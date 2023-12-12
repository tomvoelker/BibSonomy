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
package org.bibsonomy.search.management.database.manager;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class PersonSearchDatabaseManager extends AbstractDatabaseManager {

	public List<PersonName> getPersonMainNamesByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive, DBSession databaseSession) {
		final Pair<Long, Long> range = new Pair<>(firstChangeId, toPersonChangeIdExclusive);
		return this.queryForList("getPersonMainNamesByChangeIdRange", range, PersonName.class, databaseSession);
	}
	
	public List<Person> getPersonByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive, DBSession databaseSession) {
		final Pair<Long, Long> range = new Pair<>(firstChangeId, toPersonChangeIdExclusive);
		return this.queryForList("getPersonByChangeIdRange", range, Person.class, databaseSession);
	}

}
