/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.acm;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #1 #134 #153 for ACMBasicSCraper  
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class ACMBasicScraperTest {
	
	/**
	 * starts URL test with id url_1
	 */
	@Test
	public void urlTestRun1(){
		final String url = "http://portal.acm.org/citation.cfm?id=1015428&amp;coll=Portal&amp;dl=ACM&amp;CFID=22531872&amp;CFTOKEN=18437036";
		final String resultFile = "ACMBasicScraperUnitURLTest1.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_134
	 */
	@Test
	public void urlTestRun2(){
		final String url = "http://portal.acm.org/citation.cfm?id=333115.333119&amp;coll=GUIDE&amp;dl=GUIDE&amp;CFID=11052258&amp;CFTOKEN=84161555";
		final String resultFile = "ACMBasicScraperUnitURLTest2.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_153
	 */
	@Test
	public void urlTestRun3(){
		final String url = "http://portal.acm.org/citation.cfm?id=1105676";
		final String resultFile = "ACMBasicScraperUnitURLTest3.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_155
	 */
	@Test
	public void urlTestRun4(){
		final String url = "http://portal.acm.org/citation.cfm?id=553876";
		final String resultFile = "ACMBasicScraperUnitURLTest4.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_184
	 */
	@Test
	public void urlTestRun5(){
		final String url = "http://portal.acm.org/beta/citation.cfm?id=359859";
		final String resultFile = "ACMBasicScraperUnitURLTest5.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	
	@Test
	public void urlTestRun6(){
		final String url = "http://portal.acm.org/citation.cfm?id=1082036.1082037&amp;coll=Portal&amp;dl=GUIDE&amp;CFID=88775871&amp;CFTOKEN=40392553#";
		final String resultFile = "ACMBasicScraperUnitURLTest6.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	/**
	 * 
	 */
	@Test
	public void urlTestRun7(){
		final String url = "http://dl.acm.org/citation.cfm?id=1571977";
		final String resultFile = "ACMBasicScraperUnitURLTest7.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * CACM
	 */
	@Test
	public void urlTestRun9(){
		final String url = "https://cacm.acm.org/magazines/2015/8/189841-understanding-the-us-domestic-computer-science-phd-pipeline/fulltext";
		final String resultFile = "ACMBasicScraperUnitURLTest9.bib";
		assertScraperResult(url, null, ACMBasicScraper.class, resultFile);
	}
	
	@Test
	public void test2() throws MalformedURLException {
		
		String url = "http://portal.acm.org/citation.cfm?id=500737.500755"; // abstract works
		url = "http://portal.acm.org/citation.cfm?id=1364171"; // abstract missing
		final ACMBasicScraper acm = new ACMBasicScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			acm.scrape(sc);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}
	
	/**
	 * Test the URL patterns that this scraper shall support.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSupportsUrl() throws Exception {
		
		final ACMBasicScraper a = new ACMBasicScraper();
		
		assertTrue(a.supportsUrl(new URL("http://portal.acm.org/citation.cfm?id=1559845.1559994")));
		assertTrue(a.supportsUrl(new URL("http://portal.acm.org/citation.cfm?id=1547343")));
		assertTrue(a.supportsUrl(new URL("http://doi.acm.org/10.1145/1105664.1105676")));
	}
	
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dl.acm.org/citation.cfm?doid=1105664.1105676"));
		
		ACMBasicScraper acm = new ACMBasicScraper();
		
		assertTrue(acm.scrape(sc));
		
		assertTrue(acm.scrapeCitedby(sc));
		
		final String cby = sc.getCitedBy();
		
		assertNotNull(cby);
		
		assertTrue(cby.length() > 100);
		
		assertEquals("<div style=\"margin-left:10px; margin-top:0px; margin-right:10px; margin-bottom: 10px;".trim(), cby.substring(0, 86).trim());
		
		assertTrue(cby.contains("Margaret-Anne Storey"));
	}
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dl.acm.org/citation.cfm?doid=1105664.1105676"));
		
		ACMBasicScraper acm = new ACMBasicScraper();
		
		assertTrue(acm.scrape(sc));
		
		assertTrue(acm.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);
		
		assertEquals("<div style=\"margin-left:10px; margin-top:0px; margin-right:10px; margin-bottom: 10px;".trim(), reference.substring(0, 86).trim());
		
		assertTrue(reference.contains("David Abrams"));
	}
}
