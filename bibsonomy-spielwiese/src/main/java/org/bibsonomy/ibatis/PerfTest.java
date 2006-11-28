package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.generic.ByTagNames;
import org.bibsonomy.model.Bookmark;

/**
 * Performance test of iBATIS.
 *
 * @author Christian Schenk
 */
public class PerfTest extends AbstractSqlMapTest {

	@SuppressWarnings({"unchecked", "unused"})
	public void testPerf() {
		try {
			final ByTagNames btn = (new JoinTest()).getDefaultBookmarkByTagNames();

			List<Bookmark> bookmarks;
			long all = 0;

			// make 20 runs of the same task, so we get a good average
			for (int i = 0; i < 20; i++) {
				// save the start time
				final long start = System.currentTimeMillis();
				for (int j = 0; j < 5; j++) {
					bookmarks = this.sqlMap.queryForList("getBookmarkByTagNames", btn);
				}
				// save the time once the task is finished
				final long end = System.currentTimeMillis();

				// on the first run iBATIS is starting up and we don't want to measure that
				if (i == 0) continue;

				all += (end - start);
			}

			System.out.println("Duration: " + (all / 19) + "ms");
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}