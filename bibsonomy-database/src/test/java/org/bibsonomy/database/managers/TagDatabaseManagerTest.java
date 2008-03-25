package org.bibsonomy.database.managers;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * Tests related to tags.
 *
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Christian Kramer
 * @version $Id$
 */
public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getTagById() {
		final Tag tag = this.tagDb.getTagById(5218, this.dbSession);
		assertEquals(5218, tag.getId());
		assertEquals("$100", tag.getName());
		assertNull(tag.getStem());
		assertEquals(1, tag.getGlobalcount());
	}

	@Test
	public void getTagByCount() {
		final List<Tag> tags = this.tagDb.getTagByCount(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
		for (final Tag tag : tags)
			assertEquals(100, tag.getGlobalcount());
	}

	@Test
	public void getAllTags() {
		final List<Tag> tags = this.tagDb.getAllTags(this.tagParam, this.dbSession);
		// assertEquals(10, tags.size());
	}

	@Test
	public void getTagsViewable() {
		final List<Tag> tags = this.tagDb.getTagsViewable(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	public void getTagsByUser() {
		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}
	
	@Test
	public void getTagsByBookmarkResourceType() {
		// declare the resource type
		this.tagParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setGroupId(GroupID.INVALID.getId());

		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);

		assertEquals(10, tags.size());
		
		// some spot tests to verify tags with bookmark as content type
		assertEquals(tags.get(0).getName(), "****");
		assertEquals(tags.get(1).getName(), "*****");
		assertEquals(tags.get(2).getName(), "1999");
		assertEquals(tags.get(3).getName(), "2.0");
	}
	
	
	@Test
	public void getTagsByBibtexResourceType() {
		// declare the resource type
		this.tagParam.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setGroupId(GroupID.INVALID.getId());

		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam, this.dbSession);

		assertEquals(10, tags.size());
		
		// some spot tests to verify tags with bibtex as content type
		assertEquals(tags.get(4).getName(), "2001");
		assertEquals(tags.get(5).getName(), "2002");
		assertEquals(tags.get(6).getName(), "2003");
		assertEquals(tags.get(7).getName(), "2004");
	}

	@Test
	public void getTagsByGroup() {
		final List<Tag> tags = this.tagDb.getTagsByGroup(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	public void getTagsByExpression() {
		this.tagParam.setLimit(1000);
		this.tagParam.setRegex("web");
		this.tagParam.setRequestedUserName("hotho");
		List<Tag> tags = this.tagDb.getTagsByExpression(this.tagParam, this.dbSession);
		assertEquals(12,tags.size());
		this.resetParameters();		
	}

	@Test
	public void insertTas() {
		this.tagDb.insertTas(this.tagParam, this.dbSession);
	}
	
	@Test
	public void getTagDetails() {
		Tag tag = this.tagDb.getTagDetails(this.tagParam, this.dbSession);
		assertNotNull(tag);	
		assertEquals(this.tagParam.getTagIndex().get(0).getTagName(), tag.getName());	
		assertNotNull(tag.getGlobalcount());
		assertNotNull(tag.getUsercount()); 
		this.resetParameters();
	}
	
	@Test
	public void getTags() {
		this.tagParam.setLimit(1500);
		this.tagParam.setRegex(null);
		this.tagParam.setRequestedUserName("hotho");
		this.tagParam.setGrouping(GroupingEntity.USER);
		this.tagParam.setUserName("hotho");
		this.tagParam.setGroupId(GroupID.INVALID.getId());		
		List<Tag> tags = this.tagDb.getTags(this.tagParam, this.dbSession);		
		assertEquals(1408,tags.size());
		// hotho is a spammer, so some other user shouldn't see his tags
		this.tagParam.setUserName("some_other_user");
		this.tagParam.setGroups(Arrays.asList(new Integer[] {0}));
		tags = this.tagDb.getTags(this.tagParam, this.dbSession);		
		assertEquals(0,tags.size());		
		this.resetParameters();			
	}		
	
	@Test
	public void getTagsByAuthor() {
		this.tagParam.setSearch("stumme");
	    this.tagDb.getTagsAuthor(this.tagParam, this.dbSession);
		//System.out.println(tags.size());
		//assertEquals(10, tags.size());
	}
	

	/**
	 * this is just a dummy test to check if the function works; please adapt
	 * it to check it the correct tags are returned when migrating to the new 
	 * test framwork (dbe)
	 */
	@Test
	public void getTagsByBibtexHash() {
		String loginUserName = "hotho";
		String hash = "palim palim";
		List<Tag> tags = this.tagDb.getTagsByBibtexHash(loginUserName, hash, HashID.INTER_HASH, 0, 20, this.dbSession);
	}
	
	/**
	 * this is just a dummy test to check if the function works; please adapt
	 * it to check it the correct tags are returned when migrating to the new 
	 * test framwork (dbe)
	 */
	@Test
	public void getTagsByBibtexHashForUser() {
		final String loginUserName = "hotho";
		final String requestedUserName = "hotho";
		final String hash = "palim palim";
		List<Tag> tags = this.tagDb.getTagsByBibtexHashForUser(loginUserName, requestedUserName, hash, HashID.INTER_HASH, 0, 20, this.dbSession);	
	}
	
	/**
	 * this is just a dummy test to check if the function works; please adapt
	 * it to check it the correct tags are returned when migrating to the new 
	 * test framwork (dbe)
	 */
	@Test
	public void getTagsByBookmarkHash() {
		String loginUserName = "hotho";
		String hash = "palim palim";
		List<Tag> tags = this.tagDb.getTagsByBookmarkHash(loginUserName, hash, 0, 20, this.dbSession);
	}
	
	/**
	 * this is just a dummy test to check if the function works; please adapt
	 * it to check it the correct tags are returned when migrating to the new 
	 * test framwork (dbe)
	 */
	@Test
	public void getTagsByBookmarkHashForUser() {
		final String loginUserName = "hotho";
		final String requestedUserName = "hotho";
		final String hash = "palim palim";		
		List<Tag> tags = this.tagDb.getTagsByBookmarkHashForUser(loginUserName, requestedUserName, hash, 0, 20, this.dbSession);
	}
	
	@Test
	public void getRelatedTags() {
		final TagParam param = new TagParam();
		param.addTagName("web");
		param.addTagName("semantic");
		List<Tag> tags = this.tagDb.getRelatedTags(param, this.dbSession);
	}

	
}