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
package org.bibsonomy.scraper.url.kde.prola;

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
 * Scraper URL tests #24 #25 for ProlaScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class ProlaScraperTest {
	
	/**
	 * starts URL test with id url_24
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_24");
	}

	/**
	 * starts URL test with id url_25
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_25");
	}
	
	/**
	 * starts URL test with id url_175
	 */
	@Test
	public void url3TestRun(){
		UnitTestRunner.runSingleTest("url_175");
	}
	
	/**
	 * starts URL test with id url_177
	 */
	@Test
	public void url4TestRun(){
		UnitTestRunner.runSingleTest("url_177");
	}
	/**
	 * starts URL test with id url_271
	 */
	@Test
	public void url5TestRun(){
		UnitTestRunner.runSingleTest("url_271");
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://journals.aps.org/pre/abstract/10.1103/PhysRevE.64.016131"));
		ProlaScraper ps = new ProlaScraper();
		assertTrue(ps.scrape(sc));
		assertTrue(ps.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<ol class=\"references\"><li id=\"c1\"><span xmlns:m=\"http://www.w3.org/1998/Math/MathML\" xmlns:".trim(), reference.substring(0, 92).trim());
		assertTrue(reference.contains("M.E.J. Newman"));
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://journals.aps.org/pre/abstract/10.1103/PhysRevE.64.016131"));
		ProlaScraper ps = new ProlaScraper();
		assertTrue(ps.scrape(sc));
		assertTrue(ps.scrapeCitedby(sc));
		
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertEquals("<h1>Citing Articles (525)</h1><div class=\"article panel\">".trim(), cby.substring(0, 57).trim());
		assertTrue(cby.contains("Rehan Sadiq"));
	}
}
