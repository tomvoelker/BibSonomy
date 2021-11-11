package org.bibsonomy.scraper.url.kde.akjournals;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class AKJournalsScraperTest {
	String resultDirectory = "akjournals/";

	@Test
	public void url1Test1Run() {
		final String url = "https://akjournals.com/view/journals/11192/52/2/article-p291.xml";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest1.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}

	@Test
	public void url2Test1Run() {
		final String url = "https://akjournals.com/view/journals/606/aop/article-10.1556-606.2021.00463/article-10.1556-606.2021.00463.xml?rskey=11F5qz&result=1";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest2.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}

	@Test
	public void url3Test1Run() {
		final String url = "https://akjournals.com/view/journals/0088/70/1/article-p65.xml?rskey=ibLcF7&result=6";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest3.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}

	@Test
	public void url4Test1Run() {
		final String url = "https://akjournals.com/view/journals/726/3/2/article-p171.xml?body=contentReferences-23894";
		final String resultFile = resultDirectory + "AKJournalsScraperUnitURLTest4.bib";
		assertScraperResult(url, AKJournalsScraper.class, resultFile);
	}
}
