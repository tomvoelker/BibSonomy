package scraper.url.kde.l3s;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

public class L3SScraper implements Scraper {
	private static final Logger log 	= Logger.getLogger(L3SScraper.class);
	private static final String info 	= "arXiv Scraper: This scraper parses a publication page from <a href=\"http://www.l3s.de/\">L3S</a> and " +
	   									  "extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String L3S_URL = "l3s.de";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		//-- url shouldn't be null
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(L3S_URL)) {
			try {
				String bibtexresult = null;
				
				//create the regex pattern to indicate if the content is bibtex or not 
				Pattern p = Pattern.compile("@\\w+\\{.+,");
				Matcher m = p.matcher(sc.getPageContent());
				
				//if its a bibtex entry then extract it
				if (m.find()){
					bibtexresult = sc.getPageContent();
				}

				
				//-- bibtex string may not be empty
				if (bibtexresult != null && !"".equals(bibtexresult)) {
					sc.setBibtexResult(bibtexresult);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);
	
					return true;
				}
			} catch (Exception e) {
				log.fatal("could not scrape L3S publication " + sc.getUrl().toString());
				log.fatal(e);
				throw new ScrapingException(e);
			}
		}
		

		return false;
	}

	public String getInfo() {
		return info;
	}
			
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
}