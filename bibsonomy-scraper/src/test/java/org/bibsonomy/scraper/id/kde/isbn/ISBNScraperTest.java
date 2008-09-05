package org.bibsonomy.scraper.id.kde.isbn;

import java.net.MalformedURLException;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Tests for ISBNScraper class (no url test)
 * @author tst
 * @version $Id$
 */
public class ISBNScraperTest {

	/**
	 * test ISBN13 detection 
	 * only numbers
	 */
	@Test
	public void getISBN13Test1(){
		assertEquals("0123456789012", ISBNScraper.extractISBN("0123456789012"));
	}
	
	/**
	 * test ISBN13 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN13Test2(){
		assertEquals("012345678901X", ISBNScraper.extractISBN("012345678901X"));
	}

	/**
	 * test ISBN13 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN13Test3(){
		assertEquals("012345678901x", ISBNScraper.extractISBN("012345678901x"));
	}
	
	/**
	 * test ISBN10 detection 
	 * only numbers
	 */
	@Test
	public void getISBN10Test1(){
		assertEquals("0123456789", ISBNScraper.extractISBN("0123456789"));
	}
	
	/**
	 * test ISBN10 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN10Test2(){
		assertEquals("012345678X", ISBNScraper.extractISBN("012345678X"));
	}
	
	/**
	 * test ISBN10 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN10Test3(){
		assertEquals("012345678x", ISBNScraper.extractISBN("012345678x"));
	}
	
	/**
	 * test clean up 
	 */
	@Test
	public void cleanISBNTest(){
		assertEquals("012345678x", ISBNScraper.cleanISBN("012-3 4-56 78 x"));
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

		assertEquals(null, ISBNScraper.extractISBN(isbn09));
		assertEquals("012345678X", ISBNScraper.extractISBN(isbn10));
		assertEquals("0123456789", ISBNScraper.extractISBN(isbn12));
		assertEquals("012345678912x", ISBNScraper.extractISBN(isbn13));
	}
	
	/**
	 * test getting URL 
	 */
	@Test
	public void getUrlForIsbnTest(){
		try {
			assertTrue(ISBNScraper.getUrlForIsbn("0123456789").toString().equals("http://www.worldcat.org/search?qt=worldcat_org_all&q=0123456789"));
		} catch (MalformedURLException ex) {
			assertTrue(false);
		}
	}
	
}
