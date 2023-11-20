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
package org.bibsonomy.scraper.url.kde.nowpublishers;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #285 #286

 * @author rja
 */
@Category(RemoteTest.class)
public class NowPublishersScraperTest {
	String resultDirectory = "nowpublishers/";

	/**
	 * starts URL test with id url_285
	 */
	@Test
	public void urlTest1Run(){
		final String url = "http://www.nowpublishers.com/article/Details/INR-043";
		final String resultFile = resultDirectory + "NowPublishersScraperUnitURLTest1.bib";
		assertScraperResult(url, null, NowPublishersScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_286
	 */
	@Test
	public void urlTest2Run(){
		final String url = "http://www.nowpublishers.com/article/Details/INR-012";
		final String resultFile = resultDirectory + "NowPublishersScraperUnitURLTest2.bib";
		assertScraperResult(url, null, NowPublishersScraper.class, resultFile);
	}
}
