package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class StatisticsDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private final String TEST_REQUESTED_USER = "hotho";
	private final String TEST_LOGIN_USER = "dbenz";

	@Test
	public void getNumberOfResourcesForUser() {
		List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0); // public group
		int numPublications = this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, TEST_REQUESTED_USER, TEST_LOGIN_USER, visibleGroupIDs, this.dbSession);
		int numBookmarks = this.statisticsDb.getNumberOfResourcesForUser(Bookmark.class, TEST_REQUESTED_USER, TEST_LOGIN_USER, visibleGroupIDs, this.dbSession);
	}
}