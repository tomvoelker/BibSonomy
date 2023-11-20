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
package org.bibsonomy.scraper.url.kde.ams;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #121 #122 for AmsScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class AmsScraperTest {
	String resultDirectory = "ams/";
	
	/**
	 * starts URL test with id url_121
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://journals.ametsoc.org/view/journals/bams/89/6/2008bams2375_1.xml";
		final String resultFile = resultDirectory + "AmsScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AmsScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://journals.ametsoc.org/view/journals/clim/34/23/JCLI-D-21-0071.1.xml?rskey=ElJFzO&result=8";
		final String resultFile = resultDirectory + "AmsScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AmsScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://journals.ametsoc.org/view/journals/atot/aop/JTECH-D-20-0160.1/JTECH-D-20-0160.1.xml?tab_body=abstract-display";
		final String resultFile = resultDirectory + "AmsScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AmsScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://journals.ametsoc.org/view/journals/atot/aop/JTECH-D-21-0071.1/JTECH-D-21-0071.1.xml";
		final String resultFile = resultDirectory + "AmsScraperUnitURLTest4.bib";
		assertScraperResult(url, null, AmsScraper.class, resultFile);
	}

	@Ignore
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://journals.ametsoc.org/view/journals/bams/89/6/2008bams2375_1.xml"));
		AmsScraper as = new AmsScraper();
		assertTrue(as.scrape(sc));
		assertTrue(as.scrapeCitedby(sc));
		
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertEquals("<div class=\"citedByEntry\"><span class=\"author\">John D. Horel</span>, <span class=\"author\">Donna Ziegenfuss</span>".trim(), cby.substring(0, 113).trim());
		assertTrue(cby.contains("Lodovica Illari"));
	}

	@Ignore
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("https://journals.ametsoc.org/view/journals/bams/89/6/2008bams2375_1.xml"));
		AmsScraper as = new AmsScraper();
		assertTrue(as.scrape(sc));
		assertTrue(as.scrapeReferences(sc));
		
		final String references = sc.getReferences();
		assertNotNull(references);
		assertTrue(references.length() > 100);
		assertEquals("<tr><td class=\"refnumber\" id=\"bib1\"> </td><td valign=\"top\">Ahmed<span class=\"NLM_x\">".trim(), references.substring(0, 84).trim());
		assertTrue(references.contains("Wang"));
	}
}
