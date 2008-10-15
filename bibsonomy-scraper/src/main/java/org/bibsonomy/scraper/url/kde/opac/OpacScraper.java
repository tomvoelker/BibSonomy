package org.bibsonomy.scraper.url.kde.opac;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.PicaToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class OpacScraper implements Scraper {
	private static final String info = "OPAC Scraper: This scraper parses a publication page from <a href=\"http://opac.bibliothek.uni-kassel.de/\">Bibliothek Kassel</a>  " +
	"and other OPAC sites. It extracts the adequate BibTeX entry from the OPAC. Author: KDE";
	
	private static final Logger log = Logger.getLogger(OpacScraper.class);
	private static final String OPAC_URL ="http://opac.???/";
	
	
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		//log.fatal("Opac");
        //log.fatal(sc.getUrl().toString());
		//Pattern.matches("^http.*?+/CHARSET=UTF-8/PRS=PP/PPN\\?PPN=[0-9X]+$", sc.getUrl().toString())
		//sc.getUrl().toString().startsWith(OPAC_URL)
		if (sc.getUrl() != null && (Pattern.matches("^http.*?/CHARSET=UTF-8/PRS=PP/PPN\\?PPN=[0-9X]+$", sc.getUrl().toString()))){
			sc.setScraper(this);
			
			String bibResult = null;
			PicaToBibtexConverter converter = null;
			
			try {
				// create a converter and start converting :)
				converter = new PicaToBibtexConverter(sc.getPageContent(), "xml", sc.getUrl().toString());

				bibResult = converter.getBibResult();

				if(bibResult != null){
					sc.setBibtexResult(bibResult);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			} catch (Exception e){
				throw new InternalFailureException(e);
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
