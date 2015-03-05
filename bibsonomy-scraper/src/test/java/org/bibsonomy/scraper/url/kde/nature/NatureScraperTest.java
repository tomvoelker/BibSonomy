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
package org.bibsonomy.scraper.url.kde.nature;

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
 * Scraper URL tests #45 for NatureScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class NatureScraperTest {

	/**
	 * starts URL test with id url_45
	 */
	@Test
	public void urlTest1Run(){
		UnitTestRunner.runSingleTest("url_45");
	}

	/**
	 * starts URL test with id url_231
	 */
	@Test
	public void urlTest2Run(){
		UnitTestRunner.runSingleTest("url_231");
	}
	/**
	 * starts URL test with id url_280
	 */
	@Test
	public void urlTest3Run(){
		UnitTestRunner.runSingleTest("url_280");
	}
	/**
	 * starts URL test with id url_282
	 */
	@Test
	public void urlTest4Run(){
		UnitTestRunner.runSingleTest("url_282");
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.nature.com/ncomms/2014/141215/ncomms6341/full/ncomms6341.html"));
		NatureScraper ns = new NatureScraper();
		assertTrue(ns.scrape(sc));
		assertTrue(ns.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("TY  - JOUR\nAU  - de Boer, P. A.".trim(), reference.substring(0, 31).trim());
		assertTrue(reference.contains("Crossley, R. E."));
	}	
}
