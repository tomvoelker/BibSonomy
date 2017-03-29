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
package org.bibsonomy.scraper.url.kde.psycontent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class PsyContentScraper extends AbstractUrlScraper implements ReferencesScraper{

	private static final String SITE_NAME = "Psy CONTENT";
	private static final String PSYCONTENT_HOST_NAME  = "http://psycontent.metapress.com";	
	private static final String SITE_URL  = PSYCONTENT_HOST_NAME+"/";
	private static final String info = "This Scraper parses a publication from "+ href(SITE_URL, SITE_NAME)+".";

	private static final String PSYCONTENT_HOST  = "psycontent.metapress.com";
	private static final String PSYCONTENT_ABSTRACT_PATH = "/content/";
	private static final String PSYCONTENT_RIS_PATH = "/export.mpx?mode=ris&code=";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PSYCONTENT_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern REFERENCES_PATTERNS = Pattern.compile("(?s)<h4>References</h4><table><!--reference c1-->(.*)</td></tr></table>");
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String url = sc.getUrl().toString();

		//get the ID of the article
		String id = null;
		id = url.substring(url.indexOf(PSYCONTENT_ABSTRACT_PATH) + PSYCONTENT_ABSTRACT_PATH.length(), url.indexOf("/?p="));

		//let's see if we got an ID
		if(id != null) {

			String downloadLink = PSYCONTENT_HOST_NAME + PSYCONTENT_RIS_PATH + id;
			//Store the cookies in a String
			String cookies = null;
			try {
				cookies = getCookies(sc.getUrl());
			} catch (IOException ex) {
				throw new InternalFailureException("Could not store cookies from " + sc.getUrl());
			}

			String risFile = null;
			try {
				risFile = getContent(new URL(downloadLink), cookies); 
			} catch (IOException ex) {
				throw new InternalFailureException("The url "+ downloadLink + " is not valid");
			}

			if(risFile != null) {
				//replace all CR+LF to LF
				risFile = risFile.replaceAll("\r\n", "\n");

				//convert the ris file to bibtex
				String bibtex = null;
				bibtex = (new RisToBibtexConverter()).toBibtex(risFile);

				if(bibtex != null) {
					sc.setBibtexResult(bibtex);
					return true;
				} else {
					throw new ScrapingFailureException("Conversion from Ris to bibtex failed");
				}
			} else {
				throw new ScrapingFailureException("Ris download failed. Result is null!");
			}
		} else {
			// missing id
			throw new PageNotSupportedException("ID for donwload link is missing.");
		}
	}

	/** FIXME: refactor
	 * @param queryURL
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	private String getContent(URL queryURL, String cookie) throws IOException {

		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		//insert cookie
		urlConn.setRequestProperty("Cookie", cookie);

		urlConn.connect();

		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();

		return out.toString();
	}

	/** FIXME: refactor
	 * @param queryURL
	 * @return
	 * @throws IOException
	 */
	private String getCookies(URL queryURL) throws IOException {
		HttpURLConnection urlConn = null;

		urlConn = (HttpURLConnection) queryURL.openConnection();

		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);

		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		urlConn.connect();
		/*
		 * extract cookie from connection
		 */
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");

		StringBuffer cookieString = new StringBuffer();

		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}

		urlConn.disconnect();

		return cookieString.toString();
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = REFERENCES_PATTERNS.matcher(WebUtils.getContentAsString(sc.getUrl().toString().replace('?' + sc.getUrl().getQuery(), "fulltext.html")));
			if(m.find()) {
				sc.setReferences(m.group(1));
				return true;
			}
		} catch(IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}
}
