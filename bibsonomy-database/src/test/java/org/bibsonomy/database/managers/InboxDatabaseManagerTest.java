package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * Tests related to inbox
 * 
 */
public class InboxDatabaseManagerTest extends AbstractDatabaseManagerTest{
	
	/**
	 * Tests the getNumInboxMessages method
	 */
	@Test
	public void getInboxNumSize(){
		int inboxSize = this.inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertNotNull(inboxSize);
		assertEquals(3, inboxSize);
	}
	
	/**
	 * Test the creation and deletion of an inbox message
	 */
	@Test
	public void createAndDeleteInboxItem(){
		int inboxSize = 0;
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		// create a bookmarkPost that can be linked by an inboxMessage
		Tag tag1 = new Tag("testTag1");
		Tag tag2 = new Tag("testTag2");
		Set<Tag> tags= new HashSet<Tag>();
		tags.add(tag1);
		tags.add(tag2);
		Post<Bookmark> bookmarkPost= new Post<Bookmark>();
		bookmarkPost.setContentId(6);
		bookmarkPost.setTags(tags);
		bookmarkPost.setResource(new Bookmark());
		bookmarkPost.getResource().setIntraHash("I'm_a_complicated_resourceHash");
		// create an inboxMessage
		this.inboxDb.createInboxMessage("testuser3", "testuser2", bookmarkPost, this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(4, inboxSize);

		
		// delete the inboxMessage we created
		this.inboxDb.deleteInboxMessage("testuser3", "testuser2", bookmarkPost.getResource().getIntraHash(), this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		// delete ALL messages of testuser2's inbox
		this.inboxDb.deleteAllInboxMessages("testuser2", this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(0, inboxSize);
	}
}