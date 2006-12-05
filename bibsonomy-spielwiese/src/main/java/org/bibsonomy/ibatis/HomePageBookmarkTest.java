package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.bookmark.HomePageBookmark;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;



/**
 * TESTCLASS
 * 
 * initialize HomePageforBookmark parameters 
 * 
 * @author mgr
 *
 */

public class HomePageBookmarkTest extends AbstractSqlMapTest {
	
	
	public HomePageBookmark getDefaultHomePageBookmark() {
		final HomePageBookmark bookVal = new HomePageBookmark();
		bookVal.setItemCount(20);
		bookVal.setStartBook(0);
		bookVal.setGroupType(ConstantID.GROUP_PUBLIC);
		return bookVal;
	}


	@SuppressWarnings("unchecked")
	public void testHomePageBookmarkTest() {
		try {
			final HomePageBookmark btn = this.getDefaultHomePageBookmark();

			final List<Bookmark> bookmarks=this.sqlMap.queryForList("getHomePageBookmark",btn);
			
			for (final Bookmark bookmark  : bookmarks) {
				System.out.println("ContentId   : " + bookmark.getContentId());
				System.out.println("Description : " + bookmark.getDescription());
				System.out.println("Extended    : " + bookmark.getExtended());
				System.out.println("Date        : " + bookmark.getDate());
				System.out.println("URL         : " + bookmark.getUrl());
				System.out.println("URLHash     : " + bookmark.getUrlHash());
				System.out.println("UserName    : " + bookmark.getUserName());
				for (final Tag tag : bookmark.getTags()) {
					System.out.print(tag.getName() + " ");
				}
				System.out.println("\n");
			}
	
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}	
	

	
}