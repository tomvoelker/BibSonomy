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
package org.bibsonomy.scraper.url.kde.muse;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #113 , #114 for ProjectmuseScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class ProjectmuseScraperTest {

	/**
	 * starts URL test with id url_113
	 */
	@Test
	public void url1TestRun(){
		assertScraperResult("http://muse.jhu.edu/article/190669", null, ProjectmuseScraper.class, "ProjectmuseScraperUnitURLTest1.bib");
	}

	/**
	 * starts URL test with id url_114
	 */
	@Test
	public void url2TestRun(){
		assertScraperResult("http://muse.jhu.edu/article/190166", null, ProjectmuseScraper.class, "ProjectmuseScraperUnitURLTest2.bib");
	}
	
	@Test
	public void url3TestRun(){
		assertScraperResult("http://muse.jhu.edu/book/104", null, ProjectmuseScraper.class, "ProjectmuseScraperUnitURLTest3.bib");
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://muse.jhu.edu/journals/social_science_history/v029/29.4mcnay.html"));
		ProjectmuseScraper pms = new ProjectmuseScraper();
		assertTrue(pms.scrape(sc));
		assertTrue(pms.scrapeReferences(sc));
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<meta name=\"citation_reference\" content=\"citation_author=S. Amin; citation_auth".trim(), reference.substring(0, 79).trim());
		assertTrue(reference.contains("S. Amin"));
	}

}
