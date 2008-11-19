package org.bibsonomy.scraper.url.kde.nber;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author wbi
 * @version $Id$
 */
public class NberScraper implements Scraper, UrlScraper {

	private static final String info = "NBER Scraper: This Scraper parses a publication from <a href=\"http://www.nber.org/\">National Bureau of Economic Research</a> "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String NBER_HOST  = "www.nber.org";
	private static final String NBER_HOST_NAME  = "http://www.nber.org";
	private static final String NBER_BIBTEX_PATH = "/papers/";
	
	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)
			throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				sc.setScraper(this);
				
				String url = sc.getUrl().toString();
				
				//here we just need to append .bib to the url and we got the bibtex file
				try {
					sc.setUrl(new URL(url + ".bib"));
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
				String bibResult = sc.getPageContent();
								
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

		}
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + NBER_HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
