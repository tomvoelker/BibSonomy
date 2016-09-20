/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.iucr;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * scraper for jornals from iucr.org. Because of the frame structure of 
 * journals.iucr.org pages only issues can be scraped which are sperated in a
 * another tab then the journal itself. The url of the issue in the new tab 
 * points dirctly to the issue and not to jounal page (if you open the issue in 
 * the same tab, then the url in the navgationbar will still point to the journal 
 * page and scraping is not possible.
 * 
 * example:
 * we want the second issue from this journal ->
 * http://journals.iucr.org/b/issues/2008/03/00/issconts.html
 * 
 * if we open the doi-link in the same tab, we get this url ->
 * http://journals.iucr.org/b/issues/2008/03/00/issconts.html
 * 
 * the issue will only be loaded in the central frame of the page and has no 
 * effect on the url. So we cannot recognize which issue was selected by the user.
 * If the user open the doi-link in a new tab, only the content of the central 
 * frame will be loaded and we can get the URL to the page of the issue. Like this
 * one ->
 * http://scripts.iucr.org/cgi-bin/paper?S0108768108005119
 * 
 * The rest is simple: extract the cnor from the url
 * like this -> http://scripts.iucr.org/cgi-bin/biblio?Action=download&cnor=ck5030&saveas=BIBTeX
 * 
 * @author tst
 */
public class IucrScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(IucrScraper.class);
	
	private static final String SITE_NAME = "International Union of Crystallography";
	private static final String SITE_URL = "http://www.iucr.org/";
	private static final String INFO = "Scraper for journals from the " + href(SITE_URL, SITE_NAME) +".";
	
	private static final String HOST = "iucr.org";
	
	private static final List<Pair<Pattern, Pattern>> patterns = Arrays.asList(
		new Pair<Pattern, Pattern>(Pattern.compile(".*scripts." + HOST), AbstractUrlScraper.EMPTY_PATTERN),
		new Pair<Pattern, Pattern>(Pattern.compile(".*journals." + HOST), AbstractUrlScraper.EMPTY_PATTERN)
	);

	/** Download link */
	private static final String DOWNLOAD_LINK_PART = "http://scripts.iucr.org/cgi-bin/biblio?Action=download&saveas=BIBTeX&cnor=";
	
	private static final Pattern DC_LINK_PATTERN = Pattern.compile("<meta name=\"DC.link\" content=\"(.*)\" />");

	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL, java.lang.String)
	 */
	@Override
	protected String getDownloadURL(final URL url, String cookies) throws ScrapingException, IOException {
		try {
			final String pageContent = WebUtils.getContentAsString(url);
			final Matcher matcher = DC_LINK_PATTERN.matcher(pageContent);
			if (matcher.find()) {
				final String id = matcher.group(1);
				if (present(id)) {
					return DOWNLOAD_LINK_PART + id.replace("http://scripts.iucr.org/cgi-bin/paper?", "");
				}
			}
		} catch (final IOException e) {
			log.error("can't get pape content", e);
		}
		return null;
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

}
