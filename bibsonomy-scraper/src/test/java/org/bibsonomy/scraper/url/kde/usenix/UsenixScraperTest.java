/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.url.kde.usenix;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #74 #75 #76 #79 #80 #81 #82 #83 #84 #85 for UsenixScraper
 * @author tst
 */
@Ignore
public class UsenixScraperTest {

	/**
	 * starts URL test with id url_74
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_74"));
	}

	/**
	 * starts URL test with id url_75
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_75"));
	}
	
	/**
	 * starts URL test with id url_76
	 */
	@Test
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_76"));
	}

	/**
	 * starts URL test with id url_79
	 */
	@Test
	public void url4TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_79"));
	}

	/**
	 * starts URL test with id url_80
	 */
	@Test
	public void url5TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_80"));
	}
	
	/**
	 * starts URL test with id url_81
	 */
	@Test
	public void url6TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_81"));
	}
	
	/**
	 * starts URL test with id url_82
	 */
	@Test
	public void url7TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_82"));
	}

	/**
	 * starts URL test with id url_83
	 */
	@Test
	public void url8TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_83"));
	}

	/**
	 * starts URL test with id url_84
	 */
	@Test
	public void url9TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_84"));
	}

	/**
	 * starts URL test with id url_85
	 */
	@Test
	public void url10TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_85"));
	}
	
}
