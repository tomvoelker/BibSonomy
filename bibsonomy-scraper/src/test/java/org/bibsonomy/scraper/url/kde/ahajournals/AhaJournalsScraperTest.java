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
package org.bibsonomy.scraper.url.kde.ahajournals;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)
public class AhaJournalsScraperTest {
	/**
	 * starts test
	 */
	@Test
	public void url1TestRun() {
		assertScraperResult("http://circ.ahajournals.org/content/early/2015/11/08/CIRCULATIONAHA.115.019768.abstract", AhaJournalsScraper.class, "AhaJournalsScraperUnitURLTest1.bib");
	}
	
	/**
	 * starts test
	 */
	@Test
	public void url2TestRun() {
		assertScraperResult("http://circ.ahajournals.org/content/114/Suppl_18/II_864.2.abstract?sid=550d5036-8de5-4fbf-94c3-55c1df42f58f", AhaJournalsScraper.class, "AhaJournalsScraperUnitURLTest2.bib");
	}
}
