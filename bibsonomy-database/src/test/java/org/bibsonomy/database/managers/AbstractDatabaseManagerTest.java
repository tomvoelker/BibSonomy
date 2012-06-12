package org.bibsonomy.database.managers;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.plugin.DatabasePlugin;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
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
 * @version $Id$
 */
public abstract class AbstractDatabaseManagerTest extends AbstractDatabaseTest {	
	// TODO: move to a TestUtilClass
	protected static final int PUBLIC_GROUP_ID = GroupID.PUBLIC.getId();
	protected static final int PRIVATE_GROUP_ID = GroupID.PRIVATE.getId();
	protected static final int FRIENDS_GROUP_ID = GroupID.FRIENDS.getId();
	protected static final int INVALID_GROUP_ID = GroupID.INVALID.getId();
	protected static final int TESTGROUP1_ID = 3;
	protected static final int TESTGROUP2_ID = 4;
	protected static final int TESTGROUP3_ID = 5;

	protected static DBSessionFactory dbSessionFactory;
	protected static DatabasePluginRegistry pluginRegistry;

	protected DatabasePluginMock pluginMock;
	protected DBSession dbSession;

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		TestDatabaseLoader.getInstance().load();

		dbSessionFactory = new IbatisDBSessionFactory();

		pluginRegistry = DatabasePluginRegistry.getInstance();
	}

	/**
	 * create new database session and reset the pluginRegistry
	 */
	@Before
	public final void setUp() {		
		this.dbSession = dbSessionFactory.getDatabaseSession();

		// load plugins (some tests are removing plugins from the plugin registry
		this.pluginMock = new DatabasePluginMock();
		pluginRegistry.clearPlugins();

		pluginRegistry.add(pluginMock);
		for (final DatabasePlugin plugin : DatabasePluginRegistry.getDefaultPlugins()) {
			pluginRegistry.add(plugin);
		}
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