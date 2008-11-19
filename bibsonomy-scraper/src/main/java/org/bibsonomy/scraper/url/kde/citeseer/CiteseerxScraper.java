package org.bibsonomy.scraper.url.kde.citeseer;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author tst
 * @version $Id$
 */
public class CiteseerxScraper implements Scraper, UrlScraper {
	
	private static final String INFO = "CiteSeerX beta Scraper: This scraper parses a publication page from the " +
									   "Scientific Literature Digital Library and Search Engine (<a href=\"http://citeseerx.ist.psu.edu/\">CiteSeerX</a>) " +
	   								   "and extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String HOST = "citeseerx.ist.psu.edu";
	
	private static final String REGEX = "</h2>\\s*(@.*)\\s*</div>";

	public String getInfo() {
		return INFO;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
			sc.setScraper(this);
			
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
				return true;

			}else
				throw new PageNotSupportedException("no bibtex snippet available");
		}
		return false;
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}

}
