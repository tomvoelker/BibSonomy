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
