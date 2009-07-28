package org.bibsonomy.database.managers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.testutil.JNDITestDatabaseBinder;
import org.bibsonomy.testutil.ParamUtils;
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
public abstract class AbstractDatabaseManagerTest {

	private static final Log log = LogFactory.getLog(AbstractDatabaseManagerTest.class);

	protected GeneralDatabaseManager generalDb;
	protected BookmarkDatabaseManager bookmarkDb;
	protected BibTexDatabaseManager bibTexDb;
	protected BibTexExtraDatabaseManager bibTexExtraDb;
	protected UserDatabaseManager userDb;
	protected TagDatabaseManager tagDb;
	protected TagRelationDatabaseManager tagRelDb;
	protected GroupDatabaseManager groupDb;
	protected AdminDatabaseManager adminDb;
	protected PermissionDatabaseManager permissionDb;
	protected StatisticsDatabaseManager statisticsDb;
	protected BasketDatabaseManager basketDb;

	protected GenericParam generalParam;
	protected BookmarkParam bookmarkParam;
	protected BibTexParam bibtexParam;
	protected UserParam userParam;
	protected TagParam tagParam;
	protected TagRelationParam tagRelationParam;
	protected GroupParam groupParam;
	protected StatisticsParam statisticsParam;

	private DBSessionFactory dbSessionFactory;
	protected DBSession dbSession;

	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		new TestDatabaseLoader().load();
	}

	/**
	 * Setup
	 */
	@Before
	public void setUp() {
		try {
			this.generalDb = GeneralDatabaseManager.getInstance();
			this.bookmarkDb = BookmarkDatabaseManager.getInstance();
			this.bibTexDb = BibTexDatabaseManager.getInstance();
			this.bibTexExtraDb = BibTexExtraDatabaseManager.getInstance();
			this.userDb = UserDatabaseManager.getInstance();
			this.tagDb = TagDatabaseManager.getInstance();
			this.tagRelDb = TagRelationDatabaseManager.getInstance();
			this.groupDb = GroupDatabaseManager.getInstance();
			this.adminDb = AdminDatabaseManager.getInstance();
			this.permissionDb = PermissionDatabaseManager.getInstance();
			this.statisticsDb = StatisticsDatabaseManager.getInstance();
			this.basketDb = BasketDatabaseManager.getInstance();

			// initialize parameter objects
			this.resetParameters();

			// bind datasource access via JNDI
			JNDITestDatabaseBinder.bind();

			this.dbSessionFactory = new IbatisDBSessionFactory();
			this.dbSession = this.dbSessionFactory.getDatabaseSession();
		} catch (final Throwable ex) {
			log.fatal("exception in testcase setUp", ex);
		}
	}

	/**
	 * Tear down
	 */
	@After
	public void tearDown() {
		this.generalDb = null;
		this.bookmarkDb = null;
		this.bibTexDb = null;
		this.bibTexExtraDb = null;
		this.tagDb = null;
		this.userDb = null;
		this.groupDb = null;
		this.adminDb = null;
		this.permissionDb = null;

		this.generalParam = null;
		this.bookmarkParam = null;
		this.bibtexParam = null;
		this.userParam = null;
		this.tagParam = null;
		this.groupParam = null;
		this.statisticsParam = null;

		JNDITestDatabaseBinder.unbind();
		// FIXME: hack ("DBSessionImpl not closed")		
		if( this.dbSession!=null )
			this.dbSession.close();
	}

	/**
	 * Resets the parameter objects, which can be useful inside one method of a
	 * testcase. On some occasions we need to do this, e.g. when more than one
	 * query is involved and the results from one query are stored in the
	 * parameter object so they can be used in the next query: in this case the
	 * parameter object is altered which can lead to side effects in the
	 * following queries.<br/>
	 * 
	 * Hint: This is done before running a testcase method, so you don't have to
	 * do this manually.
	 * 
	 * @see #setUp()
	 */
	protected void resetParameters() {
		this.generalParam = ParamUtils.getDefaultGeneralParam();
		this.bookmarkParam = ParamUtils.getDefaultBookmarkParam();
		this.bibtexParam = ParamUtils.getDefaultBibTexParam();
		this.userParam = ParamUtils.getDefaultUserParam();
		this.tagParam = ParamUtils.getDefaultTagParam();
		this.tagRelationParam = ParamUtils.getDefaultTagRelationParam();
		this.groupParam = ParamUtils.getDefaultGroupParam();
		this.statisticsParam = ParamUtils.getDefaultStatisticsParam();
	}

	protected DBSessionFactory getDbSessionFactory() {
		return this.dbSessionFactory;
	}
}