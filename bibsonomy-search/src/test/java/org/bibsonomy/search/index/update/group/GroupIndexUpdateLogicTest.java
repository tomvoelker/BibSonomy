package org.bibsonomy.search.index.update.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.search.index.update.GeneralIndexUpdateLogic;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for group index update logic test
 *
 * @author dzo
 */
public class GroupIndexUpdateLogicTest extends AbstractDatabaseManagerTest {

	private static final GeneralIndexUpdateLogic<Group> INDEX_UPDATE_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("groupSearchUpdateLogic", GeneralIndexUpdateLogic.class);

	private static final GroupDatabaseManager GROUP_DATABASE_MANAGER = testDatabaseContext.getBean(GroupDatabaseManager.class);

	@Test
	public void testGetNewerEntities() {
		final List<Group> newerEntities = INDEX_UPDATE_LOGIC.getNewerEntities(12, new Date(), 10, 0);
		assertThat(newerEntities.size(), is(12));
	}

	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();

		GROUP_DATABASE_MANAGER.deleteGroup("testgroup1", true, new User("testuser1"), this.dbSession);

		final List<Group> deletedEntities = INDEX_UPDATE_LOGIC.getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(1));
	}
}
