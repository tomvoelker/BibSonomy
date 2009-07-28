package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bibsonomy.database.params.BasketParam;
import org.junit.Test;

/**
 * Tests related to basket
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketDatabaseManagerTest extends AbstractDatabaseManagerTest{
	
	/**
	 * Tests the getNumBasketEntries method
	 */
	@Test
	public void getBasketNumSize(){
		int basketSize = this.basketDb.getNumBasketEntries("testuser1", this.dbSession);
		assertNotNull(basketSize);
		assertEquals(2, basketSize);
	}
	
	/**
	 * Test the creation and deletion of a basket item
	 */
	@Test
	public void createAndDeleteBasketItem(){
		int basketSize = 0;
		
		// build param
		BasketParam param = new BasketParam();
		param.setContentId(14);
		param.setUserName("testuser1");
		param.setRequestedUserName("testuser2");
		
		// get actual basketsize
		basketSize = this.basketDb.getNumBasketEntries("testuser1", this.dbSession);
		assertEquals(2, basketSize);
		
		// create new basket item with content id 14
		this.basketDb.createItem(param, this.dbSession);
		
		// get actual basketsize
		basketSize = this.basketDb.getNumBasketEntries("testuser1", this.dbSession);
		assertEquals(3, basketSize);
		
		// prepare param
		param.setContentId(14);
		param.setUserName("testuser1");
		
		// delete basket item with content id 14
		this.basketDb.deleteItem(param, this.dbSession);
		
		// get actual basket size
		basketSize = this.basketDb.getNumBasketEntries("testuser1", this.dbSession);
		assertEquals(2, basketSize);
		
		// delete ALL items
		this.basketDb.deleteAllItems("testuser1", this.dbSession);
		
		// get actual basket size
		basketSize = this.basketDb.getNumBasketEntries("testuser1", this.dbSession);
		assertEquals(0, basketSize);
		
	}

}
