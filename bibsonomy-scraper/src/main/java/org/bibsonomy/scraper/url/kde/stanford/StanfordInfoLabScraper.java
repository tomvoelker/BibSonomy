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

package org.bibsonomy.scraper.url.kde.stanford;

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
public class StanfordInfoLabScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "Stanford InfoLab Publication Server";
	private static final String SITE_URL  = "http://ilpubs.stanford.edu";

	/**
	 * INFO
	 */
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "ilpubs.stanford.edu"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	private static final Pattern ID_PATTERN = Pattern.compile(".*?/(\\d++)");

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

	/*@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		Matcher idMatcher = ID_PATTERN.matcher(scrapingContext.getUrl().toString());
		if (!idMatcher.find()) throw new ScrapingException("Path currently not supported.");
		String downloadURL = "http://ilpubs.stanford.edu:8090/cgi/export/" + idMatcher.group(1) + "/BibTeX/ilprints-eprint-1015.bib";
		try {
			URL url = new URL(downloadURL);
			String bibtex = WebUtils.getContentAsString(url);
			if (!present(bibtex)) return false;
			scrapingContext.setBibtexResult(bibtex);
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
		return false;
	}*/

	@Override
	public String getDownloadURL(URL url) throws ScrapingException {
		Matcher idMatcher = ID_PATTERN.matcher(url.toString());
		if (idMatcher.find()){
			return"http://ilpubs.stanford.edu:8090/cgi/export/" + idMatcher.group(1) + "/BibTeX/ilprints-eprint-1015.bib";
		}
		return  null;
	}

}
