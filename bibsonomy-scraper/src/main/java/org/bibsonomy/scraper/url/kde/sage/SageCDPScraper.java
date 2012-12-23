package org.bibsonomy.scraper.url.kde.sage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.CitationManagerScraper;

/**
 * @author hagen
 * @version $Id$
 */
public class SageCDPScraper extends CitationManagerScraper {

	private static final String SITE_NAME = "Sage journals - Clinical Psychological Science";
	private static final String SITE_URL = "http://cdp.sagepub.com/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("href=\"(/citmgr[^\"]++)");

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "cdp.sagepub.com"), AbstractUrlScraper.EMPTY_PATTERN));
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

	@Override
	public Pattern getDownloadLinkPattern() {
		return DOWNLOAD_LINK_PATTERN;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
