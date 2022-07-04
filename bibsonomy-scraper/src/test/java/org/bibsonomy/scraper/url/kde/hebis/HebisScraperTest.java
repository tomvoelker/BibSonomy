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
package org.bibsonomy.scraper.url.kde.hebis;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class HebisScraperTest {
	String resultDirectory = "hebis/";

	@Test
	public void url1TestRun(){
		final String url = "https://hds.hebis.de/ubks/Record/HEB211723959";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest1.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url2TestRun(){
		final String url = "https://hds.hebis.de/ubffm/Record/HEB204380383";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest2.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	// returned Bibtex is wrong. unclosed {
	@Ignore
	@Test
	public void url3TestRun(){
		final String url = "https://hds.hebis.de/ubgi/Record/HEB082064768";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest3.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://hds.hebis.de/fuas/Record/HEB450874737";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest4.bib";

		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url5TestRun(){
		final String url = "https://hds.hebis.de/hda/Record/HEB459141716";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest5.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url6TestRun(){
		final String url = "https://hds.hebis.de/thm/Record/HEB129109622";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest6.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url7TestRun(){
		final String url = "https://hds.hebis.de/hlbfu/Record/HEB481037438";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest7.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url8TestRun(){
		final String url = "https://hds.hebis.de/hsrm/Record/HEB309934826";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest8.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url9TestRun(){
		final String url = "https://hds.hebis.de/ubmr/Record/HEB364738537";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest9.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url10TestRun(){
		final String url = "https://hds.hebis.de/ubmz/Record/HEB336227183";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest10.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url11TestRun(){
		final String url = "https://hds.hebis.de/ulbda/Record/HEB463330811";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest11.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url12TestRun(){
		final String url = "https://hds.hebis.de/herder/Record/HEB22504210X";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest12.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url13TestRun(){
		final String url = "https://hds.hebis.de/asmr/Record/HEB454060610";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest13.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}
	@Test
	public void url14TestRun(){
		final String url = "https://hds.hebis.de/bbl/Record/HEB047331194";
		final String resultFile = resultDirectory + "HebisScraperUnitURLTest14.bib";
		assertScraperResult(url, null, HebisScraper.class, resultFile);
	}



}
