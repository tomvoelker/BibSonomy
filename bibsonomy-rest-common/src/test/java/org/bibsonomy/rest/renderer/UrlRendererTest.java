package org.bibsonomy.rest.renderer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dzo
 */
public class UrlRendererTest {

	private static final UrlRenderer RENDERER = new UrlRenderer("");

	@Test
	public void testCreateHrefForUser() throws Exception {
		assertEquals("/users/testuser%201", RENDERER.createHrefForUser("testuser 1"));
	}

	@Test
	public void testCreateHrefForTag() throws Exception {
		assertEquals("/tags/test%20test", RENDERER.createHrefForTag("test test"));
	}

	@Test
	public void testCreateHrefForGroup() throws Exception {
		assertEquals("/groups/testgroup%201", RENDERER.createHrefForGroup("testgroup 1"));
	}

	@Test
	public void testCreateHrefForResource() throws Exception {
		assertEquals("/users/testuser%201/posts/123123", RENDERER.createHrefForResource("testuser 1", "123123"));
	}

	@Test
	public void testCreateHrefForResourceDocument() throws Exception {
		assertEquals("/users/testuser1/posts/112/documents/Test%20123.pdf", RENDERER.createHrefForResourceDocument("testuser1", "112", "Test 123.pdf"));
	}
}
