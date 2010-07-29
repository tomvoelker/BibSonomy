/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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

package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.junit.Test;

/**
 * Testcase for the StringUtils class
 */
public class StringUtilsTest {
	
	private static final String TEST_VALUE1 = "test";
	private static final String TEST_VALUE2 = "hurz";
	private static final String SPECIAL_CHARS = "üöä!\"§$%&/()=,.-+#'´`";

	/**
	 * tests getMD5Hash
	 */
	@Test
	public void getMD5Hash() {
		assertEquals("098f6bcd4621d373cade4e832627b4f6", StringUtils.getMD5Hash(TEST_VALUE1));
		assertEquals("04a80d0dd1f311afd2f5b652504eb39d", StringUtils.getMD5Hash(TEST_VALUE2));
		assertEquals("c54b92795c745d5fc2c7e1dcf782a489", StringUtils.getMD5Hash(SPECIAL_CHARS));
	}
	
	/**
	 * tests {@link StringUtils#getSHA1Hash(String)}
	 */
	@Test
	public void getSHA1Hash() {
		assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", StringUtils.getSHA1Hash(TEST_VALUE1));
		assertEquals("d01f0831ca4bbe64b486906dd8a484c7a32b1cd6", StringUtils.getSHA1Hash(TEST_VALUE2));
		assertEquals("4e18e94bf8b7ccf8b91462e4b9baa6fd31a244fc", StringUtils.getSHA1Hash(SPECIAL_CHARS));
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
			someStrings.add(TEST_VALUE1 + i);
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
		assertEquals(TEST_VALUE1, StringUtils.removeNonNumbersOrLetters("!-test-!"));
		assertEquals(TEST_VALUE1, StringUtils.removeNonNumbersOrLetters(" !-test-! "));
	}

	/**
	 * tests removeNonNumbersOrLettersOrDotsOrSpace
	 */
	@Test
	public void removeNonNumbersOrLettersOrDotsOrSpace() {
		assertEquals(TEST_VALUE1, StringUtils.removeNonNumbersOrLetters("...!-test-!..."));
		assertEquals(TEST_VALUE1, StringUtils.removeNonNumbersOrLetters(". . .!-test-!. . ."));
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
		assertEquals(TEST_VALUE1, StringUtils.cropToLength("test test", 4));
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
	
	
	/**
	 * tests parseKeyValuePairs
	 */
	@Test
	public void parseKeyValuePairs() {
		// empty input
		String input = "";
		Map<String, String> result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		assertEquals(0,result.keySet().size());
		assertEquals(0,result.values().size());
		// normal mode
		input = "key1 = {value1}, key2 = {value2}, key3 = {value3}";
		result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		assertEquals(3,result.keySet().size());
		assertEquals(3,result.values().size());
		assertEquals("value1", result.get("key1"));
		assertEquals("value2", result.get("key2"));
		assertEquals("value3", result.get("key3"));
		// without spaces
		input = "key1={value1},key2={value2},key3={value3}";
		result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		assertEquals(3,result.keySet().size());
		assertEquals(3,result.values().size());
		assertEquals("value1", result.get("key1"));
		assertEquals("value2", result.get("key2"));
		assertEquals("value3", result.get("key3"));
		// with leading / trailing spaces
		input = "     key1={value1},key2={value2},key3={value3}   ";
		result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		assertEquals(3,result.keySet().size());
		assertEquals(3,result.values().size());
		assertEquals("value1", result.get("key1"));
		assertEquals("value2", result.get("key2"));
		assertEquals("value3", result.get("key3"));
		// with additional brackets
		input = "     key1={val{}{}{}ue1},key2={v{a{l}u}e2},key3={v{{al}u}e3}   ";
		result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		assertEquals(3,result.keySet().size());
		assertEquals(3,result.values().size());
		assertEquals("val{}{}{}ue1", result.get("key1"));
		assertEquals("v{a{l}u}e2", result.get("key2"));
		assertEquals("v{{al}u}e3", result.get("key3"));
		// with 'strange' keys and values
		input = "     key 1={val==ue1}, k e-y 2 ={v=a&{}lue2}, k___e   y3=={=value3=}   ";
		result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		assertEquals(3,result.keySet().size());
		assertEquals(3,result.values().size());
		assertEquals("val==ue1", result.get("key 1"));
		assertEquals("v=a&{}lue2", result.get("k e-y 2"));
		assertEquals("=value3=", result.get("k___e   y3"));
		// unmatched brackets
		input = "     key 1={val==ue1}}, k e-y 2 ={v=a&{}lue2}, k___e   y3=={=value3=}   ";	
		boolean fail = true;
		try {
			result = StringUtils.parseBracketedKeyValuePairs(input, '=', ',', '{', '}');
		} catch (InvalidModelException e) {
			fail = false;
		}
		if (fail) {fail("InvalidModelException should have been thrown!");}
	}
	
	@Test
	public void testRemoveSingleNumbers() throws Exception {
		assertEquals(null, StringUtils.removeSingleNumbers(null));
		assertEquals("Foo", StringUtils.removeSingleNumbers("Foo"));
		assertEquals("Foo ", StringUtils.removeSingleNumbers("Foo "));
		assertEquals(" Foo ", StringUtils.removeSingleNumbers(" Foo "));
		assertEquals("Foo Bar", StringUtils.removeSingleNumbers("Foo Bar"));
		assertEquals("Foo  Bar", StringUtils.removeSingleNumbers("Foo 000 Bar"));
		assertEquals("Foo Bar ", StringUtils.removeSingleNumbers("Foo Bar 000"));
		assertEquals(" Foo Bar", StringUtils.removeSingleNumbers("012 Foo Bar"));
		assertEquals("Foo Bar000", StringUtils.removeSingleNumbers("Foo Bar000"));
	}
	
}