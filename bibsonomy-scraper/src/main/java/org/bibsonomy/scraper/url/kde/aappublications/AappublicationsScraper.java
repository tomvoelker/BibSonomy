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

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.generic.BibTeXLinkOnPageScraper;

import bibtex.parser.BibtexParser;

/**
 * scraper for the
 * @author Mohammed Abed
 * @author dzo
 */
public class AappublicationsScraper extends BibTeXLinkOnPageScraper {

	private static final String SITE_NAME = "Journals of the American Academy of Pediatrics (AAP)";
	private static final String SITE_HOST = "aappublications.org";
	private static final String SITE_URL  = "https://" + SITE_HOST;
	private static final String SITE_INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN)
	);

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
		return SITE_INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	
	/**
	 * The BibTeX returned contains an id with space, e.g., "@article {de St Mauricee1186" which
	 * need to be fixed in order to be accepted by {@link BibtexParser}.
	 * 
	 * @param bibtex
	 * @return
	 */
	protected static String fixSpaceInId(final String bibtex) {
		final int index = bibtex.indexOf("\n");
		return bibtex.substring(0, index).replaceAll(" ", "") + bibtex.substring(index);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return fixSpaceInId(bibtex);
	}
}
