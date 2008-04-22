package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class StatisticsDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private List<Integer> visibleGroupIDs;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.visibleGroupIDs = new ArrayList<Integer>();
	}

	/**
	 * tests getNumberOfResourcesForUser
	 */
	@Test
	public void getNumberOfResourcesForUser() {
		// testuser1 has got 6 bookmarks and 2 bibtexs
		assertEquals(6, this.statisticsDb.getNumberOfResourcesForUser(Bookmark.class, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));

		// testuser2 has one private bibtex
		assertEquals(1, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser2", this.visibleGroupIDs, this.dbSession));
		assertEquals(0, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", this.visibleGroupIDs, this.dbSession));
		this.visibleGroupIDs.add(1); // private group
		assertEquals(1, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", this.visibleGroupIDs, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForGroup
	 */
	@Test
	public void getNumberOfResourcesForGroup() {
		assertEquals(4, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, ParamUtils.TESTGROUP1, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, ParamUtils.TESTGROUP2, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, ParamUtils.TESTGROUP3, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, ParamUtils.TESTGROUP1, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, ParamUtils.TESTGROUP2, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, ParamUtils.TESTGROUP3, this.visibleGroupIDs, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForTags
	 */
	@Test
	public void getNumberOfResourcesForTags() {
		assertEquals(3, this.statisticsDb.getNumberOfResourcesForTags(Bookmark.class, Arrays.asList("suchmaschine"), this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForTags(BibTex.class, Arrays.asList("bibsonomy"), this.visibleGroupIDs, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForUserAndTags
	 */
	@Test
	public void getNumberOfResourcesForUserAndTags() {
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUserAndTags(Bookmark.class, Arrays.asList("suchmaschine"), "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUserAndTags(BibTex.class, Arrays.asList("bibsonomy"), "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
	}
}