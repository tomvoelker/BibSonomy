package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for {@link CommunityPostIndexUpdateLogic}
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicPublicationTest extends AbstractDatabaseManagerTest {

	private static final CommunityPostIndexUpdateLogic<GoldStandardPublication> UPDATE_LOGIC = (CommunityPostIndexUpdateLogic<GoldStandardPublication>) SearchSpringContextWrapper.getBeanFactory().getBean("communityPublicationIndexUpdateLogic");

	/**
	 * tests the {@link CommunityPostIndexUpdateLogic#getNewestPostByInterHash(String)} method
	 */
	@Test
	public void testGetNewestPostByInterHash() {
		final Post<GoldStandardPublication> newestPostByInterHash = UPDATE_LOGIC.getNewestPostByInterHash("a5936835f9eeab91eb09d84948306178");
		assertThat(newestPostByInterHash.getResource().getTitle(), is("A case for abductive reasoning over ontologies"));
	}
}
