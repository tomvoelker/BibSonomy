package org.bibsonomy.scraper.url.kde.sage;

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
public class SageJournalScraper extends AbstractUrlScraper {

	private final Log log = LogFactory.getLog(SageJournalScraper.class);

	private static final String SITE_NAME = "Journal of Information Science";
	private static final String SITE_URL = "http://jis.sagepub.com/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "jis.sagepub.com"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern URL_ID_PATTERN = Pattern.compile("\\d+/\\d+/\\d+");

	private static final String BIBTEX_URL = "http://jis.sagepub.com/citmgr?type=bibtex&gca=spjis;";

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		final URL scrapingUrl = scrapingContext.getUrl();
		try {
			scrapingContext.setScraper(this);
			final String id = extractId(scrapingUrl);
			if (!present(id)) {
				log.error("can't parse publication id");
				return false;
			}
			String bibTex = WebUtils.getContentAsString(BIBTEX_URL + id);

			// some abstracts has multiple spaces
			bibTex = bibTex.replaceAll(" +", " ");

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
	 * 
	 * @param url
	 * @return paper id recovered form url
	 */
	private String extractId(final URL url) {
		final Matcher matcher = URL_ID_PATTERN.matcher(url.toString());
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
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
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
