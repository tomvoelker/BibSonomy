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
package org.bibsonomy.scraper.url.kde.jap;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URL;

/**
 * @author hagen
 *
 */
@Category(RemoteTest.class)
public class JAPScraperTest {
	String resultDirectory = "jap/";
	/**
	 * starts URL test with id url_211
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://journals.physiology.org/doi/full/10.1152/japplphysiol.00991.2010";
		final String resultFile = resultDirectory + "JAPScraperUnitURLTest.bib";
		assertScraperResult(url, null, JAPScraper.class, resultFile);
	}
	
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://journals.physiology.org/doi/full/10.1152/japplphysiol.00991.2010"));
		JAPScraper js = new JAPScraper();
		assertTrue(js.scrape(sc));
		assertTrue(js.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<li id=\"B1\" class=\"&#xA;                references__item&#xA;            \"><span class=\"references__note\"><a href=\"#B1R\" class=\"ref__number\">".trim(), reference.substring(0, 141).trim());
		assertTrue(reference.contains("Lambert"));
	}
}
