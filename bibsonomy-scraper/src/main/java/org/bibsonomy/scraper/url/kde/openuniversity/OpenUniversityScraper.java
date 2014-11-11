/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.openuniversity;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author hagen
 */
public class OpenUniversityScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "The Open University";
	private static final String SITE_URL = "http://www.open.ac.uk/";
	private static final String INFO = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";
	
	private static final String PUBLICATION_HOST = "oro.open.ac.uk";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + PUBLICATION_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	private static final Pattern ID_PATTERN = Pattern.compile("/(\\d++)/");
	
	private static final String DOWNLOAD_LINK_PREFIX = "http://oro.open.ac.uk/cgi/export/eprint/";
	private static final String DOWNLOAD_LINK_SUFFIX = "/BibTeX/oro-eprint-19554.bib";

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
	public String getDownloadURL(URL url) throws ScrapingException {
		Matcher m = ID_PATTERN.matcher(url.toExternalForm());
		if (!m.find()) return null;
		return DOWNLOAD_LINK_PREFIX + m.group(1) + DOWNLOAD_LINK_SUFFIX;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
