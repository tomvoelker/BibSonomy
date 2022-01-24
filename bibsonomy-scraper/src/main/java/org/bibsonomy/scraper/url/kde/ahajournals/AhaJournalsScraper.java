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
package org.bibsonomy.scraper.url.kde.ahajournals;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.generic.LiteratumScraper;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Mohammed Abed
 */
public class AhaJournalsScraper extends LiteratumScraper {

	private static final String SITE_NAME = "Aha Journals";
	private static final String SITE_URL = "https://www.ahajournals.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String AHA_JOURNALS_HOST = "ahajournals.org";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + AHA_JOURNALS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	private static final Pattern NO_COMMA_AFTER_DOI_PATTERN = Pattern.compile("(doi = \\{.*}[^,])");

	@Override
	protected String postProcessBibtex(ScrapingContext scrapingContext, String bibtex) {
		Matcher m_noComma = NO_COMMA_AFTER_DOI_PATTERN.matcher(bibtex);
		if (m_noComma.find()){
			return bibtex.replace(m_noComma.group(1), m_noComma.group(1) + "," );
		}
		return super.postProcessBibtex(scrapingContext, bibtex);
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
