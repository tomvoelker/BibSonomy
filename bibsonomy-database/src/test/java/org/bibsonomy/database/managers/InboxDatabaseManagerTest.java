package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to inbox
 * 
 */
public class InboxDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static InboxDatabaseManager inboxDb;
	
	/**
	 * sets inbox db manager
	 */
	@BeforeClass
	public static void setupManagers() {
		inboxDb = InboxDatabaseManager.getInstance();
	}
	
	/**
	 * Tests the getNumInboxMessages method
	 */
	@Test
	public void getInboxNumSize(){
		int inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
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
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		// create a bookmarkPost that can be linked by an inboxMessage
		
		
		Post<Bookmark> bookmarkPost= new Post<Bookmark>();
		bookmarkPost.setContentId(6);
		ModelUtils.addToTagSet(bookmarkPost.getTags(), "testTag1", "testTag2");
		bookmarkPost.setResource(new Bookmark());
		bookmarkPost.getResource().setIntraHash("I'm_a_complicated_resourceHash");
		// create an inboxMessage
		inboxDb.createInboxMessage("testuser3", "testuser2", bookmarkPost, this.dbSession);
		
		// get actual inbox size
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(4, inboxSize);

		
		// delete the inboxMessage we created
		inboxDb.deleteInboxMessage("testuser3", "testuser2", bookmarkPost.getResource().getIntraHash(), this.dbSession);
		
		// get actual inbox size
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		// delete ALL messages of testuser2's inbox
		inboxDb.deleteAllInboxMessages("testuser2", this.dbSession);
		
		// get actual inbox size
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(0, inboxSize);
	}
}