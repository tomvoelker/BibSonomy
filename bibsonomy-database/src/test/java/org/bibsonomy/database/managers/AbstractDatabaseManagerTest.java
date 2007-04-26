package org.bibsonomy.database.managers;

import java.io.IOException;

import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.After;
import org.junit.Before;

/**
 * This class provides a connection to the database and convenience methods to
 * print both bookmarks and BibTexs. Every class that implements tests for
 * methods which interact with the database should be derived from this class.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractDatabaseManagerTest {

	/** The database manager for general queries */
	protected GeneralDatabaseManager generalDb;
	/** The database manager for Bookmarks */
	protected BookmarkDatabaseManager bookmarkDb;
	/** The database manager for BibTexs */
	protected BibTexDatabaseManager bibTexDb;
	/** The database manager for Tags */
	protected TagDatabaseManager tagDb;
	/** The chain handler */
	protected GenericChainHandler chainHandler;
	/** The database manager for users */
	protected UserDatabaseManager userDb;
	/** The database manager for groups */
	protected GroupDatabaseManager groupDb;
	/** This param can be used for queries about bookmarks */
	protected BookmarkParam bookmarkParam;
	/** This param can be used for queries about BibTexs */
	protected BibTexParam bibtexParam;
	protected UserParam userParam;
	protected TagParam tagParam;
	protected GroupParam groupParam;

	@Before
	public void setUp() throws IOException {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.bookmarkDb = BookmarkDatabaseManager.getInstance();
		this.bibTexDb = BibTexDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.chainHandler = GenericChainHandler.getInstance();
		this.userDb = UserDatabaseManager.getInstance();
		this.groupDb = GroupDatabaseManager.getInstance();
		this.resetParameters();

		// testcases shouldn't write to the db
		this.generalDb.setReadonly();
		this.bookmarkDb.setReadonly();
		this.bibTexDb.setReadonly();
		this.tagDb.setReadonly();
		this.userDb.setReadonly();
		this.groupDb.setReadonly();
	}

	@After
	public void tearDown() {
		this.generalDb = null;
		this.bookmarkDb = null;
		this.bibTexDb = null;
		this.tagDb = null;
		this.userDb = null;
		this.groupDb = null;
		this.chainHandler = null;

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
		this.bookmarkParam = ParamUtils.getDefaultBookmarkParam();
		this.bibtexParam = ParamUtils.getDefaultBibTexParam();
		this.userParam = ParamUtils.getDefaultUserParam();
		this.tagParam = ParamUtils.getDefaultTagParam();
		this.groupParam = ParamUtils.getDefaultGroupParam();
	}
}