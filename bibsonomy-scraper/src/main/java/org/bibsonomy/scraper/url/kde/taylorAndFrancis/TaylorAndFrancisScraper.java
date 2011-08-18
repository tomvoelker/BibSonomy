package org.bibsonomy.scraper.url.kde.taylorAndFrancis;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author schwass
 * @version $Id$
 */
public class TaylorAndFrancisScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Taylor & Francis Online";
	private static final String SITE_URL = "http://www.tandfonline.com/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final String TANDF_HOST_NAME = "tandfonline.com";
	
	private static final List<Tuple<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + TANDF_HOST_NAME), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern DOI_PATTERN = Pattern.compile("/\\d+\\.\\d+/\\d+$");
	
	private static final String TANDF_BIBTEX_DOWNLOAD_PATH = "action/downloadCitation";
	private static final String DOWNLOADFILENAME = "tandf_rajp2080_124";
	
	private static String postContent(String doi) {
		return "doi=" + doi
		+ "&downloadFileName=" + DOWNLOADFILENAME
		+ "&format=bibtex&direct=true&include=includeCit";
	}
	
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
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		Matcher matcher = DOI_PATTERN.matcher(scrapingContext.getUrl().toString());
		if (!matcher.find()) return false;
		try {
			final String cookie = WebUtils.getCookies(scrapingContext.getUrl());
			String bibtexEntry = WebUtils.getPostContentAsString(cookie, new URL(SITE_URL + TANDF_BIBTEX_DOWNLOAD_PATH), postContent(matcher.group().substring(1)));
			if (bibtexEntry != null) {
				scrapingContext.setBibtexResult(bibtexEntry.trim());
				return true;
			} else
				throw new ScrapingFailureException("getting BibTeX failed");
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
	}

}
