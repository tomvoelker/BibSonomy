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
package org.bibsonomy.scraper.url.kde.rsc;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author wla
 */
@Category(RemoteTest.class)
public class RSCScraperTest {
	String resultDirectory = "rsc/";

	/**
	 * starts URL test with id url_223
	 */
	@Test
	public void url1TestRun() {
		final String url = "http://pubs.rsc.org/en/content/articlelanding/2012/lc/c2lc21117c";
		final String resultFile = resultDirectory + "RSCScraperUnitTest1.bib";
		assertScraperResult(url, RSCScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_247
	 */
	@Test
	public void url2TestRun() {
		final String url = "http://pubs.rsc.org/en/Content/ArticleLanding/2013/LC/C2LC41166K?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+rss%2FLC+%28RSC+-+Lab+Chip+latest+articles%29&utm_content=Google+Reader";
		final String resultFile = resultDirectory + "RSCScraperUnitTest2.bib";
		assertScraperResult(url, RSCScraper.class, resultFile);
	}

}
