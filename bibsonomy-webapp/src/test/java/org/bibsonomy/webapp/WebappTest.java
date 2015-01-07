/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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

import java.util.Arrays;
import java.util.Collection;

import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.thoughtworks.selenium.Selenium;

/**
 * TODO: setup a selenium grid?
 * 
 * abstract webapp test (starts a tomcat server and inits 
 * 
 * @author dzo
 */
@RunWith(Parameterized.class)
public abstract class WebappTest extends AbstractDatabaseManagerTest {

	/**
	 * TODO: add more drivers? else remove
	 * 
	 * run all tests with these drivers
	 * @return the web drivers to use
	 */
	@Parameters
	public static Collection<Object[]> data() {
	    return Arrays.asList(new Object[][]{
	    	{ HtmlUnitDriver.class }
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
		// JNDIBinder.bind(); TODO: replaced in other branch
		if (tomcat == null) {
			tomcat = new Tomcat();
			
			tomcat.setPort(PORT);
			tomcat.setBaseDir("");
			final String externalForm = WebappTest.class.getClassLoader().getResource("").toExternalForm();
			final Context mainContext = tomcat.addWebapp("", externalForm.substring("file:".length()));
			final ClassLoader classLoader = MinimalisticController.class.getClassLoader();
			final WebappLoader loader = new WebappLoader(classLoader);
			loader.setDelegate(true);
			mainContext.setLoader(loader);
			tomcat.start();
			
			// load home page to compile jspx files
			final DefaultHttpClient client = new DefaultHttpClient();
			final HttpGet get = new HttpGet(BASE_URL);
			client.execute(get);
		}
	}
	
	@AfterClass
	public static final void unbindJNDIContext() {
		// JNDIBinder.unbind();
	}
	
	private final Class<WebDriver> webDriverClass;
	protected Selenium selenium;
	
	/**
	 * @param webDriver	webDriver tests
	 */
	public WebappTest(final Class<WebDriver> webDriver) {
		this.webDriverClass = webDriver;
	}
	
	/**
	 * init a new webdriver based on the parameter
	 * and a selenium instance
	 * 
	 * @throws Exception
	 */
	@Before
	public void setupSelenium() throws Exception {
		final WebDriver driver = this.webDriverClass.newInstance();		
		if (driver instanceof HtmlUnitDriver) {
			final HtmlUnitDriver htmlUnitDriver = (HtmlUnitDriver) driver;
			htmlUnitDriver.setJavascriptEnabled(true);
		}
	    this.selenium = new WebDriverBackedSelenium(driver, BASE_URL);
	}
	
	/**
	 * close selenium
	 */
	@After
	public void shutdownSelenium() {
		this.selenium.close();
	}
}
