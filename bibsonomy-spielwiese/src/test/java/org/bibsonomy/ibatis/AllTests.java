package org.bibsonomy.ibatis;

import org.bibsonomy.ibatis.params.ParamTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All (important) testcases are executed here.
 * 
 * @author Christian Schenk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ParamTest.class, SimpleTest.class, BookmarkTest.class, BibTexTest.class })
public class AllTests {
}