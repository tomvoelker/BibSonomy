package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.extra.ExtendedFields;
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
	public void getNumResourcesForUser() {
		List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0); // public group
		int numPublications = this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, TEST_REQUESTED_USER, TEST_LOGIN_USER, visibleGroupIDs, this.dbSession);
		int numBookmarks = this.statisticsDb.getNumberOfResourcesForUser(Bookmark.class, TEST_REQUESTED_USER, TEST_LOGIN_USER, visibleGroupIDs, this.dbSession);
	}

}
