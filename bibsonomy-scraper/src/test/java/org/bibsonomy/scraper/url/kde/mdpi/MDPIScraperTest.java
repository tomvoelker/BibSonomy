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

package org.bibsonomy.scraper.url.kde.mdpi;

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
 * Scraper URL tests #275 for MDPIScraper
 *
 * @author Haile
 */
@Category(RemoteTest.class)
public class MDPIScraperTest {
	/**
	 * starts URL test with id url_275
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_275");
	}
	
	@Test
	public void testCitedBy() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.mdpi.com/2072-4292/5/10/5122"));
		MDPIScraper ms = new MDPIScraper();
		assertTrue(ms.scrape(sc));
		assertTrue(ms.scrapeCitedby(sc));
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertEquals("Zhang, L.; Guo, H.; Li, X.; Wang, L. Ecosystem assessment in the Tonle Sap Lake region".trim(), cby.substring(0, 86).trim());
		assertTrue(cby.contains("Zhang, L."));
	}
}
