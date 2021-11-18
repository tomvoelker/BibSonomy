package org.bibsonomy.scraper.url.kde.ashpublications;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.CitationManager2Scraper;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class AshPublicationsScraper extends CitationManager2Scraper {
	private static final String SITE_NAME = "ASH Publications";
	private static final String SITE_URL = "https://ashpublications.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + "ashpublications.org"), AbstractUrlScraper.EMPTY_PATTERN
					));

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

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

}
