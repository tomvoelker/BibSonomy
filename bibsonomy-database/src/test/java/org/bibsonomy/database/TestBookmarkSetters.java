package org.bibsonomy.database;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

import junit.framework.TestCase;

public class TestBookmarkSetters extends TestCase {

	
	public void testsetter(){
		 
		/*
		 * create a postobject with given parameters
		 */
		
		
		BookmarkDatabaseManager db= BookmarkDatabaseManager.getInstance();
		
		final User user =new User();
		final Tag tagfirstTest = new Tag();
		final Tag tagsecondTest = new Tag();
		final Tag tagthirdTest = new Tag();
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
      
  		final Date date =new Date();
		/*
		 * TODO field extension must implemented
		 * TODO change_date is equal to date
		 */
        user.setName("grahl");	
        bookmark.setIntraHash("a18a7ee661402fb0651800fd5f871dba");
		bookmark.setDescription("Testbookmark");
		bookmark.setUrl("http://www.testbookmark.de/");
		post.setResource(bookmark);
		post.setDate(date);
		post.setUser(user);
		
		
        List <Tag> testtags=new LinkedList<Tag>();

        /*
         * set tagnames
         */
        
        tagfirstTest.setName("testtag1");
        tagsecondTest.setName("testtag2");
        tagthirdTest.setName("testtag3");
		
        testtags.add(tagfirstTest);
        testtags.add(tagsecondTest);
        testtags.add(tagthirdTest);
     
        
        
        
        /*
         * only for testcases
         */
        
		post.setTags(testtags);
 	    db.storePost(post.getUser().getName(),post,true);
		
	}
	
	
	
	
}
