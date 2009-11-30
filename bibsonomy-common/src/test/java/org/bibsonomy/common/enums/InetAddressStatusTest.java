/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public class InetAddressStatusTest {

	/**
	 * Tests if conversion from int/string to enum and back work.
	 */
	@Test
	public void getInetAddressStatus() {
		// Enum -> int
		InetAddressStatus status = InetAddressStatus.WRITEBLOCKED;
		assertEquals(1, status.getInetAddressStatus());
		
		// String -> Enum
		status = InetAddressStatus.getInetAddressStatus("1");
		assertEquals(InetAddressStatus.WRITEBLOCKED, status);
		
		// int -> enum
		status = InetAddressStatus.getInetAddressStatus(1);
		assertEquals(InetAddressStatus.WRITEBLOCKED, status);
	}
	
}
