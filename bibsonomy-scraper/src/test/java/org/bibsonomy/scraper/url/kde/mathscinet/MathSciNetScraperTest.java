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
package org.bibsonomy.scraper.url.kde.mathscinet;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #20 #21 for MathSciNetScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class MathSciNetScraperTest {
	String resultDirectory = "mathscinet/";

	/**
	 * starts URL test with id url_20
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.ams.org/mathscinet/search/publdoc.html?pg1=IID&s1=198275&r=1";
		final String resultFile = resultDirectory + "MathSciNetScraperUnitURLTest1.bib";
		assertScraperResult(url, null, MathSciNetScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_21
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.ams.org/mathscinet/search/publications.html?fmt=bibtex&pg1=MR&s1=2305904";
		final String resultFile = resultDirectory + "MathSciNetScraperUnitURLTest2.bib";
		assertScraperResult(url, null, MathSciNetScraper.class, resultFile);
	}

}
