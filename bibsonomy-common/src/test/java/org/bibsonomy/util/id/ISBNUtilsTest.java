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

package org.bibsonomy.util.id;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author rja
 * @version $Id$
 */
public class ISBNUtilsTest {

	/**
	 * test ISBN13 detection 
	 * only numbers
	 */
	@Test
	public void getISBN13Test1(){
		assertEquals("9783456789012", ISBNUtils.extractISBN("9783456789012"));
		assertEquals("9793456789012", ISBNUtils.extractISBN("9793456789012"));
		assertEquals(null, ISBNUtils.extractISBN("9773456789012"));
	}
	
	/**
	 * test ISBN13 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN13Test2(){
		assertEquals("978345678901X", ISBNUtils.extractISBN("978345678901X"));
		assertEquals("979345678901X", ISBNUtils.extractISBN("979345678901X"));
		assertEquals(null, ISBNUtils.extractISBN("977345678901X"));
	}

	/**
	 * test ISBN13 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN13Test3(){
		assertEquals("978345678901x", ISBNUtils.extractISBN("978345678901x"));
		assertEquals("979345678901x", ISBNUtils.extractISBN("979345678901x"));
		assertEquals(null, ISBNUtils.extractISBN("977345678901x"));
	}
	
	/**
	 * test ISBN13 detection 
	 * only numbers
	 */
	@Test
	public void getISBN13Test4(){
		assertEquals("9783456789012", ISBNUtils.extractISBN("ysdfsdf9783456789012sdfsdf"));
		assertEquals("9793456789012", ISBNUtils.extractISBN("ysdfsdf9793456789012sdfsdf"));
		assertEquals(null, ISBNUtils.extractISBN("ysdfsdf9773456789012sdfsdf"));
	}
	
	/**
	 * test ISBN10 detection 
	 * only numbers
	 */
	@Test
	public void getISBN10Test1(){
		assertEquals("0123456789", ISBNUtils.extractISBN("0123456789"));
	}
	
	/**
	 * test ISBN10 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN10Test2(){
		assertEquals("012345678X", ISBNUtils.extractISBN("012345678X"));
	}
	
	/**
	 * test ISBN10 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN10Test3(){
		assertEquals("012345678x", ISBNUtils.extractISBN("012345678x"));
	}
	
	/**
	 * test ISBN10 detection 
	 * only numbers
	 */
	@Test
	public void getISBN10Test4(){
		assertEquals("0123456789", ISBNUtils.extractISBN("asdffsda0123456789avcsad"));
	}
	
	/**
	 * test clean up 
	 */
	@Test
	public void cleanISBNTest(){
		assertEquals("012345678x", ISBNUtils.cleanISBN("012-3 4-56 78 x"));
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void getISBNTest1() throws Exception {
		final String isbn09 = "01234567X";
		final String isbn10 = "012345678X";
		final String isbn13 = "978345678912x";

		assertEquals(null, ISBNUtils.extractISBN(isbn09));
		assertEquals("012345678X", ISBNUtils.extractISBN(isbn10));
		assertEquals("978345678912x", ISBNUtils.extractISBN(isbn13));
	}
	
	/**
	 * test ISSN13 detection 
	 * only numbers
	 */
	@Test
	public void getISSN13Test1(){
		assertEquals("9773456789012", ISBNUtils.extractISSN("9773456789012"));
		assertEquals(null, ISBNUtils.extractISSN("9793456789012"));
	}
	
	/**
	 * test ISSN13 detection 
	 * with X in checksum
	 */
	@Test
	public void getISSN13Test2(){
		assertEquals("977345678901X", ISBNUtils.extractISSN("977345678901X"));
		assertEquals(null, ISBNUtils.extractISSN("979345678901X"));
	}

	/**
	 * test ISSN13 detection 
	 * with x in checksum
	 */
	@Test
	public void getISSN13Test3(){
		assertEquals("977345678901x", ISBNUtils.extractISSN("977345678901x"));
		assertEquals(null, ISBNUtils.extractISSN("979345678901x"));
	}
	
	/**
	 * test ISSN13 detection 
	 * only numbers
	 */
	@Test
	public void getISSN13Test4(){
		assertEquals("9773456789012", ISBNUtils.extractISSN("ysdfsdf9773456789012sdfsdf"));
		assertEquals(null, ISBNUtils.extractISSN("ysdfsdf9793456789012sdfsdf"));
	}
	
	/**
	 * test ISSN8 detection 
	 * only numbers
	 */
	@Test
	public void getISSN8Test1(){
		assertEquals("01234567", ISBNUtils.extractISSN("01234567"));
	}
	
	/**
	 * test ISSN8 detection 
	 * with X in checksum
	 */
	@Test
	public void getISSN8Test2(){
		assertEquals("0123456X", ISBNUtils.extractISSN("0123456X"));
	}
	
	/**
	 * test ISSN8 detection 
	 * with x in checksum
	 */
	@Test
	public void getISSN8Test3(){
		assertEquals("0123456x", ISBNUtils.extractISSN("0123456x"));
	}
	
	/**
	 * test ISSN8 detection 
	 * only numbers
	 */
	@Test
	public void getISSN8Test4(){
		assertEquals("0123456x", ISBNUtils.extractISSN("asdfjkls0123456xyxdvoije"));
	}
	
	
}
