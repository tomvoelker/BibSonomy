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
package org.bibsonomy.scraper.url.kde.apa;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests for the {@link APAScraper}
 */
@Category(RemoteTest.class)
public class APAScraperTest {
	String resultDirectory = "apa/";

	/**
	 * starts URL test with id url_244
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://psycnet.apa.org/record/1996-02773-003";
		final String resultFile = resultDirectory + "APAScraperUnitURLTest1.bib";
		assertScraperResult(url, APAScraper.class, resultFile);
	}


	@Test
	public void url2TestRun(){
		final String url = "https://psycnet.apa.org/record/1992-37683-001";
		final String resultFile = resultDirectory + "APAScraperUnitURLTest2.bib";
		assertScraperResult(url, APAScraper.class, resultFile);
	}

}
