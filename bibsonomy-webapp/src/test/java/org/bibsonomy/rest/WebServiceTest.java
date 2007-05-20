package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Blackbox tests for the REST-API.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class WebServiceTest {

	private HttpClient client;
	private Document doc;
	private String apiUrl;

	@Before
	public void setUp() throws ConfigurationException {
		// Load configuration
		final Configuration config = new PropertiesConfiguration("rest-api.properties");
		final String host = config.getString("host");
		final Integer port = config.getInt("port");
		final String realm = config.getString("realm");
		final String user = config.getString("user");
		final String pass = config.getString("pass");
		this.apiUrl = config.getString("url");

		// configure the HTTP client
		this.client = new HttpClient();
		final AuthScope authScope = new AuthScope(host, port, realm);
		final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
		this.client.getState().setCredentials(authScope, credentials);
	}

	@After
	public void tearDown() {
		this.client = null;
		this.apiUrl = null;
	}

	/**
	 * Starts a GET request with authentication on the REST-API
	 */
	private GetMethod getWebServiceAction(final String path) {
		GetMethod get = null;
		try {
			get = new GetMethod(this.apiUrl + path);
			get.setDoAuthentication(true);
			this.client.executeMethod(get);
		} catch (final Exception ex) {
			fail("Exception");
		}
		return get;
	}

	/**
	 * Converts the XML response from the REST-API into a DOM
	 */
	private Document getResponseBodyAsDocument(final GetMethod get) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(get.getResponseBodyAsString());
		} catch (final DocumentException ex) {
			fail("DocumentException");
		} catch (final IOException ex) {
			fail("IOException");
		}
		return doc;
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
		final GetMethod get = new GetMethod(this.apiUrl);
		this.client.executeMethod(get);
		assertEquals(HttpServletResponse.SC_FORBIDDEN, get.getStatusCode());
	}

	@Test
	public void requestWithoutAction() {
		final GetMethod get = this.getWebServiceAction("");
		assertEquals(HttpServletResponse.SC_FORBIDDEN, get.getStatusCode());
	}

	@Test
	public void getPosts() {
		this.doc = this.getDocumentForWebServiceAction("posts?resourcetype=bibtex");
		// Check posts count
		final Node posts = this.doc.selectSingleNode("//posts");
		assertEquals(0, Integer.parseInt(posts.valueOf("@start")));
		assertEquals(18, Integer.parseInt(posts.valueOf("@end")));

		this.doc = this.getDocumentForWebServiceAction("posts?start=0&end=10&resourcetype=bibtex");
		this.doc = this.getDocumentForWebServiceAction("posts?start=0&end=10&resourcetype=bookmark");

		// add tags=web
		// this.doc = this.getDocumentForWebServiceAction("posts?user=hotho&start=0&end=10&resourcetype=bibtex");
	}
}