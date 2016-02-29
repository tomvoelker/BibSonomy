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
package org.bibsonomy.scraper.url.kde.degruyter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringEscapeUtils;
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
 */
public class DeGruyterScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "De Gruyter";
	private static final String SITE_URL = "http://www.degruyter.com/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final Pattern TAC = Pattern.compile("<input value=\"(.*)\" name=\"t:ac\" type=\"hidden\"/>");
	private static final Pattern TFORMDATA = Pattern.compile("<input value=\"(H.*=)\" name=\"t:formdata\" type=\"hidden\"/>");
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "degruyter.com"), AbstractUrlScraper.EMPTY_PATTERN));
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		final URL url = scrapingContext.getUrl();
		
		try {
			final String inRIS = getCitationInRIS(url.toString());
			final String newRIS = addHTTP(inRIS);
			final RisToBibtexConverter con = new RisToBibtexConverter();
			final String bibtex = con.toBibtex(newRIS);
			
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final IOException e) {
			throw new InternalFailureException(e);
		}
	}
	
	private static String addHTTP(String ris) {
		final String regex = "UR  - (.*)";
		Pattern DOI_PATTERN_FROM_URL = Pattern.compile(regex);
		final Matcher m = DOI_PATTERN_FROM_URL.matcher(ris);
		if (m.find()) {
			String newURL = "http:" + m.group(1);
			ris = ris.replaceAll(regex, "UR  - " + newURL);
		}
		return ris;
	}
	
	private static String getCitationInRIS(final String stURL) throws IOException {
		final URL url = new URL(stURL);
		final String path = "http://" + url.getHost().toString() +  url.getPath().toString().replace("/", "$002f").replace("$002fview$002f", "/dg/cite/$002f") + "?nojs=true";
		
		final URL postURL = new URL("http://" + url.getHost().toString() + "/dg/cite.form");
		
		final String html = WebUtils.getContentAsString(path);
		
		final Matcher m_tac = TAC.matcher(html);
		String tac = "";
		if (m_tac.find()) 
			tac = m_tac.group(1);
		
		final Matcher m_formdata = TFORMDATA.matcher(html);
		String formdata = "";
		if(m_formdata.find())
			formdata = m_formdata.group(1);

		final PostMethod post = new PostMethod(postURL.toExternalForm());
		post.addParameters(new NameValuePair[] {
				new NameValuePair("t:ac", StringEscapeUtils.unescapeHtml(tac)),
				new NameValuePair("t:formdata", StringEscapeUtils.unescapeHtml(formdata)),
				new NameValuePair("previewFormat","apa"),
				new NameValuePair("submit", "Export"),
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