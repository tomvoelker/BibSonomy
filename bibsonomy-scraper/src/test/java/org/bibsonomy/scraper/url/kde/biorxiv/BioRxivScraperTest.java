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
package org.bibsonomy.scraper.url.kde.biorxiv;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.junit.RemoteTestAssert;
import org.bibsonomy.scraper.url.kde.bibsonomy.BibSonomyScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for BioRxivScraper
 * @author Johannes
 */
@Category(RemoteTest.class)
public class BioRxivScraperTest {

	/**
	 * starts URL test 1
	 */
	@Test
	public void url1Test1Run(){		
		final String url = "http://biorxiv.org/content/early/2016/11/30/090654";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = BioRxivScraper.class;
		final String resultFile = "BioRxivScraperUnitURLTest1.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	/**
	 * starts URL test 2
	 */
	@Test
	public void url1Test2Run(){		
		final String url = "http://biorxiv.org/content/early/2016/11/30/090514.full.pdf+html";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = BioRxivScraper.class;
		final String resultFile = "BioRxivScraperUnitURLTest2.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
}
