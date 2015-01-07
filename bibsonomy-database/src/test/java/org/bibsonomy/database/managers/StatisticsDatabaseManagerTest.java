/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.testutil.DBTestUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class StatisticsDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static StatisticsDatabaseManager statisticsDb;
	
	@BeforeClass
	public static void setManager() {
		statisticsDb = StatisticsDatabaseManager.getInstance();
	}
	
	private List<Integer> visibleGroupIDs;
	
	@Before
	public void setUpVisibleGroups() {
		this.visibleGroupIDs = new ArrayList<Integer>();
	}
	
	/**
	 * tests getNumberOfResourcesForUser
	 */
	@Ignore
	@Test
	public void getNumberOfResourcesForUser() {
		// testuser1 has got 6 bookmarks and 2 bibtexs
		assertEquals(6, statisticsDb.getNumberOfResourcesForUser(Bookmark.class, "testuser1", "testuser1", 0, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser1", "testuser1", 0, this.visibleGroupIDs, this.dbSession));

		// testuser2 has one private bibtex
		assertEquals(1, statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser2", 0, this.visibleGroupIDs, this.dbSession));
		assertEquals(0, statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", 0, this.visibleGroupIDs, this.dbSession));
		
		this.visibleGroupIDs.add(PRIVATE_GROUP_ID);
		assertEquals(1, statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", 0, this.visibleGroupIDs, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForGroup
	 */
	@Ignore
	@Test
	// FIXME: Test schlägt fehl!
	// FIXME: wenn visibleGroupIDs gesetzt ist, dann muss auch userName gesetzt sein
	public void getNumberOfResourcesForGroup() {
		assertEquals(4, statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, null, null, ParamUtils.TESTGROUP1, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, null, null, ParamUtils.TESTGROUP2, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, null, null, ParamUtils.TESTGROUP3, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, statisticsDb.getNumberOfResourcesForGroup(BibTex.class, null, null, ParamUtils.TESTGROUP1, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, statisticsDb.getNumberOfResourcesForGroup(BibTex.class, null, null, ParamUtils.TESTGROUP2, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, statisticsDb.getNumberOfResourcesForGroup(BibTex.class, null, null, ParamUtils.TESTGROUP3, this.visibleGroupIDs, this.dbSession));
	}

	// FIXME: die beiden Methoden stellen sicher dass "bibsonomoy" 2 mal als tag auftaucht, in der db taucht bibsonomy kein einziges mal auf !?!
	/**
	 * tests getNumberOfResourcesForTags
	 */
	@Ignore
	@Test
	public void getNumberOfResourcesForTags() {
		List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");
			
		assertEquals(3, statisticsDb.getNumberOfResourcesForTags(Bookmark.class, tagIndex, PUBLIC_GROUP_ID, this.dbSession));
		
		tagIndex = DBTestUtils.getTagIndex("bibsonomy");
		assertEquals(2, statisticsDb.getNumberOfResourcesForTags(BibTex.class, tagIndex, PUBLIC_GROUP_ID, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForUserAndTags
	 */
	@Ignore
	@Test
	public void getNumberOfResourcesForUserAndTags() {
		List<TagIndex> tagIndex = DBTestUtils.getTagIndex("suchmaschine");	
		assertEquals(2, statisticsDb.getNumberOfResourcesForUserAndTags(Bookmark.class,tagIndex, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
		
		tagIndex = DBTestUtils.getTagIndex("bibsonomy");
		assertEquals(2, statisticsDb.getNumberOfResourcesForUserAndTags(BibTex.class, tagIndex, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
	}
	
	@Ignore
	@Test
	public void getResourcesPopularDaysTest(){
		final int days = 2;
		assertTrue(statisticsDb.getPopularDays(BibTex.class, days, this.dbSession) != 0);
		assertTrue(statisticsDb.getPopularDays(Bookmark.class, days, this.dbSession) != 0);
	}
	
}