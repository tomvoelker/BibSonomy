/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.editlib;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author wbi
 */
public class EditLibScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "Ed/ITLib";
	private static final String EDITLIB_HOST_NAME  = "http://www.editlib.org";
	private static final String SITE_URL = EDITLIB_HOST_NAME+"/";

	private static final String info = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final String EDITLIB_HOST  = "editlib.org";
	private static final String EDITLIB_PATH  = "/index.cfm";
	private static final String EDITLIB_ABSTRACT_PATH = "/index.cfm?fuseaction=Reader.ViewAbstract&paper_id=";
	private static final String EDITLIB_BIBTEX_PATH = "/index.cfm?fuseaction=Reader.ChooseCitationFormat&paper_id=";
	private static final String EDITLIB_BIBTEX_DOWNLOAD_PATH = "/index.cfm/files/citation_{id}.bib?fuseaction=Reader.ExportAbstract&citationformat=BibTex&paper_id=";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + EDITLIB_HOST), Pattern.compile(EDITLIB_PATH + ".*")));
	
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
		String id = null;
		String sturl = url.toString();
		if(sturl.startsWith(EDITLIB_HOST_NAME + EDITLIB_ABSTRACT_PATH)) {
			id = sturl.substring(sturl.indexOf(EDITLIB_ABSTRACT_PATH) + EDITLIB_ABSTRACT_PATH.length());
			return EDITLIB_HOST_NAME + EDITLIB_BIBTEX_DOWNLOAD_PATH.replace("{id}", id) + id;
		}
		if(sturl.toString().startsWith(EDITLIB_HOST_NAME + EDITLIB_BIBTEX_PATH)) {
			id = sturl.substring(sturl.indexOf(EDITLIB_BIBTEX_PATH) + EDITLIB_BIBTEX_PATH.length());
			return EDITLIB_HOST_NAME + EDITLIB_BIBTEX_DOWNLOAD_PATH.replace("{id}", id) + id;
		}
		return null;
	}
}
