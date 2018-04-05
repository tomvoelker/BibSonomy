/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.cambridge;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL test #73 #110 for CambridgeScraper
 * @author wbi
 */
@Category(RemoteTest.class)
public class CambridgeScraperTest {

	/**
	 * starts URL test with id url_73
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://www.cambridge.org/core/journals/central-european-history/article/max-rubner-and-the-biopolitics-of-rational-nutrition/8A076B9FF653BF6A34C870B7718AFAD5";
		final String resultFile = "CambridgeScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CambridgeScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_110
	 */
	@Test
	public void url2TestRun(){
		final String url = "https://www.cambridge.org/core/journals/journal-of-fluid-mechanics/article/separatedflow-model-for-collapsibletube-oscillations/F8B8F4ABDC08F5B156B6C574F9DF4C01";
		final String resultFile = "CambridgeScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CambridgeScraper.class, resultFile);
	}
}

