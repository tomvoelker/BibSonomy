package org.bibsonomy.util.id;

import org.junit.Test;
import static org.junit.Assert.*;


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
		assertEquals("0123456789012", ISBNUtils.extractISBN("0123456789012"));
	}
	
	/**
	 * test ISBN13 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN13Test2(){
		assertEquals("012345678901X", ISBNUtils.extractISBN("012345678901X"));
	}

	/**
	 * test ISBN13 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN13Test3(){
		assertEquals("012345678901x", ISBNUtils.extractISBN("012345678901x"));
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
		final String isbn12 = "01234567891x";
		final String isbn13 = "012345678912x";

		assertEquals(null, ISBNUtils.extractISBN(isbn09));
		assertEquals("012345678X", ISBNUtils.extractISBN(isbn10));
		assertEquals("0123456789", ISBNUtils.extractISBN(isbn12));
		assertEquals("012345678912x", ISBNUtils.extractISBN(isbn13));
	}
	
}
