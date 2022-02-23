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
package org.bibsonomy.scraper.url.kde.emerald;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class EmeraldScraperTest {
	String resultDirectory = "emerald/";

	/**
	 * starts URL test with id url_230 for the host
	 * http://www.emeraldinsight.com/
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://www.emerald.com/insight/content/doi/10.1108/S1876-0562(2004)0000004009/full/html";
		final String resultFile = resultDirectory + "EmeraldScraperUnitTest1.bib";
		assertScraperResult(url, null, EmeraldScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://www.emerald.com/insight/content/doi/10.1108/S1479-067X20160000015006/full/html";
		final String resultFile = resultDirectory + "EmeraldScraperUnitTest2.bib";
		assertScraperResult(url, null, EmeraldScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://www.emerald.com/insight/content/doi/10.1108/EEMCS-09-2020-0348/full/html";
		final String resultFile = resultDirectory + "EmeraldScraperUnitTest3.bib";
		assertScraperResult(url, null, EmeraldScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://www.emerald.com/insight/content/doi/10.1108/CCIJ-02-2020-0047/full/html";
		final String resultFile = resultDirectory + "EmeraldScraperUnitTest4.bib";
		assertScraperResult(url, null, EmeraldScraper.class, resultFile);
	}

}
