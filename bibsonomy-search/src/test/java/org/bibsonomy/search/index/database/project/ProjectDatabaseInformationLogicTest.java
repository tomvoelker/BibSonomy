package org.bibsonomy.search.index.database.project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.junit.Test;

/**
 * tests for {@link ProjectDatabaseInformationLogic}
 * @author dzo
 */
public class ProjectDatabaseInformationLogicTest extends AbstractDatabaseManagerTest {

	private static final ProjectDatabaseInformationLogic LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(ProjectDatabaseInformationLogic.class);

	@Test
	public void testDBState() {
		final DefaultSearchIndexSyncState state = LOGIC.getDbState();
		assertThat(state.getLastPostContentId(), is(3l));
	}

}