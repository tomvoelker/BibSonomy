package org.bibsonomy.community.importer;

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
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.community.model.Cluster;
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
public class LDAkMeansImporterTest {
	private static final Log log = LogFactory.getLog(LDAkMeansImporterTest.class);


	private Algorithm algorithm = new MockAlgorithm("testDummyAlgorithm", "test");
	
	private static String FILE_USERMAP            = "clusterings/LDAkMeans/usernames.nospam";
	private static String FILE_CONTENTIDS         = "clusterings/LDAkMeans/contentIDs.reduced";
	private static String FILE_CLUSTERING         = "clusterings/LDAkMeans/bibsonomy_public_tas_nospammer_nodblp_filtered_20100127.mallet-LDA-100.csv-kMeans-75.clustering";
	private static String FILE_COMMUNITYTOPICS    = "clusterings/LDAkMeans/bibsonomy_public_tas_nospammer_nodblp_filtered_20100127.mallet-LDA-100.csv-kMeans-75.communityTopics";
	private static String FILE_COMMUNITYRESOURCES = "clusterings/LDAkMeans/bibsonomy_public_tas_nospammer_nodblp_filtered_20100127.mallet-LDA-100.csv-kMeans-75.resources";
	

	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	
	/**
	 * Test adding new algorithm
	 * @throws Exception 
	 */
	@Test
	@Ignore
	public void testImport() throws Exception {
		CommunityImporter importer = new LDAkMeansImporter(FILE_USERMAP, FILE_CONTENTIDS, FILE_CLUSTERING, FILE_COMMUNITYRESOURCES, FILE_COMMUNITYTOPICS);
	}	
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * Create an mockup post
	 */
	private static Post<? extends Resource> createPost() {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		bibtex.setYear("2009");
		bibtex.setBibtexKey("test");
		bibtex.setEntrytype("twse");
		post.setResource(bibtex);
		post.setContentId(new Integer(0));
		return post;
	}
	
		
}
