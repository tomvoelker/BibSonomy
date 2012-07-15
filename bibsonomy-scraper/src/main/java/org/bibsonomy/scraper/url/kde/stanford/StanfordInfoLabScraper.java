package org.bibsonomy.scraper.url.kde.stanford;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author hagen
 * @version $Id$
 */
public class StanfordInfoLabScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Stanford InfoLab Publication Server";
	private static final String SITE_URL  = "http://ilpubs.stanford.edu";

	/**
	 * INFO
	 */
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "ilpubs.stanford.edu"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	private static final Pattern ID_PATTERN = Pattern.compile(".*?/(\\d++)");

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
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		Matcher idMatcher = ID_PATTERN.matcher(scrapingContext.getUrl().toString());
		if (!idMatcher.find()) throw new ScrapingException("Path currently not supported.");
		String downloadURL = "http://ilpubs.stanford.edu:8090/cgi/export/" + idMatcher.group(1) + "/BibTeX/ilprints-eprint-1015.bib";
		try {
			URL url = new URL(downloadURL);
			String bibtex = WebUtils.getContentAsString(url);
			if (!present(bibtex)) return false;
			scrapingContext.setBibtexResult(bibtex);
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
		return false;
	}

}
