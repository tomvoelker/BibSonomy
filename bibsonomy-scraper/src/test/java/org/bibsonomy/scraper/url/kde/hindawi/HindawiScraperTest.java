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
package org.bibsonomy.scraper.url.kde.hindawi;
import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
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
 * @author Haile
 */
@Category(RemoteTest.class)
public class HindawiScraperTest {
	/**
	 * starts URL test with id url_256
	 */
	@Test
	public void urlTestRun() {
		final String url = "https://www.hindawi.com/journals/ijcb/2010/507821/cta/";
		final String resultFile = "HindawiScraperUnitURLTest.bib";
		assertScraperResult(url, null, HindawiScraper.class, resultFile);
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.hindawi.com/journals/tswj/2014/625754/ref/"));
		
		HindawiScraper hs = new HindawiScraper();
		
		assertTrue(hs.scrape(sc));
		
		assertTrue(hs.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);
		
		assertEquals("<h4>Linked References</h4>".trim(), reference.substring(0, 26).trim());
		
		assertTrue(reference.contains("C. V. Rao, D. M. Wolf"));
	}
}
