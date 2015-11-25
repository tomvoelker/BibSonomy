/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.TestDatabaseManager;
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
	/**
	 * Test the creation and deletion of an inbox message
	 */
	@Test
	public void createAndDeleteInboxItem(){
		// test initial inboxSize from testData
		int inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertNotNull(inboxSize);
		assertEquals(3, inboxSize);
		
		TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
		
		// get actual inbox size
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(3, inboxSize);

		//ensure log_inboxMail is empty
		assertEquals(0, testDatabaseManager.getLogInboxCount(null));
		
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
		
		//get actual log size for testuser2
		assertEquals(1, testDatabaseManager.getLogInboxCount("testuser2"));
		
		// get actual inbox size
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(3, inboxSize);
		
		
		// delete ALL messages of testuser2's inbox
		inboxDb.deleteAllInboxMessages("testuser2", this.dbSession);
		
		//now all 4 messages must be logged
		assertEquals(4, testDatabaseManager.getLogInboxCount("testuser2"));
		//and must be only messages of testuser2
		assertEquals(testDatabaseManager.getLogInboxCount("testuser2"), testDatabaseManager.getLogInboxCount(null));
		
		// get actual inbox size
		inboxSize = inboxDb.getNumInboxMessages("testuser2", this.dbSession);
		assertEquals(0, inboxSize);
	}
}