/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.mdpi;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * scraper for MDPI
 *
 * @author Haile
 */
public class MDPIScraper extends AbstractUrlScraper implements CitedbyScraper{
	private static final Log log = LogFactory.getLog(MDPIScraper.class);
	
	private static final String SITE_NAME = "MDPI - Open Access Publishing";
	private static final String SITE_URL = "http://www.mdpi.com/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "mdpi.com"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<meta name=\"description\" content=\"(.*)\" >");
	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<input type=\"hidden\" name=\"articles_ids\\[\\]\" value=\"(\\d+)\">");
	private static final Pattern CITATION_PATTERN = Pattern.compile("<meta name=\"citation_doi\" content=\"(.*)\">");

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		try{
			final String pageContent = WebUtils.getContentAsString(scrapingContext.getUrl()); // TODO: cache!!
			Matcher m = CITATION_PATTERN.matcher(pageContent);
			if (m.find()) {
				scrapingContext.setCitedBy(WebUtils.getContentAsString(SITE_URL + "citedby/" + m.group(1)));
				return true;
			}
		} catch (final Exception e) {
			log.error("error while scraping cited by " + scrapingContext.getUrl(), e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			final String pageContent = WebUtils.getContentAsString(scrapingContext.getUrl());
			String id = "";
			Matcher m = BIBTEX_PATTERN.matcher(pageContent);
			if (m.find()) {
				id = m.group(1);
			}
			final PostMethod post = new PostMethod(SITE_URL + "export");
			post.addParameters(new NameValuePair[] {
					new NameValuePair("articles_ids[]", id),
					new NameValuePair("export_format_top", "bibtex_no_abstract"),
					new NameValuePair("export_submit_top", "")
			});

			String bibtex =  WebUtils.getPostContentAsString(WebUtils.getHttpClient(), post);
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractParser(pageContent)));
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final IOException e) {
			log.error("error while scraping " + scrapingContext.getUrl(), e);
		}
		return false;
	}
	
	private static String abstractParser(final String pageContent) {
		try {
			Matcher m = ABSTRACT_PATTERN.matcher(pageContent);
			if (m.find()) {
				return m.group(1);
			}
		} catch (final Exception e) {
			log.error("error while reading abstract", e);
		}
		return null;
	}

}
