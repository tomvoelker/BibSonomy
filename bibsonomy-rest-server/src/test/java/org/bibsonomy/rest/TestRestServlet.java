package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bibsonomy.rest.database.TestDBLogic;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class TestRestServlet {

	private RestServlet servlet;
	private NullRequest request;
	private NullResponse response;
	
	@Before
	public void setUp() {
		this.servlet = new RestServlet();
		this.servlet.setLogicInterface(TestDBLogic.factory);
		
		this.request = new NullRequest();
		this.response = new NullResponse();
	}

	/**
	 * tests {@link RestServlet#validateAuthorization(String)}
	 * @throws Exception
	 */
	@Test
	public void testValidateAuthorization() throws Exception {
		try {
			this.servlet.validateAuthorization("YXNkZjphc2Rm");
			fail("exception should have been thrown");
		} catch (final AuthenticationException e) {
		}

		try {
			this.servlet.validateAuthorization("Basic ASDFASDF");
		} catch (final BadRequestOrResponseException e) {
		}

		assertEquals("error decoding string", "asdf", this.servlet.validateAuthorization("Basic YXNkZjphc2Rm").getAuthenticatedUser().getName());
	}
	
	@Test
	public void testUnauthorized() throws Exception {
		this.servlet.doGet(this.request, this.response);
		compareWithFile(this.response.getContent(), "failAuth.txt");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	@Test
	public void testSimpleStuff() throws Exception {
		this.request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
		// try to get '/'
		this.servlet.doGet(this.request, this.response);
		compareWithFile(this.response.getContent(), "failAccess.txt");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	@Test
	public void testGetComplexStuff() throws Exception {
		this.request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
		this.request.getHeaders().put("User-Agent", RestProperties.getInstance().getApiUserAgent());
		this.request.setPathInfo("/users");

		this.servlet.doGet(this.request, this.response);
		compareWithFile(this.response.getContent(), "exampleComplexResult1.txt");
		System.out.println(this.response.getContentLength());
		System.out.println( this.response.getContent().length());
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	@Test
	@Ignore // FIXME: do we want this to work?
	public void testUTF8() throws Exception {
//		final NullRequest request = new NullRequest();
//		request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
//		request.getHeaders().put("User-Agent", RestProperties.getInstance().getApiUserAgent());
//		final NullResponse response = new NullResponse();
//
//		final RestServlet servlet = new RestServlet();
//		servlet.setLogicInterface(new TestDBLogic());
//		final LogicInterface logic = servlet.getLogic();
//		final User user = new User();
//		user.setName("üöäßéèê");
//		logic.storeUser(user, false);
//		request.setPathInfo("/users");
//
//		servlet.doGet(request, response);
//		compareWithFile(response.getContent(), "UTF8TestResult.txt");
//		assertEquals(813, response.getContentLength()); // 813 vs 799
	}
	
	// TODO: duplicate code @see 
	private void compareWithFile(final String sw, final String filename) throws IOException {
		final StringBuilder sb = new StringBuilder(200);
		final File file = new File("src/test/resources/" + filename);
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s + "\n");
		}
		assertEquals(sb.toString(), sw);
	}
}