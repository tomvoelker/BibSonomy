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
package org.bibsonomy.scraper.url.kde.oup;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL test #371 and #372 for OxfordUniversityPressScraper
 * 
 * @author rja
 */
@Category(RemoteTest.class)
public class OxfordUniversityPressScraperTest {
	String resultDirectory = "oup/";

	/**
	 * starts URL test with id url_371
	 */
	@Test
	public void url1Test1Run() {
		final String url = "https://academic.oup.com/rev/article/22/3/157/1521720";
		final String resultFile = resultDirectory + "OxfordUniversityPressUnitURLTest1.bib";
		assertScraperResult(url, null, OxfordUniversityPressScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_372
	 */
	@Test
	public void url1Test2Run() {
		final String url = "https://academic.oup.com/rev/article/22/3/157/1521720/A-study-of-global-and-local-visibility-as-web";
		final String resultFile = resultDirectory + "OxfordUniversityPressUnitURLTest1.bib";
		assertScraperResult(url, null, OxfordUniversityPressScraper.class, resultFile);
	}
	
	/**
	 * 
	 */
	@Test
	public void url2Test1Run(){
		final String url = "https://academic.oup.com/comjnl/article-abstract/55/1/82/511672/Orange4WS-Environment-for-Service-Oriented-Data";
		final String resultFile = resultDirectory + "OxfordUniversityPressUnitURLTest2.bib";
		assertScraperResult(url, null, OxfordUniversityPressScraper.class, resultFile);
	}
}
