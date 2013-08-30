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
package org.bibsonomy.scraper.url.kde.firstmonday;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;


/**
 * @author Haile
 * @version $Id$
 */
public class FirstMondayScraper extends AbstractUrlScraper{
	private final Log log = LogFactory.getLog(FirstMondayScraper.class);
	private static final String SITE_NAME = "First Monday";
	private static final String SITE_URL = "http://firstmonday.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final String BIBTEX_PATH = "/ojs/index.php/fm/rt/captureCite/";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "firstmonday.org/index"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern ID_PATTERN = Pattern.compile("\\d+(/\\d+)*?");

	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		
		final URL url = scrapingContext.getUrl();		
		final String bibTexUrl= getBibTexURL(url);
		
		if (!present(bibTexUrl)) {
			log.error("can't parse publication BibTex URL");
			return false;
		}
		try {
				//The BibTex is eclosed in side pre tag
				//jsoup is used for parsing
				// FIXME: there is currently no jsoup in bibsonomy s this does not compile - why not use jtidy as it is already in the classpath?
				String bibTex = null; //WebUtils.getContentAsString(bibTexUrl);
				//Document document = Jsoup.parse(bibTex);
				//bibTex = document.getElementsByTag("pre").text();
		
				if(present(bibTex)){
					scrapingContext.setBibtexResult(bibTex);
					return true;
				} else {
					throw new ScrapingFailureException("getting bibtex failed");
				}
		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}
	}
	/*
	 * Construct a url
	 * return the url to scrapeInternal 
	 */
	private String getBibTexURL(final URL url) {
		final String host = url.getHost();
		final Matcher match = ID_PATTERN.matcher(url.toString());		
		if (match.find()) {
			String id = match.group(0);
			if (match.groupCount() == 1) id = id +"/0";
			return  "http://" + host + BIBTEX_PATH + id + "/BibtexCitationPlugin";
		}else
			return null;
	}
	@Override
	public String getInfo() {
		return INFO;
	}	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
