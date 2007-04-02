package org.bibsonomy.database;

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
@Suite.SuiteClasses( { ParamTest.class, TransactionTest.class, TagTest.class, GeneralTest.class, BookmarkTest.class, BibTexTest.class })
public class AllTests {
}