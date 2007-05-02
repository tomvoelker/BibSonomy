package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.model.Tag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getTagById() {
		final Tag tag = this.tagDb.getTagById(this.tagParam);
		assertEquals(5218, tag.getId());
		assertEquals("$100", tag.getName());
		assertEquals("", tag.getStem());
		assertEquals(5, tag.getCount());
	}

	@Test
	public void getTagByCount() {
		final List<Tag> tags = this.tagDb.getTagByCount(this.tagParam);
		assertEquals(19, tags.size());
		for (final Tag tag : tags)
			assertEquals(100, tag.getCount());
	}

	@Test
	public void getAllTags() {
		final List<Tag> tags = this.tagDb.getAllTags(this.tagParam);
		assertEquals(10, tags.size());
	}

	@Test
	public void getTagsViewable() {
		final List<Tag> tags = this.tagDb.getTagsViewable(this.tagParam);
		assertEquals(10, tags.size());
	}

	@Test
	public void getTagsByUser() {
		final List<Tag> tags = this.tagDb.getTagsByUser(this.tagParam);
		assertEquals(10, tags.size());
	}

	@Test
	public void getTagsByGroup() {
		final List<Tag> tags = this.tagDb.getTagsByGroup(this.tagParam);
		assertEquals(10, tags.size());
	}

	/*
	 * TODO implemented, but further restriction have to add!!
	 */
	
	@Test
	 public void getTagsByExpression() { this.tagDb.
	 getTagsByExpression(this.tagParam); }
	 
}