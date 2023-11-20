package org.bibsonomy.scraper.url.kde.copernicus;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopernicusPreprintScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "Copernicus Publications";
	private static final String SITE_URL = "https://publications.copernicus.org/";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String SITE_HOST = "copernicus.org";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(".*" + SITE_HOST), Pattern.compile("preprints")));

	private static final Pattern URL_ID_PATTERN = Pattern.compile("preprints/(.*?)/");

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
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		Matcher m_id = URL_ID_PATTERN.matcher(url.getPath());
		if (!m_id.find()){
			return null;
		}
		return url  + m_id.group(1) + ".bib";
	}
}
