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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Testcase for the StringUtils class
 */
public class StringUtilsTest {

	private final String SPECIAL_CHARS = "üöä!\"§$%&/()=,.-+#'´`";

	/**
	 * tests getMD5Hash
	 */
	@Test
	public void getMD5Hash() {
		assertEquals("098f6bcd4621d373cade4e832627b4f6", StringUtils.getMD5Hash("test"));
		assertEquals("04a80d0dd1f311afd2f5b652504eb39d", StringUtils.getMD5Hash("hurz"));
		assertEquals("c54b92795c745d5fc2c7e1dcf782a489", StringUtils.getMD5Hash(this.SPECIAL_CHARS));
	}

	/**
	 * tests matchExtension
	 */
	@Test
	public void matchExtension() {
		assertTrue(StringUtils.matchExtension("test.ps", "ps"));
		assertTrue(StringUtils.matchExtension("test.ps", "PS"));
		assertTrue(StringUtils.matchExtension("ps", "ps"));
		assertFalse(StringUtils.matchExtension("test.ps", "dvi"));
		assertFalse(StringUtils.matchExtension("test.ps", "dvi", "DOC", "pdf"));
	}

	/**
	 * tests getStringFromList
	 */
	@Test
	public void getStringFromList() {
		assertEquals("[]", StringUtils.getStringFromList(Collections.<String> emptyList()));

		final List<String> someStrings = new ArrayList<String>();
		for (final int i : new int[] { 1, 2, 3 })
			someStrings.add("test" + i);
		assertEquals("[test1,test2,test3]", StringUtils.getStringFromList(someStrings));
	}

	/**
	 * tests removeNonNumbers
	 */
	@Test
	public void removeNonNumbers() {
		for (final String str : new String[] { "123test", "test123", "t1e2s3t" }) {
			assertEquals("123", StringUtils.removeNonNumbers(str));
		}
	}

	/**
	 * tests removeNonNumbersOrLetters
	 */
	@Test
	public void removeNonNumbersOrLetters() {
		assertEquals("test", StringUtils.removeNonNumbersOrLetters("!-test-!"));
		assertEquals("test", StringUtils.removeNonNumbersOrLetters(" !-test-! "));
	}

	/**
	 * tests removeNonNumbersOrLettersOrDotsOrSpace
	 */
	@Test
	public void removeNonNumbersOrLettersOrDotsOrSpace() {
		assertEquals("test", StringUtils.removeNonNumbersOrLetters("...!-test-!..."));
		assertEquals("test", StringUtils.removeNonNumbersOrLetters(". . .!-test-!. . ."));
	}

	/**
	 * tests removeWhitespace
	 */
	@Test
	public void removeWhitespace() {
		assertEquals("Theansweris42", StringUtils.removeWhitespace("The answer is 42"));
	}

	/**
	 * tests normalizeWhitespace
	 */
	@Test
	public void normalizeWhitespace() {
		assertEquals("The answer is 42", StringUtils.normalizeWhitespace("The  answer    is  42"));
	}

	/**
	 * tests cropToLength
	 */
	@Test
	public void cropToLength() {
		assertEquals("42", StringUtils.cropToLength("42", 2));
		assertEquals("42", StringUtils.cropToLength("42", 42));
		assertEquals("test", StringUtils.cropToLength("test test", 4));
	}

	/**
	 * tests secureCompareTo
	 */
	@Test
	public void secureCompareTo() {
		assertEquals(0, StringUtils.secureCompareTo(null, null));
		assertEquals(1, StringUtils.secureCompareTo("", null));
		assertEquals(-1, StringUtils.secureCompareTo(null, ""));

		assertEquals(0, StringUtils.secureCompareTo("", ""));
		assertEquals(0, StringUtils.secureCompareTo("a", "a"));
		assertEquals(1, StringUtils.secureCompareTo("a", ""));
		assertEquals(-1, StringUtils.secureCompareTo("", "a"));
	}
}