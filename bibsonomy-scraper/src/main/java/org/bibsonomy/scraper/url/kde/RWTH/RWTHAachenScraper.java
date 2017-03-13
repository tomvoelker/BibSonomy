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
package org.bibsonomy.scraper.url.kde.RWTH;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * this scraper supports download links from the host rwth-aachen.de
 * 
 * @author Mohammed Abed
 */
public class RWTHAachenScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "RWTH Aachen University";
	private static final String SITE_URL = "https://www.rwth-aachen.de";
	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	private static final String RWTH_HOST = "publications.rwth-aachen.de";
	private static final String DOWNLOAD_BIBTEX_FORMAT = "/export/hx?ln=de";
	private static final Pattern PATTERN_TO_PICK_BIBTEX_FROM_PAGE_CONTENT = Pattern.compile("<pre>(.+?)</pre>", Pattern.DOTALL);

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ RWTH_HOST), Pattern.compile("/record/")));
	}
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		try {
			final String content = WebUtils.getContentAsString(sc.getUrl() + DOWNLOAD_BIBTEX_FORMAT);
			final Matcher m = PATTERN_TO_PICK_BIBTEX_FROM_PAGE_CONTENT.matcher(content);
			if (m.find()) {
				final String bibtexresult = m.group(1);
				sc.setBibtexResult(bibtexresult);
				return true;
			}
		} catch (final IOException e) {
			throw new ScrapingFailureException(e);
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
		return patterns;
	}
}
