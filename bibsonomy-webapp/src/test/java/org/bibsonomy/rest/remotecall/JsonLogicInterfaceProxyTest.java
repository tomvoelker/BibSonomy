package org.bibsonomy.rest.remotecall;

import org.bibsonomy.rest.renderer.RenderingFormat;
import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * TODO: parameterize for {@link LogicInterfaceProxyTest}
 * and remove this test class
 * 
 * @author jensi
 */
public class JsonLogicInterfaceProxyTest extends LogicInterfaceProxyTest {

	/**
	 * configures the server and the webapp and starts the server
	 */
	@BeforeClass
	public static void initServer() {
		initServer(RenderingFormat.JSON);
	}


	/**
	 * stops the servlet container after all tests have been run
	 */
	@AfterClass
	public static void shutdown() {
		LogicInterfaceProxyTest.shutdown();
	}
}
