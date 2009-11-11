package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.beans.TagIndex;
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
	@Ignore
	@Test
	public void getNumberOfResourcesForUser() {
		// testuser1 has got 6 bookmarks and 2 bibtexs
		assertEquals(6, this.statisticsDb.getNumberOfResourcesForUser(Bookmark.class, "testuser1", "testuser1", 0, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser1", "testuser1", 0, this.visibleGroupIDs, this.dbSession));

		// testuser2 has one private bibtex
		assertEquals(1, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser2", 0, this.visibleGroupIDs, this.dbSession));
		assertEquals(0, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", 0, this.visibleGroupIDs, this.dbSession));
		this.visibleGroupIDs.add(1); // private group
		assertEquals(1, this.statisticsDb.getNumberOfResourcesForUser(BibTex.class, "testuser2", "testuser1", 0, this.visibleGroupIDs, this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForGroup
	 */
	@Ignore
	@Test
	// FIXME: Test schlägt fehl!
	// FIXME: wenn visibleGroupIDs gesetzt ist, dann muss auch userName gesetzt sein
	public void getNumberOfResourcesForGroup() {
		assertEquals(4, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, null, null, ParamUtils.TESTGROUP1, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, null, null, ParamUtils.TESTGROUP2, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(Bookmark.class, null, null, ParamUtils.TESTGROUP3, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, null, null, ParamUtils.TESTGROUP1, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, null, null, ParamUtils.TESTGROUP2, this.visibleGroupIDs, this.dbSession));
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForGroup(BibTex.class, null, null, ParamUtils.TESTGROUP3, this.visibleGroupIDs, this.dbSession));
	}

	// FIXME: die beiden Methoden stellen sicher dass "bibsonomoy" 2 mal als tag auftaucht, in der db taucht bibsonomy kein einziges mal auf !?!
	/**
	 * tests getNumberOfResourcesForTags
	 */
	@Ignore
	@Test
	public void getNumberOfResourcesForTags() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();		
		TagIndex t1 = new TagIndex("suchmaschine",1);	
		TagIndex t2 = new TagIndex("bibsonomy",1);	
				
		tagIndex.add(t1);		
		assertEquals(3, this.statisticsDb.getNumberOfResourcesForTags(Bookmark.class, tagIndex, GroupID.PUBLIC.getId(), this.dbSession));
		
		tagIndex.clear();
		tagIndex.add(t2);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForTags(BibTex.class, tagIndex, GroupID.PUBLIC.getId(), this.dbSession));
	}

	/**
	 * tests getNumberOfResourcesForUserAndTags
	 */
	@Ignore
	@Test
	public void getNumberOfResourcesForUserAndTags() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();		
		TagIndex t1 = new TagIndex("suchmaschine",1);	
		TagIndex t2 = new TagIndex("bibsonomy",1);	
				
		tagIndex.add(t1);		
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUserAndTags(Bookmark.class,tagIndex, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
		
		tagIndex.clear();
		tagIndex.add(t2);
		assertEquals(2, this.statisticsDb.getNumberOfResourcesForUserAndTags(BibTex.class, tagIndex, "testuser1", "testuser1", this.visibleGroupIDs, this.dbSession));
	}
	
	@Ignore
	@Test
	public void getResourcesPopularDaysTest(){
		final int days = 2;
		assertTrue(this.statisticsDb.getPopularDays(BibTex.class, days, this.dbSession) != 0);
		assertTrue(this.statisticsDb.getPopularDays(Bookmark.class, days, this.dbSession) != 0);
	}
	
}