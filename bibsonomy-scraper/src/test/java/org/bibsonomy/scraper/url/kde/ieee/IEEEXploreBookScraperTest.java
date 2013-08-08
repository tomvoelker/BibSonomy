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

package org.bibsonomy.scraper.url.kde.ieee;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #36 for IEEEXploreBookScraper
 * @author tst
 *
 *
 * Sometime tests are failing though the output and the string in the test file seems to be the same.
 * In that case, check if the scraped context has dos line endings.
 */
@Category(RemoteTest.class)
public class IEEEXploreBookScraperTest {
	
	/**
	 * starts URL test with id url_36
	 */
	@Test
	public void urlTestRun1(){
		assertTrue(UnitTestRunner.runSingleTest("url_36"));
	}
	
	/**
	 * starts URL test with id url_157
	 */
	@Test
	public void urlTestRun2(){
		assertTrue(UnitTestRunner.runSingleTest("url_157"));
	}
	
	/**
	 * starts URL test with id url_158
	 */
	@Test
	public void urlTestRun3(){
		assertTrue(UnitTestRunner.runSingleTest("url_158"));
	}
}
