package org.bibsonomy.scraper.url.kde.ieee;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IEEEComputerSocietyJournalMagazineScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "IEEE Computer Society";
	private static final String SITE_URL = "https://www.computer.org";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME);
	private static final String HOST = "computer.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Arrays.asList(
					new Pair<>(Pattern.compile(".*" + HOST), Pattern.compile("csdl/journal/")),
					new Pair<>(Pattern.compile(".*" + HOST), Pattern.compile("csdl/magazine/"))
	);

	private static final Pattern URL_JOURNAL_AND_MAGAZINE_PATTERN = Pattern.compile("((?:magazine|journal)/.*?/.*?/.*?/.*?)/");
	private static final String START_OF_DOWNLOAD_URL = "https://www.computer.org/csdl/api/v1/citation/bibtex/";

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		Matcher m_urlJournalMagazine = URL_JOURNAL_AND_MAGAZINE_PATTERN.matcher(url.toString());
		if (m_urlJournalMagazine.find()){
			return START_OF_DOWNLOAD_URL + m_urlJournalMagazine.group(1);
		}
		return null;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		Matcher m_bibtex = Pattern.compile("<pre>(.*?)</pre>", Pattern.DOTALL).matcher(bibtex);
		if (m_bibtex.find()){
			return m_bibtex.group(1);
		}
		return bibtex;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
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
