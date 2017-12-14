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
package org.bibsonomy.scraper.id.kde.doi;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for HTMLMetaDataDOIScraper
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class HTMLMetaDataDOIScraperTest {

	@Test
	public void testGetDoiFromMetaData() throws ScrapingException, MalformedURLException {
		URL testURL;
		testURL = new URL("https://www.biorxiv.org/content/early/2017/10/06/199430");
		String doi = new HTMLMetaDataDOIScraper().getDoiFromMetaData(testURL);
		assertEquals("10.1101/199430", doi);
	}

	@Test
	public void urlTest1() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("https://link.springer.com/book/10.1007/978-3-319-60492-3#about"));
		HTMLMetaDataDOIScraper scraper = new HTMLMetaDataDOIScraper();

		assertFalse(scraper.scrape(sc));
		assertEquals("10.1007/978-3-319-60492-3", sc.getSelectedText());
	}
}
