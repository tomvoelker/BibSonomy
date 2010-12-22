/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.url.kde.rsoc;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.generic.CitationManagerScraper;

/**
 * @author wbi
 * @version $Id$
 */
public class RSOCScraper extends CitationManagerScraper {
	public static final Pattern downloadLinkPattern = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download to citation manager</a>");
	
	public static final  String SITE_NAME = "Royal Society Publishing";
	
	public static final  String SITE_URL = "http://royalsocietypublishing.org/";
	
	public static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	public static final  List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(
			Pattern.compile(".*" + "royalsocietypublishing.org"), 
			Pattern.compile("/content" + ".*")
		));

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
	public Pattern getDownloadLinkPattern() {
		return downloadLinkPattern;
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
