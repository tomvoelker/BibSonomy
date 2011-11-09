package org.bibsonomy.scraper.url.kde.googlebooks;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author clemens
 * @version $Id$
 */
public class GoogleBooksScraper extends AbstractUrlScraper {

	private static final String SITE_URL  = "http://books.google.com/";
	private static final String SITE_NAME = "Google Books";
	private static final String INFO      = "Scrapes BibTeX from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String HOST = "books.google.";
	private static final String PATH = "/books";
	
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("<a class=\"gb-button \" href=\\\"([^\\\"]*)\\\" dir=ltr>BiBTeX</a>");
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST + ".*"), Pattern.compile(PATH + ".*")));
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		try {
			final String content = WebUtils.getContentAsString(sc.getUrl());
			
			// get download link
			final Matcher downloadLinkMatcher = DOWNLOAD_LINK_PATTERN.matcher(content);
			final String downloadLink;
			if(downloadLinkMatcher.find())
				downloadLink = downloadLinkMatcher.group(1);
			else
				throw new ScrapingFailureException("Download link is not available");

			// download bibtex
			final String bibtex = WebUtils.getContentAsString(new URL(downloadLink));
			if (bibtex != null) {
				sc.setBibtexResult(bibtex);
				return true;
			}
			return false;
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}		
	}

	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
