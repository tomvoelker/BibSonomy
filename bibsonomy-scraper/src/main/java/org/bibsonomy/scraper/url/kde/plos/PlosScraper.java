package org.bibsonomy.scraper.url.kde.plos;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for X.plosjournals.org
 * @author tst
 */
public class PlosScraper extends AbstractUrlScraper {

	/**
	 * INFO
	 */
	private static final String INFO = "PLoS Scraper: Scraper for journals from " + href("http://www.plos.org/journals/index.php", "PLoS");

	/**
	 * ending of plos journal URLs
	 */
	private static final String PLOS_HOST_ENDING = "plosjournals.org";

	/**
	 * title value from link to a citation page
	 */
	private static final String CITATION_PAGE_LINK_TITLE = "title=\"Download Citation\"";

	/**
	 * name of a citation download link
	 */
	private static final String CITATION_LINK_NAME = ">EndNote Format<";

	/*
	 * regex
	 */

	/**
	 * pattern for links
	 */
	private static final String PATTERN_LINK = "<a\\b[^<]*</a>";

	/**
	 * pattern for href field
	 */
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";

	/**
	 * get INFO
	 */
	public String getInfo() {
		return INFO;
	}

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + PLOS_HOST_ENDING), AbstractUrlScraper.EMPTY_PATTERN));

	
	/**
	 * Scrapes journals from plos.org 
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			// citation as endnote
			String citation = null;

			// may be journal page or citation page
			String journalPage = sc.getPageContent();
			String citationPage = null;

			// search link to citation in journal page
			Pattern linkPattern = Pattern.compile(PATTERN_LINK);
			Matcher linkMatcher = linkPattern.matcher(journalPage);
			while(linkMatcher.find()){
				String citationPageLink = linkMatcher.group();
				if(citationPageLink.contains(CITATION_PAGE_LINK_TITLE)){
					Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
					Matcher hrefMatcher = hrefPattern.matcher(citationPageLink);
					if(hrefMatcher.find()){
						String citationPageLinkHref = hrefMatcher.group();
						citationPageLinkHref = "http://" + sc.getUrl().getHost() + "/perlserv/?" + citationPageLinkHref.substring(6, citationPageLinkHref.length()-1);
						// citation page found
						citationPage = WebUtils.getContentAsString(new URL(citationPageLinkHref));
					}
				}
			}

			// no citation found, may be current page is already the citation page
			if(citationPage == null)
				citationPage = journalPage;

			// search link to citation (in endnote)
			Matcher citationLinkMatcher = linkPattern.matcher(citationPage);
			while(citationLinkMatcher.find()){
				String citationLink = citationLinkMatcher.group();
				if(citationLink.contains(CITATION_LINK_NAME)){
					Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
					Matcher hrefMatcher = hrefPattern.matcher(citationLink);
					if(hrefMatcher.find()){
						String citationLinkHref = hrefMatcher.group();
						citationLinkHref = "http://" + sc.getUrl().getHost() + "/perlserv/" + citationLinkHref.substring(6, citationLinkHref.length()-1);
						citation = WebUtils.getContentAsString(new URL(citationLinkHref));
					}
				}
			}

			// build bibtex
			if(citation != null){
				EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
				String bibtex = converter.processEntry(citation);
				if(bibtex != null){
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			}else
				throw new ScrapingFailureException("endnote is not available");

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
