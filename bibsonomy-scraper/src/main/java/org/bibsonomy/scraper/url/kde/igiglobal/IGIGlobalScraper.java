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
package org.bibsonomy.scraper.url.kde.igiglobal;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 *
 */
public class IGIGlobalScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "IGI Global";
	private static final String SITE_URL = "http://www.igi-global.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final Pattern EVENTVALIDATION = Pattern.compile("<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"(.*?)\" />");
	private static final Pattern EVENTTARGET = Pattern.compile("<input type=\"hidden\" name=\"__EVENTTARGET\" id=\"__EVENTTARGET\" value=\"(.*?)\" />");
	private static final Pattern EVENTARGUMENT = Pattern.compile("<input type=\"hidden\" name=\"__EVENTARGUMENT\" id=\"__EVENTARGUMENT\" value=\"(.*?)\" />");
	private static final Pattern VIEWSTATE = Pattern.compile("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"(.*?)\" />");
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "igi-global.com"), AbstractUrlScraper.EMPTY_PATTERN));
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		final URL url = scrapingContext.getUrl();
		
		try {
			final String inRIS = getCitationInRIS(url.toString());
			final RisToBibtexConverter con = new RisToBibtexConverter();
			final String bibtex = con.toBibtex(inRIS);
			System.out.println(inRIS);
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}
	}
	private String getCitationInRIS(final String url) throws Exception {
		final String html = WebUtils.getContentAsString(url);
		
		Matcher m_eventvalidation = EVENTVALIDATION.matcher(html);
		String eventvalidation = "";
		if(m_eventvalidation.find())
			eventvalidation = m_eventvalidation.group(1);
		
		Matcher m_eventtarget = EVENTTARGET.matcher(html);
		String eventtarget = "";
		if(m_eventtarget.find())
			eventtarget = m_eventtarget.group(1);
	
		Matcher m_eventargument = EVENTARGUMENT.matcher(html);
		String eventargument = "";
		if (m_eventargument.find())
			eventargument = m_eventargument.group(1);
		
		Matcher m_viewstate = VIEWSTATE.matcher(html);
		String viewstate = "";
		if(m_viewstate.find())
			viewstate = m_viewstate.group(1);

		final PostMethod post = new PostMethod(url);
		post.addParameters(new NameValuePair[] {
				new NameValuePair("ctl00$ctl00$ucBookstoreSearchTop$txtSearch", "Search title, author, ISBN..."),
				new NameValuePair("ctl00$ctl00$cphMain$cphFeatured$ucCiteContent$lnkSubmitToEndNote.x", "30"),
				new NameValuePair("ctl00$ctl00$cphMain$cphFeatured$ucCiteContent$lnkSubmitToEndNote.y", "7"),
				new NameValuePair("ctl00$ctl00$cphMain$cphSidebarRightTop$ucInfoSciOnDemandSidebar$txtSearchPhrase", "Full text search term(s)"),
				new NameValuePair("__EVENTVALIDATION", eventvalidation),
				new NameValuePair("__EVENTTARGET", eventtarget),
				new NameValuePair("__EVENTARGUMENT", eventargument),
				new NameValuePair("__VIEWSTATE", viewstate)
		});

		return WebUtils.getPostContentAsString(WebUtils.getHttpClient(), post);
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
	public String getInfo() {
		return INFO;
	}
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
}