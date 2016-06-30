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
package org.bibsonomy.scraper.url.kde.firstmonday;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.WebUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
/**
 * @author Haile
 */
public class FirstMondayScraper extends AbstractUrlScraper{
	private final Log log = LogFactory.getLog(FirstMondayScraper.class);
	
	private static final String SITE_NAME = "Firtst Monday";
	private static final String SITE_URL = "http://firstmonday.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final String BIBTEX_PATH = "/ojs/index.php/fm/rt/captureCite/";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "journals.uic.edu"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern ID_PATTERN = Pattern.compile("\\d+(/\\d+)*?");
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		final URL url = scrapingContext.getUrl();
		final String bibTexUrl= getBibTexURL(url);

		if (!present(bibTexUrl)) {
			log.error("can't parse publication id");
			return false;
		}
		try {
			// TODO: why do not use the DomUtils method to get the DOM?
			Tidy tidy = new Tidy();
			final ByteArrayInputStream inputStream = new ByteArrayInputStream(WebUtils.getContentAsString(bibTexUrl).getBytes(StringUtils.CHARSET_UTF_8));
			final Document doc = tidy.parseDOM(inputStream, null);  
			final Node title = doc.getElementsByTagName("pre").item(0);
			final String bibTex = title.getFirstChild().getNodeValue();
			
			if (present(bibTex)) {
				scrapingContext.setBibtexResult(bibTex);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final IOException | DOMException e) {
			throw new InternalFailureException(e);
		}
	}
	/**
	 * extracts publication id from url
	 * 
	 * @param url
	 * @return publication id
	 */
	private static String getBibTexURL(final URL url) {
		final String host = url.getHost();
		final Matcher match = ID_PATTERN.matcher(url.toString());
		if (match.find()) {
			String id = match.group(0);
			if (match.groupCount() == 1) id = id +"/0";
			return  "http://" + host + BIBTEX_PATH + id + "/BibtexCitationPlugin";
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
