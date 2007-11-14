package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextTest extends AbstractContextTest {

	private HashMap<String, String[]> parameterMap;
	private final String NOT_SPLITTED_MSG = "tag parameters are not correctly splitted!";

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
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("bar"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.size() == 2);
	}

	@Test
	public void testGetTags() {
		this.parameterMap.put("tags", new String[] { "foo bar ->subtags -->transitiveSubtags supertags-> transitiveSupertags--> <->correlated" });
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap, null, null);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("bar"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("->subtags"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("-->transitiveSubtags"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("supertags->"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("transitiveSupertags-->"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("<->correlated"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.size() == 7);
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