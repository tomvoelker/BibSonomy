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
