package org.bibsonomy.scraper.url.kde.pnas;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.generic.CitationManagerScraper;

/**
 * @author clemens
 * @version $Id$
 */
public class PNASScraper extends CitationManagerScraper {
	private static final String SITE_NAME = "PNAS";
	private static final String SITE_URL = "http://www.pnas.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download to citation manager</a>");
	private static final List<Tuple<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "pnas.org"), AbstractUrlScraper.EMPTY_PATTERN));

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
