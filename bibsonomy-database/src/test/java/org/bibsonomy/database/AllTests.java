package org.bibsonomy.database;

import org.bibsonomy.database.params.ParamTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All (important) testcases are executed here.
 * 
 * @author Christian Schenk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ParamTest.class, TagTest.class, GeneralTest.class, BookmarkTest.class, BibTexTest.class })
public class AllTests {
}