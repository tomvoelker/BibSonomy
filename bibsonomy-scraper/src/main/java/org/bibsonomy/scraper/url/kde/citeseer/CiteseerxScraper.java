package org.bibsonomy.scraper.url.kde.citeseer;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author tst
 * @version $Id$
 */
public class CiteseerxScraper extends UrlScraper {
	
	private static final String INFO = "CiteSeerX beta Scraper: This scraper parses a publication page from the " +
									   "Scientific Literature Digital Library and Search Engine " + href("http://citeseerx.ist.psu.edu/", "CiteSeerX");
	
	private static final String HOST = "citeseerx.ist.psu.edu";
	private static final Pattern pattern = Pattern.compile("</h2>\\s*(@.*)\\s*</div>");

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
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
			final Matcher matcher = pattern.matcher(page);
			
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
