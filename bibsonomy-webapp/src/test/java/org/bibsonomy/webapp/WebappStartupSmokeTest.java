/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Java-21-safe startup smoke for embedded webapp runtime.
 */
@Category(WebappTest.class)
public class WebappStartupSmokeTest extends AbstractDatabaseManagerTest {
	private static final String SERVER_PROPERTIES_FILE = "server.properties";
	private static final String WEBAPP_TEST_DATABASE_CONFIG_FILE = "webapp-test-database.properties";

	private static final int PORT = 31415;
	private static final String BASE_URL = "http://localhost:" + PORT + "/";
	private static Tomcat tomcat;

	@Override
	protected String getDatabaseConfigFile() {
		return WEBAPP_TEST_DATABASE_CONFIG_FILE;
	}

	@BeforeClass
	public static void startServer() throws Exception {
		if (tomcat != null) {
			return;
		}

		final String webappDirLocation = "src/main/webapp/";
		tomcat = new Tomcat();
		tomcat.setPort(PORT);
		tomcat.setBaseDir("");

		final String externalForm = new File(webappDirLocation).getAbsolutePath();
		final Context context = tomcat.addWebapp("/", externalForm);
		final ClassLoader classLoader = MinimalisticController.class.getClassLoader();
		final WebappLoader loader = new WebappLoader(classLoader);
		loader.setDelegate(true);
		context.setLoader(loader);

		final ApplicationParameter parameter = new ApplicationParameter();
		parameter.setOverride(false);
		final String serverProps = WebappStartupSmokeTest.class.getClassLoader().getResource(SERVER_PROPERTIES_FILE).getFile();
		parameter.setValue(serverProps);
		parameter.setName("config.location");
		context.addApplicationParameter(parameter);

		tomcat.start();
	}

	@AfterClass
	public static void stopServer() {
		if (tomcat != null) {
			try {
				tomcat.stop();
				tomcat.destroy();
			} catch (final Exception e) {
				System.err.println("Warning: embedded Tomcat stop failed: " + e.getMessage());
			} finally {
				tomcat = null;
			}
		}
	}

	@Test
	public void apiUsersEndpointRequiresAuthOrResponds() throws IOException {
		assertStatus2xx3xxOr401(BASE_URL + "api/users");
	}

	@Test
	public void apiTagsEndpointRequiresAuthOrResponds() throws IOException {
		assertStatus2xx3xxOr401(BASE_URL + "api/tags");
	}

	private static void assertStatus2xx3xxOr401(final String url) throws IOException {
		try (final CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
				final CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			final int statusCode = response.getStatusLine().getStatusCode();
			assertTrue("Expected status code 2xx/3xx/401 for " + url + " but got " + statusCode,
				(statusCode >= 200 && statusCode < 400) || statusCode == 401);
		}
	}
}
