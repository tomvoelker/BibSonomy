/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database;

import static org.bibsonomy.testutil.Assert.assertTagsByName;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.*;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Repository;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.util.Sets;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Jens Illig
 */
public class DBLogicTest extends AbstractDatabaseManagerTest {
	private static final String TEST_USER_1 = "testuser1";
	private static final String TEST_USER_2 = "testuser2";
	private static final String TEST_USER_3 = "testuser3";
	private static final String TEST_USER_NAME = "jaeschke";
	private static final String TEST_SPAMMER_NAME = "testspammer2";
	private static final String TEST_LIMITED_USER_NAME = "testlimited";
	private static final String TEST_SPAMMER_EMAIL = "testspammer@bibsonomy.org";
	private static final String TEST_SPAMMER_ALGORITHM = "testlogging";
	private static final int    TEST_SPAMMER_PREDICTION = 1;
	private static final double TEST_SPAMMER_CONFIDENCE = 0.42;

	private static final String TEST_REQUEST_USER_NAME = "jaeschke";
	private static final String TEST_REQUEST_HASH = "7d85e1092613fd7c91d6ba5dfcf4a044";

	private static final List<String> DEFAULT_TAG_LIST = new LinkedList<String>(Arrays.asList("semantic"));
	private static final Set<String> DEFAULT_TAG_SET = new HashSet<String>(DEFAULT_TAG_LIST);

	private static final Set<String> DEFAULT_USERNAME_SET = new HashSet<String>(Arrays.asList(TEST_USER_NAME));

	private static final DBLogic ADMIN_LOGIC = testDatabaseContext.getBean("dbLogicPrototype", DBLogic.class);
	private static final List<SortCriteria> SORT_CRITERIUMS_DATE = Collections.singletonList(new SortCriteria(SortKey.DATE, SortOrder.DESC));

	private static UserDatabaseManager userDb;
	
	/**
	 * sets up required managers
	 */
	@BeforeClass
	public static void setupManagers() {
		userDb = UserDatabaseManager.getInstance();
		User loginUser = new User("");
		loginUser.setRole(Role.ADMIN);
		ADMIN_LOGIC.setLoginUser(loginUser);
	}
	
	protected static List<String> getUserNamesByGroupId(final int groupId, final DBSession dbSession) {
		return userDb.getUserNamesByGroupId(groupId, dbSession);
	}
	
	protected LogicInterface getDbLogic() {
		return this.getDbLogic(TEST_USER_1);
	}

	protected LogicInterface getDbLogic(final String userName) {
		return this.getDbLogic(userName, Role.DEFAULT);
	}

	protected LogicInterface getDbLogic(final String userName, final Role role) {
		final User user = ADMIN_LOGIC.getUserDetails(userName);
		user.setRole(role);

		final DBLogic dbLogic = testDatabaseContext.getBean("dbLogicPrototype", DBLogic.class);
		dbLogic.setLoginUser(user);

		return dbLogic;
	}

	protected LogicInterface getAdminDbLogic(final String userName) {
		return this.getDbLogic(userName, Role.ADMIN);
	}
	
	private static void assertList(final List<Post<BibTex>> posts, final Set<String> checkUserNameOneOf, final List<SortCriteria> sortCriteria, final Set<String> checkTags, final String checkInterHash, final Set<Integer> mustBeInGroups, final Set<Integer> mustNotBeInGroups) {
		final Set<Integer> alreadyFound = new HashSet<Integer>();
		long orderValue = Long.MAX_VALUE;
		
		for (final Post<? extends Resource> post : posts) {
			assertTrue("contentid occured twice", alreadyFound.add(post.getContentId()));

			if (checkUserNameOneOf != null) {
				assertTrue("userName test with " + post.getUser().getName(), checkUserNameOneOf.contains(post.getUser().getName()));
			}
			if (sortCriteria == SORT_CRITERIUMS_DATE) {
				final long nextOrderValue = post.getDate().getTime();
				assertTrue("order test", (orderValue >= nextOrderValue));
				orderValue = nextOrderValue;
			}
			/*
			FIXME: not tested, because no rank is in model (and probably should not be)
			if (checkOrder == Order.POPULAR) {
				int nextOrderValue = p.getResource().getRank();
				assertTrue("order test", (this.orderValue >= nextOrderValue));
				this.orderValue = nextOrderValue;
			}
			*/
			if (checkTags != null) {
				assertTrue("tag-test", ModelUtils.hasTags(post, checkTags));
			}
			if (checkInterHash != null) {
				assertEquals(post.getResource().getInterHash(), checkInterHash);
			}
			if ((mustBeInGroups != null) || (mustNotBeInGroups != null)) {
				assertTrue("group-test", ModelUtils.checkGroups(post, mustBeInGroups, mustNotBeInGroups));
			}
		}
	}



	/**
	 * tests getPostsByTagName
	 */
	@Test
	@Ignore
	public void getPostsByTagName() {
		LogicInterface anonymousAccess = this.getDbLogic(null, null);

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.ALL)
				.setGroupingName("")
				.setTags(DEFAULT_TAG_LIST)
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(5, 0);

		List<Post<BibTex>> bibTexPostsList = anonymousAccess.getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(5, bibTexPostsList.size());
		assertList(bibTexPostsList, null, null, DEFAULT_TAG_SET, null, null, null);
		
		anonymousAccess = this.getDbLogic("", null);
		final PostQueryBuilder postQueryBuilder2 = new PostQueryBuilder();
		postQueryBuilder2.setGrouping(GroupingEntity.ALL)
				.setGroupingName("")
				.setTags(DEFAULT_TAG_LIST)
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(4, 5);

