package org.bibsonomy.scraper.url.kde.trier;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class DBLPScraper implements Scraper {

	private static final String info = "DBLP Scraper: This scraper parses a publication page from the <a href=\"http://search.mpi-inf.mpg.de/dblp/\">University of Trier Digital Bibliography & Library Project</a> " +
	   "and extracts the adequate BibTeX entry. Author: KDE";

	private static final String DBLP_HOST_NAME = "http://search.mpi-inf.mpg.de/dblp/";
	
	private static final String  DBLP_BIB_PATTERN   = ".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*";
	
	public boolean validForUrl(URL url) {
		return url.toString().startsWith(DBLP_HOST_NAME);
	}

	public boolean scrape(ScrapingContext sc) throws ScrapingException{
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(DBLP_HOST_NAME)){
			Pattern p = Pattern.compile(DBLP_BIB_PATTERN, Pattern.MULTILINE | Pattern.DOTALL);
			Matcher m = p.matcher(sc.getPageContent());	
			System.out.println(sc.getBibtexResult());
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

