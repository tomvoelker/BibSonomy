package org.bibsonomy.pingback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
    private static final String TOP_OF_HTML_PAGE = "" +
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
    "<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\">\n" +
    "\n" +
    "    <head profile=\"http://gmpg.org/xfn/11\">\n" +
    "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
    "\n" +
    "    <title>Something blah blah &laquo;  Ping tester</title>\n" +
    "\n" +
    "    <link rel=\"pingback\" href=\"http://localhost/~emalethan/wordpress/xmlrpc.php\" />\n" +
    "\n";

	private ServletTester tester;
	private String baseUrl;
	private HttpClientLinkLoader linkLoader;
	
	@Before
	public void setUp() throws Exception {
		this.tester = new ServletTester();
		this.tester.setContextPath("/");
		this.tester.addServlet(TestServlet.class, "/*");
		this.baseUrl = tester.createSocketConnector(true);
		this.tester.start();
		
		this.linkLoader = new HttpClientLinkLoader();
		
	}
	
	@After
	public void shutDown() throws Exception {
		this.tester.stop();
	}
	
	@Test
	public void testLoadLink() {

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

    @Test
    public void testStopsReadingAPageAtTheEndOfHead() throws IOException {
        String htmlPage = "" +
                TOP_OF_HTML_PAGE +
                "   <!-- hello -->\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"page\">\n" +
                "\n" +
                "\n" +
                "    <div id=\"header\">";

        String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
        System.out.println(captured);
        assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
        assertFalse("should not have got as far as the end-head tag", captured.indexOf("</head>") > -1);
        assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
    }

    public void testStopsReadingAPageAtTheStartOfBody() throws IOException {
        String htmlPage = "" +
                TOP_OF_HTML_PAGE +
                "   <!-- hello -->\n" +
                "<body>\n" +
                "    <div id=\"page\">\n" +
                "\n" +
                "\n" +
                "    <div id=\"header\">";

        String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
        System.out.println(captured);
        assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
        assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
    }

    public void testStopsReadingAPageAtTheStartOfBodyWithAttributes() throws IOException {
        String htmlPage = "" +
                TOP_OF_HTML_PAGE +
                "   <!-- hello -->\n" +
                "<body class=\"wide\">\n" +
                "    <div id=\"page\">\n" +
                "\n" +
                "\n" +
                "    <div id=\"header\">";

        String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
        System.out.println(captured);
        assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
        assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
    }
	
    private BufferedReader getReaderForString(String htmlPage) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(htmlPage.getBytes())));
    }
    
}
