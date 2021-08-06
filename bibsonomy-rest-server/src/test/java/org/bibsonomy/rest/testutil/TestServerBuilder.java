/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import org.bibsonomy.rest.BasicAuthenticationHandler;
import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.database.TestDBLogicInterfaceFactory;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Ignore;

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

		final Resource resource = Resource.newResource("API_URL");
		resource.setAssociate(apiUrl);
		final ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath("/api");
		handler.setBaseResource(resource);

		final RestServlet restServlet = new RestServlet();
		restServlet.setUrlRenderer(new UrlRenderer(apiUrl));
		restServlet.setRendererFactory(new RendererFactory(new UrlRenderer(apiUrl)));

		final BasicAuthenticationHandler basicAuthenticationHandler = new BasicAuthenticationHandler();
		basicAuthenticationHandler.setLogicFactory(this.logicInterfaceFactory);
		restServlet.setAuthenticationHandlers(Arrays.asList(basicAuthenticationHandler));

		handler.addServlet(new ServletHolder(restServlet), "/*");

		server.setHandler(handler);
		return server;
	}
}
