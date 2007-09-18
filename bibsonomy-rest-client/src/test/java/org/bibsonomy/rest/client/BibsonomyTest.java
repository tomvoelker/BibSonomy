package org.bibsonomy.rest.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.bibsonomy.rest.client.queries.get.GetUserDetailsQuery;
import org.junit.Test;

/*
 * FIXME: please don't use "BibSonomy" as name for classes, variables, methods, etc.
 */

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class BibsonomyTest {

	@Test
	public void testInstantiation() {
		try {
			new Bibsonomy("", "test");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given username is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		try {
			new Bibsonomy("test", "");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiKey is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		assertNotNull("instantiation failed", new Bibsonomy("user", "pw"));
	}

	@Test
	public void testSetUsername() {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.setUsername("");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given username is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		bib.setUsername("foo");
	}

	@Test
	public void testSetPassword() {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.setApiKey("");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiKey is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		bib.setApiKey("foo");
	}

	@Test
	public void testSetApiURL() {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.setApiURL("");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiURL is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		try {
			bib.setApiURL("/");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiURL is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		bib.setApiURL("foo");
	}

	@Test
	public void testExecuteQuery() throws Exception {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.executeQuery(new GetUserDetailsQuery("foo"));
			fail("exception should have been thrown");
		} catch (final IllegalStateException e) {
		}
		bib.setUsername("foo");
		try {
			bib.executeQuery(new GetUserDetailsQuery("foo"));
			fail("exception should have been thrown");
		} catch (final IllegalStateException e) {
		}
	}
}