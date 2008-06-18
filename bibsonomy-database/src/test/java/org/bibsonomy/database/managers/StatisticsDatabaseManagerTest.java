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
	/**@Test
	public void getNumberOfResourcesForUser() {
		// testuser1 has got 6 bookmarks and 2 bibtexs
		assertEquals(6, this.statisticsDb.getNumberOfResourcesForUser(Bookmark.class, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));

		// testuser2 has one private bibtex
		assertEquals(1, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser2", this.visibleGroupIDs, this.dbSession));
		assertEquals(0, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", this.visibleGroupIDs, this.dbSession));
		this.visibleGroupIDs.add(1); // private group
		assertEquals(1, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", this.visibleGroupIDs, this.dbSession));
	}*/

	/**
	 * tests getNumberOfResourcesForGroup
	 */
	@Test
	public void getNumberOfResourcesForGroup() {
		this.statisticsParam.setGroupId(ParamUtils.TESTGROUP1);
		this.statisticsParam.setGroups(this.visibleGroupIDs);
		assertEquals(4, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, this.statisticsParam, this.dbSession));
		this.statisticsParam.setGroupId(ParamUtils.TESTGROUP2);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, this.statisticsParam, this.dbSession));
		this.statisticsParam.setGroupId(ParamUtils.TESTGROUP3);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, this.statisticsParam, this.dbSession));
		this.statisticsParam.setGroupId(ParamUtils.TESTGROUP1);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, this.statisticsParam, this.dbSession));
		this.statisticsParam.setGroupId(ParamUtils.TESTGROUP2);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, this.statisticsParam, this.dbSession));
		this.statisticsParam.setGroupId(ParamUtils.TESTGROUP3);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, this.statisticsParam, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForTags
	 */
	@Test
	public void getNumberOfResourcesForTags() {
		this.statisticsParam.setGroups(this.visibleGroupIDs);
		this.statisticsParam.addTagName("suchmaschine");
		assertEquals(3, this.statisticsDb.getNumberOfResourcesForTags(Bookmark.class, this.statisticsParam, this.dbSession));
		
		this.statisticsParam.addTagName("bibsonomy");
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForTags(BibTex.class, this.statisticsParam, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForUserAndTags
	 */
/*	@Test
	public void getNumberOfResourcesForUserAndTags() {
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUserAndTags(Bookmark.class, Arrays.asList("suchmaschine"), "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUserAndTags(BibTex.class, Arrays.asList("bibsonomy"), "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
	}*/
}