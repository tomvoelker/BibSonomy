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
package org.bibsonomy.search.management.database.manager;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelationLogStub;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
@Deprecated // move to PersonDatabaseManager
public class PersonSearchDatabaseManager  extends AbstractDatabaseManager {
	private final static PersonSearchDatabaseManager singleton = new PersonSearchDatabaseManager();

	public static PersonSearchDatabaseManager getInstance() {
		return singleton;
	}
	
	private PersonSearchDatabaseManager() {
	}

	public List<ResourcePersonRelationLogStub> getPubPersonChangesByChangeIdRange(long fromPersonChangeId, long toPersonChangeIdExclusive, DBSession databaseSession) {
		final Pair<Long, Long> range = new Pair<>(fromPersonChangeId, toPersonChangeIdExclusive);
		return this.queryForList("getPubPersonChangesByChangeIdRange", range, ResourcePersonRelationLogStub.class, databaseSession);
	}

	public List<PersonName> getPersonMainNamesByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive, DBSession databaseSession) {
		final Pair<Long, Long> range = new Pair<>(firstChangeId, toPersonChangeIdExclusive);
		return this.queryForList("getPersonMainNamesByChangeIdRange", range, PersonName.class, databaseSession);
	}
	
	public List<Person> getPersonByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive, DBSession databaseSession) {
		final Pair<Long, Long> range = new Pair<>(firstChangeId, toPersonChangeIdExclusive);
		return this.queryForList("getPersonByChangeIdRange", range, Person.class, databaseSession);
	}
}
