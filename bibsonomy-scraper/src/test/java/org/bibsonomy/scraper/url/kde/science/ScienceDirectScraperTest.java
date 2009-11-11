/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #28 for ScienceDirectScraper
 * @author tst
 *
 */
public class ScienceDirectScraperTest {

	/**
	 * starts URL test with id url_28
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_28"));
	}

	/**
	 * starts URL test with id url_141
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_141"));
	}

	/**
	 * starts URL test with id url_159
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_159"));
	}
	
	@Test
	public void testCleanBibtex() {
		final ScienceDirectScraper s = new ScienceDirectScraper();

		final String in = "@article{jaeschke2008tag,\n" + 
		"title = {Tag Recos},\n" +
		"pages = \"56 - 70\",\n" + 
		"year  = 2008\n" +
		"}";

		final String expected = "@article{jaeschke2008tag,\n" + 
		"title = {Tag Recos},\n" +
		"pages = \"56--70\",\n" +
		"year  = 2008\n" +
		"}";

		final String out = s.cleanBibTeX(in);

		Assert.assertEquals(expected, out);
	}

}
