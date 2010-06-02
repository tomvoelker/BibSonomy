package org.bibsonomy.database.managers;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.plugin.DatabasePlugin;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.systemstags.SystemTagFactory;
import org.bibsonomy.database.util.DatabaseManagerInitializer;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.testutil.DatabasePluginMock;
import org.bibsonomy.testutil.JNDITestDatabaseBinder;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.After;
import org.junit.AfterClass;
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
public abstract class AbstractDatabaseManagerTest {	
	// TODO: move to a TestUtilClass
	protected static final int PUBLIC_GROUP_ID = GroupID.PUBLIC.getId();
	protected static final int PRIVATE_GROUP_ID = GroupID.PRIVATE.getId();
	protected static final int FRIENDS_GROUP_ID = GroupID.FRIENDS.getId();
	protected static final int INVALID_GROUP_ID = GroupID.INVALID.getId();
	protected static final int TESTGROUP1_ID = 3;
	protected static final int TESTGROUP2_ID = 4;
	
	private static DBSessionFactory dbSessionFactory;
	protected static DatabasePluginRegistry pluginRegistry;
	protected static DatabaseManagerInitializer dbManagerInitializer;
	
	protected DatabasePluginMock pluginMock;
	protected DBSession dbSession;
	
	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		
		TestDatabaseLoader.getInstance().load();
		
		// set searchmode to lucene
		System.setProperty("searchMode", "lucene");
		
		dbSessionFactory = new IbatisDBSessionFactory();
		
		pluginRegistry = DatabasePluginRegistry.getInstance();
		
		// init managers
		final SystemTagFactory systemTagFactory = new SystemTagFactory();
		systemTagFactory.setSessionFactory(dbSessionFactory);
		
		dbManagerInitializer = new DatabaseManagerInitializer();
		dbManagerInitializer.setSystemTagFactory(systemTagFactory);
	}
	
	/**
	 * unbinds jndi
	 */
	@AfterClass
	public static void unbind() {
		JNDITestDatabaseBinder.unbind();
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

	protected DBSessionFactory getDbSessionFactory() {
		return dbSessionFactory;
	}
}