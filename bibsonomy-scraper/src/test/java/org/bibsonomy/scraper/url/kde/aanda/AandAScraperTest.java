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

package org.bibsonomy.scraper.url.kde.aanda;

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
 * @author DaiLL
 */
@Category(RemoteTest.class)
public class AandAScraperTest {
	/**
	 * starts URL test with id url_181
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_181");
	}

	/**
	 * starts URL test with id url_182
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_182");
	}
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.aanda.org/index.php?option=article&amp;access=doi&amp;doi=10.1051/0004-6361/201014294"));
		
		AandAScraper aas = new AandAScraper();
		
		assertTrue(aas.scrape(sc));
		
		assertTrue(aas.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);
		
		assertEquals("<li>\n\t\t\t<a name=\"BH98\"></a>Balbus, S. A., &amp; Hawley, J. F. 1998, Rev. Mod.".trim(), reference.substring(0, 85).trim());
		
		assertTrue(reference.contains("Balbus, S. A."));
	}
}
