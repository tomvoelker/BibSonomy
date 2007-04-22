package org.bibsonomy.database;


import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Test;


/**
 * Performance test of iBATIS.
 * 
 * @author Christian Schenk
 */
public class PerfTest extends AbstractDatabaseManagerTest {

	@Test
	@SuppressWarnings( { "unchecked", "unused" })
	public void testPerf() {
		try {
			final BookmarkParam param = ParamUtils.getDefaultBookmarkParam();

			List<Bookmark> bookmarks;
			long all = 0;

			// make 20 runs of the same task, so we get a good average
			for (int i = 0; i < 20; i++) {
				// save the start time
				final long start = System.currentTimeMillis();
				for (int j = 0; j < 5; j++) {
					//bookmarks = this.db.getBookmark().getBookmarkByTagNames(param);
				}
				// save the time once the task is finished
				final long end = System.currentTimeMillis();

				// on the first run iBATIS is starting up and we don't want to
				// measure that
				if (i == 0) continue;

				all += (end - start);
			}

			System.out.println("Duration: " + (all / 19) + "ms");
		} catch (final RuntimeException ex) {
			if (ex.getCause() instanceof SQLException) {
				ex.printStackTrace();
				fail("SQLException");
			}
		}
	}
}