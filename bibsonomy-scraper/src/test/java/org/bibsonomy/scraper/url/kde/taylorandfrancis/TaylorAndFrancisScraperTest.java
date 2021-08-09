/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
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

	@Test
	public void url1TestRun() {
		assertScraperResult("https://www.tandfonline.com/doi/abs/10.1080/09540091.2014.906388", TaylorAndFrancisScraper.class, "taylorAndFrancis/TaylorAndFrancisScraperUnitURLTest1.bib");
	}

	@Test
	public void url2TestRun() {
		assertScraperResult("https://www.tandfonline.com/doi/abs/10.1080/09540091.2011.587505", TaylorAndFrancisScraper.class, "taylorAndFrancis/TaylorAndFrancisScraperUnitURLTest2.bib");
	}

	@Test
	public void url3TestRun() {
		assertScraperResult("https://www.tandfonline.com/doi/abs/10.1080/14786419.2010.534733?url_ver=Z39.88-2003&amp;rfr_id=ori:rid:crossref.org&amp;rfr_dat=cr_pub%3dpubmed", TaylorAndFrancisScraper.class, "taylorAndFrancis/TaylorAndFrancisScraperUnitURLTest3.bib");
	}

	@Test
	public void url4TestRun() {
		assertScraperResult("https://amstat.tandfonline.com/doi/abs/10.1080/01621459.1977.10479922", TaylorAndFrancisScraper.class, "taylorAndFrancis/TaylorAndFrancisScraperUnitURLTest4.bib");
	}
	
	@Test
	public void url5TestRun() {
		assertScraperResult("https://www.tandfonline.com/doi/abs/10.2753/MIS0742-1222270205", TaylorAndFrancisScraper.class, "taylorAndFrancis/TaylorAndFrancisScraperUnitURLTest5.bib");
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
