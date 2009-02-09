package org.bibsonomy.recommender;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.params.RecQueryParam;
import org.bibsonomy.recommender.params.RecSettingParam;
import org.junit.Test;

/**
 * Test case for recommender's DBAccess class
 * @author fei
 * @version $Id$
 */
public class DBAccessTest {
	private static final Logger log = Logger.getLogger(DBAccessTest.class);
/*
	public static void main( String[] args ) throws Exception {
		testAddQuery();
    }
*/
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
}
