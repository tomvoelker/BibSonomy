package org.bibsonomy.scraper.url.kde.ieee;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.junit.RemoteTestAssert;
import org.bibsonomy.scraper.url.kde.bibsonomy.BibSonomyScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * TODO: add documentation to this class
 *
 * @author Johannes
 */

@Category(RemoteTest.class)
public class IEEEXploreScraperTest {

	//StandardsScraperTest
	@Test
	public void url1TestRun(){
		
		final String url = "http://ieeexplore.ieee.org/xpl/freeabs_all.jsp?tp=&isnumber=21156&arnumber=982216&punumber=7718";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper2.class;
		final String resultFile = "IEEEXploreStandardsScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	//JournalProceedingsScraperTest
	@Test
	public void url2TestRun(){
		
		final String url = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?tp=&arnumber=6136685&contentType=Conference+Publications&searchField%3DSearch_All%26queryText%3DEnergy+efficient+hierarchical+epidemics+in+peer-to-peer+systems";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper2.class;
		final String resultFile = "IEEEXploreJournalProceedingsScraperUnitURLTest1.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	//TODO: hier muss der Titel mit dem verglichen wird, geändert werden, daniel fragen ob man das einfach machen darf
	@Test
	public void url3TestRun(){
		
		final String url = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=4536262";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper2.class;
		final String resultFile = "IEEEXploreJournalProceedingsScraperUnitURLTest2.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	//BookScraperTest
	//TODO: hier wird nichts gefunden, einfach isbn scraper übernehmen, damit funktioniert?
	@Test
	public void url4TestRun(){
		
		final String url = "http://ieeexplore.ieee.org/xpl/bkabstractplus.jsp?bkn=5263132";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper2.class;
		final String resultFile = "IEEEXploreBookScraperUnitURLTest.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	//TODO: hier muss der Titel mit dem verglichen wird, geändert werden, daniel fragen ob man das einfach machen darf
	@Test
	public void url5TestRun(){
		
		final String url = "http://ieeexplore.ieee.org/search/freesrchabstract.jsp?arnumber=5286085&isnumber=5284878&punumber=5284806&k2dockey=5286085@ieecnfs&query=%28limpens+freddy%3Cin%3Eau%29&pos=0&access=no";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper2.class;
		final String resultFile = "IEEEXploreBookScraperUnitURLTest1.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
	
	@Test
	public void url6TestRun(){
		
		final String url = "http://ieeexplore.ieee.org/search/srchabstract.jsp?arnumber=4383076&isnumber=4407525&punumber=10376&k2dockey=4383076@ieeejrns&query=%28%28hotho%29%3Cin%3Eau+%29&pos=0&access=n0";
		final String selection = null;
		final Class<? extends Scraper> scraperClass = org.bibsonomy.scraper.url.kde.ieee.IEEEXploreScraper2.class;
		final String resultFile = "IEEEXploreBookScraperUnitURLTest2.bib";
		RemoteTestAssert.assertScraperResult(url, selection, scraperClass, resultFile);
	}
}
