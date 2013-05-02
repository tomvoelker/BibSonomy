package org.bibsonomy.scraper.url.kde.asm;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.CitationManagerScraper;
import org.bibsonomy.scraper.url.kde.hindawi.HindawiScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 * @version $Id$
 */
public class AsmScraper extends CitationManagerScraper {
	
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("<a href=\"(.+?)\">Download to citation manager</a>");
	private static final String SITE_NAME = "American Society for microbiology";
	private static final String SITE_URL = "aem.asm.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + "asm.org"), 
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
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
}
