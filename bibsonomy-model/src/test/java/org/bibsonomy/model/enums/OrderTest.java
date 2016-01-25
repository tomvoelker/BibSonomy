/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
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