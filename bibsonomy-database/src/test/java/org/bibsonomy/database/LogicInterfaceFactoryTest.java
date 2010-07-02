package org.bibsonomy.database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.util.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceFactoryTest extends AbstractDatabaseManagerTest {

	private static DBLogicUserInterfaceFactory userFactory;
	private static DBLogicApiInterfaceFactory apiFactory;

	/**
	 * Initializes the factories
	 */
	@BeforeClass
	public static void initUserAndApiFactory() {
		userFactory = new DBLogicUserInterfaceFactory();
		userFactory.setDbSessionFactory(getDbSessionFactory());
		apiFactory = new DBLogicApiInterfaceFactory();
		apiFactory.setDbSessionFactory(getDbSessionFactory());
	}

	/**
	 * tests getLogicAccess from DBLogicUserInterfaceFactory
	 */
	@Test
	public void getLogicAccessUser() {
		for (final String username : new String[] { "testuser1", "testuser2", "testuser3" }) {
			assertNotNull(userFactory.getLogicAccess(username, StringUtils.getMD5Hash("test123")));
			this.assertNoLogin(userFactory, username);
		}
	}

	/**
	 * tests getLogicAccess from DBLogicApiInterfaceFactory
	 */
	@Test
	public void getLogicAccessApi() {
		for (final String[] credentials : new String[][] { { "testuser1", "11111111111111111111111111111111" }, { "testuser2", "22222222222222222222222222222222" }, { "testuser3", "33333333333333333333333333333333" } }) {
			assertNotNull(apiFactory.getLogicAccess(credentials[0], credentials[1]));
			this.assertNoLogin(apiFactory, credentials[0]);
		}

		// users with no API key may not log in
		this.assertNoLogin(apiFactory, "testspammer");
	}

	private void assertNoLogin(final LogicInterfaceFactory factory, final String username) {
		for (final String password : new String[] { "", " ", null, "invalid-password" }) {
			try {
				factory.getLogicAccess(username, password);
				fail("Should throw AccessDeniedException");
			} catch (final AccessDeniedException ignore) {
			}
		}
	}
}