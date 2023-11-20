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
package org.bibsonomy.scraper.url.kde.editlib;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author wbi
 */
public class EditLibScraper extends GenericBibTeXURLScraper {
	private static final String EDITLIB_OLD_HOST  = "editlib.org";
	private static final String EDITLIB_HOST  = "learntechlib.org";
	private static final String EDITLIB_PATH  = "/index.cfm";

	private static final String SITE_NAME = "LearnTechLib";
	private static final String SITE_URL = "https://" + EDITLIB_HOST + "/";

	private static final String info = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();

	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + EDITLIB_HOST), Pattern.compile(EDITLIB_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + EDITLIB_OLD_HOST), Pattern.compile(EDITLIB_PATH + ".*")));
	}

	private static final Pattern PAPER_ID_PATTERN = Pattern.compile("paper_id=(\\d+)");
	private static final String DOWNLOAD_URL = "https://www.learntechlib.org/?fuseaction=Reader.ExportAbstract&citationformat=BibTex&paper_id=";

	
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
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		Matcher m_paperId = PAPER_ID_PATTERN.matcher(url.toString());
		if (!m_paperId.find()){
			throw new ScrapingException("paper-id not found");
		}
		return DOWNLOAD_URL + m_paperId.group(1);
	}
}
