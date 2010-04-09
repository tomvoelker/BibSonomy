package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to basket
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final String TESTUSER1_NAME = "testuser1";
	
	private static BasketDatabaseManager basketDb;
	
	/**
	 * sets up used managers
	 */
	@BeforeClass
	public static void setupManager() {
		basketDb = BasketDatabaseManager.getInstance();
	}
	
	/**
	 * Tests the getNumBasketEntries method
	 */
	@Test
	public void getBasketNumSize(){
		int basketSize = basketDb.getNumBasketEntries(TESTUSER1_NAME, this.dbSession);
		assertNotNull(basketSize);
		assertEquals(2, basketSize);
	}
	
	/**
	 * Test the creation and deletion of a basket item
	 */
	@Test
	public void createAndDeleteBasketItem(){
		int basketSize = 0;
		
		// get actual basketsize
		basketSize = basketDb.getNumBasketEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(2, basketSize);
		
		// create new basket item with content id 14
		basketDb.createItem(TESTUSER1_NAME, 14, this.dbSession);
		
		// get actual basketsize
		basketSize = basketDb.getNumBasketEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(3, basketSize);
		
		// delete basket item with content id 14
		basketDb.deleteItem(TESTUSER1_NAME, 14, this.dbSession);
		
		// get actual basket size
		basketSize = basketDb.getNumBasketEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(2, basketSize);
		
		// delete ALL items
		basketDb.deleteAllItems(TESTUSER1_NAME, this.dbSession);
		
		// get actual basket size
		basketSize = basketDb.getNumBasketEntries(TESTUSER1_NAME, this.dbSession);
		assertEquals(0, basketSize);
		
	}

}
