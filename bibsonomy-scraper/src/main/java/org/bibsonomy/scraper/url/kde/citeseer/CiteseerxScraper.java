package org.bibsonomy.scraper.url.kde.citeseer;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author tst
 * @version $Id$
 */
public class CiteseerxScraper implements Scraper {
	
	private static final String INFO = "SCraper for bibtex snippets from citeseerx.ist.psu.edu";
	
	private static final String HOST = "citeseerx.ist.psu.edu";
	
	private static final String REGEX = "</h2>\\s*(@.*)\\s*</div>";

	public String getInfo() {
		return INFO;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			
			// check for selected bibtex snippet
			if(sc.getSelectedText() != null){
				sc.setBibtexResult(sc.getSelectedText());
				sc.setScraper(this);
				return true;
			}
			
			// no snippet selected
			String page = sc.getPageContent();
			
			// search snippet in html
			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(page);
			
			if(matcher.find()){
				String bibtex = matcher.group(1);
				
				// remove HTML entities
				bibtex = bibtex.replace("<br/>", "\n");
				bibtex = bibtex.replace("&nbsp;", " ");
				
				sc.setBibtexResult(bibtex);
				sc.setScraper(this);
				return true;

			}else
				throw new PageNotSupportedException("no bibtex snippet available");
		}
		return false;
	}

}
