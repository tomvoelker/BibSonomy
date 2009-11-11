package org.bibsonomy.database;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Performance tests for database methods.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class PerfTest extends AbstractDatabaseManagerTest {

	private static final Log log = LogFactory.getLog(PerfTest.class);

	/** This is used for the great switch statement in callMethod() */
	private enum Method {
		/** getBookmarkByTagNames */
		getBookmarkByTagNames,
		/** getBookmarkByTagNamesForUser */
		getBookmarkByTagNamesForUser,
		/** getBookmarkByConceptForUser */
		getBookmarkByConceptForUser,
		/** getBookmarkByUserFriends */
		getBookmarkByUserFriends,
		/** getBookmarkForHomepage */
		getBookmarkForHomepage,
		/** getBookmarkPopular */
		getBookmarkPopular,
		/** getBookmarkByHash */
		getBookmarkByHash,
		/** getBookmarkByHashCount */
		getBookmarkByHashCount,
		/** getBookmarkByHashForUser */
		getBookmarkByHashForUser,
		/** getBookmarkSearch */
		getBookmarkSearch,
		/** getBookmarkSearchCount */
		getBookmarkSearchCount,
		/** getBookmarkViewable */
		getBookmarkViewable,
		/** getBookmarkForGroup */
		getBookmarkForGroup,
		/** getBookmarkForGroupCount */
		getBookmarkForGroupCount,
		/** getBookmarkForGroupByTag */
		getBookmarkForGroupByTag,
		/** getBookmarkForUser */
		getBookmarkForUser,
		/** getBookmarkForUserCount */
		getBookmarkForUserCount
	}

	/**
	 * Executes all methods we'd like to evaluate.
	 */
	@Test
	public void testPerf() {
		for (final Method method : Method.values()) {
			this.runPerfTest(method);
		}
	}

	/**
	 * Runs one method several times and prints statistics to the debug logger.
	 */
	private void runPerfTest(final Method method) {
		int totalQueries = 0;
		try {
			final BookmarkParam param = ParamUtils.getDefaultBookmarkParam();
			final String methodname = method.name();
			long all = 0;

			// make 20 runs of the same task, so we get a good average
			for (int i = 0; i <= 20; i++) {
				// save the start time
				final long start = System.currentTimeMillis();

				// run method 5 times
				for (int j = 0; j < 5; j++) {
					/*
					 * HERE we call the database method
					 */
					this.callMethod(method, param);
					totalQueries++;
				}

				// save the time once the task is finished
				final long end = System.currentTimeMillis();

				// on the first run iBATIS is starting up and we don't want to
				// measure that. Even though this isn't correct after the first
				// run for the following methods, we leave it in and execute
				// one unnecessary call for every method.
				if (i == 0) continue;

				all += (end - start);
			}

			log.debug("Executed " + (totalQueries - 5) + " queries of " + methodname + " in: " + all + " ms");
			log.debug("5 queries of " + methodname + " took: " + (all / 20) + " ms");
			log.debug("1 query   of " + methodname + " took: " + ((all / 20) / 5) + " ms");
			log.debug("Under this circumstances " + 1000 / ((all / 20) / 5) + " queries could be executed in one second");
		} catch (final Throwable ex) {
			// ex.printStackTrace();
			fail("Exception: " + ex.getMessage());
		}
	}

	/**
	 * Calls the specified method.
	 */
	private void callMethod(final Method method, final BookmarkParam param) {
		switch (method) {
		case getBookmarkByTagNames:
			this.bookmarkDb.getPostsByTagNames(param.getGroupId(), param.getTagIndex(), param.getOrder(), param.getLimit(), param.getOffset(), this.dbSession);
			break;
		case getBookmarkByTagNamesForUser:
			this.bookmarkDb.getPostsByTagNamesForUser(param.getRequestedUserName(), param.getTagIndex(), param.getGroupId(), param.getGroups(), param.getLimit(), param.getOffset(), param.getFilter(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkByConceptForUser:
			this.bookmarkDb.getPostsByConceptForUser(param.getUserName(), param.getRequestedUserName(), param.getGroups(), param.getTagIndex(), param.isCaseSensitiveTagNames(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkByUserFriends:
			this.bookmarkDb.getPostsByUserFriends(param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkForHomepage:
			this.bookmarkDb.getPostsForHomepage(param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkPopular:
			this.bookmarkDb.getPostsPopular(param.getDays(), param.getLimit(), param.getOffset(), HashID.getSimHash(param.getSimHash()), this.dbSession);
			break;
		case getBookmarkByHash:
			this.bookmarkDb.getPostsByHash(param.getHash(), HashID.getSimHash(param.getSimHash()), param.getGroupId(), param.getLimit(), param.getOffset(), this.dbSession);
			break;
		case getBookmarkByHashCount:
			this.bookmarkDb.getPostsByHashCount(param.getHash(), HashID.getSimHash(param.getSimHash()), this.dbSession);
			break;
		case getBookmarkByHashForUser:
			this.bookmarkDb.getPostsByHashForUser(param.getUserName(), param.getHash(), param.getRequestedUserName(), new ArrayList<Integer>(), HashID.INTRA_HASH, this.dbSession);
			break;
		case getBookmarkSearch:
			this.bookmarkDb.getPostsSearch(param.getGroupId(), param.getSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), this.dbSession);
			break;
		case getBookmarkSearchCount:
			this.bookmarkDb.getPostsSearchCount(param.getGroupId(), param.getSearch(), param.getRequestedUserName(), this.dbSession);
			break;
		case getBookmarkViewable:
			this.bookmarkDb.getPostsViewable(param.getRequestedGroupName(), param.getUserName(), param.getGroupId(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkForGroup:
			this.bookmarkDb.getPostsForGroup(param.getGroupId(), param.getGroups(), param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkForGroupCount:
			this.bookmarkDb.getPostsForGroupCount(param.getRequestedUserName(), param.getUserName(), param.getGroupId(), param.getGroups(), this.dbSession);
			break;
		case getBookmarkForGroupByTag:
			this.bookmarkDb.getPostsForGroupByTag(param.getGroupId(), param.getGroups(), param.getUserName(), param.getTagIndex(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);	
			break;
		case getBookmarkForUser:
			this.bookmarkDb.getPostsForUser(param.getUserName(), param.getRequestedUserName(), HashID.getSimHash(param.getSimHash()), param.getGroupId(), param.getGroups(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), this.dbSession);
			break;
		case getBookmarkForUserCount:
			this.bookmarkDb.getPostsForUserCount(param.getRequestedUserName(), param.getUserName(), param.getGroupId(), param.getGroups(), this.dbSession);
			break;
		default:
			throw new RuntimeException("The method " + method.name() + " can't be found in the switch");
		}
	}
}