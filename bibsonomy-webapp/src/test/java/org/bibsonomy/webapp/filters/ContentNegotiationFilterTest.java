package org.bibsonomy.webapp.filters;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.testing.ServletTester;

/**
 * @author rja
 * @version $Id$
 */
public class ContentNegotiationFilterTest {

	private ServletTester tester;
	private String baseUrl;
	private HttpClient client;


	/**
	 * start jetty container
	 * 
	 * @throws Exception
	 */
	@Before
	public void initServletContainer () throws Exception {
		tester = new ServletTester();
		tester.setContextPath("/");
		tester.addFilter(ContentNegotiationFilter.class, "/*", 1);
		tester.addServlet(TestServlet.class, "/*");
		baseUrl = tester.createSocketConnector(true);
		tester.start();
		client = new HttpClient();
	}

	/**
	 * stop jetty container
	 * @throws Exception 
	 */
	@After
	public void cleanupServletContainer () throws Exception {
		tester.stop();
	}

	/**
	 * test the doFilter() method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDoFilter () throws Exception {
		assertArrayEquals(new String[]{"302", "/burst/user/jaeschke"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "application/rdf+xml"));
		assertArrayEquals(new String[]{"302", "/json/user/jaeschke"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "application/json"));
		assertArrayEquals(new String[]{"302", "/csv/user/jaeschke"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "text/csv"));
		assertArrayEquals(new String[]{"302", "/bib/user/jaeschke"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "text/x-bibtex"));

		/*
		 * per default, API calls and static resources are ignored 
		 */
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/resources/css/style.css", "", "application/rdf+xml"));
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/api/users", "", "application/rdf+xml"));
		
		/*
		 * no redirect when format is explicitly specified
		 */
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/burst/user/jaeschke", "", "application/rdf+xml"));
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/publ/user/jaeschke", "", "application/rdf+xml"));
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/user/jaeschke", "format=bib", "application/rdf+xml"));

		/*
		 * no specific format requested - get HTML page
		 * (some typical headers sent by web browsers)  
		 */
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "text/xhtml"));
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "text/css,*/*;q=0.1"));
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "*/*"));
		assertArrayEquals(new String[]{"200"}, sendHttpGet(baseUrl + "/user/jaeschke", "", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));


	}
	
	/**
	 * Sends GET requests to the specified URL with the given query string and
	 * accept header value. 
	 * 
	 * @param url
	 * @param queryString
	 * @param accept
	 * @return An array containing the HTTP response code as first value. If the 
	 * code is a redirect (302), the second value contains the location the 
	 * redirect points to. 
	 * 
	 * @throws Exception
	 */
	private String[] sendHttpGet (final String url, final String queryString, final String accept) throws Exception {

		try {
			final GetMethod get = new GetMethod(url);
			get.setFollowRedirects(false);
			get.setRequestHeader(new Header("Accept", accept));
			get.setQueryString(queryString);
			client.executeMethod(get);

			final int statusCode = get.getStatusCode();
			final String[] result;
			switch (statusCode) {
			case 302:
				result = new String[]{statusCode + "", stripHost(get.getResponseHeader("Location").getValue())};
				break;
			default:
				result = new String[]{statusCode + ""};
			}
			get.releaseConnection();
			return result;
		} catch (Exception e) {
			throw new Exception("request failed", e);
		} 
	}

	private String stripHost (final String url) {
		return url.replace(baseUrl, "");
	}
	
	/**
	 * Only for testing purposes - does nothing useful.
	 * 
	 * @author rja
	 *
	 */
	public static class TestServlet extends HttpServlet {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8692283813700271210L;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			new BufferedWriter(new OutputStreamWriter(resp.getOutputStream(), "UTF-8")).write("Hello World!");
		}
	}

}
