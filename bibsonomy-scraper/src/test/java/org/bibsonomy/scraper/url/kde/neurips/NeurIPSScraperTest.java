package org.bibsonomy.scraper.url.kde.neurips;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for NeurIPSScraper
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class NeurIPSScraperTest {

	/**
	 * test for abstract page
	 */
	@Test
	public void urlTestRun1(){
		final String url = "https://proceedings.neurips.cc/paper/2016/hash/90e1357833654983612fb05e3ec9148c-Abstract.html";
		final String resultFile = "NeurIPSScraperUnitURLTest1.bib";
		assertScraperResult(url, null, NeurIPSScraper.class, resultFile);
	}
	
	/**
	 * same paper but for reviews page
	 */
	@Test
	public void urlTestRun2(){
		final String url = "https://proceedings.neurips.cc/paper/2016/file/90e1357833654983612fb05e3ec9148c-Reviews.html";
		final String resultFile = "NeurIPSScraperUnitURLTest1.bib";
		assertScraperResult(url, null, NeurIPSScraper.class, resultFile);
	}
}
