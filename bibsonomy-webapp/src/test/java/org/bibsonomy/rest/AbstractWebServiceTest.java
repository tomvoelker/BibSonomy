package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Blackbox tests for the REST-API.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractWebServiceTest {
	private static final Log log = LogFactory.getLog(AbstractWebServiceTest.class);

	private HttpClient client;
	private static AuthScope authScope;
	private static UsernamePasswordCredentials credentials;
	private static String apiUrl;
	protected Document doc;

	@BeforeClass
	public static void classSetUp() throws ConfigurationException {
		// Load configuration
		final Configuration config = new PropertiesConfiguration("src/test/resources/rest-api.properties");
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
	private GetMethod getWebServiceAction(final String path, final boolean authenticate) {
		GetMethod get = null;
		try {
			get = new GetMethod(apiUrl + path);
			get.setDoAuthentication(authenticate);
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
	protected Document getDocumentForWebServiceAction(final String path, final int expectedState, final boolean authenticate) {
		final GetMethod get = this.getWebServiceAction(path, authenticate);
		assertEquals(expectedState, get.getStatusCode());
		return this.getResponseBodyAsDocument(get);
	}
}