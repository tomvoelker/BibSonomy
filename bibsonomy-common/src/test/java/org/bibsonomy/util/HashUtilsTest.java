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

package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

/**
 * @author daill
 * @version $Id$
 * 
 * Testcases for HashUtils
 */
public class HashUtilsTest {
	private final String SPECIAL_CHARS = "üöä!\"§$%&/()=,.-+#'´`";
	
	/**
	 * tests toHexString
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void toHexString() throws UnsupportedEncodingException {
		assertEquals("74657374", HashUtils.toHexString("test".getBytes("UTF-8")));
		assertEquals("6875727a", HashUtils.toHexString("hurz".getBytes("UTF-8")));
		assertEquals("c3bcc3b6c3a42122c2a72425262f28293d2c2e2d2b2327c2b460", HashUtils.toHexString(this.SPECIAL_CHARS.getBytes("UTF-8")));
	}
}
