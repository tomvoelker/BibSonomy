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
package org.bibsonomy.scraper.InformationExtraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.ReachabilityTestRunner;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 */
public class IEScraperTest {

	private static final String expectedBibtex = 
		"@misc{noauthororeditor2008,\n" +
		"booktitle = {Michael May and Bettina Berendt and Antoine Cornuejols and Joao Gama and Fosca Giannotti and Andreas Hotho and Donato Malerba and Ernestina Menesalvas and Katharina Morik and Rasmus Pedersen and Lorenza Saitta and Yucel Saygin and Assaf Schuster and Koen Vanhoof. Research Challenges in Ubiquitous Knowledge Discovery. Next Generation of Data Mining(Chapman & Hall / Crc Data Mining and Knowledge Discovery Series), Chapman & Hall / CRC},\n" +
		"date = {2008},\n" +
		"year = {2008}\n" +
		",url = {http://www.example.com/reasearch_challenges.html}\n" +
		"}";

	@Test
	public void testScrape() throws ScrapingException {
		final ScrapingContext sc = ReachabilityTestRunner.IE_SCRAPER_TEST_CONTEXT;
		sc.setUrl(TestUtils.createURL("http://www.example.com/reasearch_challenges.html"));
		
		final IEScraper scraper = new IEScraper();
		final boolean scrape = scraper.scrape(sc);
		assertTrue(scrape);

		final String bibtex = sc.getBibtexResult();
		assertEquals(expectedBibtex, bibtex);
	}

}
