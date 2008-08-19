package org.bibsonomy.scraper.id.kde.isbn;

import java.net.MalformedURLException;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

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
		assertTrue(ISBNScraper.getISBN13("0123456789012").equals("0123456789012"));
	}
	
	/**
	 * test ISBN13 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN13Test2(){
		assertTrue(ISBNScraper.getISBN13("012345678901X").equals("012345678901X"));
	}

	/**
	 * test ISBN13 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN13Test3(){
		assertTrue(ISBNScraper.getISBN13("012345678901x").equals("012345678901x"));
	}
	
	/**
	 * test ISBN10 detection 
	 * only numbers
	 */
	@Test
	public void getISBN10Test1(){
		assertTrue(ISBNScraper.getISBN10("0123456789").equals("0123456789"));
	}
	
	/**
	 * test ISBN10 detection 
	 * with X in checksum
	 */
	@Test
	public void getISBN10Test2(){
		assertTrue(ISBNScraper.getISBN10("012345678X").equals("012345678X"));
	}
	
	/**
	 * test ISBN10 detection 
	 * with x in checksum
	 */
	@Test
	public void getISBN10Test3(){
		assertTrue(ISBNScraper.getISBN10("012345678x").equals("012345678x"));
	}
	
	/**
	 * test clean up 
	 */
	@Test
	public void cleanISBNTest(){
		assertTrue(ISBNScraper.cleanISBN("012-3 4-56 78 x").equals("012345678x"));
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
