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
package org.bibsonomy.scraper.url.kde.ams;

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
 * Scraper URL tests #121 #122 for AmsScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class AmsScraperTest {
	
	/**
	 * starts URL test with id url_121
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_121");
	}

	/**
	 * starts URL test with id url_122
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_122");
	}

	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://journals.ametsoc.org/doi/abs/10.1175/2008BAMS2375.1"));
		AmsScraper as = new AmsScraper();
		assertTrue(as.scrape(sc));
		assertTrue(as.scrapeCitedby(sc));
		
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertEquals("<div class=\"citedByEntry\"><span class=\"author\">John D. Horel</span>, <span class=\"author\">Donna Ziegenfuss</span>".trim(), cby.substring(0, 113).trim());
		assertTrue(cby.contains("Lodovica Illari"));
	}
	
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://journals.ametsoc.org/doi/full/10.1175/JAMC-D-13-0338.1"));
		AmsScraper as = new AmsScraper();
		assertTrue(as.scrape(sc));
		assertTrue(as.scrapeReferences(sc));
		
		final String references = sc.getReferences();
		assertNotNull(references);
		assertTrue(references.length() > 100);
		assertEquals("<tr><td class=\"refnumber\" id=\"bib1\"> </td><td valign=\"top\">Ahmed<span class=\"NLM_x\">".trim(), references.substring(0, 84).trim());
		assertTrue(references.contains("Wang"));
	}
}
