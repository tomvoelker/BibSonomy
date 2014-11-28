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

	public UserLoginTest(final Class<WebDriver> webDriverClass) {
		super(webDriverClass);
	}
	
	@Test
	public void quickLoginInternal() {
		// open homepage
		this.driver.get(BASE_URL);

		// type username in field
		final WebElement usernamefield = this.driver.findElement(By.id("un"));
		usernamefield.sendKeys("testuser1");
		// click hack field for password
		final WebElement copy = this.driver.findElement(By.id("pw_form_copy"));
		copy.click();
		// type password
		final WebElement passwordField = this.driver.findElement(By.id("pw"));
		passwordField.sendKeys("test123");
		// click login
		final WebElement loginButton = this.driver.findElement(By.className("jsLoginButtonNonPermanent"));
		loginButton.click();
		
		assertTrue(this.driver.findElement(By.id("navigation2")).getText().contains("logged in as"));
		
		assertTrue(this.driver.manage().getCookieNamed(INTERNAL_COOKIE_NAME) != null);
	}
}
