/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.url.kde.jci;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wla
 */
public class JCIScraper extends AbstractUrlScraper implements ReferencesScraper{
	private static final Log log = LogFactory.getLog(JCIScraper.class);
	
	private static final String SITE_NAME = "The Journal of Clinical Investigation";

	private static final String SITE_URL = "www.jci.org";

	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String BIBTEX_URL = "/cite/bibtex";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(SITE_URL), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern REFERENCES = Pattern.compile("(?s)<ol compact>(.*)</ol>");
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		//scraper information
		scrapingContext.setScraper(this);
		
		try {
			final String bibTex = WebUtils.getContentAsString(scrapingContext.getUrl() + BIBTEX_URL);
			if (present(bibTex)) {
				scrapingContext.setBibtexResult(bibTex);
				return true;
			}
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}
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
		return patterns;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext)throws ScrapingException {
		try {
			final Matcher m = REFERENCES.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			if (m.find()) {
				scrapingContext.setReferences(m.group(1));
				return true;
			}
		} catch(final Exception e) {
			log.error("error while scraping references for " + scrapingContext.getUrl(), e);
		}
		return false;
	}

}
