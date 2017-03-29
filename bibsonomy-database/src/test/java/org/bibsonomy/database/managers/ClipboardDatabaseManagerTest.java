/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to clipboard
 * 
 * @author Christian Kramer
 */
public class ClipboardDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final String TESTUSER1_NAME = "testuser1";
	
	private static ClipboardDatabaseManager clipboardDb;
	
	/**
	 * sets up used managers
	 */
	@BeforeClass
	public static void setupManager() {
		clipboardDb = ClipboardDatabaseManager.getInstance();
	}
	
	/**
	 * Test the creation and deletion of a clipboard item
	 */
	@Test
	public void createAndDeleteClipboardItem(){
		int clipboardSize = 0;
		
		// get actual clipboardsize
		clipboardSize = clipboardDb.getNumberOfClipboardEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(2, clipboardSize);
		
		// create new clipboard item with content id 14
		clipboardDb.createItem(TESTUSER1_NAME, 14, this.dbSession);
		
		// get actual clipboardsize
		clipboardSize = clipboardDb.getNumberOfClipboardEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(3, clipboardSize);
		
		// delete clipboard item with content id 14
		clipboardDb.deleteItem(TESTUSER1_NAME, 14, this.dbSession);
		
		// get actual clipboard size
		clipboardSize = clipboardDb.getNumberOfClipboardEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(2, clipboardSize);
		
		// delete ALL items
		clipboardDb.deleteAllItems(TESTUSER1_NAME, this.dbSession);
		
		// get actual clipboard size
		clipboardSize = clipboardDb.getNumberOfClipboardEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(0, clipboardSize);
	}

}
