package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.ByTagNames;
import org.bibsonomy.model.Bookmark;

public class PerfTest extends AbstractSqlMapTest {

	@SuppressWarnings({"unchecked", "unused"})
	public void testPerf() {
		try {
			final ByTagNames btn = (new JoinTest()).getDefaultByTagNames();

			List<Bookmark> bookmarks;
			long all = 0;

			for (int i = 0; i < 20; i++) {
				final long start = System.currentTimeMillis();
				for (int j = 0; j < 5; j++) {
					bookmarks = this.sqlMap.queryForList("getBookmarkByTagNames", btn);
				}
				final long end = System.currentTimeMillis();

				// beim ersten Durchlauf fährt iBATIS hoch
				if (i == 0) continue;

				all += (end - start);
			}

			System.out.println("Duration: " + (all / 19) + "ms");
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}