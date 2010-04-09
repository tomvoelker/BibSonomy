package org.bibsonomy.database.managers;

import static org.bibsonomy.testutil.Assert.assertTagsByName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to tags.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Kramer
 * @author Christian Schenk
 * @author Clemens Baier
 * @version $Id$
 */
public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static TagDatabaseManager tagDb;
	
	/**
	 * sets the tag database manager
	 */
	@BeforeClass
	public static void setupManager() {
		tagDb = TagDatabaseManager.getInstance();
	}
	
	@Test
	@Ignore
	public void getAllTags() {
		final List<Tag> tags = tagDb.getAllTags(ParamUtils.getDefaultTagParam(), this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	@Ignore
	public void getTagsViewable() {
		final List<Tag> tags = tagDb.getTagsViewable(ParamUtils.getDefaultTagParam(), this.dbSession);
		assertEquals(10, tags.size());
	}

	/**
	 * tests getTagsByUser with order by Order.FREQUENCY
	 */
	@Test
	public void getTagsByUserOrderedByFrequency() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		tagParam.setRequestedUserName("testuser1");
		tagParam.setGroupId(0);
		tagParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		tagParam.setOrder(Order.FREQUENCY);
		final List<Tag> tags = tagDb.getTagsByUser(tagParam, this.dbSession);
		int count = tags.get(0).getUsercount();
		for (final Tag tag : tags) {
			assertTrue(count + " is smaller than " + tag.getUsercount(), count >= tag.getUsercount());
			count = tag.getUsercount();
		}
	}

	@Test
	@Ignore
	public void getTagsByUser() {
		final List<Tag> tags = tagDb.getTagsByUser(ParamUtils.getDefaultTagParam(), this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	@Ignore
	public void getTagsByBookmarkResourceType() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		// declare the resource type
		tagParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		tagParam.setRequestedUserName("hotho");
		tagParam.setGrouping(GroupingEntity.USER);
		tagParam.setGroupId(INVALID_GROUP_ID);

		final List<Tag> tags = tagDb.getTagsByUser(tagParam, this.dbSession);
		assertEquals(10, tags.size());
		// some spot tests to verify tags with bookmark as content type
		assertEquals("****", tags.get(0).getName());
		assertEquals("*****", tags.get(1).getName());
	}

	@Test
	@Ignore
	public void getTagsByBibtexResourceType() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		// declare the resource type
		tagParam.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		tagParam.setRequestedUserName("hotho");
		tagParam.setGrouping(GroupingEntity.USER);
		tagParam.setGroupId(INVALID_GROUP_ID);

		final List<Tag> tags = tagDb.getTagsByUser(tagParam, this.dbSession);
		assertEquals(10, tags.size());
		// some spot tests to verify tags with bibtex as content type
		assertEquals("2001", tags.get(4).getName());
		assertEquals("2002", tags.get(5).getName());
		assertEquals("2003", tags.get(6).getName());
		assertEquals("2004", tags.get(7).getName());
	}

	@Test
	@Ignore
	public void getTagsByGroup() {
		final List<Tag> tags = tagDb.getTagsByGroup(ParamUtils.getDefaultTagParam(), this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	@Ignore
	public void getTagsByExpression() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		tagParam.setLimit(1000);
		tagParam.setRegex("web");
		tagParam.setRequestedUserName("hotho");
		List<Tag> tags = tagDb.getTagsByExpression(tagParam, this.dbSession);
		assertEquals(12, tags.size());
	}

	@Ignore
	@Test
	public void insertTas() {
		// TODO: write test
	}

	@Test
	@Ignore
	public void getTagDetails() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		Tag tag = tagDb.getTagDetails(tagParam, this.dbSession);
		assertNotNull(tag);
		assertEquals(tagParam.getTagIndex().get(0).getTagName(), tag.getName());
		assertNotNull(tag.getGlobalcount());
		assertNotNull(tag.getUsercount());
	}

	@Test
	@Ignore
	public void getTags() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		tagParam.setLimit(1500);
		tagParam.setRegex(null);
		tagParam.setRequestedUserName("hotho");
		tagParam.setGrouping(GroupingEntity.USER);
		tagParam.setUserName("hotho");
		tagParam.setGroupId(INVALID_GROUP_ID);
		tagParam.setTagIndex(null);
		List<Tag> tags = tagDb.getTags(tagParam, this.dbSession);
		assertEquals(1412, tags.size());
		// hotho is a spammer, so some other user shouldn't see his tags
		tagParam.setUserName("some_other_user");
		tagParam.setGroups(Arrays.asList(0));
		tags = tagDb.getTags(tagParam, this.dbSession);
		assertEquals(0, tags.size());
	}

	/**
	 * tests getTagsByAuthor
	 * 
	 * we have only a single entry in the
	 */
	@Test
	public void getTagsByAuthor() {
		/*
		 * we have only a single entry in the bibtex search table (authored by
		 * "author", points to entry with content id 10; has 2 tags assigned
		 */
		TagParam param = new TagParam();
		param.setSearch("author");
		param.setSearchEntity(SearchEntity.AUTHOR);
		param.setBibtexKey(null);
		param.setLimit(1000);
		param.setOffset(0);
		param.setContentTypeByClass(BibTex.class);
		List<Tag> tags = tagDb.getTagsByAuthor(param, this.dbSession);
		
		assertEquals(2, tags.size());
		assertEquals(1, tags.get(0).getGlobalcount());
		assertEquals(1, tags.get(1).getGlobalcount());
		assertTrue(tags.contains(new Tag("testbibtex")));
		assertTrue(tags.contains(new Tag("testtag")));
		/*
		 * search for non-existing author -> no results
		 */
		param.setSearch("nonexistingauthor");
		tags = tagDb.getTagsByAuthor(param, this.dbSession);
		assertEquals(0, tags.size());
	}

	/**
	 * this is just a dummy test to check if the function works; please adapt it
	 * to check it the correct tags are returned when migrating to the new test
	 * framwork (dbe)
	 */
	@Test
	@Ignore
	public void getTagsByBibtexHash() {
		String loginUserName = "hotho";
		String hash = "palim palim";
		List<Integer> visibleGroups = Collections.singletonList(PUBLIC_GROUP_ID);
		List<Tag> tags = tagDb.getTagsByBibtexHash(loginUserName, hash, HashID.INTER_HASH, visibleGroups, 0, 20, this.dbSession);
	}

	/**
	 * this is just a dummy test to check if the function works; please adapt it
	 * to check it the correct tags are returned when migrating to the new test
	 * framwork (dbe)
	 */
	@Test
	@Ignore
	public void getTagsByBibtexHashForUser() {
		final String loginUserName = "hotho";
		final String requestedUserName = "hotho";
		final String hash = "palim palim";
		List<Integer> visibleGroups = Collections.singletonList(PUBLIC_GROUP_ID);
		List<Tag> tags = tagDb.getTagsByBibtexHashForUser(loginUserName, requestedUserName, hash, HashID.INTER_HASH, visibleGroups, 0, 20, this.dbSession);
	}

	/**
	 * this is just a dummy test to check if the function works; please adapt it
	 * to check it the correct tags are returned when migrating to the new test
	 * framwork (dbe)
	 */
	@Test
	@Ignore
	public void getTagsByBookmarkHash() {
		String loginUserName = "hotho";
		String hash = "palim palim";
		final List<Integer> visibleGroups = Collections.singletonList(PUBLIC_GROUP_ID);
		List<Tag> tags = tagDb.getTagsByBookmarkHash(loginUserName, hash, visibleGroups, 0, 20, this.dbSession);
	}

	/**
	 * this is just a dummy test to check if the function works; please adapt it
	 * to check it the correct tags are returned when migrating to the new test
	 * framwork (dbe)
	 */
	@Test
	@Ignore
	public void getTagsByBookmarkHashForUser() {
		final String loginUserName = "hotho";
		final String requestedUserName = "hotho";
		final String hash = "palim palim";
		final List<Integer> visibleGroups = Collections.singletonList(PUBLIC_GROUP_ID);
		List<Tag> tags = tagDb.getTagsByBookmarkHashForUser(loginUserName, requestedUserName, hash, visibleGroups, 0, 20, this.dbSession);
	}

	@Test
	@Ignore
	public void getRelatedTags() {
		final TagParam param = new TagParam();
		param.addTagName("web");
		param.addTagName("semantic");
		param.addGroup(PUBLIC_GROUP_ID);
		List<Tag> tags = tagDb.getRelatedTags(param, this.dbSession);
	}

	@Test
	@Ignore
	public void getRelatedTagsForUser() {
		final TagParam param = new TagParam();
		param.addTagName("clustering");
		param.addTagName("text");
		final List<Integer> visibleGroupIDs = Collections.singletonList(PUBLIC_GROUP_ID);
		List<Tag> tags = tagDb.getRelatedTagsForUser(null, "hotho", param.getTagIndex(), visibleGroupIDs, 0, 10, this.dbSession);
	}

	@Test
	public void updateTags() {
		final User user = new User("testuser1");

		final List<Tag> tagsToReplace = new LinkedList<Tag>();
		tagsToReplace.add(new Tag("suchmaschine"));

		final List<Tag> replacementTags = new LinkedList<Tag>(ModelUtils.getTagSet("search", "engine"));

		tagDb.updateTags(user, tagsToReplace, replacementTags, this.dbSession);
		
		// TODO: implement test
	}

	/**
	 * tests getTagsByBibtexkey
	 */
	@Test
	public void getTagsByBibtexkey() {
		/*
		 * fetch tags from public bibtex entries with the key "test bibtexkey"
		 * (should be 3)
		 */
		List<Integer> visibleGroups = Collections.singletonList(PUBLIC_GROUP_ID);
		
		String requestedUserName = null;
		String loginUserName = null;
		List<Tag> tags = tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, 10, 0, this.dbSession);
		assertTagsByName(ModelUtils.getTagSet("spam", "testbibtex", "testtag"), tags);

		/*
		 * fetch tags from public bibtex entries of testuser1 with the key
		 * "test bibtexkey" (should be 2)
		 */
		requestedUserName = "testuser1";
		tags = tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, 10, 0, this.dbSession);
		assertTagsByName(ModelUtils.getTagSet("testbibtex", "testtag"), tags);

		/*
		 * fetch tags from private bibtexs of testuser1 with the key
		 * "test bibtexkey" (should be 1)
		 */
		requestedUserName = null;
		loginUserName = "testuser1";
		visibleGroups = Collections.singletonList(PRIVATE_GROUP_ID);
		tags = tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, 10, 0, this.dbSession);
		assertEquals(1, tags.size());
		assertEquals("privatebibtex", tags.get(0).getName());
	}

}