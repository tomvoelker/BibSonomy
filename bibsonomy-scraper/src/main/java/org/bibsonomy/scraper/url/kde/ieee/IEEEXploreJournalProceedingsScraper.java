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
package org.bibsonomy.scraper.url.kde.ieee;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper;

/**
 * Scraper for journals from IEEE Explore.
 * 
 * @author rja
 */
public class IEEEXploreJournalProceedingsScraper extends AbstractGenericFormatURLScraper {
	private static final String SITE_NAME = "IEEEXplore Journals";
	private static final String SITE_URL = "https://ieeexplore.ieee.org/";
	private static final String info = "This scraper creates a BibTeX entry for the journals and proceedings at " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String IEEE_HOST = "ieeexplore.ieee.org";
	private static final String IEEE_PATH = "document";

	private static final Pattern ID_PATTERN = Pattern.compile("/document/([0-9]+).*");

	
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<>(Pattern.compile(".*" + IEEE_HOST), Pattern.compile("/" + IEEE_PATH + ".*")));
	
	@Override
	public String getInfo() {
		return info;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
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
	protected String getDownloadURL(final URL url, final String cookies) throws ScrapingException, IOException {
		final String path = url.getPath();

		final Matcher idMatcher = ID_PATTERN.matcher(path);
		if (idMatcher.find()) {
			final String recordId = idMatcher.group(1);
			return "https://ieeexplore.ieee.org/rest/search/citation/format?recordIds=" + recordId + "&download-format=download-bibtex";
		}
		return null;
	}

	@Override
	protected String convert(String downloadResult) {
		final JSONObject responseAsJson = JSONObject.fromObject(downloadResult);
		return responseAsJson.getString("data");
	}
}