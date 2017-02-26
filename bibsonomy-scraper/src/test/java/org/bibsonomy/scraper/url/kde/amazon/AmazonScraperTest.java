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
package org.bibsonomy.scraper.url.kde.amazon;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for AmazonScraper
 * @author tst
 */
@Category(RemoteTest.class)
public class AmazonScraperTest {
	
	/**
	 * starts URL test with id url_42
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.amazon.com/Semantic-Primer-Cooperative-Information-Systems/dp/0262012103/ref=pd_bbs_sr_1?ie=UTF8&amp;s=books&amp;qid=1200485020&amp;sr=1-1";
		final String resultFile = "AmazonScraperUnitURLTest1.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_46
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.amazon.de/gp/product//3827415020/ref=br_fq_2";
		final String resultFile = "AmazonScraperUnitURLTest2.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_47
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://www.amazon.ca/Digital-Photography-Book-Scott-Kelby/dp/032147404X/ref=sr_1_3?ie=UTF8&s=books&qid=1201615938&sr=1-3";
		final String resultFile = "AmazonScraperUnitURLTest3.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_48
	 */
	@Test
	public void url4TestRun(){
		final String url = "http://www.amazon.fr/Mac-programmation-AppleScript-Dashboard-Core-Animation/dp/2100500767/ref=sr_1_5?ie=UTF8&s=books&qid=1201616015&r=1-5";
		final String resultFile = "AmazonScraperUnitURLTest4.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_49
	 */
	@Test
	public void url5TestRun(){
		final String url = "http://www.amazon.co.jp/Programming-Collective-Intelligence-Building-Applications/dp/0596529325/ref=sr_1_1/249-7857785-0612358?ie=UTF8&s=gateway&qid=1201616160&sr=8-1";
		final String resultFile = "AmazonScraperUnitURLTest5.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_50
	 */
	@Test
	public void url6TestRun(){
		final String url = "http://www.amazon.co.uk/Pro-2008-NET-Platform-Fourth/dp/1590598849/ref=sr_1_1?ie=UTF8&s=books&qid=1201616235&sr=1-1";
		final String resultFile = "AmazonScraperUnitURLTest6.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_57
	 */
	@Test
	public void url7TestRun(){
		final String url = "http://www.amazon.com/Text-Mining-Predictive-Unstructured-Information/dp/0387954333";
		final String resultFile = "AmazonScraperUnitURLTest7.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}

	/**
	 * starts URL test with id url_105
	 */
	@Test
	public void url8TestRun(){
		final String url = "http://www.amazon.de/Web-2-0-Unternehmenspraxis-Grundlagen-Fallstudien/dp/3486585797/ref=pd_bbs_sr_1?ie=UTF8&s=books&qid=1214381841&sr=8-1";
		final String resultFile = "AmazonScraperUnitURLTest8.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_160
	 */
	@Test
	public void url9TestRun(){
		final String url = "http://www.amazon.de/gp/product/3426274914/ref=s9_top_bw_i3/278-8327421-8514538?pf_rd_m=A3JWKAKR8XB7XF&pf_rd_s=center-4&pf_rd_r=0QDEYW5TCNM11354JY22&pf_rd_t=101&pf_rd_p=473590033&pf_rd_i=299956";
		final String resultFile = "AmazonScraperUnitURLTest9.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_161
	 */
	@Test
	public void url10TestRun(){
		final String url = "https://www.amazon.de/Sebastian-Deisler-Zur%C3%BCck-Geschichte-Fu%C3%9Fballspielers/dp/3941378287%3FSubscriptionId%3DAKIAJOMMTFWDOHFJSJXQ%26tag%3Dws%26linkCode%3Dxm2%26camp%3D2025%26creative%3D165953%26creativeASIN%3D3941378287";
		final String resultFile = "AmazonScraperUnitURLTest10.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}
	/**
	 * starts URL test with id url_272
	 */
	@Test
	public void url11TestRun(){
		final String url = "http://www.amazon.de/Computational-Intelligence-Cyber-Security-Models-ebook/dp/B00GXY5FFC/ref=sr_1_1";
		final String resultFile = "AmazonScraperUnitURLTest11.bib";
		assertScraperResult(url, null, AmazonScraper.class, resultFile);
	}
}
