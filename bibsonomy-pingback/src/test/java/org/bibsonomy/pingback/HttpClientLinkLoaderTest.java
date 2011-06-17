package org.bibsonomy.pingback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

	private static final String TRACKBACK_RDF1 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
	"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
	"xmlns:trackback=\"http://madskills.com/public/xml/rss/module/trackback/\">\n" + 
	"<rdf:Description\n" +
	"rdf:about=\"http://www.foo.com/archive.html#foo\"\n" +
	"dc:identifier=\"http://www.foo.com/archive.html#foo\"\n" +
	"dc:title=\"Foo Bar\"\n" +
	"trackback:ping=\"http://www.foo.com/tb.cgi/5\" />\n" +
	"</rdf:RDF>\n";
	private static final String TRACKBACK_RDF2 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
	"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
	"xmlns:trackback=\"http://madskills.com/public/xml/rss/module/trackback/\">\n" + 
	"<rdf:Description\n" +
	"rdf:about=\"http://www.foo.com/book.html\"\n" +
	"dc:identifier=\"http://www.foo.com/book.html\"\n" +
	"dc:title=\"Foo Bar\"\n" +
	"trackback:ping=\"http://www.foo.com/tb.cgi/5\" />\n" +
	"</rdf:RDF>\n";

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
	public void testHttpErrors() {
		assertFalse(linkLoader.loadLink(baseUrl + "/errors").isPingbackEnabled());
		assertFalse(linkLoader.loadLink(baseUrl + "/stream").isPingbackEnabled());
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
	
	@Test
	@Ignore
	public void testStopsReadingAPageAtTheStartOfBody() throws IOException {
		final String htmlPage = TOP_OF_HTML_PAGE +
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

	@Test
	public void testStopsReadingAPageAtTheStartOfBodyWithAttributes() throws IOException {
		final String htmlPage = TOP_OF_HTML_PAGE +
		"   <!-- hello -->\n" +
		"<body class=\"wide\">\n" +
		"    <div id=\"page\">\n" +
		"\n" +
		"\n" +
		"    <div id=\"header\">";

		final String captured = linkLoader.readHeadSectionOfPage(getReaderForString(htmlPage));
		System.out.println(captured);
		assertTrue("Have got this '<!-- hello -->' in", captured.indexOf("<!-- hello -->") > -1);
		assertFalse("should not have got as far as the body tag", captured.indexOf("<body>") > -1);
	}

	@Test
	public void testReadRemainingPage() throws IOException {
		/*
		 * create a buffer larger than 500kb
		 */
		final StringBuilder buf = new StringBuilder();
		for (int i=0; i < 60000; i++) {
			buf.append("0123456789\n");
		}
		final String result = linkLoader.readRemainingPage(getReaderForString(buf.toString()));
		/*
		 * ensure that not much more than 500kb are read
		 */
		assertTrue(result.length() < 500100);
		assertTrue(result.length() > 499000);
	}
	
	@Test
	public void testGetTrackbackUrlFromHtml() {
		System.out.println("weg = "  + "http://www.foo.com/archive.html".replaceAll("\\#.*$", ""));
		
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/archive.html#foo", TRACKBACK_RDF1));
		assertNull(linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/archive.html", TRACKBACK_RDF1));
		
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/book.html#foo", TRACKBACK_RDF2));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/book.html", TRACKBACK_RDF2));

		/*
		 * trackback URL in HTML head section
		 */
		final String html = TOP_OF_HTML_PAGE + "<body>" + TRACKBACK_RDF1 + "\n\n<b>Cool!</b>\n" + TRACKBACK_RDF2;
		
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/book.html#foo", html));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/book.html", html));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/archive.html#foo", html));
		assertNull(linkLoader.getTrackbackUrlFromHtml("http://www.foo.com/archive.html", html));
		
		
	}

	@Test 
	public void testGetTrackbackUrl() throws IOException {

		/*
		 * trackback URL in HTML head section
		 */
		final String html = TOP_OF_HTML_PAGE + TRACKBACK_RDF1 + TRACKBACK_RDF2 + "<body>" + "\n\n<b>Cool!</b>\n";
		final BufferedReader reader = getReaderForString(html);
		final String headSection = linkLoader.readHeadSectionOfPage(reader);
		
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrl("http://www.foo.com/book.html#foo", headSection, reader));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrl("http://www.foo.com/book.html", headSection, reader));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrl("http://www.foo.com/archive.html#foo", headSection, reader));
		assertNull(linkLoader.getTrackbackUrl("http://www.foo.com/archive.html", headSection, reader));
		
		/*
		 * trackback URL in HTML body section
		 */
		final String html2 = TOP_OF_HTML_PAGE + "<body>" + TRACKBACK_RDF1 + "\n\n<b>Cool!</b>\n" + TRACKBACK_RDF2;
		
		final BufferedReader reader2 = getReaderForString(html2);
		final String headSection2 = linkLoader.readHeadSectionOfPage(reader2);
		
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrl("http://www.foo.com/book.html#foo", headSection2, reader2));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrl("http://www.foo.com/book.html", headSection2, reader2));
		assertEquals("http://www.foo.com/tb.cgi/5", linkLoader.getTrackbackUrl("http://www.foo.com/archive.html#foo", headSection2, reader2));
		assertNull(linkLoader.getTrackbackUrl("http://www.foo.com/archive.html", headSection2, reader2));
		
		
		
	}
	

	private BufferedReader getReaderForString(String htmlPage) {
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(htmlPage.getBytes())));
	}

}
