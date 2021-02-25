package org.bibsonomy.search.index.database.cris;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.junit.Test;

/**
 * tests for a CRISLinkDa
 *
 * @author dzo
 */
public class CRISLinkDatabaseInformationLogicTest extends AbstractDatabaseManagerTest {
	private static final CRISLinkDatabaseInformationLogic INFORMATION_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("crisLinkDatabaseInformationLogic", CRISLinkDatabaseInformationLogic.class);

	@Test
	public void testGetDBState() {
		final DefaultSearchIndexSyncState dbState = INFORMATION_LOGIC.getDbState();
		assertThat(dbState.getLastPostContentId(), is(2l));
	}
}