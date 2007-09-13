package org.bibsonomy.database.managers;

import java.util.List;

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
		assertEquals(10, tags.size());
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
	public void getTagsByGroup() {
		final List<Tag> tags = this.tagDb.getTagsByGroup(this.tagParam, this.dbSession);
		assertEquals(10, tags.size());
	}

	@Test
	public void getTagsByExpression() {
		this.tagParam.setLimit(1000);
		List<Tag> tags = this.tagDb.getTagsByExpression(this.tagParam, this.dbSession);
		assertEquals(161,tags.size());
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
		this.tagParam.setLimit(1000);
		List<Tag> tags = this.tagDb.getTags(this.tagParam, this.dbSession);		
		assertEquals(161,tags.size());
		this.resetParameters();			
	}		
	
	@Test
	public void getTagsByAuthor() {
		this.tagParam.setSearch("stumme");
	    this.tagDb.getTagsAuthor(this.tagParam, this.dbSession);
		//System.out.println(tags.size());
		//assertEquals(10, tags.size());
	}

	
}