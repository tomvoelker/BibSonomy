package org.bibsonomy.scraper.url.kde.plos;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for X.plosjournals.org
 * @author tst
 */
public class PlosScraper implements Scraper {
	
	/**
	 * INFO
	 */
	private static final String INFO = "Scraper for journals from plos.org";
	
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

	/**
	 * this scraper
	 */
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	/**
	 * Scrapes journals from plos.org 
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(PLOS_HOST_ENDING)){
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
							citationPage = sc.getContentAsString(new URL(citationPageLinkHref));
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
							citation = sc.getContentAsString(new URL(citationLinkHref));
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

			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}

		}
		return false;
	}

}
