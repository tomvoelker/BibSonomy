/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.sciencemag;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.generic.LiteratumScraper;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author clemens
 */
public class ScienceMagScraper extends LiteratumScraper {
	private static final String SITE_NAME = "Science";
	private static final String SITE_HOST = "science.org";
	private static final String SITE_URL = "https://www.science.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new LinkedList<>();

	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), EMPTY_PATTERN));

	}
	private static final Pattern NO_COMMA_AFTER_DOI_PATTERN = Pattern.compile("(doi = \\{.*}[^,])");


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
		return URL_PATTERNS;
	}

	@Override
	protected String postProcessBibtex(ScrapingContext scrapingContext, String bibtex) {
		Matcher m_noComma = NO_COMMA_AFTER_DOI_PATTERN.matcher(bibtex);
		if (m_noComma.find()){
			return bibtex.replace(m_noComma.group(1), m_noComma.group(1) + "," );
		}
		return super.postProcessBibtex(scrapingContext, bibtex);
	}
}
