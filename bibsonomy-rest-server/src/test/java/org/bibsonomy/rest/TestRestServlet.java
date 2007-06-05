package org.bibsonomy.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class TestRestServlet extends TestCase {

	private RestServlet servlet;
	private NullRequest request;
	private NullResponse response;

	@Override
	protected void setUp() throws Exception {
		this.servlet = new RestServlet();
		this.servlet.setLogicInterface(new TestDatabase());
		
		this.request = new NullRequest();
		this.response = new NullResponse();
	}

	public void testValidateAuthorization() throws Exception {
		try {
			this.servlet.validateAuthorization("YXNkZjphc2Rm");
			fail("exception should have been thrown");
		} catch (AuthenticationException e) {
		}

		try {
			this.servlet.validateAuthorization("Basic ASDFASDF");
		} catch (BadRequestOrResponseException e) {
		}

		assertEquals("error decoding string", this.servlet.validateAuthorization("Basic YXNkZjphc2Rm"), "asdf");
	}

	public void testUnauthorized() throws Exception {
		this.servlet.doGet(this.request, this.response);
		compareWithFile(this.response.getContent(), "failAuth.txt");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	public void testSimpleStuff() throws Exception {
		this.request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
		// try to get '/'
		this.servlet.doGet(this.request, this.response);
		compareWithFile(this.response.getContent(), "failAccess.txt");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	public void testGetComplexStuff() throws Exception {
		this.request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
		this.request.getHeaders().put("User-Agent", RestProperties.getInstance().getApiUserAgent());
		this.request.setPathInfo("/users");

		this.servlet.doGet(this.request, this.response);
		compareWithFile(this.response.getContent(), "exampleComplexResult1.txt");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	// FIXME: do we want this to work?
	/* public void testUTF8() throws Exception {
		final NullRequest request = new NullRequest();
		request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
		request.getHeaders().put("User-Agent", RestProperties.getInstance().getApiUserAgent());
		final NullResponse response = new NullResponse();

		final RestServlet servlet = new RestServlet();
		servlet.setLogicInterface(new TestDatabase());
		final LogicInterface logic = servlet.getLogic();
		final User user = new User();
		user.setName("üöäßéèê");
		logic.storeUser(user, false);
		request.setPathInfo("/users");

		servlet.doGet(request, response);
		compareWithFile(response.getContent(), "UTF8TestResult.txt");
		assertEquals(813, response.getContentLength()); // 813 vs 799
	}*/

	private void compareWithFile(final String sw, final String filename) throws IOException {
		final StringBuffer sb = new StringBuffer(200);
		final File file = new File("src/test/resources/" + filename);
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s + "\n");
		}
		assertTrue("output not as expected", sw.equals(sb.toString()));
	}
}