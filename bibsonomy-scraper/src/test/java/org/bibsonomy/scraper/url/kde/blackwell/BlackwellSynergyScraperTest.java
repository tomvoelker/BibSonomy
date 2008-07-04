package org.bibsonomy.scraper.url.kde.blackwell;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #51 #52 for BlackwellSynergyScraper
 * @author tst
 *
 * This scraper is offline 
 * blackwell is offline and all journals are now available under intersience.wiley.com
 * all tests removed
 * 
 * backup from UnitTestData.xml:
	
	<URLTest id="url_51">
		<TestDescription>Blackwell-Synergy scraping test (download form, single publ)</TestDescription>
		<URL>http://www.blackwell-synergy.com/action/showCitFormats?href=/toc/isj/17/4&amp;doi=10.1111/j.1365-2575.2007.00275.x</URL>
		<BibFile>BlackwellSynergyScraperUnitURLTest1.bib</BibFile>
		<Scraper>org.bibsonomy.scraper.url.kde.blackwell.BlackwellSynergyScraper</Scraper>
	</URLTest>
	<URLTest id="url_52">
		<TestDescription>Blackwell-Synergy scraping test (download page)</TestDescription>
		<URL>http://www.blackwell-synergy.com/action/downloadCitation?include=abs&amp;format=bibtex&amp;doi=10.1111/j.1365-2575.2007.00248.x</URL>
		<BibFile>BlackwellSynergyScraperUnitURLTest2.bib</BibFile>
		<Scraper>org.bibsonomy.scraper.url.kde.blackwell.BlackwellSynergyScraper</Scraper>
	</URLTest>
	<URLTest id="url_53">
		<TestDescription>Blackwell-Synergy scraping test (download form, multi publ)</TestDescription>
		<URL>http://www.blackwell-synergy.com/action/showCitFormats?href=/toc/isj/17/4&amp;doi=10.1111/j.1365-2575.2007.00275.x&amp;doi=10.1111/j.1365-2575.2007.00248.x</URL>
		<BibFile>BlackwellSynergyScraperUnitURLTest3.bib</BibFile>
		<Scraper>org.bibsonomy.scraper.url.kde.blackwell.BlackwellSynergyScraper</Scraper>
	</URLTest>

 */
public class BlackwellSynergyScraperTest {

	/*
	 * starts URL test with id url_51
	 *
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_51"));
	}

	/**
	 * starts URL test with id url_52
	 *
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_52"));
	}

	/**
	 * starts URL test with id url_53
	 *
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_53"));
	}
	*/

}