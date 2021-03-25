/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.osa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class OSAScraper extends AbstractUrlScraper implements ReferencesScraper{
	private static final Log log = LogFactory.getLog(OSAScraper.class);
	
	private static final String SITE_NAME = "Optical Society of America";
	private static final String SITE_URL  = "https://www.osapublishing.org/";
	private static final String info = "This Scraper parses a publication from the " + href(SITE_URL, SITE_NAME)+".";
	private static final String OSA_HOST  = "osapublishing.org";
	private static final String HTTP = "https://www.";
	private static final String OSA_BIBTEX_DOWNLOAD_PATH = "/custom_tags/IB_Download_Citations.cfm";
	
	private static final Pattern inputPattern = Pattern.compile("<input\\b[^>]*>");
	private static final Pattern valuePattern = Pattern.compile("value=\"[^\"]*\"");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + OSA_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	final static Pattern references_pattern = Pattern.compile("(?s)<h3>References</h3>\\s+<div .*>\\s+<ol>(.*)</ol>");
	
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final String id = getId(sc.getPageContent());
		
		final String bibResult;
		try {
			final String cookie;
			try {
				cookie = WebUtils.getCookies(sc.getUrl());
			} catch (final IOException ex) {
				throw new InternalFailureException("An unexpected IO error has occurred. No Cookie has been generated.");
			}
			final List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
			postData.add(new BasicNameValuePair("articles", id));
			postData.add(new BasicNameValuePair("ArticleAction", "export_bibtex"));
			
			bibResult = WebUtils.getContentAsString(HTTP + OSA_HOST + OSA_BIBTEX_DOWNLOAD_PATH, cookie, postData, null);
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		if (ValidationUtils.present(bibResult)) {
			sc.setBibtexResult(bibResult);
			return true;
		}
		throw new ScrapingFailureException("getting bibtex failed");
	}

	/**
	 * Extract id from page content.
	 * 
	 * @param pageContent
	 * @return
	 */
	private static String getId(final String pageContent) {
		final Matcher inputMatcher = inputPattern.matcher(pageContent);

		while (inputMatcher.find()) {
			final String input = inputMatcher.group();
			if (input.contains("name=\"articles\"")) {
				final Matcher valueMatcher = valuePattern.matcher(input);

				if(valueMatcher.find()) {
					final String value = valueMatcher.group();
					return value.substring(7,value.length()-1);
				}
			}
		}
		return null;
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext)throws ScrapingException {
		try{
			Matcher m = references_pattern.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			if(m.find()){
				scrapingContext.setReferences(m.group(1));
				return true;
			}
		} catch(final Exception e) {
			log.error("error while scraping references for " + scrapingContext.getUrl(), e);
		}
		return false;
	}
}
