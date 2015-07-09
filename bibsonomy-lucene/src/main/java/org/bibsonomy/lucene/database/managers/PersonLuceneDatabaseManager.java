package org.bibsonomy.lucene.database.managers;

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
public class PersonLuceneDatabaseManager  extends AbstractDatabaseManager {
	//private static final Log log = LogFactory.getLog(PersonLuceneDatabaseManager.class);

	private final static PersonLuceneDatabaseManager singleton = new PersonLuceneDatabaseManager();

	public static PersonLuceneDatabaseManager getInstance() {
		return singleton;
	}
	
	private PersonLuceneDatabaseManager() {
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
