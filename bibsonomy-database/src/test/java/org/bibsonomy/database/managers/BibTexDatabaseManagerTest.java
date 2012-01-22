package org.bibsonomy.database.managers;

import static org.bibsonomy.testutil.Assert.assertByTagNames;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.testutil.DBTestUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to BibTex.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexDatabaseManagerTest extends PostDatabaseManagerTest<BibTex> {
	
	private static BibTexDatabaseManager publicationDb;
	private static BibTexExtraDatabaseManager bibTexExtraDb;
	private static final User loginUser = new User("testuser1");
	
	/**
	 * sets up the used managers
	 */
	@BeforeClass
	public static void setupDatabaseManager() {
		bibTexExtraDb = BibTexExtraDatabaseManager.getInstance();
		publicationDb = BibTexDatabaseManager.getInstance();
	}
	
	@Override
	public void setMananger() {
		resourceDB = BibTexDatabaseManager.getInstance();
	}
	
	/**
	 * tests getPostsByHash
	 */
	@Override
	public void testGetPostsByHash() {
		printMethod("testGetPostsByHash");
		final String hash0 = "9abf98937435f05aec3d58b214a2ac58";
		final String hash1 = "097248439469d8f5a1e7fad6b02cbfcd";
		final String hash2 = "b77ddd8087ad8856d77c740c8dc2864a";
		// get post with SIM_HASH0 = hash0
		final List<Post<BibTex>> posts = publicationDb.getPostsByHash(null, hash0, HashID.SIM_HASH0, PUBLIC_GROUP_ID, null, 10, 0, this.dbSession);
		assertNotNull(posts);
		assertEquals(1, posts.size());
		assertEquals(1, posts.get(0).getGroups().size());
		
		// check inter- and intra hash
		assertEquals(hash1, posts.get(0).getResource().getInterHash()); 
		assertEquals(hash2, posts.get(0).getResource().getIntraHash());
	}
	
	@Override
	public void testGetPostsFromInbox() {
		printMethod("testGetPostsFromInbox");
		publicationDb.getPostsFromInbox("", 10, 0, this.dbSession);
		// TODO: implement more checks
	}

	/**
	 * Check if the getPostsByKey() method returns the correct
	 * bibtexkey from the database.
	 */
	@Override
	public void testGetPostsByHashCount() {
		printMethod("testGetPostsByHashCount");
		final String hash0 = "9abf98937435f05aec3d58b214a2ac58";
		final int count = publicationDb.getPostsByHashCount(hash0, HashID.SIM_HASH0, this.dbSession);
		assertEquals(1, count);
	}

	/**
	 * tests getPostsByHashForUser
	 */
	@Override
	public void testGetPostsByHashForUser() {
		printMethod("testGetPostsByHashForUser");
		// no hash => no post
		List<Post<BibTex>> posts;
		String loginUserName = "";
		String requestedUserName = "testuser1";
		String intraHash = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>(0); // TODO: create an arraylist with capacity 0 or add public id to list?!?
		posts = publicationDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(0, posts.size());
		
		// check inter & simhash0 for a intrahash
		intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		posts = publicationDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(1, posts.size());
		assertEquals("097248439469d8f5a1e7fad6b02cbfcd", posts.get(0).getResource().getInterHash());
		assertEquals("9abf98937435f05aec3d58b214a2ac58", posts.get(0).getResource().getSimHash0());

		// user == friend, existing hash and no spammer
		loginUserName = "testuser1";
		requestedUserName = "testuser1";
		intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		posts = publicationDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals(1, posts.size());
		assertEquals("097248439469d8f5a1e7fad6b02cbfcd", posts.get(0).getResource().getInterHash());
		assertEquals("9abf98937435f05aec3d58b214a2ac58", posts.get(0).getResource().getSimHash0());
		
		// testuser1 and testuser2 are member of group 3
		visibleGroupIDs.add(TESTGROUP1_ID);
		loginUserName = "testuser2";
		requestedUserName = "testuser1";
		intraHash = "522833042311cc30b8775772335424a7";
		posts = publicationDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals("097248439469d8f5a1e7fad6b02cbfcd", posts.get(0).getResource().getInterHash());
		assertEquals("92e8d9c7588eced69419b911b31580ee", posts.get(0).getResource().getSimHash0());
		
		// no hash => no post
		loginUserName = "testspammer";
		requestedUserName = "testspammer";
		intraHash = "";
		posts = publicationDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals(0, posts.size());
		
		// spammer are able to see own post
		intraHash = "65e49a5791c3dae2356d26fb9040fe29";
		posts = publicationDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(1, posts.size());
		assertEquals("b386bdfc8ac7b76ca96e6784736c4b95", posts.get(0).getResource().getSimHash0());
		
		loginUserName = "";
		requestedUserName = "testuser1";
		posts = publicationDb.getPostsByHashForUser("testuser1", intraHash, "testspammer", visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(0, posts.size());
	}

	/**
	 * tests getPostsByTagNames
	 */
	@Override
	public void testGetPostsByTagNames() {
		printMethod("testGetPostsByTagNames");
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testtag");
		final List<Post<BibTex>> posts = publicationDb.getPostsByTagNames(PUBLIC_GROUP_ID, tagIndex, null, 10, 0, this.dbSession);
		assertEquals(1, posts.size());
		assertByTagNames(tagIndex, posts);
	}
	
	/**
	 * tests getPostsByTagNamesCount
	 */
	@Override
	public void testGetPostsByTagNamesCount() {
		printMethod("testGetPostsByTagNamesCount");
		final List<TagIndex> tags = DBTestUtils.getTagIndex("testtag");
		final int count1 = publicationDb.getPostsByTagNamesCount(tags, PUBLIC_GROUP_ID, this.dbSession);
		assertEquals(1, count1);
		DBTestUtils.addToTagIndex(tags, "testbibtex");
		final int count2 = publicationDb.getPostsByTagNamesCount(tags, PUBLIC_GROUP_ID, this.dbSession);
		assertEquals(1, count2);
	}

	/**
	 * tests testGetPostsByTagNamesForUser
	 */
	@Override
	public void testGetPostsByTagNamesForUser() {
		printMethod("testGetPostsByTagNamesForUser");
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testtag");
		List<Post<BibTex>> posts = publicationDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex, PUBLIC_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		//this.assertByTagNames(posts); // no param?!?

		posts = publicationDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex, INVALID_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		final List<TagIndex> tagIndex2 = new ArrayList<TagIndex>();
		tagIndex2.add(new TagIndex("privatebibtex", 1));
		posts = publicationDb.getPostsByTagNamesForUser(null, "testuser2", tagIndex2, PRIVATE_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		final List<TagIndex> tagIndex3 = new ArrayList<TagIndex>();
		tagIndex3.add(new TagIndex("friendbibtex", 1));
		posts = publicationDb.getPostsByTagNamesForUser(null, "testuser2", tagIndex3, FRIENDS_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		final List<TagIndex> tagIndex4 = new ArrayList<TagIndex>();
		tagIndex4.add(new TagIndex("bibtexgroup", 1));
		posts = publicationDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex4, TESTGROUP1_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		// just call the sql statements
		publicationDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex4, TESTGROUP1_ID, new LinkedList<Integer>(), 10, 0, FilterEntity.POSTS_WITH_DOCUMENTS, null, this.dbSession);
		publicationDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex4, TESTGROUP1_ID, new LinkedList<Integer>(), 10, 0, FilterEntity.JUST_PDF, null, this.dbSession);
	}

	/**
	 * Prints the name of the called method. Used during debugging - to find, if
	 * a test modifies posts another test needs.
	 *  
	 * @param method
	 */
	private void printMethod(final String method) {
		//System.out.println(method + "()");
	}
	
	/**
	 * tests getPostsByConceptForUser
	 */
	@Override
	public void testGetPostsByConceptForUser() {
		printMethod("testGetPostsByConceptForUser");
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(PUBLIC_GROUP_ID);
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testbibtex");
		String requestedUserName = "testuser1";
		boolean caseSensitive = false;
		
		List<Post<BibTex>> posts = publicationDb.getPostsByConceptForUser(null, requestedUserName, visibleGroupIDs, tagIndex, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		
		String loginUser = "testuser1";
		posts = publicationDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
		
		visibleGroupIDs.add(TESTGROUP1_ID); // testuser1 & testuser2 are members of group 3 (testgroup1)
		loginUser = "testuser2";
		posts = publicationDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex, caseSensitive, 10, 0, null, this.dbSession);
		assertNotNull(posts);
		assertEquals(2, posts.size());
		
		final List<TagIndex> tagIndex2 = DBTestUtils.getTagIndex("friendbibtex");
		loginUser = "testuser1";
		requestedUserName = "testuser2";
		posts = publicationDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex2, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		
		// test it with casesensitive and caseinsensitive tagnames
		final List<TagIndex> tagIndex3 = DBTestUtils.getTagIndex("TESTbibTEX");

		List<Post<BibTex>> post2 = publicationDb.getPostsByConceptForUser(null, "testuser1", visibleGroupIDs, tagIndex3, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(1, post2.size());
		caseSensitive = true;
		post2 = publicationDb.getPostsByConceptForUser(null, "testuser1", visibleGroupIDs, tagIndex3, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(0, post2.size());
	}

	/**
	 * tests getPostsByUserFriends
	 */
	@Override
	public void testGetPostsByUserFriends() {
		printMethod("testGetPostsByUserFriends");
		final List<Post<BibTex>> post = publicationDb.getPostsByUserFriends("testuser1", HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests testGetPostsFromBasketForUser
	 */
	@Override
	public void testGetPostsFromBasketForUser() {
		printMethod("testGetPostsFromBasketForUser");
		List<Post<BibTex>> posts = publicationDb.getPostsFromBasketForUser("testuser1", Integer.MAX_VALUE, 0, this.dbSession);
		assertEquals(2, posts.size());
		
		posts = publicationDb.getPostsFromBasketForUser("testuser2", Integer.MAX_VALUE, 0, this.dbSession);
		assertEquals(2, posts.size());
	}

	/**
	 * tests getPostsForHomepage
	 */
	@Override
	public void testGetPostsForHomepage() {
		printMethod("testGetPostsForHomepage");
		final List<Post<BibTex>> post = publicationDb.getPostsForHomepage(null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
	}

	/**
	 * tests testGetPostsPopular
	 */
	@Override
	public void testGetPostsPopular() {
		printMethod("testGetPostsPopular");
		final List<Post<BibTex>> l = publicationDb.getPostsPopular(0, 10, 0, HashID.INTER_HASH, this.dbSession);
		assertEquals(1, l.size());
	}

	/**
	 * tests testGetPostsViewable
	 * 
	 * if groupId is special (>= 0 and <3) you have to set loginUserName
	 * if groupId is not special, loginUserName is checked by chain, you don't need it
	 */
	@Override
	public void testGetPostsViewable() {
		printMethod("testGetPostsViewable");
		String requestedGroupName = "public";
		String loginUserName = "testuser1";
		List<Post<BibTex>> post = publicationDb.getPostsViewable(requestedGroupName, loginUserName, PUBLIC_GROUP_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		requestedGroupName = "testgroup1";
		post = publicationDb.getPostsViewable(requestedGroupName, null, TESTGROUP1_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		requestedGroupName = "private";
		loginUserName = "testuser2";
		post = publicationDb.getPostsViewable(requestedGroupName, loginUserName, PRIVATE_GROUP_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		requestedGroupName = "";
		loginUserName = "testuser1";
		post = publicationDb.getPostsViewable(requestedGroupName, loginUserName, INVALID_GROUP_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(0, post.size());

	}

	/**
	 * tests testGetPostsDuplicate
	 */
	@Test
	public void testGetPostsDuplicate() {
		printMethod("testGetPostsDuplicate");
		final List<Post<BibTex>> post = publicationDb.getPostsDuplicate("testuser1", Collections.singletonList(PUBLIC_GROUP_ID), HashID.INTER_HASH, this.dbSession, null);
		assertEquals(1, post.size());
	}

	/**
	 * tests testGetPostsDuplicateCount
	 */
	@Test
	public void testGetPostsDuplicateCount() {
		printMethod("testGetPostsDuplicateCount");
		final int count = publicationDb.getPostsDuplicateCount("testuser1", this.dbSession);
		assertEquals(1, count);
	}

	/**
	 * tests testGetPostsForUsersInGroup
	 */
	@Override
	public void testGetPostsForGroup() {
		printMethod("testGetPostsForGroup");
		List<Integer> groups = Collections.singletonList(PUBLIC_GROUP_ID);
		
		String loginUserName = "testuser1";
		List<Post<BibTex>> post = publicationDb.getPostsForGroup(3, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(3, post.size());
		
		post = publicationDb.getPostsForGroup(TESTGROUP2_ID, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
		
		loginUserName = "testuser2";
		post = publicationDb.getPostsForGroup(TESTGROUP1_ID, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(3, post.size());
		
		post = publicationDb.getPostsForGroup(TESTGROUP2_ID, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		groups = new ArrayList<Integer>();
		post = publicationDb.getPostsForGroup(TESTGROUP2_ID, groups, null, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = publicationDb.getPostsForGroup(TESTGROUP1_ID, groups, null, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		// just call the statements
		publicationDb.getPostsForGroup(TESTGROUP1_ID, groups, null, HashID.INTER_HASH, FilterEntity.JUST_PDF, 10, 0, null, this.dbSession);
		publicationDb.getPostsForGroup(TESTGROUP1_ID, groups, null, HashID.INTER_HASH, FilterEntity.POSTS_WITH_DOCUMENTS, 10, 0, null, this.dbSession);
	}

	/**
	 * tests testGetPostsForGroupCount
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 */
	@Override
	public void testGetPostsForGroupCount() {
		printMethod("testGetPostsForGroupCount");
		final String requestedUserName = "";
		final String loginUserName = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		final int count1 = publicationDb.getPostsForGroupCount(requestedUserName, loginUserName, 3, visibleGroupIDs, this.dbSession);
		assertEquals(1, count1);
		
		final int count2 = publicationDb.getPostsForGroupCount(requestedUserName, loginUserName, 4, visibleGroupIDs, this.dbSession);
		assertEquals(1, count2);
		
		final int count3 = publicationDb.getPostsForGroupCount(requestedUserName, loginUserName, 6, visibleGroupIDs, this.dbSession);
		assertEquals(0, count3);
	}

	/**
	 * tests testGetPostsForGroupByTag
	 * set userName or visibleGroups
	 */
	@Override
	public void testGetPostsForGroupByTag() {
		printMethod("testGetPostsForGroupByTag");
		final List<Integer> visibleGroupIDs = Collections.singletonList(PUBLIC_GROUP_ID);
		List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testbibtex");
		
		String loginUser = "testuser1";
		
		List<Post<BibTex>> post = publicationDb.getPostsForGroupByTag(TESTGROUP2_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
		
		DBTestUtils.addToTagIndex(tagIndex, "testtag");
		post = publicationDb.getPostsForGroupByTag(TESTGROUP2_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		tagIndex = DBTestUtils.getTagIndex("privatebibtex");
		loginUser = "testuser2";
		post = publicationDb.getPostsForGroupByTag(TESTGROUP2_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(0, post.size());
		
		post = publicationDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		tagIndex = Collections.singletonList(new TagIndex("friendbibtex", 1));
		
		post = publicationDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		// just call the sql statements
		publicationDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, loginUser, tagIndex, FilterEntity.POSTS_WITH_DOCUMENTS, 10, 0, null, this.dbSession);
		publicationDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, loginUser, tagIndex, FilterEntity.JUST_PDF, 10, 0, null, this.dbSession);
	}

	/**
	 * tests testGetPostsForUser
	 */
	@Override
	public void testGetPostsForUser() {
		printMethod("testGetPostsForUser");
		String requestedUserName = "testuser1";
		final List<Integer> groups = new ArrayList<Integer>();
		
		List<Post<BibTex>> post = publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, TESTGROUP1_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, PUBLIC_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, INVALID_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
		
		groups.add(PUBLIC_GROUP_ID);
		post = publicationDb.getPostsForUser("testuser2", requestedUserName, HashID.INTRA_HASH, TESTGROUP1_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		groups.clear();
		
		requestedUserName = "testuser2";
		post = publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, PRIVATE_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, FRIENDS_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		// just call the statements
		publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, FRIENDS_GROUP_ID, groups, FilterEntity.JUST_PDF, 10, 0, null, this.dbSession);
		publicationDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, FRIENDS_GROUP_ID, groups, FilterEntity.POSTS_WITH_DOCUMENTS, 10, 0, null, this.dbSession);
	}

	/**
	 * Check if documents are proper attached to posts
	 */
	@Test
	public void getPublicationForUserWithDocuments() {		
		printMethod("getPublicationForUserWithDocuments");
		final List<Post<BibTex>> posts = publicationDb.getPostsForUser("testuser1", "testuser1", HashID.INTER_HASH, PUBLIC_GROUP_ID, Collections.singletonList(PUBLIC_GROUP_ID), FilterEntity.POSTS_WITH_DOCUMENTS, 10, 0, null, this.dbSession);
		
		// testuser 1 has 1 public post
		assertEquals(1, posts.size());
		
		// this post has two documents
		final List<Document> documents = posts.get(0).getResource().getDocuments();
		assertEquals(2, documents.size());
		// order might matter .. then the following assertions fail -> disable them
		assertEquals("00000000000000000000000000000000", documents.get(0).getMd5hash());
		assertEquals("00000000000000000000000000000001", documents.get(1).getMd5hash());
	}
	
	/**
	 * tests testGetPostsForUserCount
	 * 
	 * groupId or
	 * visibleGroupIDs && userName && (userName != requestedUserName)
	 */
	@Override
	public void testGetPostsForUserCount() {
		printMethod("testGetPostsForUserCount");
		final String loginUserName = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		final int count1 = publicationDb.getPostsForUserCount("testuser1", loginUserName, TESTGROUP1_ID, visibleGroupIDs, this.dbSession);
		assertEquals(1, count1);
		
		final int count2 = publicationDb.getPostsForUserCount("testuser2", loginUserName, PRIVATE_GROUP_ID, visibleGroupIDs, this.dbSession);
		assertEquals(1, count2);
	}

	/**
	 * tests getContentIdForBibTex
	 */
	@Test
	public void getContentIdForBibTex() {
		printMethod("getContentIdForBibTex");
		for (final String hash : new String[] { "", " ", null }) {
			for (final String username : new String[] { "", " ", null }) {
				try {
					publicationDb.getContentIdForPost(hash, username, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ex) {
					// ignore
				}
			}
		}
		
		int contentId = publicationDb.getContentIdForPost("b77ddd8087ad8856d77c740c8dc2864a", "testuser1", this.dbSession);
		assertEquals(10, contentId);
		
		contentId = publicationDb.getContentIdForPost("1b298f199d487bc527a62326573892b8", "testuser2", this.dbSession);
		assertEquals(13, contentId);
	}

	/**
	 * tests getPosts
	 */
	@Test
	public void testGetPosts() {
		printMethod("testGetPosts");
		final BibTexParam param = new BibTexParam();
		param.setHash("");
		
		List<Post<BibTex>> posts = publicationDb.getPosts(param, this.dbSession);
		assertEquals(2, posts.size());
		
		// setting group id to public shouldn't change anything
		param.setGroupId(PUBLIC_GROUP_ID);
		posts = publicationDb.getPosts(param, this.dbSession);
		assertEquals(2, posts.size());
	}

	
	/**
	 * generate a BibTex Post
	 * @throws PersonListParserException 
	 */
	private Post <BibTex> generateBibTexDatabaseManagerTestPost() throws PersonListParserException {
		final Post<BibTex> post = new Post<BibTex>();

		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		group.setGroupId(0);
		post.getGroups().add(group);
		
		ModelUtils.addToTagSet(post.getTags(), "tag1", "tag2");

		post.setContentId(null); // will be set in storePost()
		post.setDescription("trallalla");
		post.setDate(new Date());
		post.setChangeDate(new Date());
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		post.setUser(user);
		
		final BibTex publication = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(publication);
		publication.setCount(0);		
		publication.setEntrytype("inproceedings");
		publication.setAuthor(PersonNameUtils.discoverPersonNames("Testauthor, Hans and Testauthorin, Liese"));
		publication.setEditor(PersonNameUtils.discoverPersonNames("Silie, Peter"));		
		publication.setTitle("test friend title");
		publication.setYear("test year");
		publication.setJournal("test journal");
		publication.setBooktitle("test booktitle");
		publication.setVolume("test volume");
		publication.setNumber("test number");
		publication.setType("2");
		publication.recalculateHashes();
		
		post.setResource(publication);
		return post;
	}
	
	/**
	 * tests storePostBibTexUpdatePlugin
	 */
	// TODO: move to bibtex extra database test?
	@Ignore // FIXME: Test läuft nur einzeln erfolgreich
	@Test
	public void storePostBibTexUpdatePlugin() {
		printMethod("storePostBibTexUpdatePlugin");
		final String hash = "b77ddd8087ad8856d77c740c8dc2864a";		
		final String loginUserName = "testuser1";

		List<BibTexExtra> extras = bibTexExtraDb.getURL(hash, loginUserName, this.dbSession);
		assertEquals(1, extras.size());

		// TODO: ist das nicht immer public?
		// this.bibtexParam.setGroupType(GroupID.PRIVATE); 
		this.postDuplicate(hash);

		final Post<BibTex> post = publicationDb.getPostsByHash(null, hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, null, 10, 0, this.dbSession).get(0);
		assertNotNull(post);

		extras = bibTexExtraDb.getURL(hash, loginUserName, this.dbSession);
		// TODO: wieso 2 ? Duplicate macht nicht 2 daraus
		//assertEquals(2, extras.size());
		assertEquals(1, extras.size());
	}
	
	/**
	 * tests storePost
	 * @throws PersonListParserException 
	 */
	@Override
	public void testCreatePost()  {
		printMethod("testCreatePost");
		Post<BibTex> toInsert = null;
		try {
			toInsert = this.generateBibTexDatabaseManagerTestPost();
		} catch (final PersonListParserException ex) {
			fail("got exception: " + ex.getMessage());
		}
		toInsert.getResource().recalculateHashes();
		
		final String bibtexHashForUpdate = "14143c6508fe645ca312d0aa5d0e791b"; // INTRA-hash of toInsert

		publicationDb.createPost(toInsert, this.dbSession);
		
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { "tag1", "tag2" }), "", null, 0, 50, null, null, toInsert.getUser());
		param.setSimHash(HashID.INTRA_HASH);
		final List<Post<BibTex>> posts = publicationDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		CommonModelUtils.assertPropertyEquality(toInsert, posts.get(0), Integer.MAX_VALUE, null, new String[] { "resource", "tags", "user", "date", "changeDate"});
		toInsert.getResource().setCount(1);
		CommonModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), Integer.MAX_VALUE, null, new String[] { "openURL", "numberOfRatings", "rating"});

		// post a duplicate and check whether plugins are called		
		assertFalse(this.pluginMock.isOnBibTexUpdate());
		this.pluginMock.reset();
		
		this.postDuplicate(bibtexHashForUpdate);
		assertTrue(this.pluginMock.isOnBibTexUpdate());
		
		publicationDb.deletePost(toInsert.getUser().getName(), toInsert.getResource().getIntraHash(), this.dbSession);
	}
	
	/**
	 * tests assertDeleteBibTex
	 * @throws PersonListParserException 
	 */
	@Override
	public void testDeletePost() {
		printMethod("testDeletePost");
		assertFalse(this.pluginMock.isOnBibTexDelete());
		this.pluginMock.reset();

		// first: insert post such that we can delete it later
		
		Post<BibTex> toInsert = null;
		try {
			toInsert = this.generateBibTexDatabaseManagerTestPost();
		} catch (final PersonListParserException ex) {
			fail("got exception: " + ex.getMessage());
		}
		publicationDb.createPost(toInsert, this.dbSession);
		
		// delete public post		
		final String username = "testuser1";
		final String requestedUserName = username;
		final String hash = "14143c6508fe645ca312d0aa5d0e791b";
		
		List<Post<BibTex>> posts = publicationDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals(1, posts.size());
		
		boolean succ = publicationDb.deletePost(username, hash, this.dbSession);
		
		assertTrue("Post could not be deleted", succ);
		
		assertEquals(0, publicationDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession).size());
		assertTrue(this.pluginMock.isOnBibTexDelete());
		
		// delete private post
		toInsert.getGroups().clear();
		final Group group = GroupUtils.getPrivateGroup();
		toInsert.getGroups().add(group);
		
		final BibTexParam postParam = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { "tag1", "tag2" }), "", null, 0, 50, null, null, toInsert.getUser());
		List<Post<BibTex>> post2 = publicationDb.getPosts(postParam, this.dbSession);
		posts = publicationDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
		assertEquals(0, posts.size());
		assertEquals(0, post2.size());
		
		publicationDb.createPost(toInsert, this.dbSession);
		post2 = publicationDb.getPosts(postParam, this.dbSession);
		posts = publicationDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
		assertEquals(1, posts.size());
		assertEquals(1, post2.size());
		
		succ = publicationDb.deletePost(requestedUserName, hash, this.dbSession);
		assertTrue("Post could not be deleted", succ);
		
		assertEquals(0, publicationDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession).size());
	}

	/**
	 * tests storePostWrongUsage
	 * @throws PersonListParserException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void storePostWrongUsage() throws PersonListParserException {
		printMethod("storePostWrongUsage");
		final Post<BibTex> toInsert = this.generateBibTexDatabaseManagerTestPost();

		publicationDb.updatePost(toInsert, null, null, this.dbSession, loginUser);
	}

	/**
	 * Makes sure that we don't lose information if we change something on an
	 * existing post.
	 */
	@Test
	public void storePostDuplicate() {
		printMethod("storePostDuplicate");
		for (final String intraHash : new String[] {"b77ddd8087ad8856d77c740c8dc2864a"}) {

			final Post<BibTex> originalPost = publicationDb.getPostsByHash(null, intraHash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, null, 10, 0, this.dbSession).get(0);
			this.postDuplicate(intraHash);
			final Post<BibTex> newPost = publicationDb.getPostsByHash(null, intraHash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, null, 10, 0, this.dbSession).get(0);
			assertNotSame(originalPost.getContentId(), newPost.getContentId());
			assertEquals(originalPost.getDate().toString(), newPost.getDate().toString());
			assertEquals(originalPost.getDescription(), newPost.getDescription());
			assertEquals(originalPost.getGroups().size(), newPost.getGroups().size());
			assertEquals(originalPost.getTags().size(), newPost.getTags().size());
			assertEquals(originalPost.getUser().getName(), newPost.getUser().getName());
			assertEquals(originalPost.getResource().getSimHash0(), newPost.getResource().getSimHash0());
			assertEquals(originalPost.getResource().getSimHash1(), newPost.getResource().getSimHash1());
			assertEquals(originalPost.getResource().getSimHash2(), newPost.getResource().getSimHash2());
			assertEquals(originalPost.getResource().getSimHash3(), newPost.getResource().getSimHash3());
			
			// TODO: cannot get privnote with getPostsByHash, privnote is always null
			assertEquals(originalPost.getResource().getPrivnote(), newPost.getResource().getPrivnote());
			// TODO: more tests please...
		}
	}

	private void postDuplicate(final String hash) {
		final List<Post<BibTex>> someBibTexPost = publicationDb.getPostsByHash(null, hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, null, 10, 0, this.dbSession);
		assertEquals(1, someBibTexPost.size());
		// someBibTexPost.getGroups().clear();
		final Post<BibTex> publication = someBibTexPost.get(0);
		final int count = publication.getResource().getCount();
		publicationDb.updatePost(publication, hash, PostUpdateOperation.UPDATE_ALL, this.dbSession, loginUser);
		
		// check if resource counter is updated correctly
		final List<Post<BibTex>> afterUpdate = publicationDb.getPostsByHash(null, hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, null, 10, 0, this.dbSession);
		assertEquals(count, afterUpdate.get(0).getResource().getCount());
	}

	/**
	 * tests whether the query timeout specified in SqlMapConfig.xml works
	 * done by retrieving all publication entries of user dblp, which will take longer
	 * than 10 seconds
	 */
	@Ignore // we don't want to wait 10 seconds each time we run the tests, not possible for new local db
	@Test
	public void testQueryTimeout() {
		printMethod("testQueryTimeout");
		final BibTexParam bibtexParam = ParamUtils.getDefaultBibTexParam();
		bibtexParam.setUserName("dblp");
		bibtexParam.setRequestedUserName("dblp");
		bibtexParam.setLimit(100000000); 
		bibtexParam.setOffset(0);
		bibtexParam.setGroupId(PUBLIC_GROUP_ID);
		try {
			publicationDb.getPostsForUser(bibtexParam, this.dbSession);
			fail();
		} catch (final Exception e) {
			// timeout
		}
	}

	/**
	 * tests testGetPostsByConceptForGroup
	 */
	@Override
	public void testGetPostsByConceptForGroup() {
		printMethod("testGetPostsByConceptForGroup");
		publicationDb.getPostsByConceptForGroup("", Collections.<Integer>emptyList(), "", Collections.singletonList(new TagIndex("google", 0)), 10, 0, Collections.<SystemTag>emptyList(), this.dbSession);
		// TODO: add params to call add more asserts old test below
//		final BibTexParam param = new BibTexParam();
//		
//		param.addSimpleConceptName("clustering");
//		param.setRequestedGroupName("kde");
//		param.setUserName("hotho");
//		param.addGroup(PUBLIC_GROUP_ID);
//		
//		param.setGrouping(GroupingEntity.GROUP);
//		param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
//		
//		final List<Post<BibTex>> posts2 = bibTexDb.getPosts(param, this.dbSession);
//		assertEquals(10, posts2.size());
	}
	
	/**
	 * tests testGetPostsByKey
	 */
	@Test
	public void testGetPostsByBibTeXKey() {
		printMethod("testGetPostsByBibTeXKey");
		final String bibtexKey = "test %";
		final String requestedUserName = "testuser1";
		
		final List<Post<BibTex>> posts = publicationDb.getPostsByBibTeXKey(bibtexKey, requestedUserName, PUBLIC_GROUP_ID, 20, 0, null, this.dbSession);
		assertEquals(1,posts.size());
		assertEquals(posts.get(0).getResource().getBibtexKey(), "test bibtexKey");
	}
	
	/**
	 * tests {@link BibTexDatabaseManager#getPostsByFollowedUsers(String, List, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Override
	public void testGetPostsByFollowedUsers() {
		printMethod("testGetPostsByFollowedUsers");
		/*
		 * testuser 1 follows testuser 2, who has two posts.
		 */
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(PUBLIC_GROUP_ID);
		visibleGroupIDs.add(PRIVATE_GROUP_ID);
		visibleGroupIDs.add(FRIENDS_GROUP_ID);
		final List<Post<BibTex>> posts = publicationDb.getPostsByFollowedUsers("testuser1", visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(2, posts.size());
		assertEquals("testuser2", posts.get(0).getUser().getName());
		assertEquals("testuser2", posts.get(1).getUser().getName());
	}
	
	/**
	 * tests if {@link BibTexDatabaseManager#createPost(Post, org.bibsonomy.database.common.DBSession)}
	 * respects the max field length of table columns
	 * @throws PersonListParserException 
	 */
	@Test
	public void maxFieldLengthErrorCreatePost() throws PersonListParserException {
		printMethod("maxFieldLengthErrorCreatePost");
		final String longField = "1234567890ß1234567890ß1234567890ß1234567890ß1234567890ß"; // > 46
		/*
		 * create post
		 */
		final Post<BibTex> testPost = this.generateBibTexDatabaseManagerTestPost();
		final BibTex resource = testPost.getResource();
		resource.setTitle("Max Field Length in DB");
		resource.setAuthor(PersonNameUtils.discoverPersonNames("W. Walt"));
		resource.setYear(longField);
		resource.setMonth(longField);
		
		try {
			publicationDb.createPost(testPost, this.dbSession);
			fail("expected a DatabaseException");
		} catch (final DatabaseException ex) {
			final List<ErrorMessage> messages = ex.getErrorMessages(resource.getIntraHash());
			assertEquals(1, messages.size());
			
			assertEquals(FieldLengthErrorMessage.class, messages.get(0).getClass());
		}
	}
	
	/**
	 * tests if {@link BibTexDatabaseManager#updatePost(Post, String, PostUpdateOperation, org.bibsonomy.database.common.DBSession)}
	 * respects the max field length of table columns
	 */
	@Test
	public void maxFieldLengthErrorUpdatePost() {
		printMethod("maxFieldLengthErrorUpdatePost");
		final String longField = "1234567890ß1234567890ß1234567890ß1234567890ß1234567890ß";
		/*
		 * update post
		 */
		final String userName = "testuser1";
		final String intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final List<Integer> groups = Collections.singletonList(PUBLIC_GROUP_ID);
		
		final List<Post<BibTex>> updatePosts = publicationDb.getPostsByHashForUser(userName, intraHash, userName, groups, HashID.INTRA_HASH, this.dbSession);
		
		assertEquals(1, updatePosts.size());
		
		final Post<BibTex> updatePost = updatePosts.get(0);
		
		final BibTex updateResource = updatePost.getResource();
		updateResource.setMonth(longField);
		
		try {
			publicationDb.updatePost(updatePost, updateResource.getIntraHash(), PostUpdateOperation.UPDATE_ALL, this.dbSession, loginUser);
			fail("expected a DatabaseException");
		} catch (final DatabaseException ex) {
			final List<ErrorMessage> messages = ex.getErrorMessages(updateResource.getIntraHash());
			assertEquals(1, messages.size());
			
			assertEquals(FieldLengthErrorMessage.class, messages.get(0).getClass());
		}
	}
	
	/**
	 * tests logged posts
	 */
	@Test
	public void testLoggedPostsRedirect() {
		printMethod("testLoggedPostsRedirect");
		/*
		 * Post history:
		 * content_id 	intrahash
		 * 17			b71d5283dc7f4f59f306810e73e9bc9a
		 * 18			e2fb0763068b21639c3e36101f64aefe
		 * 19			b71d5283dc7f4f59f306810e73e9bc9a
		 * 20			891518b4900cd1832d77a0c8ae20dd14
		 */
		try {
			publicationDb.getPostDetails("testuser1", "b71d5283dc7f4f59f306810e73e9bc9a", "testuser3", Collections.singletonList(PUBLIC_GROUP_ID), this.dbSession);
			fail("expected ResourceMovedException");
		} catch (final ResourceMovedException e) {
			/*
			 * The requested hash appears twice.
			 * We want to ensure, that we get the post with content_id 20, i.e., 
			 * the one after the latest post with the requested hash.  
			 */
			assertEquals("891518b4900cd1832d77a0c8ae20dd14", e.getNewIntraHash());
		}
		
		try {
			publicationDb.getPostDetails("testuser1", "e2fb0763068b21639c3e36101f64aefe", "testuser3", Collections.singletonList(PUBLIC_GROUP_ID), this.dbSession);
			fail("expected ResourceMovedException");
		} catch (final ResourceMovedException e) {
			/*
			 * We get just the next hash.
			 */
			assertEquals("b71d5283dc7f4f59f306810e73e9bc9a", e.getNewIntraHash());
		}
		
	}

	@Override
	public void testGetPostsFromInboxByHash() {
		printMethod("testGetPostsFromInboxByHash");
		// TODO: dummy to execute sql statement; implement test
		publicationDb.getPostsFromInboxByHash("", "", this.dbSession);
	}

	@Override
	@Ignore
	public void testUpdatePost() {
		printMethod("testUpdatePost");
		// called by other methods
	}
}