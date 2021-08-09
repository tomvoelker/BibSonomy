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
package org.bibsonomy.scraper.url.kde.base;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class BaseScraperTest {

	/**
	 * Test for URL 1
	 */
	@Test
	public void testUrl1() {
		final String url = "https://www.base-search.net/Record/a3643e97bbaec922cff6ff853dff6bb1f442ac60c14352064504b2a58c2eb24f/";
		assertScraperResult(url, null, BaseScraper.class, "BaseScraperUnitURLTest1.bib");
	}

	/**
	 * Test for URL 2
	 */
	@Test
	public void testUrl2() {
		final String url = "https://www.base-search.net/Record/a68b394bd5b038827fb2c3e4c9b0ebf9a818bcbb61eaacee1159d116a7047224//";
		assertScraperResult(url, null, BaseScraper.class, "BaseScraperUnitURLTest2.bib");
	}

}
