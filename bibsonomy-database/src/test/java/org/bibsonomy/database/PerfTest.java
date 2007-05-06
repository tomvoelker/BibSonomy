package org.bibsonomy.database;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Test;

/**
 * Performance test of iBATIS.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class PerfTest extends AbstractDatabaseManagerTest {

	private static final Logger log = Logger.getLogger(PerfTest.class);

	@Test
	@SuppressWarnings( { "unchecked", "unused" })
	public void testPerf() {
		int totalQueries = 0;
		try {
			final BookmarkParam param = ParamUtils.getDefaultBookmarkParam();

			List<Post<Bookmark>> bookmarks;
			long all = 0;

			// make 20 runs of the same task, so we get a good average
			for (int i = 0; i <= 20; i++) {
				// save the start time
				final long start = System.currentTimeMillis();
				for (int j = 0; j < 5; j++) {
					bookmarks = this.bookmarkDb.getBookmarkByTagNames(param, this.dbSession);
					totalQueries++;
				}
				// save the time once the task is finished
				final long end = System.currentTimeMillis();

				// on the first run iBATIS is starting up and we don't want to
				// measure that
				if (i == 0) continue;

				all += (end - start);
			}

			log.debug("Executed " + totalQueries + " queries of getBookmarkByTagNames in: " + all + " ms");
			log.debug("5 queries of getBookmarkByTagNames took: " + (all / 20) + " ms");
			log.debug("1 query   of getBookmarkByTagNames took: " + ((all / 20) / 5) + " ms");
			log.debug("Under this circumstances " + 1000 / ((all / 20) / 5) + " queries could be executed in one second");
		} catch (final RuntimeException ex) {
			if (ex.getCause() instanceof SQLException) {
				ex.printStackTrace();
				fail("SQLException");
			}
		}
	}
}