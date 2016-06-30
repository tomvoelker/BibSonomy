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
package org.bibsonomy.scraper.url.kde.dlib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Scraper URL tests #54 & #55 for DLibScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class DLibScraperTest {

	/**
	 * starts URL test with id url_54
	 */
	@Test
	public void urlTest1Run(){
		UnitTestRunner.runSingleTest("url_54");
	}
		
	/**
	 * starts URL test with id url_55
	 */
	@Test
	public void urlTest2Run(){
		UnitTestRunner.runSingleTest("url_55");
	}
	/**
	 * starts URL test with id url_266
	 */
	@Test
	public void urlTest3Run(){
		UnitTestRunner.runSingleTest("url_266");
	}
	@Test
	public void referencesTest() throws ScrapingException, MalformedURLException{
		ScrapingContext sc = new ScrapingContext(new URL("http://www.dlib.org/dlib/may08/monnich/05monnich.html"));
		DLibScraper ds = new DLibScraper();
		assertTrue(ds.scrape(sc));
		assertTrue(ds.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		assertEquals("<p align=\"left\">".trim(), reference.substring(0, 19).trim());
		assertTrue(reference.contains("DFG - Deutsche Forschungsgemeinschaft"));
	}
}
