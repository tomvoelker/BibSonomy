package org.bibsonomy.recommender;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.params.RecQueryParam;
import org.bibsonomy.recommender.params.RecSettingParam;
import org.bibsonomy.recommender.params.SelectorSettingParam;
import org.junit.Test;

/**
 * Test case for recommender's DBAccess class
 * @author fei
 * @version $Id$
 */
public class DBAccessTest {
	private static final Logger log = Logger.getLogger(DBAccessTest.class);

	/**
	 * Method for interactive testing.
	 */
	
	public static void main( String[] args ) throws Exception {
		DBAccessTest obj = new DBAccessTest(); 
		obj.testAddNewSelector2();
		obj.testAddNewSelector();
		obj.testAddSelectedTags();
    }
	
	/**
	 * Test registering a new recommender
	 * @throws SQLException 
	 */
	@Test
	public void testAddQuery() throws SQLException {
		Post<? extends Resource> post = createPost();
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		// store and retrieve query
		Long qid = DBAccess.addQuery(post.getUser().getName(), ts, post);
		RecQueryParam retVal = DBAccess.getQuery(qid);
		
		String    queryUN = retVal.getUserName();
		assertEquals(post.getUser().getName(), queryUN);
	}
	
	/**
	 * Test registering a new recommender
	 * @throws SQLException 
	 */
	@Test
	public void testAddNewRecommender() throws SQLException  {
		Long qid       = new Long(0);
		String recInfo = "TestCase-non-Recommender";
		String recMeta = "NON-NULL-META";
		// store and retrieve recommender informations
		Long sid;
		RecSettingParam retVal = null;
		sid = DBAccess.addRecommender(qid, recInfo, recMeta.getBytes());
		retVal = DBAccess.getRecommender(sid);
		assertEquals(recInfo, retVal.getRecId());
		assertArrayEquals(recMeta.getBytes(), retVal.getRecMeta());
	}

	/**
	 * Test registering a new selector
	 * @throws SQLException 
	 */
	@Test
	public void testAddNewSelector() throws SQLException  {
		Long qid       = new Long(0);
		String selectorInfo = "TestCase-non-Selector";
		String selectorMeta = "NON-NULL-META";
		// store and retrieve recommender informations
		Long sid = null;
		SelectorSettingParam retVal = null;
		sid = DBAccess.addResultSelector(qid, selectorInfo, selectorMeta.getBytes());
		retVal = DBAccess.getSelector(sid);
		assertEquals(selectorInfo, retVal.getInfo());
		assertArrayEquals(selectorMeta.getBytes(), retVal.getMeta());
	}	
	
	/**
	 * Test registering a new selector
	 * @throws SQLException 
	 */
	@Test
	public void testAddNewSelector2() throws SQLException  {
		Long qid       = new Long(0);
		String selectorInfo = "TestCase-non-Selector";
		byte[] selectorMeta = null;
		// store and retrieve recommender informations
		Long sid = null;
		SelectorSettingParam retVal = null;
		sid = DBAccess.addResultSelector(qid, selectorInfo, selectorMeta);
		retVal = DBAccess.getSelector(sid);
		assertEquals(selectorInfo, retVal.getInfo());
		assertArrayEquals(null, retVal.getMeta());
	}		
	/**
	 * Test adding selected tags.
	 * @throws SQLException 
	 */
	@Test
	public void testAddSelectedTags() throws SQLException  {
		Long qid = new Long(0);
		Long rid = new Long(0);
		int nr = 5;
		
		// create tags
		SortedSet<RecommendedTag> tags = createRecommendedTags(nr);
		// store tags
		int count = DBAccess.storeRecommendation(qid, rid, tags);
		// fetch tags
		List<RecommendedTag> result = DBAccess.getSelectedTags(new Long(0));
		
		// compare tags
		SortedSet<RecommendedTag> sort = new TreeSet<RecommendedTag>();
		assertEquals(nr, count);
		sort.addAll(result);
		int i=0;
		for( RecommendedTag tag : sort ) {
			assertEquals(tag.getName(), "Tag"+(new Integer(i)).toString());
			assertEquals((1.0*i)/count, tag.getScore());
			assertEquals(1.0/count, tag.getConfidence());
		}
	}
	
	/**
	 * Test adding recommender response
	 * @throws SQLException 
	 */
	@Test
	public void testAddRecommenderResult() throws SQLException  {
		/*
		*/
	}	
	
	/**
	 * Test registering an already known recommender
	 */
	@Test
	public void testAddKnownRecommender() {
	}
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * Create an mockup post
	 */
	private static Post<? extends Resource> createPost() {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag();
		tag.setName("foobar");
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

		return post;
	}
	
	/**
	 * Create list of recommended tags.
	 * 
	 * @return
	 */
	protected SortedSet<RecommendedTag> createRecommendedTags(int nr) {
		TreeSet<RecommendedTag> extracted = new TreeSet<RecommendedTag>();

		// create informative recommendation:
		for( int i=0; i<nr; i++) {
			double score = (1.0*i)/nr;
			double confidence = 1.0/nr;
			DecimalFormat df = new DecimalFormat( "0.00" );
			String re = "Tag"+(new Integer(i)).toString();
			extracted.add(new RecommendedTag(re, score, confidence));
		};
		return extracted;
	}		
}
