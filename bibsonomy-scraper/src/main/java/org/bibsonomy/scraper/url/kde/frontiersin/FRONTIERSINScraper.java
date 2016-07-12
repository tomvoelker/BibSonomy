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
package org.bibsonomy.scraper.url.kde.frontiersin;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * 
 * @author Mohammed Abed
 */
public class FRONTIERSINScraper extends GenericBibTeXURLScraper {

	private static final Log log = LogFactory.getLog(FRONTIERSINScraper.class);
	private static final String SITE_NAME = "Cold Spting Harbor Perspetives in Biology";
	private static final String SITE_URL = "http://cshperspectives.cshlp.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String FRONTIERSIN_HOST = "journal.frontiersin.org";
	private static final Pattern PATTERN_FOR_BIBTEX_URL = Pattern.compile("<a data-test-id=\"article-bibtex\" href=\"(.*?)\"");

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + FRONTIERSIN_HOST), Pattern.compile("/article")));
	}
	
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		try {
			final String pageContent = WebUtils.getContentAsString(url);
			final Matcher m = PATTERN_FOR_BIBTEX_URL.matcher(pageContent); 
			if (m.find()) {
				final String downloadURL = m.group(1);
				return downloadURL.replaceAll("amp;", "");
			}
		} catch (final IOException e) {
			log.warn("page conteent could not be found", e);
		}
		return null;
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
