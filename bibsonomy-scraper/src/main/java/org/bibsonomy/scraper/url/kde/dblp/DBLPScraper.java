package org.bibsonomy.scraper.url.kde.dblp;

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

	private static final String DBLP_HOST_NAME1  = "http://dblp.uni-trier.de";
	private static final String DBLP_HOST_NAME2  = "http://search.mpi-inf.mpg.de/dblp/";
	private static final String DBLP_HOST_NAME3  = "http://www.sigmod.org/dblp/";
	private static final String DBLP_HOST_NAME4  = "http://www.vldb.org/dblp/";
	private static final String DBLP_HOST_NAME5  = "http://sunsite.informatik.rwth-aachen.de/dblp/";
	private static final Pattern DBLP_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc.getUrl() != null) {
			/*
			 * extract URL and check against several (mirror) host names
			 */
			final String url = sc.getUrl().toString();
			if (url.startsWith(DBLP_HOST_NAME1) || 
					url.startsWith(DBLP_HOST_NAME2) ||
					url.startsWith(DBLP_HOST_NAME3) ||
					url.startsWith(DBLP_HOST_NAME4) ||
					url.startsWith(DBLP_HOST_NAME5)) { 

				final Matcher m = DBLP_PATTERN.matcher(sc.getPageContent());	
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

