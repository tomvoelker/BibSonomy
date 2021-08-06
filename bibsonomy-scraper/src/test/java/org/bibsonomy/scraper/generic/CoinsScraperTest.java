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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #130 for CoinsScraper
 * 
 * @author tst
 */
@Category(RemoteTest.class)
public class CoinsScraperTest {
	
	/**
	 * starts URL test with id url_130
	 */
	@Ignore
	@Test
	public void url1TestRun(){
		assertScraperResult("http://www.westmidlandbirdclub.com/bibliography/NBotWM.htm", CoinsScraper.class, "CoinsScraperUnitURLTest1.bib");
	}
	
	/**
	 * starts URL test with id url_299
	 * this site does not exist 404 Not Found
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		assertScraperResult("http://revista.ibict.br/ciinf/index.php/ciinf/article/view/2262/1879", CoinsScraper.class, "CoinsScraperUnitURLTest5.bib");
	}
	
	/**
	 * starts URL test with id url_333
	 */
	@Test
	public void url4TestRun(){
		assertScraperResult("http://katalogplus.ub.uni-bielefeld.de/cgi-bin/new_titel.cgi?katkey=2014704&art=f&kat1=freitext&kat2=ti&kat3=aup&op1=AND&op2=AND&var1=clustering&var2=&var3=&vr=1&pagesize=10&sprache=GER&bestand=lok&sess=4f5e41e8d8e96721f5500fd05eed192a", CoinsScraper.class, "CoinsScraperUnitURLTest3.bib");
	}
}
