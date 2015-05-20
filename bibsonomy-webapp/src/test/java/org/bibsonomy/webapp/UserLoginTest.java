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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author dzo
 */
@Category(WebappTest.class)
public class UserLoginTest extends WebappTest {

	private static final String INTERNAL_COOKIE_NAME = "db_user";

	public UserLoginTest(final WebDriver webDriver) {
		super(webDriver);
	}
	
	@Test
	public void quickLoginInternal() {
		// open login page
		// TODO: use urlgenerator
		this.webDriver.get(BASE_URL + "login");

		// type username in field
		final WebElement usernamefield = this.webDriver.findElement(By.id("username"));
		usernamefield.clear();
		final String username = "testuser1";
		usernamefield.sendKeys(username);
		// type password
		final WebElement passwordField = this.webDriver.findElement(By.id("password"));
		passwordField.clear();
		passwordField.sendKeys("test123");
		// remember me button
		final WebElement rememberMe = this.webDriver.findElement(By.id("rememberMe1"));
		rememberMe.click();
		
		// click login
		final WebElement loginButton = this.webDriver.findElement(By.cssSelector("button.btn.btn-success"));
		loginButton.click();
		
		assertEquals(BASE_URL, this.webDriver.getCurrentUrl());
		
		// check if cookie set
		assertTrue(this.webDriver.manage().getCookieNamed(INTERNAL_COOKIE_NAME) != null);
	}
}
