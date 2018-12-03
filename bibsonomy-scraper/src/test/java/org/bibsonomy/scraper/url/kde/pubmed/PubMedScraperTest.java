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
package org.bibsonomy.scraper.url.kde.pubmed;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #27 #35 #91 for PubMedScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class PubMedScraperTest {
	
	/**
	 * starts URL test with id url_27
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.ncbi.nlm.nih.gov/sites/entrez?Db=pubmed&Cmd=ShowDetailView&TermToSearch=17623893&ordinalpos=1&itool=EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_RVDocSum";
		final String resultFile = "PubMedScraperUnitURLTest1.bib";
		assertScraperResult(url, null, PubMedScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_35
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=PubMed&id=17623893&mode=medline";
		final String resultFile = "PubMedScraperUnitURLTest2.bib";
		assertScraperResult(url, null, PubMedScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_91
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://www.ncbi.nlm.nih.gov/pubmed/18506939?ordinalpos=2&itool=EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_RVDocSum";
		final String resultFile = "PubMedScraperUnitURLTest3.bib";
		assertScraperResult(url, null, PubMedScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_176
	 */
	@Test
	public void url4TestRun(){
		final String url = "http://ukpmc.ac.uk/abstract/MED/19426458";
		final String resultFile = "PubMedScraperUnitURLTest4.bib";
		assertScraperResult(url, null, PubMedScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_246
	 */
	@Test
	public void url5TestRun(){
		final String url = "http://europepmc.org/abstract/MED/19426458";
		final String resultFile = "PubMedScraperUnitURLTest5.bib";
		assertScraperResult(url, null, PubMedScraper.class, resultFile);
	}

}
