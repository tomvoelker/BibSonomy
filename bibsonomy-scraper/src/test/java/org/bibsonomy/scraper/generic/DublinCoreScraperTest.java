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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.biorxiv.BioRxivScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Lukas
 */
@Category(RemoteTest.class)
public class DublinCoreScraperTest {

	@Test
	public void testDCScraper() {
		final String url = "http://www.repo.uni-hannover.de/handle/123456789/287";
		final String resultFile = "DCScraperTest.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);
	}
	
	@Test
	public void testDCScraper2() {
		final String url = "http://www.phcogres.com/article.asp?issn=0974-8490;year=2009;volume=1;issue=4;spage=172;epage=174;aulast=Shuge;t=6";
		final String resultFile = "DCScraperTest2.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);
	}
	@Test
	public void testDCScraper3() {
		final String url = "http://www.tara.tcd.ie/handle/2262/13178?mode=full&amp;submit_simple=Show+full+item+record";
		final String resultFile = "DCScraperTest3.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);
	}
	@Test
	public void testDCScraper4() {
		final String url = "http://www.scirp.org/journal/PaperInformation.aspx?PaperID=37807";
		final String resultFile = "DCScraperTest4.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);	
	}
	@Test
	public void testDCScraper5() {
		final String url = "http://www.uel.br/revistas/uel/index.php/informacao/article/view/19996";
		final String resultFile = "DCScraperTest5.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);	
	}
	
	@Test
	public void testDCScraper6() {
		final String url = "http://firstmonday.org/ojs/index.php/fm/article/view/7414/6096";
		final String resultFile = "DCScraperTest6.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);	
	}
	
	@Test
	public void testDCScraper7() {
		final String url = "http://www.vtei.cz/2017/06/aktualni-stav-problematiky-ochrannych-pasem-vodnich-zdroju/";
		final String resultFile = "DCScraperTest7.bib";
		assertScraperResult(url, null, DublinCoreScraper.class, resultFile);	
	}
	
}
