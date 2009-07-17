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

package org.bibsonomy.scraper.url.kde.rsoc;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * @version $Id$
 */
public class RSOCScraper extends AbstractUrlScraper {

	private static final String info = "RSOC Scraper: This Scraper parses a journal from " + href("http://royalsocietypublishing.org/", "Royal Society Publishing");

	private static final Pattern downloadLinkPattern = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download to citation manager</a>");


	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(
			Pattern.compile(".*" + "royalsocietypublishing.org"), 
			Pattern.compile("/content" + ".*")
	));

	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {
			final String content = WebUtils.getContentAsString(sc.getUrl());

			// get link to download page
			final Matcher downloadLinkMatcher = downloadLinkPattern.matcher(content);
			final String downloadLink;
			if(downloadLinkMatcher.find()) // add type=bibtex to the end of the link
				downloadLink = "http://" + sc.getUrl().getHost() + downloadLinkMatcher.group(1) + "&type=bibtex";
			else
				throw new ScrapingFailureException("Download link is not available");

			// download bibtex directly
			final String bibtex = WebUtils.getContentAsString(new URL(downloadLink));
			if (bibtex != null) {
				// clean up (whitespaces in bibtex key)
				final int indexOfComma = bibtex.indexOf(",");
				
				final String key = bibtex.substring(0, indexOfComma).replaceAll("\\s", "");
				final String rest = bibtex.substring(indexOfComma);
				
				sc.setBibtexResult(key + rest);
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

}
