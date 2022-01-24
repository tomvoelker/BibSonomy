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
package org.bibsonomy.scraper.url.kde.scielo;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mohammed Abed
 */
@Category(RemoteTest.class)

public class SCIELOScraperTest {
	String resultDirectory = "scielo/";

	@Test
	public void url1TestRun(){
		final String url = "http://scielo.iics.una.py/scielo.php?script=sci_arttext&pid=S1816-89492016000200009&lng=en&nrm=iso";
		final String resultFile = resultDirectory + "SCIELOScraperUnitURLTest1.bib";
		assertScraperResult(url, SCIELOScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "http://cienciaecultura.bvs.br/scielo.php?script=sci_arttext&pid=S0009-67252021000200007&lng=pt&nrm=iso";
		final String resultFile = resultDirectory + "SCIELOScraperUnitURLTest2.bib";
		assertScraperResult(url, SCIELOScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "http://scielo.senescyt.gob.ec/scielo.php?script=sci_abstract&pid=S1390-65422017000500067&lng=es&nrm=iso&tlng=es";
		final String resultFile = resultDirectory + "SCIELOScraperUnitURLTest3.bib";
		assertScraperResult(url, SCIELOScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "http://ve.scielo.org/scielo.php?script=sci_arttext&pid=S0798-02642005000100002&lng=es&nrm=iso";
		final String resultFile = resultDirectory + "SCIELOScraperUnitURLTest4.bib";
		assertScraperResult(url, SCIELOScraper.class, resultFile);
	}
}
