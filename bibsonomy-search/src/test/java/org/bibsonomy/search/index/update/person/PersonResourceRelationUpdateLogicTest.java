package org.bibsonomy.search.index.update.person;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for the {@link PersonResourceRelationUpdateLogic}
 *
 * @author dzo
 */
public class PersonResourceRelationUpdateLogicTest extends AbstractDatabaseManagerTest {

	private static final PersonResourceRelationUpdateLogic UPDATE_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("personResourceRelationDBLogic", PersonResourceRelationUpdateLogic.class);

	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = testDatabaseContext.getBean(PersonDatabaseManager.class);

	@Test
	public void testGetDeletedEntitities() throws InterruptedException {
		final Date lastLogDate = new Date();
		Thread.sleep(1000);

		PERSON_DATABASE_MANAGER.removeResourceRelation("w.test.2", "eb0000af0a0c00b0b0ac0e0a0a00d0c0", 0, PersonResourceRelationType.AUTHOR, new User("testuser1"), this.dbSession);

		final List<ResourcePersonRelation> deletedEntities = UPDATE_LOGIC.getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(1));
	}
}