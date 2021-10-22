package org.bibsonomy.scraper.url.kde.hebis;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HebisScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "hebis Discovery System";
	private static final String SITE_URL = "https://www.hebis.de/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();

	static {
		URL_PATTERNS.add(new Pair<>(Pattern.compile(".*" + "hds.hebis.de"), EMPTY_PATTERN));
	}

	private static final Pattern ID_PATTERN = Pattern.compile("\\w*/Record/(.*)/?.*");

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		String id = "";
		Matcher m_id = ID_PATTERN.matcher(url.getPath());
		if (m_id.find()) id = m_id.group(1);

		return "https://hds.hebis.de/ubks/Puma/Export?id=" + id + "&exportType=bib";
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
