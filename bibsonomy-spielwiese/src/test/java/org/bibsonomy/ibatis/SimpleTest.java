package org.bibsonomy.ibatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * This are simple tests because of the simple SQL
 * 
 * @author Christian Schenk
 */
public class SimpleTest extends AbstractSqlMapTest {

	/**
	 * Retrieves only one Tag-object
	 */
	@Test
	@SuppressWarnings("unused")
	public void testGetObject() {
		try {
			final Tag tag = (Tag) this.sqlMap.queryForObject("getTagById", 387);
			// System.out.println(tag);
			// assertEquals(387, tag.getId());
			// assertNotNull(tag.getName());
			// assertNotNull(tag.getStem());
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	/**
	 * Retrieves a List of Tag-objects
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testGetList() {
		try {
			final List<Tag> tags = sqlMap.queryForList("getTagByCount", 24);
			for (final Tag tag : tags) {
				// System.out.println(tag);
				assertTrue(tag.getId() > 0);
				assertNotNull(tag.getName());
				assertNotNull(tag.getStem());
				assertEquals(24, tag.getCount());
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}
}