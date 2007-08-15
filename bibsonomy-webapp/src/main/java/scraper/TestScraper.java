package scraper;

import java.net.MalformedURLException;
import java.net.URL;

import scraper.url.kde.acm.ACMBasicScraper;

public class TestScraper {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws ScrapingException 
	 */
	public static void main(String[] args) throws MalformedURLException, ScrapingException {
		URL url = new URL(args[0]);
		
		ScrapingContext sc = new ScrapingContext(url);
		
		Scraper scraper = new ACMBasicScraper();
		
		if (scraper.scrape(sc)) {
			System.out.println(sc.getBibtexResult());
		}
	}

}
