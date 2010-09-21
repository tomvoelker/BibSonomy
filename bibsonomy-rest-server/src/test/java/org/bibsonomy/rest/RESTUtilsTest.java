package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class RESTUtilsTest {
	
	@Test
	public void mediaType() {		
		RenderingFormat format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json", null);
		assertEquals(RenderingFormat.JSON, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json", "application/json; charset=UTF-8");
		assertEquals(RenderingFormat.JSON, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.singletonMap("format", new String[] { "xml" }), "application/json", "application/json; charset=UTF-8");
		assertEquals(RenderingFormat.XML, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "*/*", "application/json; charset=UTF-8");
		assertEquals(RenderingFormat.JSON, format);
	}
	
	@Test(expected = BadRequestOrResponseException.class)
	public void chuckNorris() {
		RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json", "application/xml; charset=UTF-8");
	}
}
