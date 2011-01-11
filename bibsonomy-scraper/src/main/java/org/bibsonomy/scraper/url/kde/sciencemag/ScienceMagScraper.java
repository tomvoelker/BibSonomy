package org.bibsonomy.scraper.url.kde.sciencemag;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.generic.CitationManagerScraper;

/**
 * @author clemens
 * @version $Id$
 */
public class ScienceMagScraper extends CitationManagerScraper {
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download Citation</a>");
	private static final String SITE_NAME = "Science Magazine";
	private static final String SITE_URL = "http://www.sciencemag.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Tuple<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Tuple<Pattern, Pattern>(
			Pattern.compile(".*" + "sciencemag.org"), 
			Pattern.compile("/content" + ".*")
		));

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	public String getInfo() {
		return INFO;
	}

	@Override
	public Pattern getDownloadLinkPattern() {
		return DOWNLOAD_LINK_PATTERN;
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
}
