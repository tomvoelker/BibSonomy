package org.bibsonomy.database;

import java.util.Date;
import junit.framework.TestCase;
import org.bibsonomy.database.managers.DatabaseManager;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

public class TestInsertBookmark extends TestCase{
	
	
	
	public  BookmarkParam getBookmarkDefault(){
		
		
		BookmarkParam bookmark= new BookmarkParam();
		final Tag tagfirstTest = new Tag();
		final Tag tagsecondTest = new Tag();
		final Tag tagthirdTest = new Tag();
		final Bookmark book=new Bookmark();
		
		final Date date =new Date();
		
       // book.setContentId(657493);
        book.setDescription("TestDescription");
        /*
         * TODO repair bookmarkExtended, return null
         */
        book.setExtended("bookmark extension");
        bookmark.setUserName("grahl");
        
        /*TODO Extension=Description Description=Title*/
    	bookmark.setHash("fd6a16aaeef484e9ebb8b9a62f3a77c");
        bookmark.setGroupId(3);
        bookmark.setDate(date);
        bookmark.setResource(book);
        bookmark.setGroupId(4);
        
        return bookmark;
	}
	
	public void testInsertBookmark() {
		DatabaseManager db = new DatabaseManager();
		final BookmarkParam param = this.getBookmarkDefault();
		db.bookmarkDatabaseManager.insert("insertBookmark", param);
		
	}

}
