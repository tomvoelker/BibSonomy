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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * Scraper für SpringerLink.
 * 
 * @author rja
 */
public class SpringerLinkScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(SpringerLinkScraper.class);
	
	private static final String SITE_NAME = "SpringerLink";
	private static final String SITE_URL = "http://link.springer.com/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String DOWNLOAD_URL = "http://citation-needed.services.springer.com/v2/references/";
	private static final String DOWNLOAD_TYPE = "?format=bibtex&flavour=citation";
	private static final String SPRINGER_CITATION_HOST = "link.springer.com";
	
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	/**
	 * @param doi
	 * @return
	 */
	private static String downloadLinkForDoi(final String doi) {
		return DOWNLOAD_URL + doi + DOWNLOAD_TYPE;
	}
	
	private static String extractDownloadLink(final String path) {
		final String doi = DOIUtils.extractDOI(path);
		if (present(doi)) {
		    // if present, strip "/fulltext.html"
		    if (doi.endsWith("/fulltext.html")) {
			return downloadLinkForDoi(doi.substring(0, doi.length() - "/fulltext.html".length()));
		    } else {
			return downloadLinkForDoi(doi);
		    }
		}
		
		return null;
	}
		
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final String path = url.getPath();
		
		final String downloadLink = extractDownloadLink(path);
		if (present(downloadLink)) {
			return downloadLink;
		}
		// sometimes the slash is encoded
		try {
			final String decodePathSegment = UrlUtils.decodePathSegment(path);
			final String extractDownloadLinkDecoded = extractDownloadLink(decodePathSegment);
			if (present(extractDownloadLinkDecoded)) {
				return extractDownloadLinkDecoded;
			}
		} catch (final URISyntaxException e) {
			log.error("error decoding path " + path, e);
		}
		
		return null;
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
	public String getSupportedSiteName() {
		return SITE_NAME;
	}
	
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
