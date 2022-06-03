/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.pnas;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL test #190 #191 for PNASScraper
 *
 * @author clemens
 */
@Category(RemoteTest.class)
public class PNASScraperTest {
	String resultDirectory = "pnas/";

	/**
	 * starts URL test with id url_190
	 */
	@Test
	public void urlTest1Run() {
		final String url = "https://www.pnas.org/content/106/52/22480.abstract";
		final String resultFile = resultDirectory + "PNASScraperUnitURLTest1.bib";
		assertScraperResult(url, PNASScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_191
	 */
	@Test
	public void urlTest2Run() {
		final String url = "https://www.pnas.org/content/101/22/8281.abstract";
		final String resultFile = resultDirectory + "PNASScraperUnitURLTest2.bib";
		assertScraperResult(url, PNASScraper.class, resultFile);
	}
	
	@Test
	@Ignore // FIXME: fix reference extracting
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://www.pnas.org/content/106/52/22480.full"));
		PNASScraper ps = new PNASScraper();
		assertTrue(ps.scrape(sc));
		assertTrue(ps.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<ol class=\"cit-list\">".trim(), reference.substring(0, 80).trim());
		assertTrue(reference.contains("DiFiglia"));
	}
}
