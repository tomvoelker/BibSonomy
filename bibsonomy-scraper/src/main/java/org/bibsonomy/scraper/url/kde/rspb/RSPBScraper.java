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
package org.bibsonomy.scraper.url.kde.rspb;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for publications from http://rspb.royalsocietypublishing.org/
 *
 * @author Johannes
 */
public class RSPBScraper extends GenericBibTeXURLScraper{

	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");
	private static final String SITE_NAME = "Proceedings of the Royal Society Biology";
	private static final String SITE_HOST = "rspb.royalsocietypublishing.org";
	private static final String SITE_URL = "http://" + SITE_HOST;
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + SITE_HOST), 
			Pattern.compile("/content" + ".*")
			));
	
	
	
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
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		try {
			final String content = WebUtils.getContentAsString(url, cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				return SITE_URL + m.group(1);
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	
}
