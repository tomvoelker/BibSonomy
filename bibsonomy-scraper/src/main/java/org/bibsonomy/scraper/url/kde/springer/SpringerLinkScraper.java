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

package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.id.DOIUtils;



/** Scraper für SpringerLink.
 * 
 * @author rja
 *
 */
public class SpringerLinkScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "SpringerLink";
	private static final String SITE_URL = "http://springerlink.com/";
	
	/*
	 * FIXME: remove
	 */
	private static final String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)";	

	private static final Pattern CONTENT_PATTERN = Pattern.compile("content/(.+?)(/|$)");
	private static final Pattern ID_PATTERN = Pattern.compile("id=([^\\&]*)");

	private static final String SPRINGER_CITATION_HOST_COM = "springerlink.com";
	private static final String SPRINGER_CITATION_HOST_DE = "springerlink.de";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	
	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();
	
	static{
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_COM), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_DE), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);

			try {
				final String url = sc.getUrl().toString();
				/*
				 *  extract document ID
				 */
				final String docid;
				final Matcher mContent = CONTENT_PATTERN.matcher(url);
				final Matcher mId = ID_PATTERN.matcher(url);
				if (mContent.find()) {
					docid = mContent.group(1);
				} else if (mId.find()) {
					docid = mId.group(1);
				} else {
					/*
					 * could not find ID
					 */
					return false;
				}
				
				/* 
				 * create query URL
				 */
				final URL queryURL = new URL((SITE_URL + "export.mpx?code=" + docid + "&mode=ris"));

				/*
				 * download RIS file
				 */
				final String RisResult = getRisFromSpringer(queryURL, getCookieFromSpringer());

				/*
				 * convert ris to bibtex
				 */
				String bibtexEntries = new RisToBibtexConverter().RisToBibtex(RisResult);
				bibtexEntries = StringEscapeUtils.unescapeHtml(bibtexEntries);

				/*
				 * cleanup doi
				 */
				bibtexEntries = DOIUtils.cleanDOI(bibtexEntries);
				
				/*
				 * Job done
				 */
				if (present(bibtexEntries)) {
					sc.setBibtexResult(bibtexEntries);
					return true;
				} 
				
				throw new ScrapingFailureException("getting bibtex failed");
			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			} catch (IOException e) {
				throw new InternalFailureException(e);
			}
	}
	
	

	/** 
	 * FIXME: refactor
	 * Downloads the RIS file for the specified URL from Springer, returns it as a String.
	 * 
	 * @param queryURL
	 * @param cookie needed to download the file.
	 * @return
	 * @throws IOException
	 */
	private String getRisFromSpringer(URL queryURL, String cookie) throws IOException {
		/*
		 * get RIS-file from springer
		 */
		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", userAgent);
		//insert cookie
		urlConn.setRequestProperty("Cookie", cookie);
		urlConn.connect();
		StringWriter out = new StringWriter();
		InputStreamReader in = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();
		return out.toString();
	}

	
	
	/** FIXME: refactor
	 * Gets Cookie from SpringerLink and returns it as string.
	 * @return
	 * @throws IOException
	 */
	private String getCookieFromSpringer () throws IOException {		
		/*
		 * receive cookie from springer
		 */
		URL mainURL = new URL(SITE_URL + "home/main.mpx");
		HttpURLConnection urlConn;
		urlConn = (HttpURLConnection) mainURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", userAgent);
		urlConn.connect();
		
		// set all necessary cookies
		/*
		 * extract cookie from connection
		 */
		List<String> cookieContent = urlConn.getHeaderFields().get("Set-Cookie");
		//extract sessionID and store in cookie
		for (String crumb : cookieContent) {
			
			if (crumb.contains("ASP")){
				
				return crumb;
			}
		}
		urlConn.disconnect();
		return null;
	}
	
	public String getInfo() {
		return INFO;
	}
	
	@Override
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
