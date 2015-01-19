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
package org.bibsonomy.scraper.url.kde.spires;

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
 * Scraper URL tests #29 for SpiresScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class SpiresScraperTest {
	
	/**
	 * starts URL test with id url_29
	 */
	@Test
	public void urlTestRun(){
		UnitTestRunner.runSingleTest("url_29");
	}
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://inspirehep.net/search?p=find+r+desy-thesis-2007-018"));
		
		SpiresScraper ss = new SpiresScraper();
		
		assertTrue(ss.scrape(sc));
		
		assertTrue(ss.scrapeCitedby(sc));
		
		final String cby = sc.getCitedBy();
		
		assertNotNull(cby);
		
		assertTrue(cby.length() > 100);
		
		assertEquals("<tr><td>".trim(), cby.substring(0, 30).trim());
		
		assertTrue(cby.contains("D'Ascenzo, Nicola"));
	}
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://inspirehep.net/search?p=find+r+desy-thesis-2007-018"));
		
		SpiresScraper ss = new SpiresScraper();
		
		assertTrue(ss.scrape(sc));
		
		assertTrue(ss.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);
		
		assertEquals(" <tr><td valign=\"top\"> </td><td> <small><strong>".trim(), reference.substring(0, 48).trim());
		
		assertTrue(reference.contains("Andreev, V."));
	}
	
}
