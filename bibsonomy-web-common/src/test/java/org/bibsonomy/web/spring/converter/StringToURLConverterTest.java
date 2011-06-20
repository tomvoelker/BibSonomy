package org.bibsonomy.web.spring.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;


/**
 * @author dzo
 * @version $Id$
 */
public class StringToURLConverterTest {
	private static final Converter<String, URL> STRING_URL_CONVERTER = new StringToURLConverter();
	
	private static final String URL_1 = "http://bibsonomy.org";
	private static final String URL_2 = "file://var/log/kernel.log";
	
	@Test
	public void testConvert() throws MalformedURLException {
		assertNull(STRING_URL_CONVERTER.convert("   "));
		
		assertEquals(new URL(URL_1), STRING_URL_CONVERTER.convert(URL_1));
		assertEquals(new URL(URL_2), STRING_URL_CONVERTER.convert(URL_2));
	}
}
