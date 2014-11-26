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

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 * @author dzo
 */
@Ignore // TODO: reactivate it
public class UserLoginTest extends WebappTest {

	private static final String INTERNAL_COOKIE_NAME = "db_user";

	public UserLoginTest(final Class<WebDriver> webDriverClass) {
		super(webDriverClass);
	}
	
	@Test
	public void quickLoginInternal() {
		// open homepage
		this.selenium.open("/");

		// type username in field
		this.selenium.type("id=un", "testuser1");
		// click hack field for password
		this.selenium.click("id=pw_form_copy");
		// type password
		this.selenium.type("id=pw", "test123");
		// click login
		this.selenium.click("css=input[type=\"image\"]");
		this.selenium.waitForPageToLoad("3000");
	
		assertTrue(this.selenium.isTextPresent("logged in as"));
		// ensure quick login sets cookie
		assertTrue(this.selenium.isCookiePresent(INTERNAL_COOKIE_NAME));
	}
}
