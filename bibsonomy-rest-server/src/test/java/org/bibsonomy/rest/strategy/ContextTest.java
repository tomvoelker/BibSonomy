package org.bibsonomy.rest.strategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ValidationException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextTest extends AbstractContextTest {

	private HashMap<String, String[]> parameterMap;
	private final String NOT_SPLITTED_MSG = "tag parameters are not correctly splitted!";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.parameterMap = new HashMap<String, String[]>();
	}

	public void testGetSimpleTags() {
		this.parameterMap.put("tags", new String[] { "foo bar" });
		final Context ctx = new Context(this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.contains("bar"));
		assertTrue(this.NOT_SPLITTED_MSG, tags.size() == 2);
	}

	public void testGetTags() {
		this.parameterMap.put("tags", new String[] { "foo bar ->subtags -->transitiveSubtags supertags-> transitiveSupertags--> <->correlated" });
		final Context ctx = new Context(this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap);

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

	public void testWrongUsage() {
		try {
			new Context(null, HttpMethod.GET, null, Collections.EMPTY_MAP);
			fail("Should throw exception");
		} catch (ValidationException ex) {
		}

		try {
			new Context(null, HttpMethod.GET, "", null);
			fail("Should throw exception");
		} catch (RuntimeException ex) {
		}
	}
}