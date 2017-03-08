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
package org.bibsonomy.scraper.url.kde.karlsruhe;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for UBKAScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class UBKAScraperTest {
	
	/**
	 * starts URL test with id url_31
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://primo.bibliothek.kit.edu/primo_library/libweb/action/display.do?tabs=detailsTab&amp;ct=display&amp;fn=search&amp;doc=KITSRC087550059&amp;indx=3&amp;recIds=KITSRC087550059&amp;recIdxs=2&amp;elementId=&amp;renderMode=poppedOut&amp;displayMode=full";
		final String resultFile = "UBKAScraperUnitURLTest1.bib";
		assertScraperResult(url, null, UBKAScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_32
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://primo.bibliothek.kit.edu/primo_library/libweb/action/display.do?tabs=detailsTab&amp;ct=display&amp;fn=search&amp;doc=KITSRC349648727&amp;indx=5&amp;recIds=KITSRC349648727&amp;recIdxs=4&amp;elementId=&amp;renderMode=poppedOut&amp;displayMode=full";
		final String resultFile = "UBKAScraperUnitURLTest2.bib";
		assertScraperResult(url, null, UBKAScraper.class, resultFile);
	}

}
