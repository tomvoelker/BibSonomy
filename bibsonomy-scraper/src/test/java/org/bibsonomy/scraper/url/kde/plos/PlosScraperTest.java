/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.url.kde.plos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Scraper URL tests #43 & #44 for PlosScraper
 * 
 * TODO:
 * This test works only on Java in 64bit version. The problem is a regex
 * in the endote converter which thorws a StackOverFlowException, because of
 * the huge abstracts (it seems that any citation on plos.org has large abstracts).
 * 
 * @author tst
 */
@Category(RemoteTest.class)
public class PlosScraperTest {
	
	/**
	 * starts URL test with id url_43
	 */
	@Test
	public void urlTest1Run(){
		UnitTestRunner.runSingleTest("url_43");
	}
	
	/**
	 * starts URL test with id url_44
	 */
	@Test
	public void urlTest2Run(){
		UnitTestRunner.runSingleTest("url_44");
	}
	
	/**
	 * starts URL test with id url_172
	 */
	@Test
	public void urlTest3Run(){
		UnitTestRunner.runSingleTest("url_172");
	}
	
	/**
	 * starts URL test with id url_173
	 */
	@Test
	public void urlTest4Run(){
		UnitTestRunner.runSingleTest("url_173");
	}
	
	/**
	 * starts URL test with id url_200
	 */
	@Test
	public void urlTest5Run(){
		UnitTestRunner.runSingleTest("url_200");
	}
	
	/**
	 * starts URL test with id url_201
	 */
	@Test
	public void urlTest6Run(){
		UnitTestRunner.runSingleTest("url_201");
	}
	
	/**
	 * starts URL test with id url_202
	 */
	@Test
	public void urlTest7Run(){
		UnitTestRunner.runSingleTest("url_202");
	}
	
	/**
	 * starts URL test with id url_203
	 */
	@Test
	public void urlTest8Run(){
		UnitTestRunner.runSingleTest("url_203");
	}
	
	/**
	 * starts URL test with id url_204
	 */
	@Test
	public void urlTest9Run(){
		UnitTestRunner.runSingleTest("url_204");
	}
	
	/**
	 * starts URL test with id url_205
	 */
	@Test
	public void urlTest10Run(){
		UnitTestRunner.runSingleTest("url_205");
	}
	
	/**
	 * starts URL test with id url_206
	 */
	@Test
	public void urlTest11Run(){
		UnitTestRunner.runSingleTest("url_206");
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.plosntds.org/article/info%3Adoi%2F10.1371%2Fjournal.pntd.0001305"));
		PlosScraper ps = new PlosScraper();
		assertTrue(ps.scrape(sc));
		assertTrue(ps.scrapeReferences(sc));
	
		final String reference = sc.getReferences();		
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<li id=\"ref1\"><span class=\"ord".trim(), reference.substring(0, 30).trim());
		assertTrue(reference.contains("Portaels F"));
	}

}
