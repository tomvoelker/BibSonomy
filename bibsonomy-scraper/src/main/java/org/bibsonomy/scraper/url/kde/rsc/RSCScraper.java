package org.bibsonomy.scraper.url.kde.rsc;

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
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wla
 * @version $Id$
 */
public class RSCScraper extends AbstractUrlScraper {

	private final Log log = LogFactory.getLog(RSCScraper.class);

	private static final String SITE_NAME = "Royal Society of Chemistry";
	private static final String SITE_URL = "http://pubs.rsc.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final String BIBTEX_URL = "http://pubs.rsc.org/en/content/formatedresult?downloadtype=article&managertype=bibtex&markedids=";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "pubs.rsc.org"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern ID_PATTERN = Pattern.compile(".*/(.*?)(?:(?:#.*)|$)");
	private static final int ID_GROUP = 1;

	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		final URL url = scrapingContext.getUrl();
		final String id = extractId(url.toString());

		if (!present(id)) {
			log.error("can't parse publication id");
			return false;
		}

		try {

			final String bibTex = WebUtils.getContentAsString(BIBTEX_URL + id);

			if (present(bibTex)) {
				scrapingContext.setBibtexResult(bibTex);
				return true;
			} else {
				throw new ScrapingFailureException("getting bibtex failed");
			}

		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}

	}

	/**
	 * extracts publication id from url
	 * 
	 * @param url
	 * @return publication id
	 */
	private String extractId(final String url) {
		final Matcher matcher = ID_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(ID_GROUP);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
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
}
