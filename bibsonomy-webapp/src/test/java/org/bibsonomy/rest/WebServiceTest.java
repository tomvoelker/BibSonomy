package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.DOMReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Blackbox tests for the REST-API.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class WebServiceTest {

	/** Logger */
	private static final Logger log = Logger.getLogger(WebServiceTest.class);

	private HttpClient client;
	private static AuthScope authScope;
	private static UsernamePasswordCredentials credentials;
	private static String apiUrl;
	private Document doc;

	@BeforeClass
	public static void classSetUp() throws ConfigurationException {
		// Load configuration
		final Configuration config = new PropertiesConfiguration("rest-api.properties");
		final String host = config.getString("host");
		final Integer port = config.getInt("port");
		final String realm = config.getString("realm");
		final String user = config.getString("user");
		final String pass = config.getString("pass");
		apiUrl = config.getString("url");
		authScope = new AuthScope(host, port, realm);
		credentials = new UsernamePasswordCredentials(user, pass);
	}

	@AfterClass
	public static void classTearDown() {
		apiUrl = null;
	}

	@Before
	public void setUp() {
		// configure the HTTP client
		this.client = new HttpClient();
		this.client.getState().setCredentials(authScope, credentials);
	}

	@After
	public void tearDown() {
		this.client = null;
	}

	/**
	 * Starts a GET request with authentication on the REST-API
	 */
	private GetMethod getWebServiceAction(final String path) {
		GetMethod get = null;
		try {
			get = new GetMethod(apiUrl + path);
			get.setDoAuthentication(true);
			log.debug("Executing: " + get.getURI());
			this.client.executeMethod(get);
		} catch (final Exception ex) {
			log.fatal(ex.getMessage(),ex);
			fail("Exception: " + ex.getMessage());
		}
		return get;
	}

	/**
	 * Converts the XML response from the REST-API into a DOM
	 */
	private Document getResponseBodyAsDocument(final GetMethod get) {
		try {
			log.debug(get.getResponseBodyAsString());
			DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document domDoc = builder.parse(get.getResponseBodyAsStream());
			return new DOMReader().read(domDoc);
		} catch (final Exception ex) {
			try {
				final OutputStream os = new FileOutputStream("/tmp/bisonomyResponse.xml");
				try {
					os.write(get.getResponseBody());
				} finally {
					os.close();
				}
			} catch (Exception ex2) {
				log.fatal(ex2.getMessage(),ex2);
			}
			log.fatal(ex.getMessage(),ex);
			fail("DocumentException: " + ex.getMessage());
		}
		return null; // unreachable
	}

	/**
	 * Wraps the methods getWebServiceAction and getResponseBodyAsDocument into
	 * a single method.
	 */
	private Document getDocumentForWebServiceAction(final String path) {
		final GetMethod get = this.getWebServiceAction(path);
		assertEquals(HttpServletResponse.SC_OK, get.getStatusCode());
		return this.getResponseBodyAsDocument(get);
	}

	@Test
	public void aGetRequestWithoutAuthentication() throws HttpException, IOException {
		final GetMethod get = new GetMethod(apiUrl);
		this.client.executeMethod(get);
		assertEquals(HttpServletResponse.SC_FORBIDDEN, get.getStatusCode());
	}

	@Test
	public void requestWithoutAction() throws IOException {
		final GetMethod get = this.getWebServiceAction("");
		assertEquals(HttpServletResponse.SC_FORBIDDEN, get.getStatusCode());
		assertTrue(get.getResponseBodyAsString().contains("error"));
	}

	@Test
	public void getPosts() throws IOException {
		for (final String resourcetype : new String[] { "bibtex"/* TODO: , "bookmark" */}) {
			this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=" + resourcetype);
			// Check posts count
			final Node posts = this.doc.selectSingleNode("//posts");
			assertEquals(0, Integer.parseInt(posts.valueOf("@start")));
			assertEquals(20, Integer.parseInt(posts.valueOf("@end")));
			final Number numPosts = this.doc.numberValueOf("count(//post)");
			assertEquals(20, numPosts.intValue());
		}
	}

	// FIXME: db inconsistency @Test
	public void get100Posts() {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex&start=5&end=30");
		// Check posts count
		final Node posts = this.doc.selectSingleNode("//posts");
		assertEquals(5, Integer.parseInt(posts.valueOf("@start")));
		final Number numPosts = this.doc.numberValueOf("count(//post)");
		assertEquals(25, numPosts.intValue());
		assertEquals(30, Integer.parseInt(posts.valueOf("@end")));
	}
}