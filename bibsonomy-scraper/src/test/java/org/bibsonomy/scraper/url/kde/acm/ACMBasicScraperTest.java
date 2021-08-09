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
package org.bibsonomy.scraper.url.kde.acm;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * scraper tests for {@link ACMBasicScraper}
 *
 * @author tst
 */
@Category(RemoteTest.class)
public class ACMBasicScraperTest {
	
	/**
	 * starts URL test with id url_1
	 */
	@Test
	public void urlTestRun1(){
		final String url = "https://dl.acm.org/doi/10.1145/1015330.1015428";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest1.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}

	/**
	 * tests the scraper for an article
	 */
	@Test
	public void testArticleScraping() {
		final String url = "https://dl.acm.org/doi/10.1137/S009753979528175X";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest2.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_153
	 */
	@Test
	public void urlTestRun3(){
		final String url = "https://dl.acm.org/doi/10.1145/1105664.1105676";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest3.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_155
	 */
	@Test
	public void urlTestRun4(){
		final String url = "https://dl.acm.org/doi/book/10.5555/553876";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest4.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_184
	 */
	@Test
	public void urlTestRun5(){
		final String url = "https://dl.acm.org/doi/10.1145/359842.359859";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest5.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	
	@Test
	public void urlTestRun6(){
		final String url = "https://dl.acm.org/doi/10.1145/1082036.1082037";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest6.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	/**
	 * 
	 */
	@Test
	public void urlTestRun7(){
		final String url = "https://dl.acm.org/doi/10.1145/1571941.1571977";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest7.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	
	/**
	 * CACM
	 */
	@Test
	public void urlTestRun9(){
		final String url = "https://cacm.acm.org/magazines/2015/8/189841-understanding-the-u-s-domestic-computer-science-phd-pipeline/fulltext";
		final String resultFile = "acm/ACMBasicScraperUnitURLTest9.bib";
		assertScraperResult(url, ACMBasicScraper.class, resultFile);
	}
	
	@Test
	@Ignore // FIXME: update cited by scraper
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dl.acm.org/citation.cfm?doid=1105664.1105676"));
		
		ACMBasicScraper acm = new ACMBasicScraper();
		
		assertTrue(acm.scrapeCitedby(sc));
		
		final String cby = sc.getCitedBy();
		
		assertNotNull(cby);
		
		assertTrue(cby.length() > 100);
		
		assertEquals("<div style=\"margin-left:10px; margin-top:0px; margin-right:10px; margin-bottom: 10px;".trim(), cby.substring(0, 86).trim());
		
		assertTrue(cby.contains("Margaret-Anne Storey"));
	}

	@Test
	@Ignore // TODO: update reference scraper
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dl.acm.org/citation.cfm?doid=1105664.1105676"));
		
		ACMBasicScraper acm = new ACMBasicScraper();
		
		assertTrue(acm.scrapeReferences(sc));
		
		final String reference = sc.getReferences();
		
		assertNotNull(reference);
		
		assertTrue(reference.length() > 100);
		
		assertEquals("<div style=\"margin-left:10px; margin-top:0px; margin-right:10px; margin-bottom: 10px;".trim(), reference.substring(0, 86).trim());
		
		assertTrue(reference.contains("David Abrams"));
	}
}
