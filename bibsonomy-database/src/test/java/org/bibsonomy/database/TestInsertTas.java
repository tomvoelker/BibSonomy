package org.bibsonomy.database;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.managers.DatabaseManager;
import org.bibsonomy.database.newImpl.general.TagDBManager;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

import junit.framework.TestCase;

public class TestInsertTas extends TestCase{

	
	
	public void insertMyTAS(){
		
        DatabaseManager db =new DatabaseManager();
		
		final Tag tagfirstTest = new Tag();
		final Tag tagsecondTest = new Tag();
		final Tag tagthirdTest = new Tag();
		
		final BookmarkParam bookmarkParam=new BookmarkParam();
		final Bookmark bookmark = new Bookmark();
		final Date date =new Date();
		
		
		bookmarkParam.setResource(bookmark);
		List <Tag> testtags=new LinkedList<Tag>();
		bookmarkParam.setNewTasId(10);
		
		
		tagfirstTest.setName("testtag1");
        tagsecondTest.setName("testtag2");
        tagthirdTest.setName("testtag3");
		
        testtags.add(tagfirstTest);
        testtags.add(tagsecondTest);
        testtags.add(tagthirdTest);
		
		bookmarkParam.setTags(testtags);
		bookmarkParam.setContendIDbyBookmark(0000);
		bookmarkParam.setUserName("grahl");
		bookmarkParam.setDate(date);
		
		System.out.println("tags" + bookmarkParam.getTags());
		System.out.println("content_id" + bookmarkParam.getContendIDbyBookmark());
		System.out.println("username" + bookmarkParam.getUserName());
		System.out.println("date" + bookmarkParam.getDate());
		System.out.println("tasId" +bookmarkParam.getNewTasId());
		System.out.println("");
		System.out.println("");
		
		
		//db.tagDatabaseManager.insertTas(bookmarkParam);
		
		
		
	}
	
	
}
