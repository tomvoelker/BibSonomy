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
package org.bibsonomy.scraper.url.kde.apha;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * This scraper supports download links from the following hosts
 * 1. ajph.aphapublications.org
 * 2. nrcresearchpress.com
 * 3. emeraldinsight.com
 * @author Mohammed Abed
 */
public class APHAScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "American Journal of PUBLIC HEALTH";
	private static final String SITE_URL = "http://ajph.aphapublications.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";

	private static final Pattern DOI_PATTERN_FROM_URL = Pattern.compile("/abs/(.+?)$");
	private static final String AJPH_HOST = "ajph.aphapublications.org";
	private static final String NRCRESEACHPRESS_HOST = "nrcresearchpress.com";
	private static final String EMERALDINSIGHT_HOST = "emeraldinsight.com";
	private static final String HTTP = "http://";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ AJPH_HOST), Pattern.compile("/doi/abs")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ NRCRESEACHPRESS_HOST), Pattern.compile("/doi/abs")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ EMERALDINSIGHT_HOST), Pattern.compile("/doi/abs")));
	}
	
	private static final List<Pattern> DOWNLOAD_URL = new LinkedList<Pattern>();
	static {
		DOWNLOAD_URL.add(Pattern.compile(HTTP + AJPH_HOST + "/action/downloadCitation"));
		DOWNLOAD_URL.add(Pattern.compile(HTTP + NRCRESEACHPRESS_HOST + "/action/downloadCitation"));
		DOWNLOAD_URL.add(Pattern.compile(HTTP + EMERALDINSIGHT_HOST + "/action/downloadCitation"));
	}
	private final RisToBibtexConverter ris = new RisToBibtexConverter();

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		try {
			final String cookie = WebUtils.getCookies(sc.getUrl());
			String doi = null;
			final Matcher m = DOI_PATTERN_FROM_URL.matcher(sc.getUrl().toString());
			if (m.find()) {
				doi = "doi=" + m.group(1);
			}
			
			if (doi != null && cookie != null) {
				String resultAsString = null;
				try {
					/*
					 * the expected resultAsString is a RIS File: because this host support only RIS format
					 */
					if (sc.getUrl().toString().contains(AJPH_HOST)) {
						resultAsString = WebUtils.getPostContentAsString(cookie, new URL(DOWNLOAD_URL.get(0).toString()), doi);
					}
					/*
					 * the expected resultAsString is a BibTex File
					 */
					else if (sc.getUrl().toString().contains(NRCRESEACHPRESS_HOST)) {
						resultAsString = WebUtils.getPostContentAsString(cookie, new URL(DOWNLOAD_URL.get(1).toString()), doi + "&format=bibtex");
						if (resultAsString != null) {
							sc.setBibtexResult(resultAsString);
							return true;
						}
					}
					/*
					 * the expected resultAsString is a BibTex File
					 */
					else {
						resultAsString = WebUtils.getPostContentAsString(cookie, new URL(DOWNLOAD_URL.get(2).toString()), doi + "&format=bibtex");
						if (resultAsString != null) {
							sc.setBibtexResult(resultAsString);
							return true;
						}
					}
				} catch (MalformedURLException ex) {
					throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
				}

				/*
				 * if the host was from ajph.aphapublications.org, then we must convert the resultAsString to BibTex format
				 */
				final String bibResult = this.ris.risToBibtex(resultAsString);
				if (bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				}
			}
		} catch (final IOException ex) {
			throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe APHA or nrcresearchpress Publications is down.");
		}
		
		return false;
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
