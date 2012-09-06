package org.bibsonomy.webapp;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 * @author dzo
 * @version $Id$
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
