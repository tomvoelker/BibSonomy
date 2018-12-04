package org.bibsonomy.search.index.generator.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Group;
import org.bibsonomy.search.index.generator.GeneralIndexGenerationLogic;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for {@link GeneralIndexGenerationLogic} for {@link Group}s
 *
 * @author dzo
 */
public class GroupIndexGenerationLogicTest extends AbstractDatabaseManagerTest {

	private static final GeneralIndexGenerationLogic<Group> GROUP_INDEX_GENERATION_LOGIC = (GeneralIndexGenerationLogic<Group>) SearchSpringContextWrapper.getBeanFactory().getBean("groupGenerationDBLogic");

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = GROUP_INDEX_GENERATION_LOGIC.getNumberOfEntities();
		assertThat(numberOfEntities, is(8));
	}

	@Test
	public void testGetEntities() {
		final List<Group> entities = GROUP_INDEX_GENERATION_LOGIC.getEntities(0, 10);
		assertThat(entities.size(), is(10));

		final Group group = entities.get(2);
		assertThat(group.getName(), is("testgroup1"));
	}
}
