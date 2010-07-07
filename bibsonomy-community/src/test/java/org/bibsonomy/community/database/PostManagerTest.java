package org.bibsonomy.community.database;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
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
import org.bibsonomy.community.util.PropertyLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for recommender's DBAccess class
 * @author fei
 * @version $Id$
 */
public class PostManagerTest {
	private static final Log log = LogFactory.getLog(PostManagerTest.class);
	
	BibTexPostManager bibTexLogic;
	BookmarkPostManager bookmarkLogic;
	
	@Before
	public void setUp() throws IOException {
		// bind datasource access via JNDI
		
		Properties config = PropertyLoader.openPropertyFile("community.properties");

		String url               = config.getProperty("url");
		String postDatabase      = config.getProperty("postDatabase");
		String communityDatabase = config.getProperty("communityDatabase");
		String username          = config.getProperty("username");
		String password          = config.getProperty("password");
		
		// initialize database manager
		JNDITestDatabaseBinder.bind("bibsonomy_community", url, communityDatabase, username, password);
		JNDITestDatabaseBinder.bind("bibsonomy_community_posts", url, postDatabase, username, password);
		
		// JNDITestDatabaseBinder.bind();
		bibTexLogic = BibTexPostManager.getInstance();
		bookmarkLogic = BookmarkPostManager.getInstance();
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}

	
	/**
	 * Test adding new algorithm
	 * @throws Exception 
	 */
	@Test
	public void testBibTexPosts() throws Exception {
		Collection<Post<BibTex>> posts = this.bibTexLogic.getPostsForCommunity(278, Ordering.POPULAR, 10, 0);
		log.debug("Got " + posts.size() + " posts.");
	}	

	/**
	 * Test adding new algorithm
	 * @throws Exception 
	 */
	@Test
	public void testBookmarkPosts() throws Exception {
		Collection<Post<Bookmark>> posts = this.bookmarkLogic.getPostsForCommunity(61, Ordering.POPULAR, 10, 0);
		log.debug("Got " + posts.size() + " posts.");
	}	
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
}
