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

package org.bibsonomy.scraper.url.kde.amazon;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #42, #46, #47, #48, #49, #50, #57, #105, #160, #161 for AmazonScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class AmazonScraperTest {
	
	/**
	 * starts URL test with id url_42
	 */
	@Test
	public void url1TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_42"));
	}
	
	/**
	 * starts URL test with id url_46
	 */
	@Test
	public void url2TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_46"));
	}

	/**
	 * starts URL test with id url_47
	 */
	@Test
	public void url3TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_47"));
	}

	/**
	 * starts URL test with id url_48
	 */
	@Test
	public void url4TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_48"));
	}

	/**
	 * starts URL test with id url_49
	 */
	@Test
	public void url5TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_49"));
	}

	/**
	 * starts URL test with id url_50
	 */
	@Test
	public void url6TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_50"));
	}
	
	/**
	 * starts URL test with id url_57
	 */
	@Test
	public void url7TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_57"));
	}

	/**
	 * starts URL test with id url_105
	 */
	@Test
	public void url8TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_105"));
	}
	
	/**
	 * starts URL test with id url_160
	 */
	@Test
	public void url9TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_160"));
	}
	
	/**
	 * starts URL test with id url_161
	 */
	@Test
	public void url10TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_161"));
	}
	/**
	 * starts URL test with id url_272
	 */
	@Test
	public void url11TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_272"));
	}
}
