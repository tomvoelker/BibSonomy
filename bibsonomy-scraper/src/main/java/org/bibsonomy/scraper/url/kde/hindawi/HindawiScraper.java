package org.bibsonomy.scraper.url.kde.hindawi;

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
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 * @version $Id$
 */
public class HindawiScraper extends AbstractUrlScraper{
	private static final Log log = LogFactory.getLog(HindawiScraper.class);
	
	private static final String SITE_NAME = "Hindawi Publishing Corporation";
	private static final String SITE_URL = "http://hindawi.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "hindawi.com"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern DOWNLOADLINK = Pattern.compile("<a href=\"([^\"]*+)\">Download citation as EndNote</a>");

	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		
		try {
			// FIXME: this only works on urls ending with .../cta
			/*
			 * TODO: we could use the ids in the urls to build the download link!
			 * Are their any guidelines what to do in this situation?
			 */
			final String endNoteLink = this.extractURL(WebUtils.getContentAsString(scrapingContext.getUrl()));

			if (endNoteLink == null) {
				log.error("can't parse publication URL");
				return false;
			}
			
			final String endNote = WebUtils.getContentAsString(new URL(endNoteLink));
			final EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
			
			final String bibTex = converter.endnoteToBibtex(endNote);
			
			if (present(bibTex)) {
				scrapingContext.setBibtexResult(bibTex);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}
	}
	
	private String extractURL(final String content){
		final Matcher matcher = DOWNLOADLINK.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
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
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}
	
}
