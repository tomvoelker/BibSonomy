package org.bibsonomy.scraper.url.kde.aps;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.SimpleGenericURLScraper;

/**
 * @author Haile
 * @version $Id$
 */
public class ApsScraper extends SimpleGenericURLScraper{
	private static final String SITE_NAME = "American Psychological Society";
	private static final String SITE_URL = "the-aps.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final String BIBTEX_URL = "citmgr?type=bibtex&gca=";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "physrev.physiology.org"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern URL_PATTERN = Pattern.compile("(http://[^/]++)(\\W+)");
	private static final Pattern URL_START = Pattern.compile("/\\w+");
	private static final Pattern ID_PATTERN = Pattern.compile("(\\d+\\W)+");
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
	public String getBibTeXURL(URL url) {
		Matcher murl = URL_PATTERN.matcher(url.toExternalForm());
		Matcher idurl = ID_PATTERN.matcher(url.toExternalForm());
		Matcher starturl = URL_START.matcher(url.toExternalForm());
		
		if(!idurl.find()) return null;
		if (!murl.find()) return null;
		if(!starturl.find()) return null;
		return murl.group(0) + BIBTEX_URL + starturl.group(0).replace("/", "") + ";" + idurl.group(0).replace(".","");
	}
}
