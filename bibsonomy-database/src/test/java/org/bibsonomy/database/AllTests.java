package org.bibsonomy.database;

import org.bibsonomy.database.managers.BibTexDatabaseManagerTest;
import org.bibsonomy.database.managers.BookmarkDatabaseManagerTest;
import org.bibsonomy.database.managers.GeneralDatabaseManagerTest;
import org.bibsonomy.database.managers.GroupDatabaseManagerTest;
import org.bibsonomy.database.managers.RestDatabaseManagerTest;
import org.bibsonomy.database.managers.TagDatabaseManagerTest;
import org.bibsonomy.database.managers.UserDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.ChainElementTest;
import org.bibsonomy.database.params.ParamTest;
import org.bibsonomy.database.util.LogicInterfaceHelperTest;
import org.bibsonomy.database.util.TransactionTest;
import org.bibsonomy.testutil.DatabasePluginMock;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All (important) testcases are executed here.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
// @RunWith(Suite.class)
@Suite.SuiteClasses( { ParamTest.class, LogicInterfaceHelperTest.class, DatabasePluginMock.class, ChainElementTest.class, BookmarkDatabaseManagerTest.class, BibTexDatabaseManagerTest.class, TagDatabaseManagerTest.class, TransactionTest.class, UserDatabaseManagerTest.class, GroupDatabaseManagerTest.class, RestDatabaseManagerTest.class, GeneralDatabaseManagerTest.class })
public class AllTests {
}