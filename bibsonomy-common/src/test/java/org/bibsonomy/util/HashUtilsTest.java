/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

/**
 * @author daill
 * 
 * Testcases for HashUtils
 */
public class HashUtilsTest {
	private static final String SPECIAL_CHARS = "üöä!\"§$%&/()=,.-+#'´`";
	
	/**
	 * tests toHexString
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void toHexString() throws UnsupportedEncodingException {
		assertEquals("74657374", HashUtils.toHexString("test".getBytes(StringUtils.CHARSET_UTF_8)));
		assertEquals("6875727a", HashUtils.toHexString("hurz".getBytes(StringUtils.CHARSET_UTF_8)));
		assertEquals("c3bcc3b6c3a42122c2a72425262f28293d2c2e2d2b2327c2b460", HashUtils.toHexString(SPECIAL_CHARS.getBytes(StringUtils.CHARSET_UTF_8)));
	}
}
