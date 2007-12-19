package scraper.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import scraper.url.kde.springer.SpringerLinkScraper;
import scraper.url.kde.wiley.intersience.WileyIntersienceScraper;
import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

/**
 * Builds a bibtex file with scraped content
 * @author tst
 *
 */
public class BibtexFileBuilder {
	
	private static final String bibFile = "src/test/resources/scraper/data/publication.bib"; 
	/**
	 * main
	 * @param args
	 * @throws IOException 
	 * @throws ScrapingException 
	 */
	public static void main(String[] args) throws IOException, ScrapingException {
		Scraper scraper = new WileyIntersienceScraper();
		String url = "http://www3.interscience.wiley.com/tools/citex?clienttype=1&subtype=1&mode=1&version=1&id=104530788&redirect=/cgi-bin/abstract/104530788/ABSTRACT";
		
		ScrapingContext context = new ScrapingContext(new URL(url));
		scraper.scrape(context);
		
		FileWriter file = new FileWriter(new File(bibFile));
		StringReader publ = new StringReader(context.getBibtexResult());
		
		int next;
		while((next=publ.read()) >= 0){
			file.write(next);
		}
		
		file.flush();
		file.close();
		publ.close();
	}
}
