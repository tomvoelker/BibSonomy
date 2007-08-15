package webdav.helper;

import junit.framework.TestCase;

public class PathHelperTest extends TestCase {

	public void testGetParent() {
		assertEquals("/", PathHelper.getParent("/"));
		assertEquals("/", PathHelper.getParent("/test1"));
		assertEquals("/test1", PathHelper.getParent("/test1/test2"));
		assertEquals("/test1/test2", PathHelper.getParent("/test1/test2/test3"));
	}

	public void testGetName() {
		assertEquals("/", PathHelper.getName("/"));
		assertEquals("test1", PathHelper.getName("/test1"));
		assertEquals("test2", PathHelper.getName("/test1/test2"));
		assertEquals("test3", PathHelper.getName("/test1/test2/test3"));
	}

	public void testBuildPath() {
		assertEquals("/test1", PathHelper.buildPath("/", "test1"));
		assertEquals("/test1/test2", PathHelper.buildPath("/test1", "test2"));
		assertEquals("/test1/test2", PathHelper.buildPath("/test1/", "test2"));
		assertEquals("/test1/test2", PathHelper.buildPath("/test1", "/test2"));
		// assertEquals("/test1/test2", PathHelper.buildPath("/test1/", "/test2"));
		assertEquals("/test1/test3/test2", PathHelper.buildPath("/test1/test3", "test2"));
	}

	public void testGetCombinedPath() {
		final String file = "test.txt";
		final String rootpath = "/root/path";

		assertEquals("/root/path/test.txt", PathHelper.getCombinedPath("/"+file, rootpath));
		assertEquals("/root/path/test.txt", PathHelper.getCombinedPath("/files/"+file, rootpath));
		assertEquals("/root/path/a/path/test.txt", PathHelper.getCombinedPath("/files/a/path/"+file, rootpath));

		try {
			PathHelper.getCombinedPath(file, rootpath);
			fail("Should throw exception");
		} catch (final UnsupportedOperationException ex) {}
	}
}