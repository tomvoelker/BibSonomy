package org.bibsonomy.scraper.url.kde.jbc;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.CitationManager4Scraper;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class JBCScraper extends CitationManager4Scraper {
	private static final String SITE_URL = "https://www.jbc.org/";
	private static final String SITE_HOST = "jbc.org";
	private static final String SITE_NAME = "Journal of Biological Chemistry ";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));


	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getInfo() {
		return INFO;
	}
}
