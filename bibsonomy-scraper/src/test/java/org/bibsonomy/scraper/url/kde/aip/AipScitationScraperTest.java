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
package org.bibsonomy.scraper.url.kde.aip;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #8 #9 for AipScitationScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class AipScitationScraperTest {
	String resultDirectory = "aip/";
	
	/**
	 * starts URL test with id url_8
	 */
	@Test
	public void url1TestRun() {
		final String url = "https://aip.scitation.org/doi/abs/10.1063/1.3680558";
		final String resultFile = resultDirectory + "AipScitationScraperUnitURLTest1.bib";
		assertScraperResult(url, AipScitationScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_9
	 */
	@Test
	public void url2TestRun() {
		final String url = "https://aip.scitation.org/doi/full/10.1063/1.4820139";
		final String resultFile = resultDirectory + "AipScitationScraperUnitURLTest2.bib";
		assertScraperResult(url, AipScitationScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_174
	 */
	@Test
	public void url3TestRun() {
		final String url = "https://asa.scitation.org/doi/abs/10.1121/1.2144160";
		final String resultFile = resultDirectory + "AipScitationScraperUnitURLTest3.bib";
		assertScraperResult(url, AipScitationScraper.class, resultFile);
	}

}
