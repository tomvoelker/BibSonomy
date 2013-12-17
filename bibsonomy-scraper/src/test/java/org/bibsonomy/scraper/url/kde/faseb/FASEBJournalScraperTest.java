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

package org.bibsonomy.scraper.url.kde.faseb;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author wla
  */
@Category(RemoteTest.class)
public class FASEBJournalScraperTest {

	/**
	 * starts URL test with id url_224
	 */
	@Test
	public void urlTestRun1() {
		assertTrue(UnitTestRunner.runSingleTest("url_224"));
	}
	
	/**
	 * starts URL test with id url_225
	 */
	@Test
	public void urlTestRun2() {
		assertTrue(UnitTestRunner.runSingleTest("url_225"));
	}
	
	/**
	 * starts URL test with id url_226
	 */
	@Test
	public void urlTestRun3() {
		assertTrue(UnitTestRunner.runSingleTest("url_226"));
	}
	
	/**
	 * starts URL test with id url_227
	 */
	@Test
	public void urlTestRun4() {
		assertTrue(UnitTestRunner.runSingleTest("url_227"));
	}

}
