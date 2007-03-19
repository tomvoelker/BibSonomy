package org.bibsonomy.database;


import org.bibsonomy.database.newImpl.content.BookmarkDBManager;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

import junit.framework.TestCase;

public class DeleteABookmark extends TestCase{

	public void testdelete(){
		
		BookmarkDBManager db =new BookmarkDBManager();
		final User user =new User();
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
		
        user.setName("jaeschke");	
        bookmark.setIntraHash("fd5a16aaeef486493esb8b9462f3678c");
		
		post.setResource(bookmark);
		post.setUser(user);
        db.deletePost(post.getUser().getName(),post.getResource().getIntraHash());
        
	}
	
	
}
