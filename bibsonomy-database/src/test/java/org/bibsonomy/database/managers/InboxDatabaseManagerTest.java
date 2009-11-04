package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bibsonomy.model.InboxMessage;
import org.junit.Test;

/**
 * Tests related to inbox
 * 
 */
public class InboxDatabaseManagerTest extends AbstractDatabaseManagerTest{
	
	/**
	 * Tests the getNumInboxItems method
	 */
	@Test
	public void getInboxNumSize(){
		int inboxSize = this.inboxDb.getNumInboxItems("testuser2", this.dbSession);
		assertNotNull(inboxSize);
		assertEquals(3, inboxSize);
	}
	
	/**
	 * Test the creation and deletion of an inbox item
	 */
	@Test
	public void createAndDeleteInboxItem(){
		int inboxSize = 0;
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxItems("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		// create new inbox item with content id 6
		this.inboxDb.createItem("testuser3", "testuser2", 6, this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxItems("testuser2", this.dbSession);
		assertEquals(4, inboxSize);

		// create new inbox item with content id 6 that already has been created (=> should  not be added) 
		this.inboxDb.createItem("testuser3", "testuser2", 6, this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxItems("testuser2", this.dbSession);
		assertEquals(4, inboxSize);
		
		System.out.println(""+this.inboxDb.getInboxMessages("testuser2", this.dbSession).size());
		for (InboxMessage im: this.inboxDb.getInboxMessages("testuser2", this.dbSession)) {
			System.out.println(im.toString());

		}
		// delete inbox item with content id 14 from inbox of testuser2
		this.inboxDb.deleteItem("testuser2", 6, this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxItems("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		// delete ALL items of testuser2's inbox
		this.inboxDb.deleteAllItems("testuser2", this.dbSession);
		
		// get actual inbox size
		inboxSize = this.inboxDb.getNumInboxItems("testuser1", this.dbSession);
		assertEquals(0, inboxSize);
		
	}

}