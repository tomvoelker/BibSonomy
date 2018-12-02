package org.bibsonomy.search.index.database.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.junit.Test;

/**
 * tests for {@link GroupDatabaseInformationLogic}
 *
 * @author dzo
 */
public class GroupDatabaseInformationLogicTest extends AbstractDatabaseManagerTest {

	private static final GroupDatabaseInformationLogic INFORMATION_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(GroupDatabaseInformationLogic.class);

	@Test
	public void testGetDBState() {
		final DefaultSearchIndexSyncState dbState = INFORMATION_LOGIC.getDbState();
		assertThat(dbState.getLastPostContentId(), is(12l));
	}
}