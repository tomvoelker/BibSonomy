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
package org.bibsonomy.scraper.url.kde.taylorandfrancis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.taylorAndFrancis.TaylorAndFrancisScraper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * @author schwass
 */
@Category(RemoteTest.class)
public class TaylorAndFrancisScraperTest {

	/**
	 * 
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_197");
	}

	/**
	 * 
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner.runSingleTest("url_198");
	}
	/**
	 * 
	 */
	@Test
	public void url3TestRun(){
		UnitTestRunner.runSingleTest("url_241");
	}
	/**
	 * 
	 */
	@Test
	public void url4TestRun(){
		UnitTestRunner.runSingleTest("url_346");
	}
	/**
	 * @throws Exception
	 */
	@Test
	@Ignore // XXX: "you need access" :(
	public void testReferences() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.tandfonline.com/doi/abs/10.1080/14786419.2010.534733?url_ver=Z39.88-2003&amp;rfr_id=ori:rid:crossref.org&amp;rfr_dat=cr_pub%3dpubmed#.VClwLRaWeUk"));
		TaylorAndFrancisScraper tfs = new TaylorAndFrancisScraper();
		assertTrue(tfs.scrapeReferences(sc));
	
		final String reference = sc.getReferences();
		assertNotNull(reference);
		assertTrue(reference.length() > 100);
		
		assertEquals("<br /><h2>References</h2> <li id=\"CIT00".trim(), reference.substring(0, 40).trim());
		assertTrue(reference.contains("Adams, RP."));
	}
}
