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
package org.bibsonomy.scraper.url.kde.ingenta;

import bibtex.parser.BibtexParser;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Scraper for ingentaconnect.
 * @author rja
 *
 */
public class IngentaconnectScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "Ingentaconnect";
	private static final String SITE_URL = "http://www.ingentaconnect.com/";
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final String INGENTA_HOST = "ingentaconnect.com";

	private static final Pattern EXPORT_PATTERN = Pattern.compile("<a href=\"(.*?)\".*?title=\"Export reference in BibTEX format\">BibT<sub>E</sub>X</a>");

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + INGENTA_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	// for fixSpaceInKey to find spaces in BibTeX keys 
	private static final Pattern PATTERN_KEY_SPACE = Pattern.compile("^(\\w+) (\\w+ =)", Pattern.MULTILINE);

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		String pageContent = WebUtils.getContentAsString(url);
		Matcher m_export = EXPORT_PATTERN.matcher(pageContent);
		if (m_export.find()){
			return SITE_URL + m_export.group(1);
		}
		return null;
	}


	/**
	 * The BibTeX returned contains keys with space, e.g., "publication date" which
	 * need to be fixed in order to be accepted by {@link BibtexParser}.
	 * 
	 * @param bibtex
	 * @return
	 */
	protected static String fixSpaceInKey(final String bibtex) {
		final Matcher matcher = PATTERN_KEY_SPACE.matcher(bibtex);
		if (matcher.find()) {
			return matcher.replaceFirst("$1$2");
		}
		return bibtex;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return fixSpaceInKey(bibtex);
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
