/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.id.kde.doi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for DOIScraper class (no url test)
 * @author tst
 */
@Category(RemoteTest.class)
public class DOIScraperTest {
	private static final DOIScraper SCRAPER = new DOIScraper();
	
	
	@Test
	public void testScraper1() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dx.doi.org/10.1007/11922162"));
		assertFalse(SCRAPER.scrape(sc));
		assertEquals("http://link.springer.com/book/10.1007/11922162", sc.getUrl().toString());
	}

	
	@Test
	public void testScraper2() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.example.com/"));
		sc.setSelectedText("10.1145/160688.160713");
		
		assertFalse(SCRAPER.scrape(sc));
		assertEquals("http://dl.acm.org/citation.cfm?doid=160688.160713", sc.getUrl().toString());
	}
	
}
