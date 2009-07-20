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

package org.bibsonomy.scraper.url.kde.citeseer;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #112 for CiteseerxScraperTest
 * @author tst
 *
 */
public class CiteseerxScraperTest {
	
	/**
	 * starts URL test with id url_112
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_112"));
	}
	
	@Test
	@Ignore
	public void test1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.14.7185";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		final CiteseerxScraper scraper = new CiteseerxScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
			Assert.assertTrue(scrape);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
		
		
	}
	
	@Test
	@Ignore
	public void testTemporaryMalformed() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary10.1.1.14.7185&description=Conceptual+Clustering+of+Text+Clusters";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		final CiteseerxScraper scraper = new CiteseerxScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
			Assert.assertTrue(scrape);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	@Ignore
	public void runTest1() throws MalformedURLException {
		String url = "http://citeseerx.ist.psu.edu/viewdoc/summary;jsessionid=352C9BD0F67928E2EDAFA8B58ACFBFB9?doi=10.1.1.110.903";
		final CiteseerxScraper scraper = new CiteseerxScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
