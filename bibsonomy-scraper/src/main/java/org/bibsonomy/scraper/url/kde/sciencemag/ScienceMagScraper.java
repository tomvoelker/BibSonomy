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
	public static final Pattern downloadLinkPattern = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download Citation</a>");
	
	public static final  String SITE_NAME = "Science Magazine";
	
	public static final  String SITE_URL = "http://www.sciencemag.org/";
	
	public static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	public static final  List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(
			Pattern.compile(".*" + "sciencemag.org"), 
			Pattern.compile("/content" + ".*")
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
