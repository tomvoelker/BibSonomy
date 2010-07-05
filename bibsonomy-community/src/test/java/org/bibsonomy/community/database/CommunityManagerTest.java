package org.bibsonomy.community.database;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.algorithm.MockAlgorithm;
import org.bibsonomy.community.database.CommunityManager;
import org.bibsonomy.community.database.DBManageInterface;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.JNDITestDatabaseBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for recommender's DBAccess class
 * @author fei
 * @version $Id$
 */
public class CommunityManagerTest {
	private static final Log log = LogFactory.getLog(CommunityManagerTest.class);
	
	BibTexPostManager bibTexLogic;
	BookmarkPostManager bookmarkLogic;
	CommunityManager communityLogic; 
	
	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		bibTexLogic    = BibTexPostManager.getInstance();
		bookmarkLogic  = BookmarkPostManager.getInstance();
		communityLogic = CommunityManager.getInstance();
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}

	
	/**
	 * test retrieving all users for a given community
	 * @throws Exception
	 */
	@Test
	public void testCommunityList() throws Exception {
		Collection<Integer> communities = communityLogic.listCommunities(17);
	}
	
	@Test
	public void testCommunityUsers() throws Exception {
		Collection<String> users = communityLogic.getUserNamesForCommunity(17, 44, Ordering.POPULAR, 10, 0);
	}

	@Test
	public void testGetCommunities() throws Exception {
		Collection<ResourceCluster> communities = communityLogic.getCommunities(17, 25, 5, 5, 4, 0);
	}
//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
}
