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
package org.bibsonomy.scraper.url.kde.osti;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class OstiScraperTest {
	String resultDirectory = "osti/";
	/**
	 * starts URL test with id url_347
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://www.osti.gov/scitech/biblio/1150628-spatial-clustering-pixels-multispectral-image";
		final String resultFile = resultDirectory + "OstiScraperUnitURLTest1.bib";
		assertScraperResult(url,  OstiScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://www.osti.gov/biblio/1755975-draft-genome-sequence-fusarium-sp-strain-ds-novel-fungal-isolate-from-grass-rhizosphere";
		final String resultFile = resultDirectory + "OstiScraperUnitURLTest2.bib";
		assertScraperResult(url,  OstiScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://www.osti.gov/biblio/1604718-measurement-prompt-d0-d+-ds+-mathrm-mathrm-production-ppb-collisions-snn-sqrt-mathrm-mathrm-nn-tev";
		final String resultFile = resultDirectory + "OstiScraperUnitURLTest3.bib";
		assertScraperResult(url,  OstiScraper.class, resultFile);
	}
}
