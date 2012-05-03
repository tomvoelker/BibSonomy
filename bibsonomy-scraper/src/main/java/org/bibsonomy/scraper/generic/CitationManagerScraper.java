/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import bibtex.parser.ParseException;

/**
 * Superclass for scraping pages, using the same system like PNAS, RSOC or ScienceMag.
 * 
 * @author clemens
 * @version $Id$
 */
public abstract class CitationManagerScraper extends AbstractUrlScraper {
		
	/**
	 * @return The pattern to find the download link.
	 */
	public abstract Pattern getDownloadLinkPattern();

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			String downloadLink = buildDownloadLink(sc.getUrl(), WebUtils.getContentAsString(sc.getUrl()));
			
			// download bibtex directly
			final String bibtexMessage = WebUtils.getContentAsString(new URL(downloadLink));
			if (bibtexMessage != null) {
				try {
					String bibtex = BibTexUtils.addBibtexKeyIfNotPresent(bibtexMessage);
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toExternalForm());
					sc.setBibtexResult(bibtex);
					return true;
				} catch (ParseException ex) {
					throw new ScrapingException(ex);
				}
			}

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		return false;
	}
	
	protected String buildDownloadLink(URL url, String content) throws ScrapingFailureException {

		// get link to download page
		final Matcher downloadLinkMatcher = getDownloadLinkPattern().matcher(content);
		final String downloadLink;
		if(downloadLinkMatcher.find()) // add type=bibtex to the end of the link
			downloadLink = "http://" + url.getHost() + downloadLinkMatcher.group(1) + "&type=bibtex";
		else
			throw new ScrapingFailureException("Download link is not available");
		return downloadLink;
	}

}
