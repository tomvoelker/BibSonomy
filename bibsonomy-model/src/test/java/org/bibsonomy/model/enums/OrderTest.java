package org.bibsonomy.model.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class OrderTest {

	/**
	 * tests getOrderByName
	 */
	@Test
	public void getOrderByName() {
		assertEquals(Order.ADDED, Order.getOrderByName("added"));
		assertEquals(Order.ADDED, Order.getOrderByName("AdDeD"));
		assertEquals(Order.POPULAR, Order.getOrderByName("popular"));
		assertEquals(Order.POPULAR, Order.getOrderByName("PoPuLaR"));
		assertEquals(Order.FOLKRANK, Order.getOrderByName("folkrank"));
		assertEquals(Order.FOLKRANK, Order.getOrderByName("FoLkRaNk"));

		for (final String test : new String[] { "", " ", null, "test" }) {
			try {
				Order.getOrderByName(test);
				fail("Should throw exception");
			} catch (IllegalArgumentException ignore) {
			}
		}
	}
}