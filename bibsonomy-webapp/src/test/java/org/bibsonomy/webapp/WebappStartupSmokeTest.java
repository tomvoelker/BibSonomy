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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.context.WebApplicationContext;

/**
 * Java-21-safe startup smoke for embedded webapp runtime.
 */
@Category(WebappTest.class)
public class WebappStartupSmokeTest extends AbstractDatabaseManagerTest {
	private static final String SERVER_PROPERTIES_FILE = "server.properties";
	private static final String WEBAPP_TEST_DATABASE_CONFIG_FILE = "webapp-test-database.properties";
	private static final String REMEMBER_ME_COOKIE_NAME = "db_user";
	private static final String TEST_USERNAME = "testuser1";
	private static final String TEST_PASSWORD = "test123";

	private static final int PORT = 31415;
	private static final String BASE_URL = "http://localhost:" + PORT + "/";
	private static Tomcat tomcat;
	private static Context webappContext;
	private static String originalUserHome;
	private static File temporaryUserHome;

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
		webappContext = context;
		final ClassLoader classLoader = MinimalisticController.class.getClassLoader();
		final WebappLoader loader = new WebappLoader(classLoader);
		loader.setDelegate(true);
		context.setLoader(loader);

		final ApplicationParameter parameter = new ApplicationParameter();
		parameter.setOverride(false);
		final String serverProps = WebappStartupSmokeTest.class.getClassLoader().getResource(SERVER_PROPERTIES_FILE).getFile();
		System.setProperty("config.location", serverProps);
		parameter.setValue(serverProps);
		parameter.setName("config.location");
		context.addApplicationParameter(parameter);

		originalUserHome = System.getProperty("user.home");
		temporaryUserHome = Files.createTempDirectory("bibsonomy-webapp-smoke-home").toFile();
		System.setProperty("user.home", temporaryUserHome.getAbsolutePath());

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
				if (originalUserHome != null) {
					System.setProperty("user.home", originalUserHome);
					originalUserHome = null;
				}
				if (temporaryUserHome != null) {
					temporaryUserHome.delete();
					temporaryUserHome = null;
				}
				tomcat = null;
				webappContext = null;
			}
		}
	}

	@Test
	public void apiUsersEndpointRequiresAuthentication() throws IOException {
		assertStatusCode(BASE_URL + "api/users", 401);
	}

	@Test
	public void internalLoginWorksWithDatabaseBackedTestUser() throws IOException {
		assertServerUsesTestDatabaseCredentials();
		final BasicCookieStore cookieStore = new BasicCookieStore();
		try (final CloseableHttpClient client = HttpClientBuilder.create()
			.disableRedirectHandling()
			.setDefaultCookieStore(cookieStore)
			.build()) {
			final HttpPost login = new HttpPost(BASE_URL + "login_internal");
			login.setEntity(new UrlEncodedFormEntity(loginParams(TEST_USERNAME, TEST_PASSWORD), StandardCharsets.UTF_8));
			try (final CloseableHttpResponse response = client.execute(login)) {
				final int statusCode = response.getStatusLine().getStatusCode();
				assertTrue("Expected redirect status after login but got " + statusCode, statusCode >= 300 && statusCode < 400);
				final Header location = response.getFirstHeader("Location");
				assertTrue(
					"Expected login redirect away from /login but got " + (location == null ? "<none>" : location.getValue()),
					location != null && !location.getValue().contains("/login"));
			}
		}
		assertTrue("Expected remember-me cookie '" + REMEMBER_ME_COOKIE_NAME + "' after login",
			cookieStore.getCookies().stream().anyMatch(cookie -> REMEMBER_ME_COOKIE_NAME.equals(cookie.getName())));
	}

	private static List<NameValuePair> loginParams(final String username, final String password) {
		return Arrays.asList(
			new BasicNameValuePair("username", username),
			new BasicNameValuePair("password", password),
			new BasicNameValuePair("rememberMe", "on"),
			new BasicNameValuePair("selTab", "0"));
	}

	private static void assertStatusCode(final String url, final int expectedStatusCode) throws IOException {
		try (final CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
				final CloseableHttpResponse response = client.execute(new HttpGet(url))) {
			final int statusCode = response.getStatusLine().getStatusCode();
			assertTrue("Expected status code " + expectedStatusCode + " for " + url + " but got " + statusCode,
				statusCode == expectedStatusCode);
		}
	}

	private static void assertServerUsesTestDatabaseCredentials() {
		assertTrue("Expected embedded webapp context to be initialized", webappContext != null);
		final Object rootContext = webappContext.getServletContext()
			.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		final Object servletContext = webappContext.getServletContext()
			.getAttribute(FrameworkServlet.SERVLET_CONTEXT_PREFIX + "bibsonomy");
		final Object selectedContext = rootContext instanceof WebApplicationContext ? rootContext : servletContext;
		assertTrue(
			"Expected a Spring WebApplicationContext (root=" + rootContext + ", servlet=" + servletContext + ")",
			selectedContext instanceof WebApplicationContext);
		final WebApplicationContext springContext = (WebApplicationContext) selectedContext;
		final Properties properties = springContext.getBean("properties", Properties.class);

		final String configuredUrl = properties.getProperty("database.main.url");
		final String configuredUsername = properties.getProperty("database.main.username");
		final String configuredPassword = properties.getProperty("database.main.password");
		final String expectedUsername = System.getenv().getOrDefault("BIB_TEST_DB_USER", "bibsonomy");
		final String expectedPassword = System.getenv().getOrDefault("BIB_TEST_DB_PASSWORD", "");

		assertTrue("Expected database.main.url to point to local test MariaDB but got " + configuredUrl,
			configuredUrl != null && configuredUrl.contains("127.0.0.1:3307/main_db"));
		assertEquals("Unexpected database.main.username", expectedUsername, configuredUsername);
		assertEquals("Unexpected database.main.password", expectedPassword, configuredPassword);
	}

}
