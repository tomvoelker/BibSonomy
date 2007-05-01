package org.bibsonomy.database;

import org.bibsonomy.database.managers.BibTexDatabaseManagerTest;
import org.bibsonomy.database.managers.BookmarkDatabaseManagerTest;
import org.bibsonomy.database.managers.GroupDatabaseManagerTest;
import org.bibsonomy.database.managers.RestDatabaseManagerTest;
import org.bibsonomy.database.managers.TagDatabaseManagerTest;
import org.bibsonomy.database.managers.UserDatabaseManagerTest;
import org.bibsonomy.database.params.ParamTest;
import org.bibsonomy.database.util.TransactionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All (important) testcases are executed here.
 * 
 * @author Christian Schenk
 * @author mgr
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ParamTest.class, BookmarkDatabaseManagerTest.class, BibTexDatabaseManagerTest.class, TagDatabaseManagerTest.class, TransactionTest.class, UserDatabaseManagerTest.class, GroupDatabaseManagerTest.class, RestDatabaseManagerTest.class })
public class AllTests {
}