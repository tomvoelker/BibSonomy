package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RenderingFormat;
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
		
		// old url paramater format handling
		format = RESTUtils.getRenderingFormatForRequest(Collections.singletonMap("format", new String[] { "xml" }), "application/json", "application/json; charset=UTF-8");
		assertEquals(RenderingFormat.XML, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "*/*", "application/json; charset=UTF-8");
		assertEquals(RenderingFormat.JSON, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "text/html,application/json;q=0.9,application/xml;q=0.9,*/*;q=0.8", "");
		assertEquals(RenderingFormat.JSON, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "text/html", "");
		assertEquals(RenderingFormat.XML, format);
	}
	
	@Test(expected = BadRequestOrResponseException.class)
	public void chuckNorris() {
		RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json", "application/xml; charset=UTF-8");
	}
}
