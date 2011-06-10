package org.bibsonomy.pingback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.testing.ServletTester;

import com.malethan.pingback.Link;

/**
 * @author rja
 * @version $Id$
 */
public class HttpClientLinkLoaderTest {

	private ServletTester tester;
	private String baseUrl;
	
	@Before
	public void setUp() throws Exception {
		tester = new ServletTester();
		tester.setContextPath("/");
		tester.addServlet(TestServlet.class, "/*");
		baseUrl = tester.createSocketConnector(true);
		tester.start();
	}
	
	@After
	public void shutDown() throws Exception {
		tester.stop();
	}
	
	@Test
	public void testLoadLink() {
		final HttpClientLinkLoader linkLoader = new HttpClientLinkLoader();

		assertFalse(linkLoader.loadLink(baseUrl + "/pingback").isPingbackEnabled());
//		assertTrue(linkLoader.loadLink(baseUrl + "/pingback?header=true").isPingbackEnabled()); // FIXME: Why infinite loop?
		
		final Link link1 = linkLoader.loadLink(baseUrl + "/pingback?body=true");
		assertTrue(link1.isPingbackEnabled());
		assertEquals(baseUrl + "/pingback/xmlrpc", link1.getPingbackUrl());
		assertEquals(baseUrl + "/pingback?body=true", link1.getUrl());
		
		final Link link2 = linkLoader.loadLink(baseUrl + "/pingback?body=true&header=true");
		assertTrue(link2.isPingbackEnabled());
		assertEquals(baseUrl + "/pingback/xmlrpc", link2.getPingbackUrl());
		assertEquals(baseUrl + "/pingback?body=true&header=true", link2.getUrl());
	}

}
