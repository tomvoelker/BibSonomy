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

package org.bibsonomy.scraper.url.kde.prola;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.SimpleGenericURLScraper;


/**
 * Scraper for prola.aps.org. It scrapes selected bibtex snippets and selected articles.
 * @author tst
 */
public class ProlaScraper extends SimpleGenericURLScraper {

	private static final String SITE_NAME = "PROLA";
	private static final String PROLA_APS_URL_BASE = "http://prola.aps.org";
	private static final String SITE_URL = PROLA_APS_URL_BASE+"/";
	private static final String INFO = "For selected BibTeX snippets and articles from " + href(SITE_URL , SITE_NAME)+".";

	/*
	 * needed URLs and components
	 */
	private static final String PROLA_APS_HOST = ".aps.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PROLA_APS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	@Override
	public String getInfo() {
		return INFO;
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
	public String getBibTeXURL(URL url) {
		return url.toString().replace("abstract", "export");
	}
}
