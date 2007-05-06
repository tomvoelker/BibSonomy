package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.model.Tag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests related to tags.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getTagById() {
		final Tag tag = this.tagDb.getTagById(this.tagParam, this.dbSession);
		assertEquals(5218, tag.getId());
		assertEquals("$100", tag.getName());
		assertEquals("", tag.getStem());
		assertEquals(5, tag.getCount());
	}

	@Test
	public void getTagByCount() {
		final List<Tag> tags = this.tagDb.getTagByCount(this.tagParam, this.dbSession);
		assertEquals(19, tags.size());
		for (final Tag tag : tags)
			assertEquals(100, tag.getCount());
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

	/*
	 * TODO implemented, but further restriction have to add!!
	 */
	@Test
	public void getTagsByExpression() {
		this.tagDb.getTagsByExpression(this.tagParam, this.dbSession);
	}

	@Test
	public void insertTas() {
		this.tagDb.insertTas(this.tagParam, this.dbSession);
	}
}