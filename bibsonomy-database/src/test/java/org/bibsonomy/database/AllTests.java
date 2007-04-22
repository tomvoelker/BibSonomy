package org.bibsonomy.database;

import org.bibsonomy.database.managers.BibTexDatabaseManagerTest;
import org.bibsonomy.database.managers.BookmarkDatabaseManagerTest;
import org.bibsonomy.database.managers.GeneralDatabaseManagerTest;
import org.bibsonomy.database.managers.TagDatabaseManagerTest;
import org.bibsonomy.database.params.ParamTest;
import org.bibsonomy.database.util.TransactionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All (important) testcases are executed here.
 * 
 * @author Christian Schenk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ParamTest.class, TransactionTest.class, TagDatabaseManagerTest.class, GeneralDatabaseManagerTest.class, BookmarkDatabaseManagerTest.class, BibTexDatabaseManagerTest.class })
public class AllTests {
}