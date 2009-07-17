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

package org.bibsonomy.scraper.url.kde.acm;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #1 #134 #153 for ACMBasicSCraper  
 * @author tst
 *
 */
public class ACMBasicScraperTest {
	
	/**
	 * starts URL test with id url_1
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_1"));
	}

	/**
	 * starts URL test with id url_134
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_134"));
	}
	
	/**
	 * starts URL test with id url_153
	 */
	@Test
	@Ignore
	public void urlTestRun3(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_153"));
	}
	
	@Test
	@Ignore
	public void test2() throws MalformedURLException {
		
		String url = "http://portal.acm.org/citation.cfm?id=500737.500755"; // abstract works
		url = "http://portal.acm.org/citation.cfm?id=1364171"; // abstract missing
		final ACMBasicScraper acm = new ACMBasicScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			acm.scrape(sc);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
