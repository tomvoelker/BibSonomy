/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.testutil.DatabasePluginMock;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This class provides a connection to the database. Every class that implements
 * test cases for methods dealing with the database should be derived from this
 * class.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 */
public abstract class AbstractDatabaseManagerTest extends AbstractDatabaseTest {
	
	/** the id used to retrieve the properties in the config file */
	public static final String DATABASE_ID = "main";
	/** the name of the file where username, password, and url are stored */
	public static final String DATABASE_CONFIG_FILE = "database-test.properties";
	/** Holds the database schema (script is at /src/main/resources) */
	private static final String SCHEMA_FILENAME = "database/bibsonomy-db-schema.sql";
	/** Holds the test data (script is found at /src/test/resources) */
	private static final String DATA_FILENAME = "database/insert-test-data.sql";
	
	/** holds the database helper class */
	public static final TestDatabaseLoader LOADER = new TestDatabaseLoader(SCHEMA_FILENAME, DATA_FILENAME);
	
	// TODO: move to a TestUtilClass
	protected static final int PUBLIC_GROUP_ID = GroupID.PUBLIC.getId();
	protected static final int PUBLIC_GROUP_ID_SPAM = GroupID.PUBLIC_SPAM.getId();
	protected static final int PRIVATE_GROUP_ID = GroupID.PRIVATE.getId();
	protected static final int FRIENDS_GROUP_ID = GroupID.FRIENDS.getId();
	protected static final int INVALID_GROUP_ID = GroupID.INVALID.getId();
	protected static final int TESTGROUP1_ID = 3;
	protected static final int TESTGROUP2_ID = 4;
	protected static final int TESTGROUP3_ID = 5;
	protected static final int TESTGROUP4_ID = 6;

	protected static DBSessionFactory dbSessionFactory;
	protected static DatabasePluginRegistry pluginRegistry;

	protected DatabasePluginMock pluginMock;
	protected DBSession dbSession;

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		dbSessionFactory = testDatabaseContext.getBean(DBSessionFactory.class);
		pluginRegistry = DatabasePluginRegistry.getInstance();
	}

	/**
	 * create new database session and reset the pluginRegistry
	 */
	@Before
	public final void setUp() {
		LOADER.load(this.getDatabaseConfigFile(), DATABASE_ID);
		this.dbSession = dbSessionFactory.getDatabaseSession();
		
		// load plugins (some tests are removing plugins from the plugin registry
		this.pluginMock = new DatabasePluginMock();
		pluginRegistry.reset();
		pluginRegistry.addPlugin(this.pluginMock);
	}

	/**
	 * @return the database config file to use
	 */
	protected String getDatabaseConfigFile() {
		return DATABASE_CONFIG_FILE;
	}

	/**
	 * Tear down
	 */
	@After
	public void tearDown() {
		// close session	
		if (this.dbSession != null) {
			this.dbSession.close();
		}
	}

	protected static DBSessionFactory getDbSessionFactory() {
		return dbSessionFactory;
	}
}