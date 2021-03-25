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
package org.bibsonomy.scraper.url.kde.lccnloc;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.XMLDublinCoreToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class LccnLocScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Library of Congress";
	private static final String SITE_URL = "https://lccn.loc.gov";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String LCCN_HOST = "lccn.loc.gov";
	private static final String CATALOG_HOST = "catalog.loc.gov";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	private static final XMLDublinCoreToBibtexConverter RIS2BIB = new XMLDublinCoreToBibtexConverter();
	private static final Pattern p = Pattern.compile("id=\"permalink\" href=\"(.*)\">");
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + LCCN_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + CATALOG_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {

		try {
			/*
			 * if it is a search page
			 */
			if (scrapingContext.getUrl().toString().contains("?searchId=")) {
				final String pageContent = WebUtils.getContentAsString(scrapingContext.getUrl());
				Matcher m = p.matcher(pageContent);
				if(m.find()) {
					final String xml = WebUtils.getContentAsString(new URL(m.group(1) + "/dc"));
					String bibtexResult = RIS2BIB.toBibtex(xml);
					bibtexResult = BibTexUtils.addFieldIfNotContained(bibtexResult, "site-url", m.group(1).toString());
					scrapingContext.setBibtexResult(bibtexResult);
					return true;
				}
			}
			/*
			 * if you are in the page where the xml file exists
			 */
			else if (scrapingContext.getUrl().toString().endsWith("/dc")) {
				final String xml = WebUtils.getContentAsString(scrapingContext.getUrl());
				String bibtexResult = RIS2BIB.toBibtex(xml);
				bibtexResult = BibTexUtils.addFieldIfNotContained(bibtexResult, "site-url", scrapingContext.getUrl().toString().replaceFirst("/dc", ""));
				scrapingContext.setBibtexResult(bibtexResult);
				return true;
			}
			/*
			 * if it is a HTML page (normal page)
			 */
			else if (!scrapingContext.getUrl().toString().endsWith("/dc")){
				final String xml = WebUtils.getContentAsString(new URL(scrapingContext.getUrl().toString() + "/dc"));
				String bibtexResult = RIS2BIB.toBibtex(xml);
				bibtexResult = BibTexUtils.addFieldIfNotContained(bibtexResult, "site-url", scrapingContext.getUrl().toString());
				scrapingContext.setBibtexResult(bibtexResult);
				return true;
			}
		}catch (IOException e) {
			throw new ScrapingException(e);
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
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}
}
