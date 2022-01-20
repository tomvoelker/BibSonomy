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
package org.bibsonomy.scraper.url.kde.bmj;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #58 & #59 for DBLPScraper
 * @author hagen
 */
@Category(RemoteTest.class)
public class BMJOpenScraperTest {
	String resultDirectory = "bmj/bmjopen/";
	
	/**
	 * starts URL test with id url_249
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://bmjopen.bmj.com/content/3/1/e002072.short?g=w_open_current_tab";
		final String resultFile = resultDirectory + "BMJOpenScraperUnitURLTest1.bib";
		assertScraperResult(url, null, BMJOpenScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_250
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://bmjopen.bmj.com/content/7/4/e012851";
		final String resultFile = resultDirectory + "BMJOpenScraperUnitURLTest2.bib";
		assertScraperResult(url, null, BMJOpenScraper.class, resultFile);
	}
}
