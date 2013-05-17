package org.bibsonomy.scraper.url.kde.sage;

import java.io.IOException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author Haile
 * @version $Id$
 */
public class mainSage extends SageJournalScraper{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ScrapingException 
	 */
	public static void main(String[] args) throws IOException, ScrapingException {
		// TODO Auto-generated method stub
		SageJournalScraper sjs = new SageJournalScraper();
		ScrapingContext sc = new ScrapingContext(new URL("http://cdp.sagepub.com/content/12/4/105.short"));
		
		System.out.println(sjs.scrape(sc));
		System.out.println(sc.getBibtexResult());
		

	}

}
