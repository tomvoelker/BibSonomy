package webdav.beans;

import java.util.List;

import webdav.tree.beans.BibtexEntry;
import webdav.tree.beans.TreeEntryTest;
import junit.framework.TestCase;

public class TagNodeCollectionTest extends TestCase {

	private TagNodeCollection tnc;

	@Override
	protected void setUp() throws Exception {
		final List<BibtexEntry> bibEntries = TreeEntryTest.getBibtexEntries();
		this.tnc = new TagNodeCollection();
		this.tnc.addNode("/");
		this.tnc.addNode("/test1", null);
		this.tnc.addNode("/test2", bibEntries.get(0));
		this.tnc.addNode("/test1/test3", bibEntries.get(1));
	}

	@Override
	protected void tearDown() throws Exception {
		this.tnc = null;
	}

	public void testAlmostEverything() {
		assertEquals("/", this.tnc.getNode("/").getName());
		assertEquals("test1", this.tnc.getNode("/test1").getName());
		assertEquals("test2", this.tnc.getNode("/test2").getName());
		assertEquals("test3", this.tnc.getNode("/test1/test3").getName());

		assertEquals(true, this.tnc.getNode("/").isCollection());
		assertEquals(true, this.tnc.getNode("/test1").isCollection());
		assertEquals(false, this.tnc.getNode("/test2").isCollection());
		assertEquals(false, this.tnc.getNode("/test1/test3").isCollection());

		assertEquals(false, this.tnc.getNode("/").hasContent());
		assertEquals(false, this.tnc.getNode("/test1").hasContent());
		assertEquals(true, this.tnc.getNode("/test2").hasContent());
		assertEquals(true, this.tnc.getNode("/test1/test3").hasContent());

		final List<TagNode> rootChildren = this.tnc.getChildren("/");
		for (final TagNode node : rootChildren) {
			final String childName = node.getName();
			if (!"test1".equals(childName) && !"test2".equals(childName)) {
				fail("Unknown child: '" + childName + "'");
			}
		}

		final List<TagNode> test1Children = this.tnc.getChildren("/test1");
		assertEquals("test3", test1Children.get(0).getName());

		assertEquals(null, this.tnc.getNode("/").getContent());
		assertEquals(null, this.tnc.getNode("/").getFileName());
		assertEquals(null, this.tnc.getNode("/test1").getContent());
		assertEquals(null, this.tnc.getNode("/test1").getFileName());

		assertTrue(this.tnc.getNode("/test2").getContent().startsWith("content"));
		assertEquals(null, this.tnc.getNode("/test2").getFileName());
		assertTrue(this.tnc.getNode("/test1/test3").getContent().startsWith("content"));
		assertEquals(null, this.tnc.getNode("/test1/test3").getFileName());

		assertEquals(0, this.tnc.getNode("/").getContentLength());
		assertEquals(0, this.tnc.getNode("/test1").getContentLength());
		assertEquals(9, this.tnc.getNode("/test2").getContentLength());
		assertEquals(9, this.tnc.getNode("/test1/test3").getContentLength());
	}

	public void testRemove() {
		assertEquals(2, this.tnc.getChildren("/").size());
		this.tnc.removeNode("/test2");
		assertEquals(1, this.tnc.getChildren("/").size());

		assertEquals(1, this.tnc.getChildren("/test1").size());
		this.tnc.removeNode("/test1/test3");
		assertEquals(0, this.tnc.getChildren("/test1").size());

		try {
			this.tnc.removeNode("/test1");
			fail("Should throw exception");
		} catch (final UnsupportedOperationException ex) {}
	}

	public void testGetAllNodes() {
		final List<String> nodes = this.tnc.getAllNodes();
		assertEquals(4, nodes.size());
		assertTrue(nodes.contains("/"));
		assertTrue(nodes.contains("/test1"));
		assertTrue(nodes.contains("/test2"));
		assertTrue(nodes.contains("/test1/test3"));
	}
}