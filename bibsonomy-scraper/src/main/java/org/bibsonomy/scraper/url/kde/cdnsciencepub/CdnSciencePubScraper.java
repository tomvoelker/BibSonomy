package org.bibsonomy.scraper.url.kde.cdnsciencepub;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.LiteratumScraper;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CdnSciencePubScraper extends LiteratumScraper {
	private static final String SITE_NAME = "Canadian Science Publishing";
	private static final String SITE_URL = "https://cdnsciencepub.com/";
	private static final String INFO = "Scraper for Journals from " + href(SITE_URL, SITE_NAME)+".";
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "cdnsciencepub.com"), AbstractUrlScraper.EMPTY_PATTERN));



	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
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
