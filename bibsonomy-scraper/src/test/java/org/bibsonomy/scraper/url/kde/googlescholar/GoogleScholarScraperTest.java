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
package org.bibsonomy.scraper.url.kde.googlescholar;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #138 for GoogleSonomyScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class GoogleScholarScraperTest {
	String resultDirectory = "googlescholar/";

	@Test
	public void urlTestRun1() {
		final String url = "https://scholar.google.com/citations?view_op=view_citation&hl=en&user=1t5awvEAAAAJ&citation_for_view=1t5awvEAAAAJ:R3hNpaxXUhUC";
		final String resultFile = resultDirectory + "GoogleScholarScraperUnitURLTest1.bib";
		assertScraperResult(url, null, GoogleScholarScraper.class, resultFile);
	}
	

	@Test
	public void urlTestRun2() {
		final String url = "https://scholar.google.com/citations?view_op=view_citation&hl=en&user=1t5awvEAAAAJ&citation_for_view=1t5awvEAAAAJ:UeHWp8X0CEIC";
		final String resultFile = resultDirectory + "GoogleScholarScraperUnitURLTest2.bib";
		assertScraperResult(url, null, GoogleScholarScraper.class, resultFile);
	}

	@Test
	public void urlTestRun3() {
		final String url = "https://scholar.google.com/citations?view_op=view_citation&hl=en&user=PH4mytYAAAAJ&citation_for_view=PH4mytYAAAAJ:W7OEmFMy1HYC";
		final String resultFile = resultDirectory + "GoogleScholarScraperUnitURLTest3.bib";
		assertScraperResult(url, null, GoogleScholarScraper.class, resultFile);
	}

	@Test
	public void urlTestRun4() {
		final String url = "https://scholar.google.com/citations?view_op=view_citation&hl=en&user=PH4mytYAAAAJ&citation_for_view=PH4mytYAAAAJ:IjCSPb-OGe4C";
		final String resultFile = resultDirectory + "GoogleScholarScraperUnitURLTest4.bib";
		assertScraperResult(url, null, GoogleScholarScraper.class, resultFile);
	}
}
