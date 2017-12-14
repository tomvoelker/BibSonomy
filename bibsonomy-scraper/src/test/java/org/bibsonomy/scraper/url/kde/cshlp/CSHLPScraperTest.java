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
package org.bibsonomy.scraper.url.kde.cshlp;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 * url_292, url_293, url_294, url_295 for the host: cshperspectives.cshlp.org
 * url_300, url_301, url_302, url_303 for the host: jbc.org
 * url_304, url_305, url_306, url_307 for the host: cancerres.aacrjournals.org
 * url_308, url_309, url_310, url_311 for the host: jimmunol.org
 */
@Category(RemoteTest.class)
public class CSHLPScraperTest {

	/**
	 * starts URL test with id url_292
	 */
	@Test
	public void url1TestRun() {
		final String url = "http://cshperspectives.cshlp.org/content/3/3/a004994.short";
		final String resultFile = "CSHLPScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_293
	 */
	@Test
	public void url2TestRun() {
		final String url = "http://cshperspectives.cshlp.org/content/3/3/a004994.abstract";
		final String resultFile = "CSHLPScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_294
	 */
	@Test
	public void url3TestRun() {
		final String url = "http://cshperspectives.cshlp.org/content/3/3/a004994.full.pdf+html";
		final String resultFile = "CSHLPScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_295
	 */
	@Test
	public void url4TestRun() {
		final String url = "http://cshperspectives.cshlp.org/content/3/3/a004994.full";
		final String resultFile = "CSHLPScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_300
	 */
	@Test
	public void url5TestRun() {
		final String url = "http://www.jbc.org/content/288/10/6904.abstract";
		final String resultFile = "CSHLPScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_301
	 */
	@Test
	public void url6TestRun() {
		final String url = "http://www.jbc.org/content/288/10/6904.short";
		final String resultFile = "CSHLPScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_302
	 */
	@Test
	public void url7TestRun() {
		final String url = "http://www.jbc.org/content/288/10/6904.full";
		final String resultFile = "CSHLPScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_303
	 */
	@Test
	public void url8TestRun() {
		final String url = "http://www.jbc.org/content/288/10/6904.full.pdf+html";
		final String resultFile = "CSHLPScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_304
	 */
	@Test
	public void url9TestRun() {
		final String url = "http://cancerres.aacrjournals.org/content/65/13/5628.abstract";
		final String resultFile = "CSHLPScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_305
	 */
	@Test
	public void url10TestRun() {
		final String url = "http://cancerres.aacrjournals.org/content/65/13/5628.short";
		final String resultFile = "CSHLPScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_306
	 */
	@Test
	public void url11TestRun() {
		final String url = "http://cancerres.aacrjournals.org/content/65/13/5628.full";
		final String resultFile = "CSHLPScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_307
	 */
	@Test
	public void url12TestRun() {
		final String url = "http://cancerres.aacrjournals.org/content/65/13/5628.full.pdf+html";
		final String resultFile = "CSHLPScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_308
	 */
	@Test
	public void url13TestRun() {
		final String url = "http://www.jimmunol.org/content/183/11/7569.abstract";
		final String resultFile = "CSHLPScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_309
	 */
	@Test
	public void url14TestRun() {
		final String url = "http://www.jimmunol.org/content/183/11/7569.short";
		final String resultFile = "CSHLPScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_310
	 */
	@Test
	public void url15TestRun() {
		final String url = "http://www.jimmunol.org/content/183/11/7569.full";
		final String resultFile = "CSHLPScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_311
	 */
	@Test
	public void url16TestRun() {
		final String url = "http://www.jimmunol.org/content/183/11/7569.full.pdf+html";
		final String resultFile = "CSHLPScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CSHLPScraper.class, resultFile);
	}
}