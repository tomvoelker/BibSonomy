package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * This are simple tests because of the simple SQL
 * 
 * @author Christian Schenk
 */
public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * Retrieves only one Tag-object
	 */
	@Test
	@SuppressWarnings("unused")
	public void getTagById() {
		try {
			final Tag tag = (Tag) this.tagDb.getTagById(387);
			// System.out.println(tag);
			// assertEquals(387, tag.getId());
			// assertNotNull(tag.getName());
			// assertNotNull(tag.getStem());
		} catch (final RuntimeException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	/**
	 * Retrieves a List of Tag-objects
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getTagByCount() {
		try {
			final List<Tag> tags = this.tagDb.getTagByCount(24);
			for (final Tag tag : tags) {
				// System.out.println(tag);
				assertTrue(tag.getId() > 0);
				assertNotNull(tag.getName());
				assertNotNull(tag.getStem());
				assertEquals(24, tag.getCount());
			}
		} catch (final RuntimeException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}