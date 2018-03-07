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
package org.bibsonomy.scraper.url.kde.proeuclid;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.ExamplePrototype;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

/**
 * @author rja
 */
public class ProjectEuclidScraper extends AbstractUrlScraper implements ExamplePrototype {

	private static final String SITE_NAME = "Astronomy and Astrophysics";
	private static final String SITE_HOST = "projecteuclid.org";
	private static final String SITE_URL = "https://" + SITE_HOST + "/";
	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	private static final String DOWNLOAD_URL = SITE_URL + "export_citations";
	private static final Pattern ID_PATTERN = Pattern.compile("/(.+)$");
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		final Matcher m = ID_PATTERN.matcher(scrapingContext.getUrl().getPath());
		if (m.find()) {
			try {
				final String id = UrlUtils.safeURIEncode(m.group(1));
				final String postContent = "h=" + id + "&format=bibtex";
				scrapingContext.setBibtexResult(WebUtils.getContentAsString(DOWNLOAD_URL, null, postContent, null));
			} catch (IOException e) {
				throw new ScrapingException(e);
			}
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
