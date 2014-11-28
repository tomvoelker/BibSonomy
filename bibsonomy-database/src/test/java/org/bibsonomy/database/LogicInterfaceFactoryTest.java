/**
 * BibSonomy-Database - Database for BibSonomy.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christian Schenk
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
			assertNotNull(userFactory.getLogicAccess(username, "b6c83e7916218ae7b77d0b2bf795d06f"));
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