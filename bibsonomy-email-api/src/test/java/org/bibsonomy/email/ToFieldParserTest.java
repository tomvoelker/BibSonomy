package org.bibsonomy.email;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$2
 * 
 */
public class ToFieldParserTest {

	@Test
	public void testParseToField() {
		final ToFieldParser parser = new ToFieldParser();
		
		final ToField to0 = parser.parseToField("johndoe-99cafad8ce2afb5879c6c85c14cc5259@api.bibsonomy.org");
		assertEquals("johndoe", to0.getUsername());
		assertEquals("99cafad8ce2afb5879c6c85c14cc5259", to0.getApikey());
		assertEquals(null, to0.getGroup());

		final ToField to1 = parser.parseToField("johndoe-99cafad8ce2afb5879c6c85c14cc5259+private@api.bibsonomy.org");
		assertEquals("johndoe", to1.getUsername());
		assertEquals("99cafad8ce2afb5879c6c85c14cc5259", to1.getApikey());
		assertEquals("private", to1.getGroup());
	}

}
