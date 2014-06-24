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

package org.bibsonomy.scraper.url.kde.aip;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #8 #9 for AipScitationScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class AipScitationScraperTest {
	
	/**
	 * starts URL test with id url_8
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_8");
	}

	/**
	 * starts URL test with id url_9
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_9");
	}
	
	/**
	 * starts URL test with id url_174
	 */
	@Test
	public void url3TestRun(){
		UnitTestRunner.runSingleTest("url_174");
	}

}
