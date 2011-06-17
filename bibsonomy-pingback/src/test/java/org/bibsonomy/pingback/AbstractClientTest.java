package org.bibsonomy.pingback;

import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.testing.ServletTester;

/**
 * @author rja
 * @version $Id$
 */
public abstract class AbstractClientTest {

	private ServletTester tester;
	protected String baseUrl;
	

	@Before
	public void setUp() throws Exception {
		this.tester = new ServletTester();
		this.tester.setContextPath("/");
		this.tester.addServlet(TestServlet.class, "/*");
		this.baseUrl = tester.createSocketConnector(true);
		this.tester.start();
	}

	@After
	public void shutDown() throws Exception {
		this.tester.stop();
	}
	
}
