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
package org.bibsonomy.scraper.url.kde.aanda;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author DaiLL
 */
@Category(RemoteTest.class)
public class AandAScraperTest {

	/**
	 * starts URL test with id url_181
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://www.aanda.org/articles/aa/abs/2006/01/aa3694-05/aa3694-05.html";
		final String resultFile = "AandAScraperUnitURLTest.bib";
		assertScraperResult(url, AandAScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_182
	 */
	@Test
	public void url2TestRun() {
		final String url = "https://www.aanda.org/articles/aa/abs/2010/05/aa14294-10/aa14294-10.html";
		final String resultFile = "AandAScraperUnitURLTest1.bib";
		assertScraperResult(url, AandAScraper.class, resultFile);
	}

	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://www.aanda.org/articles/aa/abs/2010/05/aa14294-10/aa14294-10.html"));
		
		AandAScraper aas = new AandAScraper();
		
		assertTrue(aas.scrape(sc));
		
		assertTrue(aas.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);		
		assertEquals("<li>\n"+"                "+"<a name=\"BH98\"></a>Balbus, S. A., &amp; Hawley, J. F. 1998, Rev. Mod. Phys.,".trim(), reference.substring(0, 119).trim());
		assertTrue(reference.contains("Balbus, S. A."));
	}
}
