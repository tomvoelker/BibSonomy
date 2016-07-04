/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.catalina.Context;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * TODO: setup a selenium grid?
 * 
 * abstract webapp test (starts a tomcat server and inits the selenium web
 * driver)
 * 
 * @author dzo
 */
@RunWith(Parameterized.class)
public abstract class WebappTest extends AbstractDatabaseManagerTest {
	private static final String SERVER_PROPERTIES_FILE = "server.properties";
	private static final String WEBAPP_TEST_DATABASE_CONFIG_FILE = "webapp-test-database.properties";

	/**
	 * TODO: add more drivers? else remove
	 * 
	 * run all tests with these drivers
	 * @return the web drivers to use
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
			{ new HtmlUnitDriver(BrowserVersion.FIREFOX_24) }
		});
	}
	
	private static final int PORT = 31415;
	protected static final String BASE_URL = "http://localhost:" + PORT + "/";
	private static Tomcat tomcat;
	
	/**
	 * start the server
	 * @throws Exception
	 */
	@BeforeClass
	public static final void startServer() throws Exception {
		if (tomcat == null) {
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
			
			final String serverProps = WebappTest.class.getClassLoader().getResource(SERVER_PROPERTIES_FILE).getFile();
			parameter.setValue(serverProps);
			parameter.setName("config.location");
			context.addApplicationParameter(parameter);
			
			tomcat.start();
			
			// load home page to compile jspx files
			final HttpClient client = HttpClientBuilder.create().build();
			final HttpGet get = new HttpGet(BASE_URL);
			client.execute(get);
			
		}
	}
	
	protected final WebDriver webDriver;
	
	/**
	 * @param webDriver	webDriver tests
	 */
	public WebappTest(final WebDriver webDriver) {
		this.webDriver = webDriver;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.AbstractDatabaseManagerTest#getDatabaseConfigFile()
	 */
	@Override
	protected String getDatabaseConfigFile() {
		return WEBAPP_TEST_DATABASE_CONFIG_FILE;
	}
	
	/**
	 * init a new webdriver based on the parameter
	 * and a selenium instance
	 * 
	 * @throws Exception
	 */
	@Before
	public void setupSelenium() throws Exception {
		final WebDriver driver = this.webDriver;
		if (driver instanceof HtmlUnitDriver) {
			final HtmlUnitDriver htmlUnitDriver = (HtmlUnitDriver) driver;
			htmlUnitDriver.setJavascriptEnabled(true);
		}
	}
	
	/**
	 * close selenium
	 */
	@After
	public void shutdownSelenium() {
		this.webDriver.quit();
	}
}
