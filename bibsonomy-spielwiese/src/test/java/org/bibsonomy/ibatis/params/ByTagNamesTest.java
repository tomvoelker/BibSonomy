package org.bibsonomy.ibatis.params;

import junit.framework.TestCase;

public class ByTagNamesTest extends TestCase {

	private ByTagNames byTagNames;

	@Override
	protected void setUp() throws Exception {
		this.byTagNames = new ByTagNames();
	}

	@Override
	protected void tearDown() throws Exception {
		this.byTagNames = null;
	}

	public void testGetFromGetWhere() {
		this.byTagNames.setTags(new String[] {"tag1"});
		assertEquals("", this.byTagNames.getFrom());
		assertEquals("lower(t1.tag_name) = lower(\"tag1\")", this.byTagNames.getWhere());

		this.byTagNames.setTags(new String[] {"tag1", "tag2"});
		assertEquals(", tas t2", this.byTagNames.getFrom());
		assertEquals("lower(t1.tag_name) = lower(\"tag1\") AND lower(t2.tag_name) = lower(\"tag2\")", this.byTagNames.getWhere());

		this.byTagNames.setTags(new String[] {"tag1", "tag2", "tag3"});
		assertEquals(", tas t2, tas t3", this.byTagNames.getFrom());
		assertEquals("lower(t1.tag_name) = lower(\"tag1\") AND lower(t2.tag_name) = lower(\"tag2\") AND lower(t3.tag_name) = lower(\"tag3\")", this.byTagNames.getWhere());
	}
}