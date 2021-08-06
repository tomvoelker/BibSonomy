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
package org.bibsonomy.scraper.url.kde.openrepository;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #117, #118, #199, #120 for OpenrepositoryScraper
 * 
 * @author tst
 */
@Category(RemoteTest.class)
public class OpenrepositoryScraperTest {
	
	/**
	 * starts URL test with id url_117
	 */
	@Test
	public void url1TestRun(){
		assertScraperResult("http://e-space.mmu.ac.uk/e-space/handle/2173/31869", OpenrepositoryScraper.class, "OpenrepositoryScraperUnitURLTest1.bib");
	}
	
	/**
	 * starts URL test with id url_118
	 */
	@Test
	public void url2TestRun(){
		assertScraperResult("http://hirsla.lsh.is/lsh/handle/2336/11284", OpenrepositoryScraper.class, "OpenrepositoryScraperUnitURLTest2.bib");
	}
	
	/**
	 * starts URL test with id url_119
	 */
	@Test
	public void url3TestRun(){
		assertScraperResult("http://arrts.gtcni.org.uk/gtcni/handle/2428/8327", OpenrepositoryScraper.class, "OpenrepositoryScraperUnitURLTest3.bib");
	}
	
	/**
	 * starts URL test with id url_120
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("http://eric.exeter.ac.uk/exeter/handle/10036/18938", OpenrepositoryScraper.class, "OpenrepositoryScraperUnitURLTest4.bib");
	}

}
