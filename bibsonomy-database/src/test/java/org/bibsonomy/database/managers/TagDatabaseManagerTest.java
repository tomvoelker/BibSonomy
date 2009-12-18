package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
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
 * @version $Id: TagDatabaseManagerTest.java,v 1.40 2009-11-11 10:25:59
 *          nosebrain Exp $
 */

public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/** logger */
	private static final Log log = LogFactory.getLog(TagDatabaseManagerTest.class);

	/**
	 * tests getTagById
	 */
	@Test
	@Ignore
	public void getTagById() {
		final Tag tag = this.tagDb.getTagById(5218, this.dbSession);
		assertEquals(5218, tag.getId());
		assertEquals("$100", tag.getName());
		assertNull(tag.getStem());
		assertEquals(1, tag.getGlobalcount());
	}

	@Test
	@Ignore
	public void getTagByCount() {
		final List<Tag> tags = this.tagDb.getTagByCount(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
		for (final Tag tag : tags)
			assertEquals(100, tag.getGlobalcount());
	}

	@Test
	@Ignore
	public void getAllTags() {
		final List<Tag> tags = this.tagDb.getAllTags(this.tagParam, this.dbSession);
		// assertEquals(10, tags.size());
	}

	@Test
	@Ignore
	public void getTagsViewable() {
		final List<Tag> tags = this.tagDb.getTagsViewable(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}

	/**
	 * tests getTagsByUser with order by Order.FREQUENCY
	 */
	@Test
	public void getTagsByUserOrderedByFrequency() {
		this.tagParam.setRequestedUserName("testuser1");
		this.tagParam.setGroupId(0);
		this.tagParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		this.tagParam.setOrder(Order.FREQUENCY);
		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);
		int count = tags.get(0).getUsercount();
		for(Tag tag : tags) {
				assertTrue(count + " is smalle than " + tag.getUsercount(), count >= tag.getUsercount());
				count = tag.getUsercount();
		}
	}

	@Test
	@Ignore
	public void getTagsByUser() {
		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	@Ignore
	public void getTagsByBookmarkResourceType() {
		// declare the resource type
		this.tagParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setGroupId(GroupID.INVALID.getId());

		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
		// some spot tests to verify tags with bookmark as content type
		assertEquals("****", tags.get(0).getName());
		assertEquals("*****", tags.get(1).getName());
	}

	@Test
	@Ignore
	public void getTagsByBibtexResourceType() {
		// declare the resource type
		this.tagParam.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setGroupId(GroupID.INVALID.getId());

		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);
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
		final List<Tag> tags = this.tagDb.getTagsByGroup(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	@Ignore
	public void getTagsByExpression() {
		this.tagParam.setLimit(1000);
		this.tagParam.setRegex("web");
		this.tagParam.setRequestedUserName("hotho");
		List<Tag> tags = this.tagDb.getTagsByExpression(this.tagParam, this.dbSession);
		assertEquals(12, tags.size());
		this.resetParameters();
	}

	@Ignore
	public void insertTas() {
		this.tagDb.insertTas(this.tagParam, this.dbSession);
	}

	@Test
	@Ignore
	public void getTagDetails() {
		Tag tag = this.tagDb.getTagDetails(this.tagParam, this.dbSession);
		assertNotNull(tag);
		assertEquals(this.tagParam.getTagIndex().get(0).getTagName(), tag.getName());
		assertNotNull(tag.getGlobalcount());
		assertNotNull(tag.getUsercount());
		this.resetParameters();
	}

	@Test
	@Ignore
	public void getTags() {
		this.tagParam.setLimit(1500);
		this.tagParam.setRegex(null);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setUserName("hotho");
		this.tagParam.setGroupId(GroupID.INVALID.getId());
		this.tagParam.setTagIndex(null);
		List<Tag> tags = this.tagDb.getTags(this.tagParam, this.dbSession);
		assertEquals(1412, tags.size());
		// hotho is a spammer, so some other user shouldn't see his tags
		this.tagParam.setUserName("some_other_user");
		this.tagParam.setGroups(Arrays.asList(new Integer[] { 0 }));
		tags = this.tagDb.getTags(this.tagParam, this.dbSession);
		assertEquals(0, tags.size());
		this.resetParameters();
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
		List<Tag> tags = this.tagDb.getTagsByAuthor(param, this.dbSession);
		log.debug("tags found by query getTagsByAuthor for author 'author':");
		for (Tag tag : tags) {
			log.debug(tag.getName());
		}
		assertEquals(2, tags.size());
		assertEquals(1, tags.get(0).getGlobalcount());
		assertEquals(1, tags.get(1).getGlobalcount());
		assertTrue(tags.contains(new Tag("testbibtex")));
		assertTrue(tags.contains(new Tag("testtag")));
		/*
		 * search for non-existing author -> no results
		 */
		param.setSearch("nonexistingauthor");
		tags = this.tagDb.getTagsByAuthor(param, this.dbSession);
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
		ArrayList<Integer> visibleGroups = new ArrayList<Integer>();
		visibleGroups.add(0);
		List<Tag> tags = this.tagDb.getTagsByBibtexHash(loginUserName, hash, HashID.INTER_HASH, visibleGroups, 0, 20, this.dbSession);
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
		ArrayList<Integer> visibleGroups = new ArrayList<Integer>();
		visibleGroups.add(0);
		List<Tag> tags = this.tagDb.getTagsByBibtexHashForUser(loginUserName, requestedUserName, hash, HashID.INTER_HASH, visibleGroups, 0, 20, this.dbSession);
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
		final List<Integer> visibleGroups = new ArrayList<Integer>();
		visibleGroups.add(0);
		List<Tag> tags = this.tagDb.getTagsByBookmarkHash(loginUserName, hash, visibleGroups, 0, 20, this.dbSession);
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
		final List<Integer> visibleGroups = new ArrayList<Integer>();
		visibleGroups.add(0);
		List<Tag> tags = this.tagDb.getTagsByBookmarkHashForUser(loginUserName, requestedUserName, hash, visibleGroups, 0, 20, this.dbSession);
	}

	@Test
	@Ignore
	public void getRelatedTags() {
		final TagParam param = new TagParam();
		param.addTagName("web");
		param.addTagName("semantic");
		param.addGroup(GroupID.PUBLIC.getId());
		List<Tag> tags = this.tagDb.getRelatedTags(param, this.dbSession);
	}

	@Test
	@Ignore
	public void getRelatedTagsForUser() {
		TagParam param = new TagParam();
		param.addTagName("clustering");
		param.addTagName("text");
		ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		visibleGroupIDs.add(0);
		List<Tag> tags = this.tagDb.getRelatedTagsForUser(null, "hotho", param.getTagIndex(), visibleGroupIDs, 0, 10, this.dbSession);
	}

	@Test
	public void updateTags() {
		final User user = new User("testuser1");

		final List<Tag> tagsToReplace = new LinkedList<Tag>();
		tagsToReplace.add(new Tag("suchmaschine"));

		final List<Tag> replacementTags = new LinkedList<Tag>();
		replacementTags.add(new Tag("search"));
		replacementTags.add(new Tag("engine"));

		this.tagDb.updateTags(user, tagsToReplace, replacementTags, this.dbSession);
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
		final List<Integer> visibleGroups = new ArrayList<Integer>();
		visibleGroups.add(GroupID.PUBLIC.getId());
		String requestedUserName = null;
		String loginUserName = null;
		int offset = 0;
		int limit = 10;
		List<Tag> tags = this.tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, limit, offset, this.dbSession);
		assertEquals(3, tags.size());
		assertTrue(tags.contains(new Tag("spam")));
		assertTrue(tags.contains(new Tag("testbibtex")));
		assertTrue(tags.contains(new Tag("testtag")));

		/*
		 * fetch tags from public bibtex entries of testuser1 with the key
		 * "test bibtexkey" (should be 2)
		 */
		requestedUserName = "testuser1";
		tags = this.tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, limit, offset, this.dbSession);
		assertEquals(2, tags.size());
		assertTrue(tags.contains(new Tag("testbibtex")));
		assertTrue(tags.contains(new Tag("testtag")));

		/*
		 * fetch tags from private bibtexs of testuser1 with the key
		 * "test bibtexkey" (should be 1)
		 */
		requestedUserName = null;
		loginUserName = "testuser1";
		visibleGroups.clear();
		visibleGroups.add(GroupID.PRIVATE.getId());
		tags = this.tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, limit, offset, this.dbSession);
		assertEquals(1, tags.size());
		assertEquals("privatebibtex", tags.get(0).getName());
	}

}