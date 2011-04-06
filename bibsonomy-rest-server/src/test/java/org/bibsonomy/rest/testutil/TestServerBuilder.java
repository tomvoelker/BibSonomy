package org.bibsonomy.rest.testutil;

import java.io.IOException;

import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.database.TestDBLogicInterfaceFactory;
import org.junit.Ignore;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.resource.Resource;

/**
 * @author dzo
 * @version $Id$
 */
@Ignore
public class TestServerBuilder {
	
	private static final int DEFAULT_PORT = 8090;
	
	private int port = DEFAULT_PORT;
	private Class<?> logicInterfaceFactoryClass = TestDBLogicInterfaceFactory.class;

	/**
	 * @return builds a server
	 * @throws IOException
	 */
	public Server buildServer() throws IOException {
		final Server server = new Server(this.port);
		final String apiUrl = "http://localhost:" + this.port + "/api";
		
		final Context servletContext = new Context();
		servletContext.setContextPath("/api");			
		final Resource resource = Resource.newResource("API_URL");
		resource.setAssociate(apiUrl);
		servletContext.setBaseResource(resource);
		final ServletHolder restServlet = servletContext.addServlet(RestServlet.class, "/*");
		restServlet.setInitParameter(RestServlet.PARAM_LOGICFACTORY_CLASS, this.logicInterfaceFactoryClass.getName());			
		
		server.addHandler(servletContext);
		
		return server;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @param logicInterfaceFactoryClass the logicInterfaceFactoryClass to set
	 */
	public void setLogicInterfaceFactoryClass(Class<?> logicInterfaceFactoryClass) {
		this.logicInterfaceFactoryClass = logicInterfaceFactoryClass;
	}
	
	/**
	 * starts the rest servlet with default values
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final TestServerBuilder builder = new TestServerBuilder();
		
		final Server server = builder.buildServer();
		server.start();
	}
}
