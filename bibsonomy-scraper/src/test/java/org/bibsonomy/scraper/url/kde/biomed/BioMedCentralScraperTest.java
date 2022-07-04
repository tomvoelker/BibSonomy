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
package org.bibsonomy.scraper.url.kde.biomed;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests 
 * @author wbi
 *
 */
@Category(RemoteTest.class)
public class BioMedCentralScraperTest {
	String resultDirectory = "biomed/";
	
	/**
	 * starts URL test with id url_62
	 */
	@Test
	public void url1TestRun(){
		final String url = "https://bmchematol.biomedcentral.com/articles/10.1186/1471-2326-6-7";
		// http://citation-needed.springer.com/v2/references/10.1186/1471-2326-6-7?format=bibtex&flavour=citation
		final String resultFile = resultDirectory + "BioMedCentralScraperUnitURLTest1.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_343
	 */
	@Test
	public void url2TestRun(){
		// http://citation-needed.springer.com/v2/references/10.1186/2041-1480-1-S1-S6?format=bibtex&flavour=citation
		final String url = "http://jbiomedsem.biomedcentral.com/articles/10.1186/2041-1480-1-S1-S6";
		final String resultFile = resultDirectory + "BioMedCentralScraperUnitURLTest2.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}
	/**
	 * starts URL test for Cases Journal
	 */
	@Test
	public void url3TestRun(){
		final String url = "https://casesjournal.biomedcentral.com/articles/10.1186/1757-1626-2-164";
		final String resultFile = resultDirectory + "BioMedCentralScraperUnitURLTest3.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://genomebiology.biomedcentral.com/articles/10.1186/s13059-014-0424-0#Bib1";
		final String resultFile = resultDirectory + "BioMedCentralScraperUnitURLTest4.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}

	@Test
	public void url5TestRun(){
		final String url = "https://genomebiology.biomedcentral.com/articles/10.1186/s13059-014-0424-0";
		final String resultFile = resultDirectory + "BioMedCentralScraperUnitURLTest4.bib";
		assertScraperResult(url, null, BioMedCentralScraper.class, resultFile);
	}
}
