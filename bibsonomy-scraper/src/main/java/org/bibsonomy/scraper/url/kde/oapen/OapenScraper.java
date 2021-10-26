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
package org.bibsonomy.scraper.url.kde.oapen;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OapenScraper extends GenericRISURLScraper {


	private static final String SITE_URL = "https://oapen.org/";
	private static final String SITE_NAME = "Oapen";
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<>();

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*library.oapen.org"), Pattern.compile("handle.*")));
	}

	private static final Pattern EXPORT_CITATION = Pattern.compile("<a class=\"btn btn-default\" href=\"(.*?)\">Export citation</a>");


	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		String html = WebUtils.getContentAsString(url);

		final Matcher m_exportCitation = EXPORT_CITATION.matcher(html);
		if (m_exportCitation.find()) {
			return m_exportCitation.group(1);
		}
		return null;
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

	@Override
	public String getInfo() {
		return info;
	}


}
