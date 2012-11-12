package org.bibsonomy.scraper.url.kde.jneurosci;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.generic.CitationManagerScraper;

/**
 * @author hagen
 * @version $Id$
 */
public class JNeurosciScraper extends CitationManagerScraper {

	private static final String SITE_NAME = "The Journal of Neuroscience";
	private static final String SITE_URL = "http://www.jneurosci.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS;
	
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("href=\"([^\"]++)\".*?citation manager");
	
	static {
		URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "jneurosci.org"), Pattern.compile("/content" + ".*")));
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
		return URL_PATTERNS;
	}

}
