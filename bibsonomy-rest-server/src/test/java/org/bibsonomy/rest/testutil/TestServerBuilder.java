/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.testutil;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Arrays;

import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.AuthenticationHandler;
import org.bibsonomy.rest.BasicAuthenticationHandler;
import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.database.TestDBLogicInterfaceFactory;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.junit.Ignore;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.resource.Resource;

/**
 * TODO: config of docs path, …
 * 
 * @author dzo
 */
@Ignore
public class TestServerBuilder {

	private static final int DEFAULT_PORT = 8090;
	private static final Class<? extends LogicInterfaceFactory> DEFAULT_INTERFACE_FACTORY_CLASS = TestDBLogicInterfaceFactory.class;

	/**
	 * starts the rest servlet with default values
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final TestServerBuilder builder = new TestServerBuilder();

		final Server server = builder.buildServer();
		server.start();
	}

	private final int port;
	private final LogicInterfaceFactory logicInterfaceFactory;

	/**
	 * 
	 * @param logicInterfaceFactory
	 * @param port optional, if port is null default value will be set
	 */
	public TestServerBuilder(final LogicInterfaceFactory logicInterfaceFactory, final Integer port) {
		if (present(port)) {
			this.port = port;
		} else {
			this.port = DEFAULT_PORT;
		}
		this.logicInterfaceFactory = logicInterfaceFactory;
	}

	/**
	 * port and logicInterfaceFactory will be set to default values
	 */
	public TestServerBuilder() {
		this.port = DEFAULT_PORT;
		LogicInterfaceFactory tmp = null;
		try {
			tmp = DEFAULT_INTERFACE_FACTORY_CLASS.newInstance();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		this.logicInterfaceFactory = tmp;
	}

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

		final RestServlet restServlet = new RestServlet();
		restServlet.setUrlRenderer(new UrlRenderer(apiUrl));
		restServlet.setRendererFactory(new RendererFactory(new UrlRenderer(apiUrl)));
		
		final BasicAuthenticationHandler basicAuthenticationHandler = new BasicAuthenticationHandler();
		basicAuthenticationHandler.setLogicFactory(logicInterfaceFactory);
		restServlet.setAuthenticationHandlers(Arrays.<AuthenticationHandler<?>>asList(basicAuthenticationHandler));

		servletContext.addServlet(RestServlet.class, "/*").setServlet(restServlet);

		server.addHandler(servletContext);
		return server;
	}
}
