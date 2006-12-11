package org.bibsonomy.ibatis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All (important) testcases are executed here. 
 *
 * @author Christian Schenk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ByTagNamesTest.class, PopularTest.class, HomePageTest.class, DownloadTest.class,
	ByUserFriendsTest.class, ByHashTest.class })
public class AllTests {
}