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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper für SpringerLink.
 * 
 * @author rja
 */
public class SpringerLinkScraper extends GenericBibTeXURLScraper {
	
	private static final String SITE_NAME = "SpringerLink";
	private static final String SITE_URL = "http://www.springerlink.com/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";
	private static final Pattern ID_PATTERN = Pattern.compile("(article|chapter|book)/(.+?)(/|$)");
		
	private static final String SPRINGER_CITATION_HOST_COM = "springerlink.com";
	private static final String SPRINGER_CITATION_HOST_DE = "springerlink.de";
	private static final String SPRINGER_CITATION_HOST_NEW = "link.springer.com";
	private static final String SPRINGER_LINK_METAPRESS = "springerlink.metapress.com";
	private static final String DOWNLOAD_URL = "http://citation-needed.services.springer.com/v2/references/";
	private static final String DOWNLOAD_TYPE = "?format=bibtex&flavour=citation";
	
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_COM), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_DE), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_NEW), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_LINK_METAPRESS), AbstractUrlScraper.EMPTY_PATTERN));
	}
		
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException, IOException {
	
		final Matcher m = ID_PATTERN.matcher(url.getPath());
		String id = null;
		if (m.find()) {
			id = m.group(2);
		}
		
		if (id != null) {
			return DOWNLOAD_URL + id + DOWNLOAD_TYPE;
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
