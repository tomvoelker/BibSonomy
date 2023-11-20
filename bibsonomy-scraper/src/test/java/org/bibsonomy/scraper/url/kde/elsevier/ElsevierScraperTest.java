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
package org.bibsonomy.scraper.url.kde.elsevier;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class ElsevierScraperTest {
	String resultDirectory = "elsevier/";

	/**
	 * starts URL test with id url_366
	 */
	@Test
	public void urlTestRun1() {
		final String url = "https://www.elsevier.es/en-revista-allergologia-et-immunopathologia-105-articulo-identification-therapeutic-targets-for-childhood-S0301054615000580";
		final String resultFile = resultDirectory + "ElsevierScraperUnitURLTest1.bib";
		assertScraperResult(url, ElsevierScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_365
	 */
	@Test
	public void urlTestRun2() {
		final String url = "https://www.elsevier.es/en-revista-journal-applied-research-technology-jart-81-articulo-extensions-k-medoids-with-balance-restrictions-S1665642314716219";
		final String resultFile = resultDirectory + "ElsevierScraperUnitURLTest2.bib";
		assertScraperResult(url, ElsevierScraper.class, resultFile);
	}

	@Test
	public void urlTestRun3() {
		final String url = "https://www.elsevier.es/en-revista-international-journal-clinical-health-psychology-355-articulo-loneliness-predicts-suicidal-ideation-anxiety-S1697260020300764";
		final String resultFile = resultDirectory + "ElsevierScraperUnitURLTest3.bib";
		assertScraperResult(url, ElsevierScraper.class, resultFile);
	}

	@Test
	public void urlTestRun4() {
		final String url = "https://www.elsevier.es/en-revista-international-journal-clinical-health-psychology-355-articulo-impact-covid-19-on-psychological-wellbeing-S1697260021000338";
		final String resultFile = resultDirectory + "ElsevierScraperUnitURLTest4.bib";
		assertScraperResult(url, ElsevierScraper.class, resultFile);
	}
}
