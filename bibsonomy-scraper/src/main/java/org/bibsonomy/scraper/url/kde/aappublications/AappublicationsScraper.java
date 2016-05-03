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
package org.bibsonomy.scraper.url.kde.aappublications;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * 
 * @author Mohammed Abed
 */
public class AappublicationsScraper extends AbstractUrlScraper{

	private static final String SITE_NAME = "Pediatrics official journal of the american academy of pediatrics";
	private static final String SITE_URL = "http://pediatrics.aappublications.org";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String HOST = "pediatrics.aappublications.org";
	private static final String HTTP = "http://";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	private static Pattern pattern = Pattern.compile("<li class=\"bibtext first\"><a href=\"(.*)\">BibTeX</a></li>");
	private static final String regex = "@.*?(.*),";
	private static final Pattern pattern2 = Pattern.compile(regex);

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		
		try {
			final String cookie = WebUtils.getCookies(sc.getUrl());
			final String pageContent = WebUtils.getContentAsString(sc.getUrl(), cookie);
			Matcher m = pattern.matcher(pageContent);
			if(m.find()) {
				String bibtexResult = WebUtils.getContentAsString(new URL(HTTP + HOST + m.group(1).toString()));
				final Matcher m2 = pattern2.matcher(bibtexResult);
				if(m2.find()) {
					final String Replacement = m2.group(1).replace(" ", "");
					bibtexResult = bibtexResult.replaceAll(regex, "@" + Replacement.concat(","));
				}
				sc.setBibtexResult(bibtexResult);
				return true;
			} else
				return false;
		} catch (IOException e) {
			throw new ScrapingException(e);
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
		return PATTERNS;
	}
}
