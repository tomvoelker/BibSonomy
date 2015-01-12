/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Node;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Blackbox tests for the REST-API.
 * TODO: remove? tests disabled for 3 years
 * 
 * @author Christian Schenk
 */
public class WebServiceTest extends AbstractWebServiceTest {

	@Test
	@Ignore // FIXME: test which depend on www.biblicous.org are suboptimal...
	public void aGetRequestWithoutAuthentication() throws HttpException, IOException {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex", HttpServletResponse.SC_UNAUTHORIZED, false);
	}

	@Ignore
	@Test
	public void requestWithoutAction() throws IOException {
		this.doc = this.getDocumentForWebServiceAction("", HttpServletResponse.SC_FORBIDDEN, true);
		assertEquals(1, this.doc.selectObject("count(//error)"));
	}

	@Ignore
	@Test
	public void getPosts() throws IOException {
		for (final String resourcetype : new String[] { "bibtex"/* TODO: , "bookmark" */}) {
			this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=" + resourcetype, HttpServletResponse.SC_OK, true);
			// Check posts count
			final Node posts = this.doc.selectSingleNode("//posts");
			assertEquals(0, Integer.parseInt(posts.valueOf("@start")));
			assertEquals(20, Integer.parseInt(posts.valueOf("@end")));
			final Number numPosts = this.doc.numberValueOf("count(//post)");
			assertEquals(20, numPosts.intValue());
		}
	}
	
	@Test
	@Ignore // FIXME: db inconsistency
	public void get100Posts() {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex&start=5&end=30", HttpServletResponse.SC_OK, true);
		// Check posts count
		final Node posts = this.doc.selectSingleNode("//posts");
		assertEquals(5, Integer.parseInt(posts.valueOf("@start")));
		final Number numPosts = this.doc.numberValueOf("count(//post)");
		assertEquals(25, numPosts.intValue());
		assertEquals(30, Integer.parseInt(posts.valueOf("@end")));
	}
}