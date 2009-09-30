/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.url.kde.bmj;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * @version $Id$
 */
public class BMJScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "BMJ";
	private static final String BMJ_HOST_NAME  = "http://www.bmj.com";
	private static final String SITE_URL = BMJ_HOST_NAME+"/";

	private static final String info = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final String BMJ_HOST  = "bmj.com";
	private static final String BMJ_ABSTRACT_PATH = "/cgi/content/full/";
	private static final String BMJ_BIBTEX_PATH = "/cgi/citmgr?gca=";
	private static final String BMJ_BIBTEX_DOWNLOAD_PATH = "/cgi/citmgr?type=bibtex&gca=";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BMJ_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		String url = sc.getUrl().toString();
		String id = null;

		if(url.startsWith(BMJ_HOST_NAME + BMJ_ABSTRACT_PATH)) {
			id = "bmj;" + url.substring(url.indexOf("/full/") + 6);
		}

		if(url.startsWith(BMJ_HOST_NAME + BMJ_BIBTEX_PATH)) {
			id = url.substring(url.indexOf(BMJ_BIBTEX_PATH) + BMJ_BIBTEX_PATH.length());
		}

		try {
			final String bibResult = WebUtils.getContentAsString(new URL(BMJ_HOST_NAME + BMJ_BIBTEX_DOWNLOAD_PATH + id)).trim().replaceFirst(" ", "");
			if (bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
