/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.psycontent;

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
 * Scraper URL tests #94 & #95 for PsyContentAScraper
 * @author wbi
 */
@Category(RemoteTest.class)
public class PsyContentScraperTest {
	
	/**
	 * starts URL test with id url_94
	 */
	@Test
	public void urlTest1Run(){
		UnitTestRunner.runSingleTest("url_94");
	}
	
	/**
	 * starts URL test with id url_95
	 */
	@Test
	public void urlTestRun(){
		UnitTestRunner.runSingleTest("url_95");
	}
	
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://psycontent.metapress.com/content/wlr0r32161j4j150/?p=077ed4c087c541f8a7a3d80dc02f4290&amp;pi=0"));
		PsyContentScraper ps = new PsyContentScraper();
		assertTrue(ps.scrape(sc));
		assertTrue(ps.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<tr><td valign=\"top\"><a name=\"c1\" >Dopson, S.,".trim(), reference.substring(0, 50).trim());
		
		assertTrue(reference.contains("Ferlie, E."));
	}

}
