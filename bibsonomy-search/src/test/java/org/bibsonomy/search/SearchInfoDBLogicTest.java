package org.bibsonomy.search;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for {@link SearchInfoDBLogic}
 *
 * @author dzo
 */
public class SearchInfoDBLogicTest {
	
	private static SearchInfoDBLogic LOGIC;
	
	/**
	 * retrieves the logic from the config
	 */
	@BeforeClass
	public static final void setLogic() {
		LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(SearchInfoDBLogic.class);
	}
	
	/**
	 * tests {@link SearchInfoDBLogic#getUserNamesThatShareDocumentsWithUser(String)}
	 */
	@Test
	public void testGetUserNamesThatShareDocumentsWithUser() {
		final Set<String> users = LOGIC.getUserNamesThatShareDocumentsWithUser("testuser1");
		assertEquals(0, users.size());
	}
}
