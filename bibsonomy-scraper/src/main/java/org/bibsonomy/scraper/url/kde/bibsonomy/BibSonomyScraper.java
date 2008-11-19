package org.bibsonomy.scraper.url.kde.bibsonomy;

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
 * Scraper for single publications from bibsonomy.org.
 * 
 * @author tst
 *
 */
public class BibSonomyScraper implements Scraper, UrlScraper {
	
	private static final String INFO = "BibSonomyScraper: If you don't like the copy button from <a href=\"http://www.bibsonomy.org\">bibsonomy</a> use your postBibtex bookmark.";
	
	private static final String BIBSONOMY_HOST = "bibsonomy.org";
	private static final String BIBSONOMY_BIB_PATH = "/bib/bibtex";
	private static final String BIBSONOMY_BIBTEX_PATH = "/bibtex";
	
	/**
	 * Scrapes only single publications from bibsonomy.org/bibtex and bibsonomy.org/bib/bibtex 
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
			try {
				sc.setScraper(this);
				
				String bibResult = null;
				
				// if /bibtex page then change path from /bibtex to /bib/bibtex and download
				if(sc.getUrl().getPath().startsWith(BIBSONOMY_BIBTEX_PATH)){
					String url = sc.getUrl().toString();
					url = url.replace(BIBSONOMY_BIBTEX_PATH, BIBSONOMY_BIB_PATH);
					bibResult = sc.getContentAsString(new URL(url));
					
				// if /bib/bibtex page then download directly
				}else if(sc.getUrl().getPath().startsWith(BIBSONOMY_BIB_PATH)){
					bibResult = sc.getPageContent();
				}
				
				if(bibResult != null){
					sc.setBibtexResult(bibResult);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}
		}
		return false;
	}

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BIBSONOMY_HOST), Pattern.compile(BIBSONOMY_BIB_PATH + ".*")));
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BIBSONOMY_HOST), Pattern.compile(BIBSONOMY_BIBTEX_PATH + ".*")));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
