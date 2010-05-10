package org.bibsonomy.database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceFactoryTest extends AbstractDatabaseManagerTest {

	private DBLogicUserInterfaceFactory userFactory;
	private DBLogicApiInterfaceFactory apiFactory;

	/**
	 * Initializes the factories
	 */
	@Before
	public void setup() {
		super.setUp();
		this.userFactory = new DBLogicUserInterfaceFactory();
		this.userFactory.setDbSessionFactory(this.getDbSessionFactory());
		this.apiFactory = new DBLogicApiInterfaceFactory();
		this.apiFactory.setDbSessionFactory(this.getDbSessionFactory());
	}

	/**
	 * tests getLogicAccess from DBLogicUserInterfaceFactory
	 */
	@Test
	public void getLogicAccessUser() {
		for (final String username : new String[] { "testuser1", "testuser2", "testuser3" }) {
			assertNotNull(this.userFactory.getLogicAccess(username, "test123"));
			this.assertNoLogin(this.userFactory, username);
		}
	}

	/**
	 * tests getLogicAccess from DBLogicApiInterfaceFactory
	 */
	@Test
	public void getLogicAccessApi() {
		for (final String[] credentials : new String[][] { { "testuser1", "11111111111111111111111111111111" }, { "testuser2", "22222222222222222222222222222222" }, { "testuser3", "33333333333333333333333333333333" } }) {
			assertNotNull(this.apiFactory.getLogicAccess(credentials[0], credentials[1]));
			this.assertNoLogin(this.apiFactory, credentials[0]);
		}

		// users with no API key may not log in
		this.assertNoLogin(this.apiFactory, "testspammer");
	}

	private void assertNoLogin(final LogicInterfaceFactory factory, final String username) {
		for (final String password : new String[] { "", " ", null, "invalid-password" }) {
			try {
				factory.getLogicAccess(username, password);
				fail("Should throw ValidationException");
			} catch (AccessDeniedException ignore) {
			}
		}
	}
}