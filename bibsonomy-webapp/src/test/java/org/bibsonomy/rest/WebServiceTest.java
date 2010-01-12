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
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class WebServiceTest extends AbstractWebServiceTest {

	@Test
	@Ignore // FXME: test which depend on www.biblicous.org are suboptimal...
	public void aGetRequestWithoutAuthentication() throws HttpException, IOException {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex", HttpServletResponse.SC_UNAUTHORIZED, false);
	}

	@Ignore
	@Test
	public void requestWithoutAction() throws IOException {
		this.doc = this.getDocumentForWebServiceAction("", HttpServletResponse.SC_FORBIDDEN, true);
		assertEquals(1, doc.selectObject("count(//error)"));
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

	// FIXME: db inconsistency
	@Test
	@Ignore
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