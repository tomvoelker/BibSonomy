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
package org.bibsonomy.scraper.url.kde.biorxiv;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for {@link BioRxivScraper}
 * @author Johannes
 */
@Category(RemoteTest.class)
public class BioRxivScraperTest {

	/**
	 * starts URL test 1
	 */
	@Test
	public void url1Test1Run() {
		final String url = "https://biorxiv.org/content/early/2016/11/30/090654";
		final String resultFile = "BioRxivScraperUnitURLTest1.bib";
		assertScraperResult(url, BioRxivScraper.class, resultFile);
	}

	/**
	 * starts URL test 2
	 */
	@Test
	public void url2Test1Run() {
		final String url = "https://biorxiv.org/content/early/2016/11/30/090514.full.pdf+html";
		final String resultFile = "BioRxivScraperUnitURLTest2.bib";
		assertScraperResult(url, BioRxivScraper.class, resultFile);
	}

	/**
	 * Tests for the different views(tabs) of the same article
	 */
	@Test
	public void url3Test1Run() {
		final String url = "https://www.biorxiv.org/content/10.1101/622803v1";
		final String resultFile = "BioRxivScraperUnitURLTest3.bib";
		assertScraperResult(url, BioRxivScraper.class, resultFile);
	}

	@Test
	public void url3Test2Run() {
		final String url = "https://www.biorxiv.org/content/10.1101/622803v1.full";
		final String resultFile = "BioRxivScraperUnitURLTest3.bib";
		assertScraperResult(url, BioRxivScraper.class, resultFile);
	}

	@Test
	public void url3Test3Run() {
		final String url = "https://www.biorxiv.org/content/10.1101/622803v1.article-info";
		final String resultFile = "BioRxivScraperUnitURLTest3.bib";
		assertScraperResult(url, BioRxivScraper.class, resultFile);
	}

	@Test
	public void url3Test4Run() {
		final String url = "https://www.biorxiv.org/content/10.1101/622803v1.article-metrics";
		final String resultFile = "BioRxivScraperUnitURLTest3.bib";
		assertScraperResult(url, BioRxivScraper.class, resultFile);
	}
}
