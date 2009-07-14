package org.bibsonomy.scraper.url.kde.citeseer;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author tst
 * @version $Id$
 */
public class CiteseerxScraper extends AbstractUrlScraper {
	
	private static final String INFO = "CiteSeerX beta Scraper: This scraper parses a publication page from the " +
									   "Scientific Literature Digital Library and Search Engine " + href("http://citeseerx.ist.psu.edu/", "CiteSeerX");
	
	private static final String HOST = "citeseerx.ist.psu.edu";
	private static final Pattern bibtexPattern = Pattern.compile("<h2>BibTeX.*?</h2>\\s*<div class=\"content\">\\s*(@.*?)\\s*</div>", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern abstractPattern = Pattern.compile("Abstract:.*?<p class=\"para4\">(.*?)</p>", Pattern.MULTILINE | Pattern.DOTALL);
	
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
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
			final String page = sc.getPageContent();
			
			// search BibTeXsnippet in html
			final Matcher matcher = bibtexPattern.matcher(page);
			
			if (matcher.find()) {
				String bibtex = matcher.group(1).replace("<br/>", "\n").replace("&nbsp;", " ");
				
				/*
				 * search abstract
				 */
				final Matcher abstractMatcher = abstractPattern.matcher(page);
				if (abstractMatcher.find()) {
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractMatcher.group(1));
				} else {
					System.out.println("no match");
				}
				
				// append url
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
				
				sc.setBibtexResult(bibtex);
				return true;

			}else
				throw new PageNotSupportedException("no bibtex snippet available");
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
