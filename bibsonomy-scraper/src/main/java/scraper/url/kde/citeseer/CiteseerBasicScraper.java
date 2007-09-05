package scraper.url.kde.citeseer;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

public class CiteseerBasicScraper implements Scraper {
	
	private static final String info = "CiteSeer Scraper: This scraper parses a publication page from the " +
									   "Scientific Literature Digital Library (<a href=\"http://citeseer.ist.psu.edu/\">CiteSeer</a>) " +
	   								   "and extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String  CS_HOST_NAME   = "http://citeseer.ist.psu.edu/";
	private static final String  CS_BIB_PATTERN = ".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*";

	public boolean validForUrl(URL url) {
		return url.toString().startsWith(CS_HOST_NAME);
	}

	public boolean scrape(ScrapingContext sc) throws ScrapingException{
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(CS_HOST_NAME)){
			Pattern p = Pattern.compile(CS_BIB_PATTERN, Pattern.MULTILINE | Pattern.DOTALL);
			Matcher m = p.matcher(sc.getPageContent());	
			if (m.matches()) {
				sc.setBibtexResult(m.group(1));
				/*
				 * returns itself to know, which scraper scraped this
				 */
				sc.setScraper(this);

				return true;
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
