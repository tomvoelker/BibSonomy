/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.testutil.TestRequest;
import org.bibsonomy.rest.testutil.TestResponse;
import org.bibsonomy.rest.utils.HeaderUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class RestServletTest {
	public static final ApplicationContext TEST_CONTEXT = new ClassPathXmlApplicationContext("TestRestServerContext.xml");
	
	private RestServlet servlet;
	private TestRequest request;
	private TestResponse response;

	@Before
	public void setUp() {
		this.servlet = TEST_CONTEXT.getBean(RestServlet.class);
		
		this.request = new TestRequest();
		this.response = new TestResponse();
	}

	/**
	 * tests {@link RestServlet#validateAuthorization(String)}
	 * @throws Exception
	 */
	@Test
	public void testValidateAuthorization() throws Exception {
		this.request.putIntoHeaders(HeaderUtils.HEADER_AUTHORIZATION, "YXNkZjphc2Rm");
		try {
			this.servlet.validateAuthorization(this.request);
			fail("exception should have been thrown");
		} catch (final AuthenticationException e) {
		}
		this.request.putIntoHeaders(HeaderUtils.HEADER_AUTHORIZATION, "Basic ASDFASDF");
		try {
			this.servlet.validateAuthorization(this.request);
		} catch (final BadRequestOrResponseException e) {
		}
		
		this.request.putIntoHeaders(HeaderUtils.HEADER_AUTHORIZATION, "Basic YXNkZjphc2Rm");

		assertEquals("error decoding string", "asdf", this.servlet.validateAuthorization(this.request).getAuthenticatedUser().getName());
	}

	@Test
	public void testUnauthorized() throws Exception {
		this.servlet.doGet(this.request, this.response);
		this.compareWithFile(this.response.getContent(), "failAuth.xml");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	@Test
	public void testSimpleStuff() throws Exception {
		this.request.putIntoHeaders("Authorization", "Basic YXNkZjphc2Rm");
		this.request.setRequestURI("/");
		// try to get '/'
		this.servlet.doGet(this.request, this.response);
		this.compareWithFile(this.response.getContent(), "failAccess.xml");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	@Test
	public void testGetComplexStuff() throws Exception {
		this.request.putIntoHeaders("Authorization", "Basic YXNkZjphc2Rm");
		this.request.putIntoHeaders("User-Agent", RESTConfig.API_USER_AGENT);
		this.request.setRequestURI("/api/users");

		this.servlet.doGet(this.request, this.response);
		this.compareWithFile(this.response.getContent(), "exampleComplexResult1.xml");
		assertEquals(this.response.getContentLength(), this.response.getContent().length());
	}

	@Test
	@Ignore
	// FIXME: do we want this to work?
	public void testUTF8() throws Exception {
		//		final TestRequest request = new TestRequest();
		//		request.getHeaders().put("Authorization", "Basic YXNkZjphc2Rm");
		//		request.getHeaders().put("User-Agent", RestProperties.getInstance().getApiUserAgent());
		//		final TestResponse response = new TestResponse();
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
		br.close();
		try {
			XMLAssert.assertXMLEqual(sb.toString(), sw);
		} catch (final SAXException ex) {
			throw new RuntimeException(ex);
		}

	}
}