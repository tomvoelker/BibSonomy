/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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