package webdav.tree.sort.impl;

import java.util.List;

import junit.framework.TestCase;
import webdav.tree.sort.AbstractNodeSorterTest;

public class SimpleAlphabeticalSorterTest extends TestCase {

	private SimpleAlphabeticalSorter sorter;
	private List<String> nodes;

	@Override
	protected void setUp() throws Exception {
		this.nodes = AbstractNodeSorterTest.getNodes();
		this.sorter = new SimpleAlphabeticalSorter(this.nodes);
	}

	@Override
	protected void tearDown() throws Exception {
		this.nodes = null;
		this.sorter = null;
	}

	public void testGetNodes() {
		final List<String> sorted = this.sorter.getNodes();
		assertEquals(4, sorted.size());
		assertTrue(sorted.contains("/a"));
		assertTrue(sorted.contains("/b"));
		assertTrue(sorted.contains("/c"));
		assertTrue(sorted.contains("/x"));
	}

	public void testGetChildren() {
		assertEquals(2, this.sorter.getChildren("/a").size());
		assertEquals(1, this.sorter.getChildren("/b").size());
		assertEquals(1, this.sorter.getChildren("/c").size());
		assertEquals(3, this.sorter.getChildren("/x").size());
	}
}