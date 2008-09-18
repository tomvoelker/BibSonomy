package org.bibsonomy.scraper.url.kde.citeseer;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;


public class CiteseerBasicScraper implements Scraper {
	
	private static final String info = "CiteSeer Scraper: This scraper parses a publication page from the " +
									   "Scientific Literature Digital Library (<a href=\"http://citeseer.ist.psu.edu/\">CiteSeer</a>) " +
	   								   "and extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String  CS_HOST_NAME   = "citeseer.ist.psu.edu";
	private static final String  CS_BIB_PATTERN = ".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*";

	public boolean scrape(ScrapingContext sc) throws ScrapingException{
		if (sc.getUrl() != null && sc.getUrl().getHost().equals(CS_HOST_NAME)){
			sc.setScraper(this);
			
			Pattern p = Pattern.compile(CS_BIB_PATTERN, Pattern.MULTILINE | Pattern.DOTALL);
			Matcher m = p.matcher(sc.getPageContent());	
			if (m.matches()) {
				sc.setBibtexResult(m.group(1));
				return true;
			}else
				throw new PageNotSupportedException("no bibtex snippet available");

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
