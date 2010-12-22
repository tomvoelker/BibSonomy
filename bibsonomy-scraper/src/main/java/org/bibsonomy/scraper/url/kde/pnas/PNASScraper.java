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
	public static final String SITE_NAME = "PNAS";
	
	public static final String SITE_URL = "http://www.pnas.org/";
	
	public static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	public static final Pattern downloadLinkPattern = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download to citation manager</a>");
	
	public static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "pnas.org"), AbstractUrlScraper.EMPTY_PATTERN));

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
		return info;
	}

	@Override
	public Pattern getDownloadLinkPattern() {
		return downloadLinkPattern;
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
