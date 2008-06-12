package org.bibsonomy.scraper.url.kde.langev;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class LangevScraper implements Scraper {

	private static final String info = "ISRL Scraper: This scraper parses a publication page from the <a href=\"http://www.isrl.uiuc.edu/\">University of Trier Digital Bibliography & Library Project</a> " +
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String ISRL_HOST_NAME  = "http://www.isrl.uiuc.edu";
	private static final Pattern ISRL_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);
	
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc.getUrl() != null) {
			/*
			 * extract URL and check against several (mirror) host names
			 */
			final String url = sc.getUrl().toString();
			if (url.startsWith(ISRL_HOST_NAME)) { 		
				
				final Matcher m = ISRL_PATTERN.matcher(sc.getPageContent());	
				if (m.matches()) {
					sc.setBibtexResult(m.group(1));
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);

					return true;
				}
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
