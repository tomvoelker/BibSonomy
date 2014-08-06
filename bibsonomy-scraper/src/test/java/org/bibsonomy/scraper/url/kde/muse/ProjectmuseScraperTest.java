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

package org.bibsonomy.scraper.url.kde.muse;

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
		UnitTestRunner.runSingleTest("url_113");
	}

	/**
	 * starts URL test with id url_114
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_114");
	}
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://muse.jhu.edu/journals/social_science_history/v029/29.4mcnay.html"));
		ProjectmuseScraper pms = new ProjectmuseScraper();
		assertTrue(pms.scrape(sc));
		assertTrue(pms.scrapeReferences(sc));
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<!--_references-->\nReferences\n<!--_/references-->\n</h3>\n\n<p class=\"noIndent\">".trim(), reference.substring(0, 79).trim());
		assertTrue(reference.contains("Amin, S."));
	}

}
