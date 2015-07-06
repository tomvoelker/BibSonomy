package org.bibsonomy.es;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ElasticSearchTest extends AbstractEsIndexTest {
	
	@Test
	public void testSomething() {
		PersonDatabaseManager.getInstance().getPersonSuggestion("");
	}
}
