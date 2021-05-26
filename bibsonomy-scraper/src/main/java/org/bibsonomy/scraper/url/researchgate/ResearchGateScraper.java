package org.bibsonomy.scraper.url.researchgate;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * scraper for ResearchGate
 *
 * @author dzo
 */
public class ResearchGateScraper extends GenericBibTeXURLScraper {
	private static final String HOST = "www.researchgate.net";
	private static final String SITE_URL = "https://" + HOST;

	private static final String SITE_NAME = "ResearchGate";

	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME) + ".";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Arrays.asList(
					new Pair<>(Pattern.compile(HOST), Pattern.compile("/publication/.*"))
	);

	private static final Pattern ID_PATTERN = Pattern.compile("/publication/([0-9]+)_.*");

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final String path = url.getPath();

		final Matcher idMatcher = ID_PATTERN.matcher(path);
		if (idMatcher.find()) {
			final String id = idMatcher.group(1);
			return SITE_URL + "/lite.publication.PublicationDownloadCitationModal.downloadCitation.html?fileType=BibTeX&citation=citationAndAbstract&publicationUid=" + id;
		}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
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
