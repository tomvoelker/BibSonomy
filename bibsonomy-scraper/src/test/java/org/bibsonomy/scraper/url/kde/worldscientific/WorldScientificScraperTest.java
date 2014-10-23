/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.worldscientific;

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
 * Scraper test for url_279, references and cited by
 * @author Haile
 */
@Category(RemoteTest.class)
public class WorldScientificScraperTest {
	/**
	 * starts URL test with id url_279
	 */
	@Test
	public void urlTestRun1(){
		UnitTestRunner.runSingleTest("url_279");
	}
	
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.worldscientific.com/doi/pdf/10.1142/S0219622006002271"));
		
		WorldScientificScraper ws = new WorldScientificScraper();
		assertTrue(ws.scrape(sc));
		assertTrue(ws.scrapeCitedby(sc));
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		
		assertEquals("<a class=\"entryAuthor\" href=\"/action/doSearch?Contrib=LIU%2C+RONG\">RONG LIU</a>".trim(), cby.substring(0, 79).trim());
		
		assertTrue(cby.contains("XIANYONG FANG"));
	}
	
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.worldscientific.com/doi/pdf/10.1142/S0219622006002271"));
		
		WorldScientificScraper ws = new WorldScientificScraper();
		assertTrue(ws.scrape(sc));
		assertTrue(ws.scrapeReferences(sc));
		final String references = sc.getReferences();
		assertNotNull(references);
		assertTrue(references.length() > 100);
		
		assertEquals("<li class=\"reference\"> R. Agrawal, Mining Newsgroups using networks arising from social behavior, <i>Proc".trim(), references.substring(0, 105).trim());
		
		assertTrue(references.contains("A. D.   Baxevanis"));
	}
}