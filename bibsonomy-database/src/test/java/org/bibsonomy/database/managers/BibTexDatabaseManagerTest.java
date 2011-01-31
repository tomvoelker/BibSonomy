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

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
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
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.Assert;
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
public class BibTexDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static BibTexDatabaseManager bibTexDb;
	private static BibTexExtraDatabaseManager bibTexExtraDb;
	
	/**
	 * sets up the used managers
	 */
	@BeforeClass
	public static void setupDatabaseManager() {
		bibTexExtraDb = BibTexExtraDatabaseManager.getInstance();
		bibTexDb = BibTexDatabaseManager.getInstance();
	}
	
	/**
	 * tests getPostsByHash
	 */
	@Test
	public void getBibTexByHash() {		
		final String hash0 = "9abf98937435f05aec3d58b214a2ac58";
		final String hash1 = "d9eea4aa159d70ecfabafa0c91bbc9f0";
		final String hash2 = "b77ddd8087ad8856d77c740c8dc2864a";
		// get post with SIM_HASH0 = hash0
		final List<Post<BibTex>> posts = bibTexDb.getPostsByHash(hash0, HashID.SIM_HASH0, PUBLIC_GROUP_ID, 10, 0, this.dbSession);
		assertNotNull(posts);
		assertEquals(1, posts.size());
		assertEquals(1, posts.get(0).getGroups().size());
		
		// check inter- and intra hash
		assertEquals(hash1, posts.get(0).getResource().getInterHash()); 
		assertEquals(hash2, posts.get(0).getResource().getIntraHash());
	}

	/**
	 * Check if the getPostsByKey() method returns the correct
	 * bibtexkey from the database.
	 */
	@Test
	public void getBibTexByHashCount() {
		String hash0 = "9abf98937435f05aec3d58b214a2ac58";
		final int count = bibTexDb.getPostsByHashCount(hash0, HashID.SIM_HASH0, this.dbSession);
		assertEquals(1, count);
	}

	/**
	 * tests getPostsByHashForUser
	 */
	@Test
	public void getBibTexByHashForUser() {
		// no hash => no post
		List<Post<BibTex>> posts;
		String loginUserName = "";
		String requestedUserName = "testuser1";
		String intraHash = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>(0); // TODO: create an arraylist with capacity 0 or add public id to list?!?
		posts = bibTexDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(0, posts.size());
		
		// check inter & simhash0 for a intrahash
		intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		posts = bibTexDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(1, posts.size());
		assertEquals("d9eea4aa159d70ecfabafa0c91bbc9f0", posts.get(0).getResource().getInterHash());
		assertEquals("9abf98937435f05aec3d58b214a2ac58", posts.get(0).getResource().getSimHash0());

		// user == friend, existing hash and no spammer
		loginUserName = "testuser1";
		requestedUserName = "testuser1";
		intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		posts = bibTexDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals(1, posts.size());
		assertEquals("d9eea4aa159d70ecfabafa0c91bbc9f0", posts.get(0).getResource().getInterHash());
		assertEquals("9abf98937435f05aec3d58b214a2ac58", posts.get(0).getResource().getSimHash0());
		
		// testuser1 and testuser2 are member of group 3
		visibleGroupIDs.add(TESTGROUP1_ID);
		loginUserName = "testuser2";
		requestedUserName = "testuser1";
		intraHash = "522833042311cc30b8775772335424a7";
		posts = bibTexDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals("d9eea4aa159d70ecfabafa0c91bbc9f0", posts.get(0).getResource().getInterHash());
		assertEquals("92e8d9c7588eced69419b911b31580ee", posts.get(0).getResource().getSimHash0());
		
		// no hash => no post
		loginUserName = "testspammer";
		requestedUserName = "testspammer";
		intraHash = "";
		posts = bibTexDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals(0, posts.size());
		
		// spammer are able to see own post
		intraHash = "65e49a5791c3dae2356d26fb9040fe29";
		posts = bibTexDb.getPostsByHashForUser(loginUserName, intraHash, requestedUserName, visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(1, posts.size());
		assertEquals("b386bdfc8ac7b76ca96e6784736c4b95", posts.get(0).getResource().getSimHash0());
		
		loginUserName = "";
		requestedUserName = "testuser1";
		posts = bibTexDb.getPostsByHashForUser("testuser1", intraHash, "testspammer", visibleGroupIDs, HashID.INTRA_HASH, this.dbSession);
		assertEquals(0, posts.size());
	}
	
	/**
	 * tests getPostsByAuthor
	 */
	@Test
	@Ignore // FIXME: Test läuft nur einzeln erfolgreich
	public void getBibTexByAuthor() {
		final String search = "author";
		final List<Post<BibTex>> post = bibTexDb.getPostsByAuthor(search, PUBLIC_GROUP_ID, "testuser1", "testgroup1", 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		// TODO: extend test with year, firstYear, lastYear
	}
	
	/**
	 * tests getPostsByAuthorAndTag
	 */
	@Ignore
	@Test
	public void getBibTexByAuthorAndTag() {
		final String search = "author";
		final String requestedGroupName = "testgroup1";
		final String requestedUserName = "testuser1";
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testtag");
		List<Post<BibTex>> post = bibTexDb.getPostsByAuthorAndTag(search, PUBLIC_GROUP_ID, requestedUserName, requestedGroupName, tagIndex, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		DBTestUtils.addToTagIndex(tagIndex, "testtag");
		post = bibTexDb.getPostsByAuthorAndTag(search, PUBLIC_GROUP_ID, requestedUserName, requestedGroupName, tagIndex, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		// TODO: extend test with year, firstYear, lastYear
	}

	/**
	 * tests getPostsByTagNames
	 */
	@Test
	public void getBibTexByTagNames() {
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testtag");
		final List<Post<BibTex>> posts = bibTexDb.getPostsByTagNames(PUBLIC_GROUP_ID, tagIndex, null, 10, 0, this.dbSession);
		assertEquals(1, posts.size());
		assertByTagNames(tagIndex, posts);
	}
	
	/**
	 * tests getPostsByTagNamesCount
	 */
	@Test
	public void getBibtexByTagNamesCount() {
		final List<TagIndex> tags = DBTestUtils.getTagIndex("testtag");
		final int count1 = bibTexDb.getPostsByTagNamesCount(tags, PUBLIC_GROUP_ID, this.dbSession);
		assertEquals(1, count1);
		DBTestUtils.addToTagIndex(tags, "testbibtex");
		final int count2 = bibTexDb.getPostsByTagNamesCount(tags, PUBLIC_GROUP_ID, this.dbSession);
		assertEquals(1, count2);
	}

	/**
	 * tests getBibTexByTagNamesForUser
	 */
	@Test
	public void getBibTexByTagNamesForUser() {
		final List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testtag");
		List<Post<BibTex>> posts = bibTexDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex, PUBLIC_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		//this.assertByTagNames(posts); // no param?!?

		posts = bibTexDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex, INVALID_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		final List<TagIndex> tagIndex2 = new ArrayList<TagIndex>();
		tagIndex2.add(new TagIndex("privatebibtex", 1));
		posts = bibTexDb.getPostsByTagNamesForUser(null, "testuser2", tagIndex2, PRIVATE_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		final List<TagIndex> tagIndex3 = new ArrayList<TagIndex>();
		tagIndex3.add(new TagIndex("friendbibtex", 1));
		posts = bibTexDb.getPostsByTagNamesForUser(null, "testuser2", tagIndex3, FRIENDS_GROUP_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
		
		final List<TagIndex> tagIndex4 = new ArrayList<TagIndex>();
		tagIndex4.add(new TagIndex("bibtexgroup", 1));
		posts = bibTexDb.getPostsByTagNamesForUser(null, "testuser1", tagIndex4, TESTGROUP1_ID, new LinkedList<Integer>(), 10, 0, null, null, this.dbSession);
		assertEquals(1, posts.size());
	}

	/**
	 * tests getPostsByConceptForUser
	 */
	@Test
	public void getBibTexByConceptForUser() {
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(PUBLIC_GROUP_ID);
		List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testbibtex");
		String requestedUserName = "testuser1";
		boolean caseSensitive = false;
		
		List<Post<BibTex>> posts = bibTexDb.getPostsByConceptForUser(null, requestedUserName, visibleGroupIDs, tagIndex, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		
		String loginUser = "testuser1";
		posts = bibTexDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(2, posts.size());
		
		visibleGroupIDs.add(TESTGROUP1_ID); // testuser1 & testuser2 are members of group 3 (testgroup1)
		loginUser = "testuser2";
		posts = bibTexDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex, caseSensitive, 10, 0, null, this.dbSession);
		assertNotNull(posts);
		assertEquals(2, posts.size());
		
		final List<TagIndex> tagIndex2 = DBTestUtils.getTagIndex("friendbibtex");
		loginUser = "testuser1";
		requestedUserName = "testuser2";
		posts = bibTexDb.getPostsByConceptForUser(loginUser, requestedUserName, visibleGroupIDs, tagIndex2, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(1, posts.size());
		
		// test it with casesensitive and caseinsensitive tagnames
		final List<TagIndex> tagIndex3 = DBTestUtils.getTagIndex("TESTbibTEX");

		List<Post<BibTex>> post2 = bibTexDb.getPostsByConceptForUser(null, "testuser1", visibleGroupIDs, tagIndex3, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(1, post2.size());
		caseSensitive = true;
		post2 = bibTexDb.getPostsByConceptForUser(null, "testuser1", visibleGroupIDs, tagIndex3, caseSensitive, 10, 0, null, this.dbSession);
		assertEquals(0, post2.size());
	}

	/**
	 * tests getPostsByUserFriends
	 */
	@Test
	public void getPublicationsByUserFriends() {
		List<Post<BibTex>> post = bibTexDb.getPostsByUserFriends("testuser1", HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBibTexFromBasketForUser
	 */
	@Test
	public void getPublicationsFromBasketForUser() {
		List<Post<BibTex>> posts = bibTexDb.getPostsFromBasketForUser("testuser1", Integer.MAX_VALUE, 0, this.dbSession);
		assertEquals(2, posts.size());
		
		posts = bibTexDb.getPostsFromBasketForUser("testuser2", Integer.MAX_VALUE, 0, this.dbSession);
		assertEquals(2, posts.size());
	}

	/**
	 * tests getBibTexForHomePage
	 */
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getPublicationsForHomepage() {
		List<Post<BibTex>> post = bibTexDb.getPostsForHomepage(null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
	}

	/**
	 * tests getBibTexPopular
	 */
	@Test
	public void getBibTexPopular() {
		List<Post<BibTex>> l = bibTexDb.getPostsPopular(0, 10, 0, HashID.INTER_HASH, this.dbSession);
		assertEquals(1, l.size());
	}

	/**
	 * tests getBibTexSearch
	 * 
	 * TODO: adapt to lucene search engine
	 */
	@Test
	@Ignore   // set to ignore because search is done now via lucene
	public void getBibTexSearch() {
		String search = "search string";
		String requestedUserName = "testuser1";
		List<Post<BibTex>> post = bibTexDb.getPostsSearch(PUBLIC_GROUP_ID, search, requestedUserName, 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		post = bibTexDb.getPostsSearch(PUBLIC_GROUP_ID, search, null, 10, 0, this.dbSession);
		assertEquals(1, post.size());
		
		// change words order -> no effect
		search = "test search bibtext string";
		post = bibTexDb.getPostsSearch(PUBLIC_GROUP_ID, search, requestedUserName, 10, 0, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBibTexSearchCount
	 * 
	 * TODO: adapt to lucene search engine
	 */
	@Test
	@Ignore   // set to ignore because search is done now via lucene
	public void getBibTexSearchCount() {
		final String search = "search string";
		
		final int count1 = bibTexDb.getPostsSearchCount(PUBLIC_GROUP_ID, search, "testuser1", this.dbSession);
		assertEquals(1, count1);

		final int count2 = bibTexDb.getPostsSearchCount(PUBLIC_GROUP_ID, search, null, this.dbSession);
		assertEquals(1, count2);
	}
	
	
	/**
	 * tests getBibtexSearchForGroup
	 * 
	 * groupId must be set
	 * userName must be set
	 * search must be set
	 * 
	 * TODO: adapt to lucene search engine
	 */
	@Test
	@Ignore   // set to ignore because search is done now via lucene
	public void getBibTexSearchForGroup() {
		String userName = "testuser1";
		String search = "search";

		List<Post<BibTex>> posts = bibTexDb.getPostsSearchForGroup(GroupID.PUBLIC.name(), new LinkedList<String>(), search, userName, 5, 0, null, this.dbSession);
		assertEquals(1, posts.size());

		search = "search test string bibtext";
		posts = bibTexDb.getPostsSearchForGroup(GroupID.PUBLIC.name(), new LinkedList<String>(), search, userName, 5, 0, null, this.dbSession);
		assertEquals(1, posts.size());
	}

	
	/**
	 * tests getBibTexViewable
	 * 
	 * if groupId is special (>= 0 and <3) you have to set loginUserName
	 * if groupId is not special, loginUserName is checked by chain, you don't need it
	 */
	@Ignore // FIXME: test is only successfully when running alone
	@Test
	public void getBibTexViewable() {
		String requestedGroupName = "public";
		String loginUserName = "testuser1";
		List<Post<BibTex>> post = bibTexDb.getPostsViewable(requestedGroupName, loginUserName, PUBLIC_GROUP_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		requestedGroupName = "testgroup1";
		post = bibTexDb.getPostsViewable(requestedGroupName, null, TESTGROUP1_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		requestedGroupName = "private";
		loginUserName = "testuser2";
		post = bibTexDb.getPostsViewable(requestedGroupName, loginUserName, PRIVATE_GROUP_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		requestedGroupName = "";
		loginUserName = "testuser1";
		post = bibTexDb.getPostsViewable(requestedGroupName, loginUserName, INVALID_GROUP_ID, HashID.INTER_HASH, 10, 0, null, this.dbSession);
		assertEquals(0, post.size());

	}

	/**
	 * tests getBibTexDuplicate
	 */
	@Test
	public void getBibTexDuplicate() {		
		final List<Post<BibTex>> post = bibTexDb.getPostsDuplicate("testuser1", Collections.singletonList(PUBLIC_GROUP_ID), HashID.INTER_HASH, this.dbSession, null);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBibTexDuplicateCount
	 */
	@Test
	public void getBibTexDuplicateCount() {
		final int count = bibTexDb.getPostsDuplicateCount("testuser1", this.dbSession);
		assertEquals(1, count);
	}

	/**
	 * tests getBibTexForUsersInGroup
	 */
	@Ignore // FIXME: test is only successfully when running alone 
	@Test
	public void getBibTexForUsersInGroup() {
		List<Integer> groups = Collections.singletonList(PUBLIC_GROUP_ID);
		
		String loginUserName = "testuser1";
		List<Post<BibTex>> post = bibTexDb.getPostsForGroup(3, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(3, post.size());
		
		post = bibTexDb.getPostsForGroup(TESTGROUP2_ID, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
		
		loginUserName = "testuser2";
		post = bibTexDb.getPostsForGroup(TESTGROUP1_ID, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(3, post.size());
		
		post = bibTexDb.getPostsForGroup(TESTGROUP2_ID, groups, loginUserName, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		groups = new ArrayList<Integer>();
		post = bibTexDb.getPostsForGroup(TESTGROUP2_ID, groups, null, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = bibTexDb.getPostsForGroup(TESTGROUP1_ID, groups, null, HashID.INTER_HASH, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBibTexForGroupCount
	 * 
	 * visibleGroupIDs && userName && (userName != requestedUserName) optional
	 */
	// FIXME: test is only successfully when running alone
	@Ignore
	@Test
	public void getBibTexForGroupCount() {
		final String requestedUserName = "";
		final String loginUserName = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		final int count1 = bibTexDb.getPostsForGroupCount(requestedUserName, loginUserName, 3, visibleGroupIDs, this.dbSession);
		assertEquals(1, count1);
		
		final int count2 = bibTexDb.getPostsForGroupCount(requestedUserName, loginUserName, 4, visibleGroupIDs, this.dbSession);
		assertEquals(1, count2);
		
		final int count3 = bibTexDb.getPostsForGroupCount(requestedUserName, loginUserName, 6, visibleGroupIDs, this.dbSession);
		assertEquals(0, count3);
	}

	/**
	 * tests getBibTexForGroupByTag
	 * set userName or visibleGroups
	 */
	@Test
	// TODO: group id 0 und 2 ?
	public void getBibTexForGroupByTag() {
		final List<Integer> visibleGroupIDs = Collections.singletonList(PUBLIC_GROUP_ID);
		List<TagIndex> tagIndex = DBTestUtils.getTagIndex("testbibtex");
		
		String loginUser = "testuser1";
		
		List<Post<BibTex>> post = bibTexDb.getPostsForGroupByTag(TESTGROUP2_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
		
		DBTestUtils.addToTagIndex(tagIndex, "testtag");
		post = bibTexDb.getPostsForGroupByTag(TESTGROUP2_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		tagIndex = DBTestUtils.getTagIndex("privatebibtex");
		loginUser = "testuser2";
		post = bibTexDb.getPostsForGroupByTag(TESTGROUP2_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(0, post.size());
		
		post = bibTexDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		tagIndex = Collections.singletonList(new TagIndex("friendbibtex", 1));
		
		post = bibTexDb.getPostsForGroupByTag(TESTGROUP1_ID, visibleGroupIDs, loginUser, tagIndex, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
	}

	/**
	 * tests getBibTexForUser
	 */
	@Test
	public void getBibTexForUser() {
		String requestedUserName = "testuser1";
		final List<Integer> groups = new ArrayList<Integer>();
		
		List<Post<BibTex>> post = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, TESTGROUP1_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, PUBLIC_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, INVALID_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(2, post.size());
		
		groups.add(PUBLIC_GROUP_ID);
		post = bibTexDb.getPostsForUser("testuser2", requestedUserName, HashID.INTRA_HASH, TESTGROUP1_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		groups.clear();
		
		requestedUserName = "testuser2";
		post = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, PRIVATE_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
		post = bibTexDb.getPostsForUser(requestedUserName, requestedUserName, HashID.INTER_HASH, FRIENDS_GROUP_ID, groups, null, 10, 0, null, this.dbSession);
		assertEquals(1, post.size());
		
	}

	
	/**
	 * Check if documents are proper attached to posts
	 */
	@Test
	public void getPublicationForUserWithDocuments() {		
		final List<Post<BibTex>> posts = bibTexDb.getPostsForUser("testuser1", "testuser1", HashID.INTER_HASH, PUBLIC_GROUP_ID, Collections.singletonList(PUBLIC_GROUP_ID), FilterEntity.POSTS_WITH_DOCUMENTS, 10, 0, null, this.dbSession);
		
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
	 * tests getBibTexForUserCount
	 * 
	 * groupId or
	 * visibleGroupIDs && userName && (userName != requestedUserName)
	 */
	@Test
	public void getBibTexForUserCount() {
		final String loginUserName = "";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		
		final int count1 = bibTexDb.getPostsForUserCount("testuser1", loginUserName, TESTGROUP1_ID, visibleGroupIDs, this.dbSession);
		assertEquals(1, count1);
		
		final int count2 = bibTexDb.getPostsForUserCount("testuser2", loginUserName, PRIVATE_GROUP_ID, visibleGroupIDs, this.dbSession);
		assertEquals(1, count2);
	}

	/**
	 * tests getContentIdForBibTex
	 */
	@Test
	public void getContentIdForBibTex() {
		for (final String hash : new String[] { "", " ", null }) {
			for (final String username : new String[] { "", " ", null }) {
				try {
					bibTexDb.getContentIdForPost(hash, username, this.dbSession);
					fail("Should throw an exception");
				} catch (final RuntimeException ex) {
				}
			}
		}
		
		int contentId = bibTexDb.getContentIdForPost("b77ddd8087ad8856d77c740c8dc2864a", "testuser1", this.dbSession);
		assertEquals(10, contentId);
		
		contentId = bibTexDb.getContentIdForPost("1b298f199d487bc527a62326573892b8", "testuser2", this.dbSession);
		assertEquals(13, contentId);
	}

	/**
	 * tests getPosts
	 */
	@Test
	@Ignore
	// TODO: need assertByTagNames?
	public void getPosts() {
		final BibTexParam param = new BibTexParam();
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		param.setHash("");
		
		List<Post<BibTex>> posts = bibTexDb.getPosts(param, this.dbSession);
		assertEquals(2, posts.size());
		Assert.assertByTagNames(tagIndex, posts); // need this?
		
		param.setGroupId(PUBLIC_GROUP_ID);
		posts = bibTexDb.getPosts(param, this.dbSession);
		assertEquals(2, posts.size());
	}

	
	/**
	 * generate a BibTex Post
	 */
	private Post <BibTex> generateBibTexDatabaseManagerTestPost() {
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
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		post.setUser(user);
		
		final BibTex publication = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(publication);
		publication.setCount(0);		
		publication.setEntrytype("inproceedings");
		publication.setAuthor("Hans Testauthor and Liese Testauthorin");
		publication.setEditor("Peter Silie");		
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
	@Ignore // FIXME: Test läuft nur einzeln erfolgreich
	@Test
	public void storePostBibTexUpdatePlugin() {
		final String hash = "b77ddd8087ad8856d77c740c8dc2864a";		
		final String loginUserName = "testuser1";

		List<BibTexExtra> extras = bibTexExtraDb.getURL(hash, loginUserName, this.dbSession);
		assertEquals(1, extras.size());

		// TODO: ist das nicht immer public?
		// this.bibtexParam.setGroupType(GroupID.PRIVATE); 
		this.postDuplicate(hash);

		final Post<BibTex> post = bibTexDb.getPostsByHash(hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession).get(0);
		assertNotNull(post);

		extras = bibTexExtraDb.getURL(hash, loginUserName, this.dbSession);
		// TODO: wieso 2 ? Duplicate macht nicht 2 daraus
		//assertEquals(2, extras.size());
		assertEquals(1, extras.size());
	}
	
	/**
	 * tests storePost
	 */
	@Test
	public void createPost() {
		final Post<BibTex> toInsert = this.generateBibTexDatabaseManagerTestPost();
		toInsert.getResource().recalculateHashes();
		final String bibtexHashForUpdate = "14143c6508fe645ca312d0aa5d0e791b"; // INTRA-hash of toInsert

		bibTexDb.createPost(toInsert, this.dbSession);

		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { "tag1", "tag2" }), "", null, 0, 50, null, null, toInsert.getUser());
		param.setSimHash(HashID.INTRA_HASH);
		final List<Post<BibTex>> posts = bibTexDb.getPosts(param, this.dbSession);
		assertEquals(1, posts.size());
		ModelUtils.assertPropertyEquality(toInsert, posts.get(0), Integer.MAX_VALUE, null, new String[] { "resource", "tags", "user", "date", "changeDate"});
		toInsert.getResource().setCount(1);
		ModelUtils.assertPropertyEquality(toInsert.getResource(), posts.get(0).getResource(), Integer.MAX_VALUE, null, new String[] { "openURL"});

		// post a duplicate and check whether plugins are called		
		assertFalse(this.pluginMock.isOnBibTexUpdate());
		this.pluginMock.reset();
		
		this.postDuplicate(bibtexHashForUpdate);
		assertTrue(this.pluginMock.isOnBibTexUpdate());
		
		bibTexDb.deletePost(toInsert.getUser().getName(), toInsert.getResource().getIntraHash(), this.dbSession);
	}
	
	/**
	 * tests assertDeleteBibTex
	 */
	@Test
	public void deletePublication() {
		assertFalse(this.pluginMock.isOnBibTexDelete());
		this.pluginMock.reset();

		// first: insert post such that we can delete it later
		
		final Post<BibTex> toInsert = this.generateBibTexDatabaseManagerTestPost();
		bibTexDb.createPost(toInsert, this.dbSession);
		
		// delete public post		
		final String username = "testuser1";
		final String requestedUserName = username;
		final String hash = "14143c6508fe645ca312d0aa5d0e791b";
		
		List<Post<BibTex>> posts = bibTexDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
		assertNotNull(posts);
		assertEquals(1, posts.size());
		
		boolean succ = bibTexDb.deletePost(username, hash, this.dbSession);
		
		assertTrue("Post could not be deleted", succ);
		
		assertEquals(0, bibTexDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession).size());
		assertTrue(this.pluginMock.isOnBibTexDelete());
		
		// delete private post
		toInsert.getGroups().clear();
		final Group group = GroupUtils.getPrivateGroup();
		toInsert.getGroups().add(group);
		
		final BibTexParam postParam = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { "tag1", "tag2" }), "", null, 0, 50, null, null, toInsert.getUser());
		List<Post<BibTex>> post2 = bibTexDb.getPosts(postParam, this.dbSession);
		posts = bibTexDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
		assertEquals(0, posts.size());
		assertEquals(0, post2.size());
		
		bibTexDb.createPost(toInsert, this.dbSession);
		post2 = bibTexDb.getPosts(postParam, this.dbSession);
		posts = bibTexDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
		assertEquals(1, posts.size());
		assertEquals(1, post2.size());
		
		succ = bibTexDb.deletePost(requestedUserName, hash, this.dbSession);
		assertTrue("Post could not be deleted", succ);
		
		assertEquals(0, bibTexDb.getPostsByHashForUser(username, hash, requestedUserName, new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession).size());
	}

	/**
	 * tests storePostWrongUsage
	 */
	@Test(expected = IllegalArgumentException.class)
	public void storePostWrongUsage() {
		final Post<BibTex> toInsert = this.generateBibTexDatabaseManagerTestPost();

		bibTexDb.updatePost(toInsert, null, null, this.dbSession);
	}

	/**
	 * Makes sure that we don't lose information if we change something on an
	 * existing post.
	 */
	@Test
	public void storePostDuplicate() {
		for (final String intraHash : new String[] {"b77ddd8087ad8856d77c740c8dc2864a"}) {

			final Post<BibTex> originalPost = bibTexDb.getPostsByHash(intraHash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession).get(0);
			this.postDuplicate(intraHash);
			final Post<BibTex> newPost = bibTexDb.getPostsByHash(intraHash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession).get(0);
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
			
			// TODO: cannot get privnote with getBibTexByHash, privnote is always null
			assertEquals(originalPost.getResource().getPrivnote(), newPost.getResource().getPrivnote());
			// TODO: more tests please...
		}
	}

	private void postDuplicate(final String hash) {
		List<Post<BibTex>> someBibTexPost = bibTexDb.getPostsByHash(hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(1, someBibTexPost.size());
		// someBibTexPost.getGroups().clear();
		final Post<BibTex> publication = someBibTexPost.get(0);
		final int count = publication.getResource().getCount();
		bibTexDb.updatePost(publication, hash, PostUpdateOperation.UPDATE_ALL, this.dbSession);
		
		// check if resource counter is updated correctly
		List<Post<BibTex>> afterUpdate = bibTexDb.getPostsByHash(hash, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession);
		assertEquals(count, afterUpdate.get(0).getResource().getCount());
	}

	/**
	 * tests whether the query timeout specified in SqlMapConfig.xml works
	 * done by retrieving all bibtex entries of user dblp, which will take longer
	 * than 10 seconds
	 */
	@Ignore // we don't want to wait 10 seconds each time we run the tests, not possible for new local db
	@Test
	public void testQueryTimeout() {
		final BibTexParam bibtexParam = ParamUtils.getDefaultBibTexParam();
		bibtexParam.setUserName("dblp");
		bibtexParam.setRequestedUserName("dblp");
		bibtexParam.setLimit(100000000); 
		bibtexParam.setOffset(0);
		bibtexParam.setGroupId(PUBLIC_GROUP_ID);
		try {
			bibTexDb.getPostsForUser(bibtexParam, this.dbSession);
			fail();
		} catch (Exception e) {
			// timeout
		}
	}

	/**
	 * tests getBibtexByConceptForGroup
	 */
	@Ignore // FIXME: adapt to new test db
	@Test
	public void getBibtexByConceptForGroup() {
		final BibTexParam param = new BibTexParam();
		
		param.addSimpleConceptName("clustering");
		param.setRequestedGroupName("kde");
		param.setUserName("hotho");
		param.addGroup(PUBLIC_GROUP_ID);
		
		param.setGrouping(GroupingEntity.GROUP);
		param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		
		final List<Post<BibTex>> posts2 = bibTexDb.getPosts(param, this.dbSession);
		assertEquals(10, posts2.size());
	}
	
	/**
	 * tests getBibtexByKey
	 */
	@Test
	public void getBibtexByKey() {
		final String bibtexKey = "test %";
		final String requestedUserName = "testuser1";
		
		final List<Post<BibTex>> posts = bibTexDb.getPostsByKey(bibtexKey, requestedUserName, PUBLIC_GROUP_ID, 20, 0, null, this.dbSession);
		assertEquals(1,posts.size());
		assertEquals(posts.get(0).getResource().getBibtexKey(), "test bibtexKey");
	}
	
	/**
	 * tests {@link BibTexDatabaseManager#getPostsByFollowedUsers(String, List, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void getBibTexByFollowedUsers() {
		/*
		 * testuser 1 follows testuser 2, who has two posts.
		 */
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(PUBLIC_GROUP_ID);
		visibleGroupIDs.add(PRIVATE_GROUP_ID);
		visibleGroupIDs.add(FRIENDS_GROUP_ID);
		final List<Post<BibTex>> posts = bibTexDb.getPostsByFollowedUsers("testuser1", visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(2, posts.size());
		assertEquals("testuser2", posts.get(0).getUser().getName());
		assertEquals("testuser2", posts.get(1).getUser().getName());
	}
	
	/**
	 * tests if {@link BibTexDatabaseManager#createPost(Post, org.bibsonomy.database.common.DBSession)}
	 * respects the max field length of table columns
	 */
	@Test
	public void maxFieldLengthErrorCreatePost() {
		final String longField = "1234567890ß1234567890ß1234567890ß1234567890ß1234567890ß"; // > 46
		/*
		 * create post
		 */
		final Post<BibTex> testPost = this.generateBibTexDatabaseManagerTestPost();
		final BibTex resource = testPost.getResource();
		resource.setTitle("Max Field Length in DB");
		resource.setAuthor("W: Walt");
		resource.setYear(longField);
		resource.setMonth(longField);
		
		try {
			bibTexDb.createPost(testPost, this.dbSession);
			fail("expected a DatabaseException");
		} catch (DatabaseException ex) {
			final List<ErrorMessage> messages = ex.getErrorMessages(resource.getIntraHash());
			assertEquals(1, messages.size());
			
			assertEquals(FieldLengthErrorMessage.class, messages.get(0).getClass());
		}
	}
	
	/**
	 * tests if {@link BibTexDatabaseManager#updatePost(Post, String, PostUpdateOperation, org.bibsonomy.database.util.DBSession)}
	 * respects the max field length of table columns
	 */
	@Test
	public void maxFieldLengthErrorUpdatePost() {
		final String longField = "1234567890ß1234567890ß1234567890ß1234567890ß1234567890ß";
		/*
		 * update post
		 */
		final String userName = "testuser1";
		final String intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final List<Integer> groups = Collections.singletonList(PUBLIC_GROUP_ID);
		
		final List<Post<BibTex>> updatePosts = bibTexDb.getPostsByHashForUser(userName, intraHash, userName, groups, HashID.INTRA_HASH, this.dbSession);
		
		assertEquals(1, updatePosts.size());
		
		final Post<BibTex> updatePost = updatePosts.get(0);
		
		final BibTex updateResource = updatePost.getResource();
		updateResource.setMonth(longField);
		
		try {
			bibTexDb.updatePost(updatePost, updateResource.getIntraHash(), PostUpdateOperation.UPDATE_ALL, this.dbSession);
			fail("expected a DatabaseException");
		} catch (final DatabaseException ex) {
			final List<ErrorMessage> messages = ex.getErrorMessages(updateResource.getIntraHash());
			assertEquals(1, messages.size());
			
			assertEquals(FieldLengthErrorMessage.class, messages.get(0).getClass());
		}
	}
	
	@Test
	public void testLoggedPostsRedirect() {
		/*
		 * Post history:
		 * content_id 	intrahash
		 * 17			b71d5283dc7f4f59f306810e73e9bc9a
		 * 18			e2fb0763068b21639c3e36101f64aefe
		 * 19			b71d5283dc7f4f59f306810e73e9bc9a
		 * 20			891518b4900cd1832d77a0c8ae20dd14
		 */
		try {
			bibTexDb.getPostDetails("testuser1", "b71d5283dc7f4f59f306810e73e9bc9a", "testuser3", Collections.singletonList(PUBLIC_GROUP_ID), this.dbSession);
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
			bibTexDb.getPostDetails("testuser1", "e2fb0763068b21639c3e36101f64aefe", "testuser3", Collections.singletonList(PUBLIC_GROUP_ID), this.dbSession);
			fail("expected ResourceMovedException");
		} catch (final ResourceMovedException e) {
			/*
			 * We get just the next hash.
			 */
			assertEquals("b71d5283dc7f4f59f306810e73e9bc9a", e.getNewIntraHash());
		}
		
	}
}