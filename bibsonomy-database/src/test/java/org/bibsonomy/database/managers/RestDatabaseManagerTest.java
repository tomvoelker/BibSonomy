package org.bibsonomy.database.managers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RestDatabaseManagerTest {

	private HttpClient client;
	private final String apiURLHost = "www.biblicious.org";
	private final int apiURLPort = 80;
	private final String apiURLPrefix = "http://" + this.apiURLHost + ":" + this.apiURLPort + "/api/";
	private final String apiURLRealm = "BibsonomyWebService";

	@Before
	public void setUp() {
		this.client = new HttpClient();
	}

	@After
	public void tearDown() {
		this.client = null;
	}

	private GetMethod getWebServiceAction(final String path) {
		try {
			final AuthScope authScope = new AuthScope(this.apiURLHost, this.apiURLPort, this.apiURLRealm);
			final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("csc", "hurz123"); 
			this.client.getState().setCredentials(authScope, credentials);

			final GetMethod get = new GetMethod(this.apiURLPrefix + path);
			get.setDoAuthentication(true);
			this.client.executeMethod(get);

			return get;
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

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

	@Test
	public void aGetRequestWithoutAuthentication() throws HttpException, IOException {
		final GetMethod get = new GetMethod(this.apiURLPrefix);
		this.client.executeMethod(get);
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, get.getStatusCode());
	}

	@Test
	public void requestWithoutAction() throws HttpException, IOException {
		final GetMethod get = this.getWebServiceAction("");
		assertEquals(HttpServletResponse.SC_FORBIDDEN, get.getStatusCode());
	}

	@Test
	public void getPosts() throws IOException, DocumentException {
		final GetMethod get = this.getWebServiceAction("posts");
		assertEquals(HttpServletResponse.SC_OK, get.getStatusCode());

		final Document doc = this.getResponseBodyAsDocument(get);

		// Check posts count
		final Node posts = doc.selectSingleNode("//posts");
		assertEquals(18, Integer.parseInt(posts.valueOf("@end")));
	}
}