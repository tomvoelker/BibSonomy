/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.junit.Test;

/**
 * @author dzo
 */
public class RESTUtilsTest {

	/**
	 * tests {@link RESTUtils#getRenderingFormatForRequest(java.util.Map, String, String)}
	 */
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
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "*/*", "");
		assertEquals(RenderingFormat.XML, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/*", "");
		assertEquals(RenderingFormat.APP_XML, format);
		
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/xml", "");
		assertEquals(RenderingFormat.APP_XML, format);

		// standard firefox, chromium header
		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", "");
		assertEquals(RenderingFormat.APP_XML, format);

		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/*", "");
		assertEquals(RenderingFormat.APP_XML, format);

		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json", "application/json");
		assertEquals(RenderingFormat.JSON, format);

		format = RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json;q=0.9,application/xml;q=0.91,text/xml;q=0.4,*/*;q=0.1", "");
		assertEquals(RenderingFormat.APP_XML, format);

		format = RESTUtils.getRenderingFormatForRequest(Collections.singletonMap("format", new String[] { "csl" }), "", "");
		assertEquals(RenderingFormat.CSL, format);
	}
	
	@Test(expected = UnsupportedMediaTypeException.class)
	public void testNotSupportedFormatXWWWForm() {
		RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "", "application/x-www-form-urlencoded");
	}
	
	@Test(expected = UnsupportedMediaTypeException.class)
	public void testNotSupportedFormatXWWWFormAndHeadere() {
		RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "*/*", "application/x-www-form-urlencoded");
	}

	/**
	 * tests different accept and content type headers
	 */
	@Test(expected = BadRequestOrResponseException.class)
	public void chuckNorris() {
		RESTUtils.getRenderingFormatForRequest(Collections.emptyMap(), "application/json", "application/xml; charset=UTF-8");
	}
}
