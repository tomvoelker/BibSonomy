package org.bibsonomy.scraper.url.kde.jeb;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for publications from http://jeb.biologists.org/
 *
 * @author Johannes
 */
public class JEBScraper extends GenericBibTeXURLScraper{

	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");
	private static final String SITE_NAME = "Journal of Experimental Biology";
	private static final String SITE_HOST = "jeb.biologists.org";
	private static final String SITE_URL = "http://" + SITE_HOST;
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + SITE_HOST), 
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
		return INFO;
	}

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		try {
			// using url gives a FileNotFoundException, url.toString() doesn't
			final String content = WebUtils.getContentAsString(url.toString(), cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				return SITE_URL + m.group(1);
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
}
