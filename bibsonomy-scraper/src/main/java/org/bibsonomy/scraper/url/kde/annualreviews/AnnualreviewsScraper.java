/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.url.kde.annualreviews;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for arjournals.annualreviews.org
 * @author tst
 * @version $Id$
 */
public class AnnualreviewsScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Annual Reviews";
	private static final String SITE_URL = "http://arjournals.annualreviews.org/";
	private static final String INFO = "Supports journals from " + href(SITE_URL, SITE_NAME);

	/**
	 * HOST from anualreviews
	 */
	private static final String HOST = "arjournals.annualreviews.org";

	/**
	 * path and query for download url
	 */
	private static final String DOWNLOAD_PATH_AND_QUERY = "/action/downloadCitation?format=bibtex&include=cit&doi=";

	private static final Pattern doiPattern = Pattern.compile("/doi/abs/(.*)");
	private static final Pattern doiPatternQuery = Pattern.compile("doi=([^&]*)");
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));

	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		String doi = null;
		String bibtex = null;

		// get doi from path
		Matcher doiMatcher = doiPattern.matcher(sc.getUrl().getPath());
		if(doiMatcher.find())
			doi = doiMatcher.group(1);

		// check if doi is in path
		if(doi != null)
			bibtex = download(doi, sc);
		else{

			// get doi from query
			
			doiMatcher = doiPatternQuery.matcher(sc.getUrl().getQuery());
			if(doiMatcher.find())
				doi = doiMatcher.group(1);

			if(doi != null)
				bibtex = download(doi, sc);
			else // no doi available
				throw new PageNotSupportedException("This page arjournals.annualreviews.org is not supported.");
		}

		if(bibtex != null){
			sc.setBibtexResult(bibtex);
			return true;
		}else
			throw new ScrapingFailureException("Bibtex download failed. Can't scrape any bibtex.");

	}

	/**
	 * Get a bibtex reference by its doi from arjournals.annualreviews.org
	 * @param doi
	 * @return reference as bibtex
	 * @throws ScrapingException
	 */
	private String download(String doi, ScrapingContext sc)throws ScrapingException{
		String bibtex = null;

		String downloadUrl = "http://" + HOST + DOWNLOAD_PATH_AND_QUERY + doi;
		try {
			URL download = new URL(downloadUrl);
			String cookie = getCookie();
			bibtex = getPageContent((HttpURLConnection) download.openConnection(), cookie);
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		return bibtex;
	}

	/**
	 * Gets the cookie which is needed to extract the content of pages.
	 * (changed code from ScrapingContext.getContentAsString) 
	 * @param urlConn
	 * @return The value of the cookie.
	 * @throws IOException
	 */
	private String getCookie() throws IOException{
		HttpURLConnection urlConn = (HttpURLConnection) new URL("http://" + HOST).openConnection();
		String cookie = null;

		urlConn.setAllowUserInteraction(true);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setFollowRedirects(true);
		urlConn.setInstanceFollowRedirects(false);

		urlConn.setRequestProperty(
				"User-Agent",
		"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		urlConn.connect();

		// extract cookie from header
		Map map = urlConn.getHeaderFields();
		cookie = urlConn.getHeaderField("Set-Cookie");
		if(cookie != null && cookie.indexOf(";") >= 0)
			cookie = cookie.substring(0, cookie.indexOf(";"));

		urlConn.disconnect();		
		return cookie;
	}

	/**
	 * Extract the content of a page.
	 * (changed code from ScrapingContext.getContentAsString)
	 * @param urlConn Connection to page (from url.openConnection())
	 * @param cookie Cookie for auth.
	 * @return Content of page.
	 * @throws IOException
	 */
	private String getPageContent(HttpURLConnection urlConn, String cookie) throws IOException{

		urlConn.setAllowUserInteraction(true);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setFollowRedirects(true);
		urlConn.setInstanceFollowRedirects(false);
		urlConn.setRequestProperty("Cookie", cookie);

		urlConn.setRequestProperty(
				"User-Agent",
		"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		urlConn.connect();

		// build content
		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}

		urlConn.disconnect();
		in.close();
		out.flush();
		out.close();

		return out.toString();
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return "http://arjournals.annualreviews.org";
	}

}
