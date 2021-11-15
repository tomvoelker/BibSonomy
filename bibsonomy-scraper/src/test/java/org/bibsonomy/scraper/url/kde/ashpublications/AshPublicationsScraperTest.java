package org.bibsonomy.scraper.url.kde.ashpublications;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class AshPublicationsScraperTest {
	String resultDirectory = "ashpublications/";

	@Test
	public void url1TestRun(){
		final String url = "https://ashpublications.org/bloodadvances/article/5/21/4504/477135/Role-of-radiotherapy-to-bulky-sites-of-advanced";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest1.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://ashpublications.org/books/book/6/chapter-abstract/73149/Molecular-basis-of-hematology?redirectedFrom=fulltext";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest2.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://ashpublications.org/hematology/article/2020/1/680/474368/Sanz-GF-In-MDS-is-higher-risk-higher-reward";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest3.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://ashpublications.org/books/book/6/American-Society-of-Hematology-Self-Assessment";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest4.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

	@Test
	public void url5TestRun(){
		final String url = "https://ashpublications.org/blood/article/113/4/807/25014/Reductive-isolation-from-bone-marrow-and-blood";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest5.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

	@Test
	public void url6TestRun(){
		final String url = "https://ashpublications.org/blood/article/136/Supplement%201/1/470517/RHEX-C1ORF186-Governs-JAK2-Activation";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest6.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

	@Test
	public void url7TestRun(){
		final String url = "https://ashpublications.org/hematology/article/2013/1/1/20722/Iron-and-hepcidin-a-story-of-recycling-and-balance";
		final String resultFile = resultDirectory + "AshPublicationsScraperUnitURLTest7.bib";
		assertScraperResult(url, AshPublicationsScraper.class, resultFile);
	}

}
