package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextTest extends AbstractContextTest {
	private static final String NOT_SPLITTED_MSG = "tag parameters are not correctly splitted!";
	private Map<String, String[]> parameterMap;
	

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.parameterMap = new HashMap<String, String[]>();
	}

	@Test
	public void testGetSimpleTags() {
		this.parameterMap.put("tags", new String[] { "foo bar" });
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap, null, null);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("bar"));
		assertEquals(NOT_SPLITTED_MSG, 2, tags.size());
	}

	@Test
	public void testGetTags() {
		this.parameterMap.put("tags", new String[] { "foo bar ->subtags -->transitiveSubtags supertags-> transitiveSupertags--> <->correlated" });
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap, null, null);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("bar"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("->subtags"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("-->transitiveSubtags"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("supertags->"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("transitiveSupertags-->"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("<->correlated"));
		assertEquals(NOT_SPLITTED_MSG, 7, tags.size());
	}

	@Test
	public void testWrongUsage() {
		try {
			new Context(this.is, null, HttpMethod.GET, null, Collections.EMPTY_MAP, null, null);
			fail("Should throw exception");
		} catch (ValidationException ex) {
		}

		try {
			new Context(this.is, null, HttpMethod.GET, "", null, null, null);
			fail("Should throw exception");
		} catch (RuntimeException ex) {
		}
	}
}