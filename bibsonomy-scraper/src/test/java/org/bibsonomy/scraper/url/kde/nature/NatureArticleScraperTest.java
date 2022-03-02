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
package org.bibsonomy.scraper.url.kde.nature;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for NatureArticleScraper
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class NatureArticleScraperTest {
	String resultDirectory = "nature/article/";

	/**
	 * 
	 */
	@Test
	public void urlTest1Run(){
		final String url = "http://www.nature.com/articles/ncomms14674";
		final String resultFile = resultDirectory + "NatureArticleScraperUnitURLTest1.bib";
		assertScraperResult(url, NatureArticleScraper.class, resultFile);
	}
	
	/**
	 * 
	 */
	@Test
	public void urlTest2Run(){
		final String url = "http://www.nature.com/articles/nenergy201741";
		// generated: https://citation-needed.springer.com/v2/references/10.1038/nenergy201741?format=bibtex&flavour=citation
		// real:      https://citation-needed.springer.com/v2/references/10.1038/nenergy.2017.41?format=bibtex&flavour=citation
		// they are driving us crazy!
		final String resultFile = resultDirectory + "NatureArticleScraperUnitURLTest2.bib";
		assertScraperResult(url, NatureArticleScraper.class, resultFile);
	}
	
	/**
	 * 
	 */
	@Test
	public void urlTest3Run(){
		final String url = "https://www.nature.com/articles/nnano.2014.53?cacheBust=1508217952140";
		final String resultFile = resultDirectory + "NatureArticleScraperUnitURLTest3.bib";
		assertScraperResult(url, NatureArticleScraper.class, resultFile);
	}
	
	/**
	 * migrated from former NatureJournalScraperTest
	 */
	@Test
	public void urlTest4Run(){
		final String url = "https://www.nature.com/articles/nrn.2016.150";
		final String resultFile = resultDirectory + "NatureArticleScraperUnitURLTest4.bib";
		assertScraperResult(url, NatureArticleScraper.class, resultFile);
	}
	
	/**
	 * migrated from former NatureJournalScraperTest
	 */
	@Test
	public void urlTest5Run(){
		final String url = "https://www.nature.com/articles/onc2014416";
		final String resultFile = resultDirectory + "NatureArticleScraperUnitURLTest5.bib";
		assertScraperResult(url, NatureArticleScraper.class, resultFile);
	}
	
}
