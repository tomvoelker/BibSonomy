package org.bibsonomy.es;

import java.util.List;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ElasticSearchTest extends AbstractEsIndexTest {
	
	@Test
	public void testSomething() {
		List<ResourcePersonRelation> res;
		res = PersonDatabaseManager.getInstance().getPersonSuggestion("Schorsche");
		Assert.assertTrue(res.size() > 0);
		Assert.assertEquals(res.get(0).getRelationType(), PersonResourceRelationType.AUTHOR);
		Assert.assertEquals(res.get(0).getPersonIndex(), 0);
		Assert.assertEquals(res.get(0).getPerson().getPersonId(), "h.muller");
		Assert.assertEquals(res.get(0).getPerson().getMainName(), new PersonName("Henner", "Schorsche"));
		Assert.assertEquals(res.get(0).getPost().getResource().getTitle(), "Wurst aufs Brot");
		
	}
}
