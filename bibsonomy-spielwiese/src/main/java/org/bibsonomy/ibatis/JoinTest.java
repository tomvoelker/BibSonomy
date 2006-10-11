package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ContentType;
import org.bibsonomy.ibatis.enums.GroupType;
import org.bibsonomy.ibatis.params.ByTagNames;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

public class JoinTest extends AbstractSqlMapTest {	

	@SuppressWarnings("unchecked")
	public void testGetBookmarkByTagNames() {
		try {
			final ByTagNames btn = new ByTagNames();
			btn.setContentType(ContentType.BOOKMARK);
			btn.setGroupType(GroupType.PUBLIC);
			btn.setLimit(5);
			btn.setOffset(0);
			//btn.setTags(new String[] {"Buch"});
			btn.setTags(new String[] {"Info"});
			//btn.setTags(new String[] {"Buch", "Info"});

			final List<Bookmark> bookmarks = this.sqlMap.queryForList("getBookmarkByTagNames", btn);
			for (final Bookmark bookmark : bookmarks) {
				System.out.println("ContentId   : " + bookmark.getContentId());
				System.out.println("Description : " + bookmark.getDescription());
				System.out.println("Extended    : " + bookmark.getExtended());
				System.out.println("Date        : " + bookmark.getDate());
				System.out.println("URL         : " + bookmark.getUrl());
				System.out.println("URLHash     : " + bookmark.getUrlHash());
				System.out.println("UserName    : " + bookmark.getUserName());
				System.out.print("Tags        : ");
				for (final Tag tag : bookmark.getTags()) {
					System.out.print(tag.getName() + " ");
				}
				System.out.println("\n");
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
}