package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.model.Tag;

public class SimpleTest extends AbstractSqlMapTest {

	public void testGetObject() {
		try {
			final Tag tag = (Tag) this.sqlMap.queryForObject("getTagById", 387);
			// System.out.println(tag);
			assertEquals(387, tag.getId());
			assertNotNull(tag.getName());
			assertNotNull(tag.getStem());
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}

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
		}
	}
}