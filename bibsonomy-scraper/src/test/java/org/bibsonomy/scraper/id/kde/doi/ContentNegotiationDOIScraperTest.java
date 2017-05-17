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

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import java.io.IOException;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author lha
 */
@Category(RemoteTest.class)
public class ContentNegotiationDOIScraperTest {
	
	/**
	 * tests the function of the ContentNegotiationDOIScraper for DOI URLs
	 * @throws ScrapingException
	 * @throws IOException
	 */
	@Test
	public void testCNDOIScraper1() throws ScrapingException, IOException {
		final String url = "http://dx.doi.org/10.1108/07378831211213265";
		final String resultFile = "ContentNegotiationDOIScraper1.bib";
		assertScraperResult(url, null, ContentNegotiationDOIScraper.class, resultFile);
	}

	/**
	 * tests the function of the ContentNegotiationDOIScraper for selected DOI texts
	 * @throws ScrapingException
	 * @throws IOException
	 */
	@Test
	public void testCNDOIScraper2() throws ScrapingException, IOException {
		final String url = "http://www.example.com";
		final String resultFile = "ContentNegotiationDOIScraper1.bib";
		final String selection = "DOI:    10.1108/07378831211213265";
		assertScraperResult(url, selection, ContentNegotiationDOIScraper.class, resultFile);
	}
}
