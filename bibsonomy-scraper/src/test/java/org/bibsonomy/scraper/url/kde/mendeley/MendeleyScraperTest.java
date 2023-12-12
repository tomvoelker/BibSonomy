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
package org.bibsonomy.scraper.url.kde.mendeley;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Haile
 */
@Category(RemoteTest.class)
public class MendeleyScraperTest {
	String resultDirectory = "mendeley/";

	@Test
	public void url1TestRun() {
		final String url = "http://www.mendeley.com/catalog/social-bookmarking-beispiel-bibsonomy/";
		final String resultFile = resultDirectory + "MendeleyScraperUnitURLTest1.bib";
		assertScraperResult(url, MendeleyScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "http://www.mendeley.com/research/active-learning-overcome-sample-selection-bias-application-photometric-variable-star-classification/";
		final String resultFile = resultDirectory + "MendeleyScraperUnitURLTest2.bib";
		assertScraperResult(url, MendeleyScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://www.mendeley.com/catalogue/e7efd8e9-ab1d-3366-af73-84063250f88f/";
		final String resultFile = resultDirectory + "MendeleyScraperUnitURLTest3.bib";
		assertScraperResult(url, MendeleyScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://www.mendeley.com/catalogue/4ca03e49-d30d-3c3c-9e35-3d96f75f258a/";
		final String resultFile = resultDirectory + "MendeleyScraperUnitURLTest4.bib";
		assertScraperResult(url, MendeleyScraper.class, resultFile);
	}

	@Test
	public void url5TestRun(){
		final String url = "https://www.mendeley.com/catalogue/a9e4e02c-13f5-3bdb-8749-11e4a6740e7d/";
		final String resultFile = resultDirectory + "MendeleyScraperUnitURLTest5.bib";
		assertScraperResult(url, MendeleyScraper.class, resultFile);
	}

	@Test
	public void url6TestRun(){
		final String url = "https://www.mendeley.com/catalogue/84756568-e574-3055-8e32-68a5bc91b47d/";
		final String resultFile = resultDirectory + "MendeleyScraperUnitURLTest6.bib";
		assertScraperResult(url, MendeleyScraper.class, resultFile);
	}

}
