package webdav.beans;

import webdav.tree.beans.TreeEntryTest;
import junit.framework.TestCase;

public class TagNodeTest extends TestCase {

	private TagNode node;

	@Override
	protected void setUp() throws Exception {
		this.node = new TagNode("/test1/test2");
	}

	@Override
	protected void tearDown() throws Exception {
		this.node = null;
	}

	public void testSimple() {
		assertEquals("test2", this.node.getName());

		assertEquals(false, this.node.isCollection());
		this.node.setCollection();
		assertEquals(true, this.node.isCollection());
	}

	public void testSetStringContent() {
		assertEquals(false, this.node.hasContent());
		assertEquals(0, this.node.getContentLength());

		this.node.setContent(TreeEntryTest.getBibtexEntries().get(0));

		assertEquals(true, this.node.hasContent());
		assertTrue(this.node.getContent().startsWith("content"));
		assertEquals(9, this.node.getContentLength());
	}
}