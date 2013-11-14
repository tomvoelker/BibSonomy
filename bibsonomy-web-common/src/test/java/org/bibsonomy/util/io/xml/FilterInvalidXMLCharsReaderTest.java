package org.bibsonomy.util.io.xml;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.bibsonomy.util.StringUtils;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class FilterInvalidXMLCharsReaderTest {

	/**
	 * tests {@link FilterInvalidXMLCharsReader#read()}
	 * @throws IOException
	 */
	@Test
	public void testRead() throws IOException {
		final BufferedReader reader = new BufferedReader(new FilterInvalidXMLCharsReader(new StringReader("This is \uFFFE\uFFFF my test string")));
		final String result = StringUtils.getStringFromReader(reader);
		assertEquals("This is  my test string", result.trim());
	}
}