		bibTexPostsList = anonymousAccess.getPosts(postQueryBuilder2.createPostQuery(BibTex.class));
		assertEquals(4, bibTexPostsList.size());
		assertList(bibTexPostsList, null, null, DEFAULT_TAG_SET, null, null, null);
	}

	/**
	 * tests getPostsByConceptForUser
	 */
	@Test
	@Ignore
	public void getPostsByConceptForUser() {
		final List<String> taglist = Arrays.asList("->researcher");

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.USER)
				.setGroupingName(TEST_REQUEST_USER_NAME)
				.setTags(taglist)
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(2, 0)
				.setSortCriteria(SORT_CRITERIUMS_DATE);

		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(2, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, SORT_CRITERIUMS_DATE, null, null, null, null);

		final PostQueryBuilder postQueryBuilder2 = new PostQueryBuilder();
		postQueryBuilder2.setGrouping(GroupingEntity.USER)
				.setGroupingName(TEST_REQUEST_USER_NAME)
				.setTags(taglist)
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(8, 2)
				.setSortCriteria(SORT_CRITERIUMS_DATE);
		
		bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder2.createPostQuery(BibTex.class));
		assertEquals(1, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, SORT_CRITERIUMS_DATE, null, null, null, null);
	}

	/**
	 * tests getPostsForUser
	 */
	@Test
	@Ignore
	public void getPostsForUser() {
		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.USER)
				.setGroupingName(TEST_REQUEST_USER_NAME)
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(10, 0);
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, null, null, null, null, null);

		postQueryBuilder.entriesStartingAt(10, 10);
		bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, null, null, null, null, null);
	}

	/**
	 * tests getPostsByHash on Bibtex entries
	 */
	@Test
	@Ignore
	public void getPostsByHashBibtex() {
		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.ALL)
				.setScope(QueryScope.LOCAL)
				.setHash("d9eea4aa159d70ecfabafa0c91bbc9f0")
				.entriesStartingAt(5, 0);
		final List<Post<BibTex>> listBibtex = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(1, listBibtex.size());
		assertEquals(1, listBibtex.get(0).getGroups().size());
		for (final Group g : listBibtex.get(0).getGroups()){
			assertEquals("public", g.getName());
		}

		postQueryBuilder.setHash("85ab919107e4cc79b345e996b3c0b097");
		final List<Post<Bookmark>> listBookmark = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(Bookmark.class));
		assertEquals(1, listBookmark.size());
		assertEquals(1, listBookmark.get(0).getGroups().size());
		for (final Group g : listBookmark.get(0).getGroups()){
			assertEquals("public", g.getName());
		}
	}

	/**
	 * tests getPostsByHashForUser
	 */
	@Test
	@Ignore
	public void getPostsByHashForUser() {
		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.USER)
				.setGroupingName(TEST_REQUEST_USER_NAME)
				.setScope(QueryScope.LOCAL)
				.setHash(TEST_REQUEST_HASH)
				.entriesStartingAt(20, 0);
		final List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(1, bibTexPostsList.size());
		assertEquals(1, bibTexPostsList.get(0).getGroups().size());
		assertNull(bibTexPostsList.get(0).getResource().getDocuments());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, null, null, TEST_REQUEST_HASH, null, null);
	}

	/**
	 * tests getPostsByViewable
	 */
	@Test
	public void getPostsByViewable() {
		final Set<Integer> mustGroupIds = new HashSet<>();
		final Set<String> usersInGroup = new HashSet<>();
		usersInGroup.addAll(getUserNamesByGroupId(TESTGROUP1_ID, this.dbSession));
		mustGroupIds.add(TESTGROUP1_ID);

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.VIEWABLE)
				.setGroupingName("kde")
				.setScope(QueryScope.LOCAL)
				.setSortCriteria(SORT_CRITERIUMS_DATE)
				.entriesStartingAt(2, 0);
		
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(0, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, SORT_CRITERIUMS_DATE, null, null, mustGroupIds, null);

		postQueryBuilder.entriesStartingAt(97, 3);
		bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(0, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, SORT_CRITERIUMS_DATE, null, null, mustGroupIds, null);
	}

	/**
	 * tests getPostsForUsersInGroup
	 */
	@Test
	@Ignore
	public void getPostsForUsersInGroup() {
		final Set<String> usersInGroup = new HashSet<>();
		usersInGroup.addAll(getUserNamesByGroupId(TESTGROUP1_ID, this.dbSession));

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.GROUP)
				.setGroupingName("kde")
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(10, 0);

		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, null, null, null, null);

		postQueryBuilder.entriesStartingAt(10, 10);
		
		bibTexPostsList = this.getDbLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, null, null, null, null);
	}

	/**
	 * tests getPostsForGroupByTag
	 */
	@Test
	@Ignore
	public void getPostsForGroupByTag() {
		final LogicInterface anonymousAccess = this.getDbLogic("", null);
		final Set<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(getUserNamesByGroupId(TESTGROUP1_ID, this.dbSession) );

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.GROUP)
				.setGroupingName("kde")
				.setScope(QueryScope.LOCAL)
				.setTags(DEFAULT_TAG_LIST)
				.entriesStartingAt(9, 0);
		
		List<Post<BibTex>> bibTexPostsList = anonymousAccess.getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, DEFAULT_TAG_SET, null, null, null);

		postQueryBuilder.entriesStartingAt(10, 9);
		bibTexPostsList = anonymousAccess.getPosts(postQueryBuilder.createPostQuery(BibTex.class));
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, DEFAULT_TAG_SET, null, null, null);
	}

	/**
	 * tests getBibtexOfFriendByTags
	 */
	@Test
	@Ignore
	public void getBibtexOfFriendByTags() {
		final LogicInterface buzzsAccess = this.getDbLogic("buzz", null);
		final List<String> tags = Arrays.asList("java");
		List<Post<BibTex>> bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(1, bibTexPostsList.size());
		final Set<String> tagsSet = new HashSet<String>();
		tagsSet.addAll(tags);
		final Set<String> userSet = new HashSet<>();
		userSet.add("apo");
		
		final Set<Integer> mustGroupIds = new HashSet<>();
		mustGroupIds.add(FRIENDS_GROUP_ID);
		
		final Set<Integer> mustNotGroups = new HashSet<>();
		mustNotGroups.add(PRIVATE_GROUP_ID);
		mustNotGroups.add(PUBLIC_GROUP_ID);
		assertList(bibTexPostsList, userSet, SORT_CRITERIUMS_DATE, tagsSet, null, mustGroupIds, mustNotGroups);

		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, null, QueryScope.LOCAL,null, null, null, null, 0, 19);
		assertEquals(0, bibTexPostsList.size());
	}

	/**
	 * tests getBibtexOfFriendByUser
	 */
	@Test
	@Ignore
	public void getBibtexOfFriendByUser() {
		final LogicInterface buzzsAccess = this.getDbLogic("buzz", null);
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(FRIENDS_GROUP_ID);
		final Set<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(PRIVATE_GROUP_ID);
		mustNotGroups.add(PUBLIC_GROUP_ID);
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		
		List<Post<BibTex>> bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, null, QueryScope.LOCAL,null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(2, bibTexPostsList.size());
		assertList(bibTexPostsList, userSet, SORT_CRITERIUMS_DATE, null, null, mustGroupIds, mustNotGroups);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(0, bibTexPostsList.size());
	}

	/**
	 * tests getBibtexOfTaggedUser
	 * @throws PersonListParserException 
	 */
	@Test
	public void getBibtexOfTaggedByUser() throws PersonListParserException {
		final User admUser = ModelUtils.getUser();
		admUser.setName(TEST_USER_1);
		//--------------------------------------------------------------------
		// create some test users and create some test relations among them
		//--------------------------------------------------------------------
		final User srcUser = this.createUser("buzz");
		final User dstUser1 = this.createUser("duzz");
		final User dstUser2 = this.createUser("fuzz");
		final User dstUser3 = this.createUser("suzz");

		final String relationName1 = "football";
		final String relationTag1 = SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, relationName1);
		final String relationName2 = "music";
		final String relationTag2 = SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, relationName2);
		final String relationName3 = "tv";
		final String relationTag3 = SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, relationName3);
		
		final String sharedTag1 = "sharedTag1";
		final String sharedTag2 = "sharedTag2";
		
		final LogicInterface admLogic  = this.getAdminDbLogic(admUser.getName());

		
		 // create users
		admLogic.createUser(srcUser);
		admLogic.createUser(dstUser1);
		admLogic.createUser(dstUser2);
		admLogic.createUser(dstUser3);

		final LogicInterface srcLogic  = this.getDbLogic(srcUser.getName(), null);
		final LogicInterface dstLogic  = this.getDbLogic(dstUser1.getName(), null);
		final LogicInterface dst2Logic = this.getDbLogic(dstUser2.getName(), null);

		//--------------------------------------------------------------------
		// srcUser creates tagged relations
		//--------------------------------------------------------------------
		// add a tagged relation srcUser -> dstUser (football)
		srcLogic.createUserRelationship(srcUser.getName(), dstUser1.getName(), UserRelation.OF_FRIEND, relationTag1);
		// add a tagged relation srcUser -> dstUser (music)
		srcLogic.createUserRelationship(srcUser.getName(), dstUser1.getName(), UserRelation.OF_FRIEND, relationTag2);
		// add a tagged relation srcUser -> dstUser2 (music)
		srcLogic.createUserRelationship(srcUser.getName(), dstUser2.getName(), UserRelation.OF_FRIEND, relationTag2);
		// add a tagged relation srcUser -> dstUser3 (tv)
		srcLogic.createUserRelationship(srcUser.getName(), dstUser3.getName(), UserRelation.OF_FRIEND, relationTag3);
		
		//--------------------------------------------------------------------
		// dstUser creates two posts (publications)
		//--------------------------------------------------------------------
		final List<Post<?>> btPosts = new LinkedList<Post<?>>();
		final Post<BibTex> btPost1 = ModelUtils.generatePost(BibTex.class);
		// add tags
		ModelUtils.addToTagSet(btPost1.getTags(), "btPostTag1", sharedTag1);
		btPost1.getUser().setName(dstUser1.getName());
		btPosts.add(btPost1);

		// add tags
		final Post<BibTex> btPost2 = ModelUtils.generatePost(BibTex.class);
		ModelUtils.addToTagSet(btPost2.getTags(), "btPostTag2", sharedTag1, sharedTag2);
		btPost2.getUser().setName(dstUser1.getName());
		btPost2.getResource().setTitle("Just another title");
		btPost2.getResource().setAuthor(PersonNameUtils.discoverPersonNames("Just another author"));
		btPost2.getResource().recalculateHashes();
		btPosts.add(btPost2);

		List<JobResult> createPosts = dstLogic.createPosts(btPosts);
		assertEquals(2, createPosts.size());

		//--------------------------------------------------------------------
		// dstUser2 creates two posts (bookmarks)
		//--------------------------------------------------------------------
		final List<Post<?>> bmPosts = new LinkedList<>();
		final Post<Bookmark> bmPost1 = ModelUtils.generatePost(Bookmark.class);
		// add tags
		ModelUtils.addToTagSet(bmPost1.getTags(), "bmPost1Tag", sharedTag1);
		bmPost1.getUser().setName(dstUser2.getName());
		bmPost1.getResource().setUrl("http://fuzzduzz");
		bmPosts.add(bmPost1);

		// add tags
		final Post<Bookmark> bmPost2 = ModelUtils.generatePost(Bookmark.class);
		ModelUtils.addToTagSet(bmPost2.getTags(), "bmPost2Tag", sharedTag1, sharedTag2);
		bmPost2.getUser().setName(dstUser2.getName());
		bmPost2.getResource().setTitle("Just another title");
		bmPost2.getResource().setUrl("http://duzzfuzz");
		bmPost2.getResource().recalculateHashes();
		bmPosts.add(bmPost2);

		createPosts = dst2Logic.createPosts(bmPosts);
		assertEquals(2, createPosts.size());

		//--------------------------------------------------------------------
		// srcUser queries for posts from his friends
		//--------------------------------------------------------------------
		//                                             sharedTag1 
		//                                           +--------------> btPost1
		//            relTag1, relTag2               | sharedTag1/2
		//          +-----------------> dstUser1 ----+--------------> btPost2
		//          | reltag2                          sharedTag1
		//  srcUser-+-----------------> dstUser2 ----+--------------> bmPost1
		//          | reltag3                        | sharedTag1/2
		//          +-----------------> dstUser3     +--------------> bmPost2
		//
		//
		//
		//
		
		final List<String> tags1 = new ArrayList<>();
		tags1.add(relationTag1);
		
		List<Post<BibTex>> bibTexPostsList = srcLogic.getPosts(BibTex.class, GroupingEntity.FRIEND, srcUser.getName(), tags1, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(2, bibTexPostsList.size());
		
		final List<String> tags2 = new ArrayList<>();
		tags2.add(relationTag2);
		
		List<Post<Bookmark>> bookmarkPostsList = srcLogic.getPosts(Bookmark.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(2, bookmarkPostsList.size());
		
		tags2.add(relationTag1);
		bookmarkPostsList = srcLogic.getPosts(Bookmark.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(0, bookmarkPostsList.size());
		bibTexPostsList = srcLogic.getPosts(BibTex.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(2, bibTexPostsList.size());
		
		tags2.add(relationTag3);
		bibTexPostsList = srcLogic.getPosts(BibTex.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(0, bibTexPostsList.size());
		
		// retrieve posts restricted by relation tag and 'normal' tag
		tags2.clear();
		tags2.add(relationTag2);
		tags2.add(sharedTag2);
		bookmarkPostsList = srcLogic.getPosts(Bookmark.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(1, bookmarkPostsList.size());
		bibTexPostsList = srcLogic.getPosts(BibTex.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(1, bibTexPostsList.size());

		tags2.clear();
		tags2.add(relationTag2);
		tags2.add(sharedTag1);
		bookmarkPostsList = srcLogic.getPosts(Bookmark.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(2, bookmarkPostsList.size());
		bibTexPostsList = srcLogic.getPosts(BibTex.class, GroupingEntity.FRIEND, srcUser.getName(), tags2, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(2, bibTexPostsList.size());

		// retrieve tag cloud
		tags2.clear();
		tags2.add(relationTag2);
		final List<Tag> aspectTagCloud= srcLogic.getTags(BibTex.class, GroupingEntity.FRIEND, srcUser.getName(), tags1, null, null, null, null, SortKey.FREQUENCY, null, null, 0, 25);
		assertEquals(6, aspectTagCloud.size());
		assertTrue(aspectTagCloud.contains(new Tag(sharedTag1)));
		assertTrue(aspectTagCloud.contains(new Tag(sharedTag2)));
		assertTrue(aspectTagCloud.contains(new Tag("btPostTag1")));
		assertTrue(aspectTagCloud.contains(new Tag("btPostTag2")));
	}

	/** helper function */
	private User createUser(final String userName) {
		final User srcUser = ModelUtils.getUser();
		srcUser.setName(userName);
		srcUser.setReminderPassword(null);
		srcUser.setGender("m");
		srcUser.setToClassify(0);
		srcUser.setSettings(new UserSettings());
		srcUser.getSettings().setLogLevel(0);
		srcUser.setOpenID("http://"+userName);
		srcUser.setLdapId(null);
		srcUser.setPasswordSalt(null);
		return srcUser;
	}
	
	
	/**
	 * tests getPosts with friends
	 */
	@Test
	@Ignore
	public void getBibtexByFriends() {
		final LogicInterface mwkustersAccess = this.getDbLogic("mwkuster", null);
		final Set<Integer> mustGroups = new HashSet<Integer>();
		mustGroups.add(FRIENDS_GROUP_ID);
		final Set<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(PRIVATE_GROUP_ID);
		mustNotGroups.add(PUBLIC_GROUP_ID);
		
		List<Post<BibTex>> bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, null, QueryScope.LOCAL, null, SORT_CRITERIUMS_DATE, null, null, 0, 19);
		assertEquals(19, bibTexPostsList.size());
		assertList(bibTexPostsList, null, SORT_CRITERIUMS_DATE, null, null, mustGroups, mustNotGroups);
		
		bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, null, QueryScope.LOCAL,null, SORT_CRITERIUMS_DATE, null, null, 100, 200);
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, null, SORT_CRITERIUMS_DATE, null, null, mustGroups, mustNotGroups);
	}

	/**
	 * tests getPosts with popular
	 */
	@Test
	@Ignore
	public void getPostsPopular() {
		final List<SortCriteria> popular = Collections.singletonList(new SortCriteria(SortKey.POPULAR, SortOrder.DESC));
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", null, null, null, QueryScope.LOCAL, null, popular, null, null, 0, 10);
		assertEquals(10, bibTexPostsList.size());

		assertList(bibTexPostsList, null, popular, null, null, null, null);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), null, null, QueryScope.LOCAL, null, popular, null, null, 10, 19);
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, null, popular, null, null, null, null);
	}

	/**
	 * TODO improve documentation
	 */
	@Test
	@Ignore
	public void getPostsHome() {
		final List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, TEST_REQUEST_USER_NAME, DEFAULT_TAG_LIST, null, null, QueryScope.LOCAL, null, null, null, null, 0, 15);
		assertEquals(15, bibTexPostsList.size());
	}

	/**
	 * tests concept store
	 */
    @Test
	@Ignore
	public void testConceptStore() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final Tag centerTag = new Tag("testCenterTag");

		final Tag superTag = new Tag("testSuperTag");
		superTag.setSubTags(Arrays.asList(new Tag[] {centerTag}));
		centerTag.setSuperTags(Arrays.asList(new Tag[] {superTag}));

		final Tag superSuperTag = new Tag("testSuperSuperTag");
		superSuperTag.setSuperTags(Arrays.asList(new Tag[] {superSuperTag}));
		superSuperTag.setSubTags(Arrays.asList(new Tag[] {superTag}));

		final Tag subTag = new Tag("testSubTag");
		centerTag.setSubTags(Arrays.asList(new Tag[] {subTag}));
		subTag.setSuperTags(Arrays.asList(new Tag[] {centerTag}));

		final String testUserName = this.getClass().getSimpleName();
		post.getTags().add(centerTag);
		post.getUser().setName(testUserName);

		final Group group = new Group();
		group.setGroupId(PRIVATE_GROUP_ID);
		group.setName("private");
		group.setDescription(null);
		post.setGroups(Collections.singleton(group));

		final LogicInterface testClassAccess = this.getDbLogic(testUserName);
		assertEquals(1, testClassAccess.getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, QueryScope.LOCAL, null, null, null, null, 0, 100).size());
		testClassAccess.createPosts(Collections.<Post<?>>singletonList(post));
		assertEquals(1, testClassAccess.getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, QueryScope.LOCAL, null, null, null, null, 0, 100).size());
		assertEquals(0, this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, QueryScope.LOCAL, null, null, null, null, 0, 100).size());
	}

	/**
	 * We give a null document name, hence we should get a null document ... 
	 */
	@Test
	public void getDocumentNull() {
		final Document document = this.getDbLogic().getDocument(TEST_REQUEST_USER_NAME, TEST_REQUEST_HASH, null);
		assertNull(document);
	}

	/**
	 * A user wants to get his own document: should be possible.
	 */
	@Test
	public void getDocumentOwn() {
		final String resourceHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final String documentFileName = "testdocument_1.pdf";
		final Document document = this.getDbLogic(TEST_USER_1).getDocument(TEST_USER_1, resourceHash, documentFileName);
		assertNotNull(document);
		assertEquals("00000000000000000000000000000000", document.getFileHash());
		assertEquals(documentFileName, document.getFileName());
	}
	
	/**
	 * A user wants to rename a document which belongs to him: should be possible
	 */
	@Test
	public void renameExistingDocumentTest() {
		//create document
		final String resourceHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final String documentFileName = "testdocument_x.pdf";
		final String newDocumentName = "testdocument_x_renamed.pdf";
		Document document = new Document();
		document.setFileHash("11111111111111111111111111111111");
		document.setFileName(documentFileName);
		document.setMd5hash("00000000000000000000000000000000");
		document.setUserName(TEST_USER_1);
		this.getDbLogic(TEST_USER_1).createDocument(document, resourceHash);
		
		// check wether document was successfully created
		document = this.getDbLogic(TEST_USER_1).getDocument(TEST_USER_1, resourceHash, documentFileName);
		assertNotNull(document);
		
		// rename document
		final Document newDocument = new Document();
		newDocument.setFileName(newDocumentName);
		this.getDbLogic(TEST_USER_1).updateDocument(TEST_USER_1, resourceHash, document.getFileName(), newDocument);
		
		Document renamedDoc = this.getDbLogic(TEST_USER_1).getDocument(TEST_USER_1, resourceHash, newDocumentName);
		
		//check wether document was successfully renamed
		assertNotNull(renamedDoc);
		assertEquals(newDocumentName, renamedDoc.getFileName());
		
		//remove document
		this.getDbLogic(TEST_USER_1).deleteDocument(renamedDoc, resourceHash);
		renamedDoc = this.getDbLogic(TEST_USER_1).getDocument(TEST_USER_1, resourceHash, newDocumentName);
		assertNull(renamedDoc);
	}

	/**
	 * A user wants to get another users document: should NOT be possible.
	 */
	@Test
	public void getDocumentNotOwn() {
		final String resourceHash = "4b020083ca0aca3d285569e5fbd0f5b7";
		final String documentFileName = "p16-gifford.pdf";
		final Document document = this.getDbLogic().getDocument("hotho", resourceHash, documentFileName);
		assertNull(document);
	}

	/**
	 * A user wants to get another user's document: should be possible, if a group allows this.
	 */
	@Test
	public void getDocumentNotOwnButSharedDocuments() {
		final String resourceHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final String documentFileName = "testdocument_1.pdf";
		final String documentHash = "00000000000000000000000000000000";
		final Document document = this.getDbLogic(TEST_USER_2).getDocument(TEST_USER_1, resourceHash, documentFileName);
		assertNotNull(document);
		assertEquals(documentHash, document.getFileHash());
		assertEquals(documentFileName, document.getFileName());
	}

	/**
	 * tests getUsers by folkrank
	 */
	@Ignore
	@Test
	public void testGetUsersByFolkrank(){
		final List<String> tags = new ArrayList<String>();
		tags.add("web");
		final List<User> user = this.getDbLogic().getUsers(null, null, null, tags, null, SortKey.FOLKRANK, null, null, 0, 20);
		assertEquals(20, user.size());
	}
	
	@Test
	public void testGetUsersPendingByUsername() {
		final List<User> users = this.getDbLogic().getUsers(null, GroupingEntity.PENDING, "activationtestuser1" , null, null, null, null, null, 0, 20);
		assertEquals(1, users.size());
	}
	
	/**
	 * tests getUserDetails
	 */
	@Test
	public void getUserDetails() {
		// admin or the user himself has access to spam information
		LogicInterface dbl  = this.getDbLogic(TEST_SPAMMER_NAME);
		User spammer        = dbl.getUserDetails(TEST_SPAMMER_NAME);
		assertEquals(TEST_SPAMMER_NAME, spammer.getName());
		assertEquals(TEST_SPAMMER_ALGORITHM, spammer.getAlgorithm());
		assertThat(spammer.getPrediction(), equalTo(TEST_SPAMMER_PREDICTION));
		assertEquals(TEST_SPAMMER_CONFIDENCE, spammer.getConfidence(), 0.0001);
		assertEquals(TEST_SPAMMER_EMAIL, spammer.getEmail());
		
		// one can not read spam informations about other users
		dbl     = this.getDbLogic(TEST_USER_NAME);
		spammer = dbl.getUserDetails(TEST_SPAMMER_NAME);
		assertNull(spammer.getAlgorithm());
		assertNull(spammer.getPrediction());
		assertNull(spammer.getConfidence());
	}
	
	/**
	 * tests {@link DBLogic#getUserDetails(String)}
	 */
	@Test
	public void getUserDetailsForUserNotInDB() {
		final LogicInterface dbl  = this.getDbLogic(TEST_USER_NAME);
		final User user = dbl.getUserDetails("thisuserdoesntexistindb");
		assertNull(user.getName()); // user unknown => user's name must be null
	}
	
	/**
	 * tests the profile privacy settings
	 */
	@Test
	public void userProfilePrivacy() {
		final String username1 = "testuser3";
		final String username2 = TEST_USER_1;
		final String username3 = TEST_USER_2;
		final LogicInterface logic  = this.getDbLogic(username1);
		final LogicInterface logic2 = this.getDbLogic(username2);
		final LogicInterface logic3 = this.getDbLogic(username3);
		
		final User user = logic.getUserDetails(username1);
		assertNotNull(user.getRealname()); // see my own name
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.PRIVATE);
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		final User userafterUpdatePrivate = logic2.getUserDetails(username1);
		assertNull(userafterUpdatePrivate.getRealname());
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.PUBLIC);
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		final User userAfterUpdatePublic = logic2.getUserDetails(username1);
		assertNotNull(userAfterUpdatePublic.getRealname());
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.FRIENDS);
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		final User user1AfterUpdateFriends = logic2.getUserDetails(username1);
		assertNull(user1AfterUpdateFriends.getRealname()); // testuser3 has no friends
		
		final User user2 = logic3.getUserDetails(username3);
		user2.getSettings().setProfilePrivlevel(ProfilePrivlevel.FRIENDS);
		logic3.updateUser(user2, UserUpdateOperation.UPDATE_CORE);
		
		final User user2AfterUpdateFriends = logic2.getUserDetails(username3);
		assertNotNull(user2AfterUpdateFriends.getRealname()); // testuser1 is friend of testuser2
	}
	
	/**
	 * tests {@link PostUpdateOperation#UPDATE_TAGS} for a publication
	 * @throws Exception 
	 */
	@Test
	public void testPostUpdateTagOnlyOperationPublication() {
		final LogicInterface dbl = this.getDbLogic(TEST_USER_1);
		final User user = dbl.getUserDetails(TEST_USER_1);
		/*
		 *  create a post (a publication)
		 */
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		
		// add tags
		ModelUtils.addToTagSet(post.getTags(), "testCenterTag", "secondTag");
		
		post.getUser().setName(TEST_USER_1);

		final List<Post<?>> posts = new LinkedList<>();
		posts.add(post);
		final List<JobResult> createPosts = dbl.createPosts(posts);
		assertEquals(1, createPosts.size());

		final String hash = createPosts.get(0).getId();
		final Post<? extends Resource> savedPost = dbl.getPostDetails(hash, TEST_USER_1);
		assertNotNull(savedPost);
		
		// get the contentId if more than tags were updated the contentId changes
		final int contentId = savedPost.getContentId();
		
		// abstract
		final String expectedBibtexAbstract = ((BibTex) savedPost.getResource()).getAbstract();
		/*
		 * modify the post; add and remove one tag
		 */
		ModelUtils.addToTagSet(savedPost.getTags(), "newTag");
		savedPost.getTags().remove(new Tag("testCenterTag"));
		
		final BibTex bibtex = (BibTex) savedPost.getResource();
		bibtex.setAbstract("PostUpdateOperation.UPDATE_TAGS");
		
		/*
		 * update the post
		 */
		final List<Post<?>> updates = new LinkedList<>();
		updates.add(savedPost);
		
		final List<JobResult> updatedPosts = dbl.updatePosts(updates, PostUpdateOperation.UPDATE_TAGS);
		assertEquals(1, updatedPosts.size());
		
		/*
		 * check if only tags were updated
		 */
		final Post<? extends Resource> updatedResource = dbl.getPostDetails(hash, TEST_USER_1);
		assertNotNull(updatedResource);
		
		// check content id
		assertThat(updatedResource.getContentId(), equalTo(contentId));
		
		// check tags
		assertTagsByName(ModelUtils.getTagSet("org.bibsonomy.testutil.ModelUtils", "hurz", "secondTag", "newTag"), updatedResource.getTags());
		
		// check changed
		assertEquals(expectedBibtexAbstract, ((BibTex) updatedResource.getResource()).getAbstract());
	}
	
	/**
	 * tests {@link PostUpdateOperation#UPDATE_TAGS} for a bookmark
	 * @throws Exception 
	 */
	@Test
	public void testPostUpdateTagOnlyOperationBookmark() {
		final LogicInterface dbl = this.getDbLogic(TEST_USER_1);
		final User user = dbl.getUserDetails(TEST_USER_1);
		/*
		 *  create a post (a bookmark)
		 */
		final Post<Bookmark> post = ModelUtils.generatePost(Bookmark.class, user);
		
		// add tags
		ModelUtils.addToTagSet(post.getTags(), "testCenterTag", "secondTag");
		
		post.getUser().setName(TEST_USER_1);
		final Bookmark bookmarkB = post.getResource();
		final String url = bookmarkB.getUrl();
		
		final List<JobResult> createPosts = dbl.createPosts(Collections.singletonList(post));
		assertEquals(1, createPosts.size());

		final String hash = createPosts.get(0).getId();
		final Post<? extends Resource> savedPost = dbl.getPostDetails(hash, TEST_USER_1);
		
		// get the contentId if more than tags were updated the contentId changes
		final int contentId = savedPost.getContentId();
		
		/*
		 * modify the post; add and remove one tag
		 */
		ModelUtils.addToTagSet(savedPost.getTags(), "newTag");
		savedPost.getTags().remove(new Tag("testCenterTag"));
		
		
		// update url (not tags)
		final Bookmark bookmark = (Bookmark) savedPost.getResource();
		bookmark.setUrl("http://test2.com");
		
		/*
		 * update the post
		 */
		final List<Post<?>> updates = new LinkedList<>();
		updates.add(savedPost);
		
		final List<JobResult> updatedPosts = dbl.updatePosts(updates, PostUpdateOperation.UPDATE_TAGS);
		assertEquals(1, updatedPosts.size());
		
		/*
		 * check if only tags were updated
		 */
		final Post<? extends Resource> updatedResource = dbl.getPostDetails(hash, TEST_USER_1);
		assertNotNull(updatedResource);
		
		// check content id
		assertThat(updatedResource.getContentId(), equalTo(contentId));
		
		// check tags
		assertTagsByName(ModelUtils.getTagSet("org.bibsonomy.testutil.ModelUtils", "hurz", "secondTag", "newTag"), updatedResource.getTags());
		
		// check if url was not updated
		assertEquals(url, ((Bookmark) updatedResource.getResource()).getUrl());
	}
	
	/**
	 * tests {@link DBLogic#createPosts(List)} and {@link DBLogic#updatePosts(List, PostUpdateOperation)} for storing public posts by limited users as private
	 * @throws Exception 
	 */
	@Test
	public void testNonLimitedUserPosts() throws Exception {
		postAndAssertGroup(GroupUtils.buildPublicGroup(), GroupUtils.buildPublicGroup(), TEST_USER_2, BibTex.class);
		postAndAssertGroup(GroupUtils.buildPublicGroup(), GroupUtils.buildPublicGroup(), TEST_USER_2, Bookmark.class);
	}
	
	/**
	 * tests {@link DBLogic#createPosts(List)} and {@link DBLogic#updatePosts(List, PostUpdateOperation)} for storing public posts by limited users as private
	 * @throws Exception 
	 */
	@Test
	public void testLimitedUserPosts() {
		this.postAndAssertGroup(GroupUtils.buildPublicGroup(), GroupUtils.buildPrivateGroup(), TEST_LIMITED_USER_NAME, BibTex.class);
		this.postAndAssertGroup(GroupUtils.buildPublicGroup(), GroupUtils.buildPrivateGroup(), TEST_LIMITED_USER_NAME, Bookmark.class);
		this.postAndAssertGroup(GroupUtils.buildFriendsGroup(), GroupUtils.buildPrivateGroup(), TEST_LIMITED_USER_NAME, BibTex.class);
		this.postAndAssertGroup(GroupUtils.buildFriendsGroup(), GroupUtils.buildPrivateGroup(), TEST_LIMITED_USER_NAME, Bookmark.class);
	}

	private <R extends Resource> void postAndAssertGroup(Group group, Group expectedGroup, String userName, Class<R> resourceType) {
		final DBLogic dbl = testDatabaseContext.getBean("dbLogicPrototype", DBLogic.class);
		dbl.setLoginUser(getAdminDbLogic(TEST_USER_1).getUserDetails(userName));
		final Post<R> post = ModelUtils.generatePost(resourceType);
		
		post.getUser().setName(userName);
		post.setGroups(Collections.singleton(group));
		final List<JobResult> createPosts = dbl.createPosts(Collections.singletonList(post));
		assertEquals(1, createPosts.size());
		final String hash = createPosts.get(0).getId();
		
		final Post<? extends Resource> savedPost = dbl.getPostDetails(hash, userName);
		assertEquals(1, savedPost.getGroups().size());
		assertTrue(savedPost.getGroups().contains(expectedGroup));
		
		dbl.deletePosts(userName, Collections.singletonList(hash));
	}
	
	/**
	 * Tests that updateUser works with {@link UserUpdateOperation#UPDATE_LIMITED_USER}
	 * @throws Exception
	 */
	@Test
	public void testUpdateLimitedUser() throws Exception {
		final LogicInterface logic = getAdminDbLogic(TEST_USER_1);
		User user = logic.getUserDetails(TEST_LIMITED_USER_NAME);
		assertNotNull(user);
		assertEquals(Role.LIMITED, user.getRole());
		user.setRole(Role.DEFAULT);
		final String oldRealName = user.getRealname();
		final String oldEmail = user.getRealname();
		final URL oldHomepage = user.getHomepage();
		final String oldPw = user.getPassword();
		user.setRealname("testUpdateUserRole");
		user.setHomepage(new URL("http://www.biblicious.org/testUpdateUserRole"));
		user.setEmail("testUpdateUserRole@biblicious.org");
		user.setPassword("testUpdateUserRole");
		logic.updateUser(user, UserUpdateOperation.UPDATE_LIMITED_USER);
		user.setRole(Role.NOBODY);
		user.setRealname("quatsch");
		user.setHomepage(new URL("http://www.biblicious.org/quatsch"));
		user.setEmail("quatsch@biblicious.org");
		user.setPassword("quatsch");
		user = logic.getUserDetails(TEST_LIMITED_USER_NAME);
		assertEquals(Role.DEFAULT, user.getRole());
		assertEquals("testUpdateUserRole", user.getRealname());
		assertEquals(new URL("http://www.biblicious.org/testUpdateUserRole"), user.getHomepage());
		assertEquals("testUpdateUserRole@biblicious.org", user.getEmail());
		assertEquals(oldPw, user.getPassword());
		
		user.setRole(Role.LIMITED);
		user.setRealname(oldRealName);
		user.setHomepage(oldHomepage);
		user.setEmail(oldEmail);
		logic.updateUser(user, UserUpdateOperation.UPDATE_LIMITED_USER);
		user = logic.getUserDetails(TEST_LIMITED_USER_NAME);
		assertEquals(Role.LIMITED, user.getRole());
	}
	
	/**
	 * tests the {@link PostUpdateOperation#UPDATE_ALL}
	 */
	@Test
	public void updateOperationAll() {
		final LogicInterface dbl = this.getDbLogic(TEST_USER_1);

		final User user = dbl.getUserDetails(TEST_USER_1);

		final Post<Bookmark> post = ModelUtils.generatePost(Bookmark.class, user);
		post.getResource().setUrl("http://www.notest.org");
		post.getResource().recalculateHashes();

		final List<JobResult> createdPosts = dbl.createPosts(Collections.singletonList(post));
		assertEquals(1, createdPosts.size());
		
		final Post<?> createdPost = dbl.getPostDetails(createdPosts.get(0).getId(), TEST_USER_1);
		
		final Bookmark createdBookmark = (Bookmark) createdPost.getResource();
		
		final String newURL = "http://www.testAll2.com";
		createdBookmark.setUrl(newURL);
		
		final List<JobResult> updatedPosts = dbl.updatePosts(Collections.singletonList(createdPost), PostUpdateOperation.UPDATE_ALL);
		assertEquals(1, updatedPosts.size());
		
		final Post<?> updatedPost  = dbl.getPostDetails(updatedPosts.get(0).getId(), TEST_USER_1);
		
		final Bookmark updatedBookmark = (Bookmark) updatedPost.getResource();
		assertEquals(newURL, updatedBookmark.getUrl());
	}
	
	/**
	 * tests the {@link PostUpdateOperation#UPDATE_REPOSITORY}	
	 * @throws Exception 
	 */
	@Test
	@Ignore
	public void updateOperationRepository() throws Exception {
		final LogicInterface dbl = this.getDbLogic(TEST_REQUEST_USER_NAME);
		
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		post.getResource().setUrl("http://www.PostUpdateOperation#UPDATE_REPOSITORY.org");
		post.getResource().setTitle("PostUpdateOperation#UPDATE_REPOSITORY");
		post.getResource().recalculateHashes();
		
		final List<JobResult> createdPosts = dbl.createPosts(Collections.singletonList(post));
		assertEquals(1, createdPosts.size());
		
		final Post<?> createdPost = dbl.getPostDetails(createdPosts.get(0).getId(), TEST_REQUEST_USER_NAME);
		final List<Repository> repositorys = new ArrayList<>();

		Repository repo = new Repository();
		repo.setId("TEST_REPOSITORY_1");
		repositorys.add(repo );
		createdPost.setRepositorys(repositorys );

		List<JobResult> updatedPosts = dbl.updatePosts(Collections.singletonList(createdPost), PostUpdateOperation.UPDATE_REPOSITORY);
		assertEquals(1, updatedPosts.size());

		repositorys.clear();
		
		repo = new Repository();
		repo.setId("TEST_REPOSITORY_2");
		repositorys.add(repo );
		createdPost.setRepositorys(repositorys );
		
		updatedPosts = dbl.updatePosts(Collections.singletonList(createdPost), PostUpdateOperation.UPDATE_REPOSITORY);
		assertEquals(1, updatedPosts.size());
		
		final List<Post<BibTex>> posts = dbl.getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, null, "36a19ee7b7923b062a99a6065fe07792", null, QueryScope.LOCAL, Sets.asSet(FilterEntity.POSTS_WITH_REPOSITORY), null, null, null, 0, Integer.MAX_VALUE);
		assertEquals(3, posts.size());
		
		Post<BibTex> b = posts.get(0);
		assertEquals(b.getRepositorys().size() , 2);
		
		b = posts.get(1);
		assertEquals(b.getRepositorys().size() , 1);

		b = posts.get(2);
		assertEquals(b.getRepositorys().size() , 1);
	}

	/**
	 * tests {@link DBLogic#validateGroups(User, Set, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testValidateGroups() {
		final User user = new User(TEST_USER_2);
		final DBLogic logic = testDatabaseContext.getBean("dbLogicPrototype", DBLogic.class);
		logic.setLoginUser(user);
		
		/*
		 * test empty group, public group must be added
		 */
		final Set<Group> groups = new HashSet<Group>();
		logic.validateGroups(user, groups, this.dbSession);
		
		assertEquals(1, groups.size());
		final Group group = groups.iterator().next();
		assertEquals(GroupUtils.buildPublicGroup(), group);
		assertEquals(GroupID.PUBLIC.getId(), group.getGroupId());
		
		/*
		 * test if validateGroup inserts correct id for special group
		 */
		groups.clear();
		final Group publicGroup = new Group("public");
		groups.add(publicGroup);
		
		logic.validateGroups(user, groups, this.dbSession);
		assertEquals(GroupID.PUBLIC.getId(), publicGroup.getGroupId());
		
		/*
		 * two special groups are prohibited
		 */
		try {
			groups.add(GroupUtils.buildPrivateGroup());
			logic.validateGroups(user, groups, this.dbSession);
			fail("invalid groups not found");
		} catch (final ValidationException ex) {
			// ok
		}
		
		/*
		 * only testgroup1 should validate and set the correct group id
		 */
		groups.clear();
		final Group testGroup1 = new Group("testgroup1");
		groups.add(testGroup1);
		logic.validateGroups(user, groups, this.dbSession);
		assertEquals(TESTGROUP1_ID, testGroup1.getGroupId());
		
		/*
		 * testuser2 is not member of testgroup2
		 */
		final Group testGroup2 = new Group("testgroup2");
		groups.add(testGroup2);
		
		try {
			logic.validateGroups(user, groups, this.dbSession);
			fail("user is not member of group but validation was successful");
		} catch (final ValidationException ex) {
			// ok
		}
		
		/*
		 * test if validation finds inexistent group
		 */
		groups.remove(testGroup2);
		groups.add(new Group("thisisaspecialgroup"));
		try {
			logic.validateGroups(user, groups, this.dbSession);
			fail("inexistent group not found");
		} catch (final ValidationException ex) {
			// ok
		}		
	}


	@Test
	public void testCreateOrganizationAsAdmin() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);
		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface dblogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		dblogic.createGroup(organization);
		Group retrievedGroup = dblogic.getGroupDetails(groupName, false);

		assertThat(retrievedGroup.getName(), equalTo(groupName));
		assertThat(retrievedGroup.isOrganization(), equalTo(true));
	}


	@Test(expected = AccessDeniedException.class)
	public void testCreateOrganizationAsDefaultUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);
		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface dblogic = this.getDbLogic(DBLogicTest.TEST_USER_1);

		dblogic.createGroup(organization);
	}


	@Test
	public void testCreateGroupAsAdmin() {
		final String groupName = "my group";

		final Group group = new Group(groupName);

		group.setDescription("This is group");
		group.setAllowJoin(true);
		group.setOrganization(false);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		group.setGroupRequest(groupRequest);

		LogicInterface dblogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		dblogic.createGroup(group);
		Group retrievedGroup = dblogic.getGroupDetails(groupName, true);

		assertThat(retrievedGroup.getName(), equalTo(groupName));
		assertThat(retrievedGroup.isOrganization(), equalTo(false));
	}


	@Test
	public void testCreateGroupAsDefaultUser() {
		final String groupName = "my group";

		final Group group = new Group(groupName);

		group.setDescription("This is a group");
		group.setAllowJoin(true);
		group.setOrganization(false);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		group.setGroupRequest(groupRequest);

		LogicInterface dblogic = this.getDbLogic(DBLogicTest.TEST_USER_1);

		dblogic.createGroup(group);

		Group retrievedGroup = dblogic.getGroupDetails(groupName, true);

		assertThat(retrievedGroup.getName(), equalTo(groupName));
		assertThat(retrievedGroup.isOrganization(), equalTo(false));
	}


	@Test(expected = AccessDeniedException.class)
	public void testUpdateOrganizationAsDefaultUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		LogicInterface defaultDbLogic = this.getDbLogic(groupName);
		Group retrievedGroup = defaultDbLogic.getGroupDetails(groupName, false);

		defaultDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.UPDATE_SETTINGS, null);
	}


	@Test
	public void testUpdateOrganizationAsAdminUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		// requery the groups
		adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);
		Group retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);

		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.UPDATE_SETTINGS, null);
	}


	@Test
	public void testAddMemberToOrganizationAsAdminUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		Group retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);
		GroupMembership membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_2), GroupRole.MODERATOR, false);

		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_MEMBER, membership);
	}

	@Test
	public void testUserJoinsOrganisationAfterInviteByModerator() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);
		Group retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);

		// If a user is added to a group he always gets the USER role first, so we have to adjust it later
		// add a user to a group
		GroupMembership membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_2), GroupRole.USER, false);
		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_MEMBER, membership);

		// retrieve the updated group object
		retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);
		membership = retrievedGroup.getGroupMembershipForUser(DBLogicTest.TEST_USER_2);

		// update the role
		membership.setGroupRole(GroupRole.MODERATOR);
		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.UPDATE_GROUPROLE, membership);

		// switch to the user that now has the assigned role
		LogicInterface moderatorDbLogic = this.getDbLogic(DBLogicTest.TEST_USER_2);
		retrievedGroup = moderatorDbLogic.getGroupDetails(groupName, false);
		membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_3), GroupRole.USER, false);

		// invite user to join the group
		moderatorDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_INVITED, membership);

		LogicInterface userDbLogic = this.getDbLogic(DBLogicTest.TEST_USER_3);
		Group g = userDbLogic.getGroupDetails(groupName, false);
		GroupMembership m = new GroupMembership(userDbLogic.getUserDetails(DBLogicTest.TEST_USER_3), GroupRole.USER, false);

		userDbLogic.updateGroup(g, GroupUpdateOperation.ADD_MEMBER, m);
	}

	@Test
	public void testUserJoinsOrganisationAfterInviteByAdministrator() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);
		Group retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);

		// If a user is added to a group he always gets the USER role first, so we have to adjust it later
		// add a user to a group
		GroupMembership membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_2), GroupRole.USER, false);
		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_MEMBER, membership);

		// retrieve the updated group object
		retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);
		membership = retrievedGroup.getGroupMembershipForUser(DBLogicTest.TEST_USER_2);

		// update the role
		membership.setGroupRole(GroupRole.ADMINISTRATOR);
		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.UPDATE_GROUPROLE, membership);

		// switch to the user that now has the assigned role
		LogicInterface moderatorDbLogic = this.getDbLogic(DBLogicTest.TEST_USER_2);
		retrievedGroup = moderatorDbLogic.getGroupDetails(groupName, false);
		membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_3), GroupRole.USER, false);

		// invite user to join the group
		moderatorDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_INVITED, membership);

		LogicInterface userDbLogic = this.getDbLogic(DBLogicTest.TEST_USER_3);
		Group g = userDbLogic.getGroupDetails(groupName, false);
		GroupMembership m = new GroupMembership(userDbLogic.getUserDetails(DBLogicTest.TEST_USER_3), GroupRole.USER, false);

		userDbLogic.updateGroup(g, GroupUpdateOperation.ADD_MEMBER, m);
	}


	@Test(expected=AccessDeniedException.class)
	public void testUserJoinsOrganisationAfterInviteByUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);
		Group retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);

		// If a user is added to a group he always gets the USER role first, so we have to adjust it later
		// add a user to a group
		GroupMembership membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_2), GroupRole.USER, false);
		adminDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_MEMBER, membership);

		// switch to the user that now has the assigned role
		LogicInterface moderatorDbLogic = this.getDbLogic(DBLogicTest.TEST_USER_2);
		retrievedGroup = moderatorDbLogic.getGroupDetails(groupName, false);
		membership = new GroupMembership(adminDbLogic.getUserDetails(DBLogicTest.TEST_USER_3), GroupRole.USER, false);

		// invite user to join the group
		moderatorDbLogic.updateGroup(retrievedGroup, GroupUpdateOperation.ADD_INVITED, membership);
	}


	@Test
	public void testDeleteOrganizationAsAdminUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);
		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		// requery the groups
		adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);
		Group retrievedGroup = adminDbLogic.getGroupDetails(groupName, false);

		adminDbLogic.deleteGroup(groupName, false, false);
	}


	@Test(expected = AccessDeniedException.class)
	public void testDeleteOrganizationAsDefaultUser() {
		final String groupName = "my organization";

		final Group organization = new Group(groupName);

		organization.setDescription("This is an organization");
		organization.setAllowJoin(true);
		organization.setOrganization(true);

		final GroupRequest groupRequest = new GroupRequest();
		groupRequest.setUserName(DBLogicTest.TEST_USER_1);
		groupRequest.setReason("no real reason");

		organization.setGroupRequest(groupRequest);

		LogicInterface adminDbLogic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);

		/*
		 * organizations are automatically activated, so pending is set to false.
		 */
		adminDbLogic.createGroup(organization);

		// requery the groups
		LogicInterface dbLogic = this.getDbLogic(DBLogicTest.TEST_USER_1);
		dbLogic.deleteGroup(groupName, false, false);
	}


	@Test
	public void testGetAllGroups() {
		LogicInterface logic = this.getDbLogic(DBLogicTest.TEST_USER_1);

		final GroupQuery query = GroupQuery.builder().pending(false).userName(DBLogicTest.TEST_USER_1).
						start(0).end(100).build();
		final List<Group> groups = logic.getGroups(query);

		assertThat(groups.size(), equalTo(8));
	}


	@Test
	public void testGetGroupByExternalId() {
		LogicInterface logic = this.getDbLogic(DBLogicTest.TEST_USER_1);
		final GroupQuery query = GroupQuery.builder().pending(false).userName(DBLogicTest.TEST_USER_1).
						start(0).end(100).externalId("extid1").build();
		List<Group> groups = logic.getGroups(query);

		assertThat(groups.size(), equalTo(1));

		Group g = groups.get(0);

		assertThat(g.getInternalId(), equalTo("extid1"));
	}


	@Test
	public void testGetAllPendingGroups() {
		LogicInterface logic = this.getAdminDbLogic(DBLogicTest.TEST_USER_1);
		final GroupQuery query = GroupQuery.builder().pending(true).start(0).end(100).build();
		List<Group> groups = logic.getGroups(query);

		assertThat(groups.size(), equalTo(2));

	}


	@Test
	public void testGetPendingGroupsForUser() {
		LogicInterface logic = this.getAdminDbLogic("testrequestuser1");
		final GroupQuery query = GroupQuery.builder().pending(true).start(0).end(100).userName("testrequestuser1").build();
		List<Group> groups = logic.getGroups(query);

		assertThat(groups.size(), equalTo(1));
	}


}