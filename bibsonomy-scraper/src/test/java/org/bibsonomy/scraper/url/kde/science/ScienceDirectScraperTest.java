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

package org.bibsonomy.scraper.url.kde.science;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #28 #141 #159 #162 for ScienceDirectScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class ScienceDirectScraperTest {

	/**
	 * starts URL test with id url_28
	 */
	@Test
	public void urlTestRun1(){
		assertTrue(UnitTestRunner.runSingleTest("url_28"));
	}

	/**
	 * starts URL test with id url_141
	 */
	@Test
	public void urlTestRun2(){
		assertTrue(UnitTestRunner.runSingleTest("url_141"));
	}

	/**
	 * starts URL test with id url_159
	 */
	@Test
	public void url3TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_159"));
	}
	
	/**
	 * starts URL test with id url_162
	 */
	@Test
	public void url4TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_162"));
	}
	
	/**
	 * starts URL test with id url_179
	 */
	@Test
	public void url5TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_179"));
	}
	
	/**
	 * starts URL test with id url_185
	 */
	@Test
	public void url6TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_185"));
	}

}
