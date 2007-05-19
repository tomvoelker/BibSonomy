package org.bibsonomy.database.managers;

import org.apache.log4j.Logger;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.After;
import org.junit.Before;

/**
 * This class provides a connection to the database and convenience methods to
 * print both bookmarks and BibTexs. Every class that implements tests for
 * methods which interact with the database should be derived from this class.
 * 
 * @author Christian Schenk
 * @author Jens Illig
 * @version $Id$
 */
public abstract class AbstractDatabaseManagerTest {
	private static final Logger log = Logger.getLogger(AbstractDatabaseManagerTest.class);

	protected GeneralDatabaseManager generalDb;
	protected BookmarkDatabaseManager bookmarkDb;
	protected BibTexDatabaseManager bibTexDb;
	protected TagDatabaseManager tagDb;
	protected UserDatabaseManager userDb;
	protected GroupDatabaseManager groupDb;

	protected GenericParam generalParam;
	protected BookmarkParam bookmarkParam;
	protected BibTexParam bibtexParam;
	protected UserParam userParam;
	protected TagParam tagParam;
	protected GroupParam groupParam;
	protected Transaction dbSession;

	@Before
	public void setUp() {
		try {
			this.generalDb = GeneralDatabaseManager.getInstance();
			this.bookmarkDb = BookmarkDatabaseManager.getInstance();
			this.bibTexDb = BibTexDatabaseManager.getInstance();
			this.tagDb = TagDatabaseManager.getInstance();
			this.userDb = UserDatabaseManager.getInstance();
			this.groupDb = GroupDatabaseManager.getInstance();
			this.resetParameters();

			// testcases shouldn't write into the db		
			dbSession = DatabaseUtils.getDatabaseSession();
			dbSession.beginTransaction();
		} catch (Throwable e) {	
			log.fatal("exception in testcase setUp",e);
		}
	}

	@After
	public void tearDown() {
		dbSession.endTransaction();
		dbSession.close();

		this.generalDb = null;
		this.bookmarkDb = null;
		this.bibTexDb = null;
		this.tagDb = null;
		this.userDb = null;
		this.groupDb = null;

		this.bookmarkParam = null;
		this.bibtexParam = null;
		this.tagParam = null;
		this.userParam = null;
		this.groupParam = null;
	}

	/**
	 * Resets the parameter objects, which can be useful inside one method of a
	 * testcase. On some occasions we need to do this, e.g. when more than one
	 * query is involved and the results from one query are stored in the
	 * parameter object so they can be used in the next query: in this case the
	 * parameter object is altered which can lead to side effects in the
	 * following queries.<br/>
	 * 
	 * This is done before running a testcase method.
	 */
	protected void resetParameters() {
		this.generalParam = ParamUtils.getDefaultGeneralParam();
		this.bookmarkParam = ParamUtils.getDefaultBookmarkParam();
		this.bibtexParam = ParamUtils.getDefaultBibTexParam();
		this.userParam = ParamUtils.getDefaultUserParam();
		this.tagParam = ParamUtils.getDefaultTagParam();
		this.groupParam = ParamUtils.getDefaultGroupParam();
	}
}