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

package org.bibsonomy.scraper.url.kde.jstor;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * @version $Id$
 */
public class JStorScraper extends AbstractUrlScraper {

	private static final String info = "This Scraper parses a publication from " + href("http://www.jstor.org/", "JSTOR");

	private static final String JSTOR_HOST  = "jstor.org";
	private static final String JSTOR_HOST_NAME  = "http://www.jstor.org";
	private static final String JSTOR_ABSTRACT_PATH = "/pss/";
	private static final String JSTOR_EXPORT_PATH = "/action/exportSingleCitation";
	private static final String JSTOR_STABLE_PATH = "/stable/";
	private static final String JSTOR_DISCOVER_PATH = "/discover/";
	private static final String EXPORT_PAGE_URL = "https://www.jstor.org/action/exportSingleCitation?singleCitation=true&doi=";
	
	private static final Pattern PAGE_CONTENT_DOI_PATTERN = Pattern.compile("(?m)<div id=\"doi\" class=\"hide\">([^>]+?)<");
	private static final Pattern EXPORT_URL_DOI_PATTERN = Pattern.compile("doi=([^\\&]++)");
	
	private static final Pattern EXPORT_LINK_PATTERN = Pattern.compile("href=\"([^\"]++).*?id=\"export\"");
	private static final Pattern SUBMIT_ACTION_NODOI_PATTERN = Pattern.compile("<input.*?id=\"noDoi\".*?value=\"([^\"]++)\"");
	private static final Pattern SUBMIT_ACTION_DOI_PATTERN = Pattern.compile("<input.*?name=\"doi\".*?value=\"([^\"]*+)\"");
	private static final Pattern NUMBER_CITS_EXPORTED_PATTERN = Pattern.compile("NUMBER OF CITATIONS : (\\d++)");

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	private static final Pattern SUBMIT_ACTION_PATTERN = Pattern.compile("href=\"javascript:submitActionInNewWindow[^']++'([^']++)");

	static {
		final Pattern hostPattern = Pattern.compile(".*" + JSTOR_HOST);
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_ABSTRACT_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_EXPORT_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_STABLE_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_DISCOVER_PATH + ".*")));
	}
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {

		sc.setScraper(this);
		
		HttpClient client = WebUtils.getHttpClient();
		
		String bibtexResult = null;
		
		try {
			
			//targeting download page and from there submit action
			String downloadPage, submitAction;
			
			//get the page content or at least get the cookies
			GetMethod getMethod = new GetMethod(sc.getUrl().toExternalForm());
			String page = WebUtils.getContentAsString(client, getMethod);
			
			if(page == null) throw new ScrapingException("Cannot access requested location");

			//first assume we are on export page and search for submit action
			Matcher m = SUBMIT_ACTION_PATTERN.matcher(page);
			if (m.find()) {
				
				//found it
				submitAction = m.group(1);
				downloadPage = page;
			} else {
				
				//we didn't find it but want to download export page
				String exportPageLink;

				//search for the doi in the url
				m = EXPORT_URL_DOI_PATTERN.matcher(sc.getUrl().toExternalForm());
				if (m.find()) {
					exportPageLink = EXPORT_PAGE_URL + m.group(1);
				} else {
					//next try: search the page for doi
					m = PAGE_CONTENT_DOI_PATTERN.matcher(page);
					if (m.find()) {
						exportPageLink = EXPORT_PAGE_URL + m.group(1);
					} else {
						//we havn't found the doi, so let's search for export link
						m = EXPORT_LINK_PATTERN.matcher(page);
						if (!m.find()) throw new ScrapingException("Cannot continue. JStor Scraper must get updated");
						exportPageLink = m.group(1).replace("&amp;", "&");				}
				}
				
				//is the export page link present?
				if (!present(exportPageLink)) throw new ScrapingException("Cannot continue, finally not having submit action");
				
				//download export page
				getMethod = new GetMethod(exportPageLink);
				downloadPage = WebUtils.getContentAsString(client, getMethod);
				
				// get the submit action from the export page
				m = SUBMIT_ACTION_PATTERN.matcher(downloadPage);
				
				//did we find it?
				if (!m.find()) throw new ScrapingException("Downloaded export page but didn't find submit action");
				
				submitAction = m.group(1);
			}
			
			//get some data from the page and switch submit action
			Matcher noDoiMatcher = SUBMIT_ACTION_NODOI_PATTERN.matcher(downloadPage);
			Matcher doiMatcher = SUBMIT_ACTION_DOI_PATTERN.matcher(downloadPage);
			if (!noDoiMatcher.find() || !doiMatcher.find()) throw new ScrapingException("Couldn't get required data for export form");
			String noDoi = noDoiMatcher.group(1);
			URI actionURL;
			if ("noDoi".equalsIgnoreCase(noDoi)) {
				actionURL = getMethod.getURI();
			} else {
				actionURL = new URI(submitAction, true);
			}
			
			//post export form
			PostMethod postMethod = new PostMethod();
			postMethod.setURI(actionURL);
			postMethod.addParameter("redirectUri", getMethod.getPath());
			postMethod.addParameter("noDoi", noDoi);
			postMethod.addParameter("doi", doiMatcher.group(1));
			bibtexResult = WebUtils.getPostContentAsString(client, postMethod);

		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
		
		//check the result
		if (!present(bibtexResult)) throw new ScrapingException("Could not submit export form");
		Matcher numberOfCitMatcher = NUMBER_CITS_EXPORTED_PATTERN.matcher(bibtexResult);
		if (!numberOfCitMatcher.find()) throw new ScrapingException("no citations received");
		int numberOfCit = Integer.parseInt(numberOfCitMatcher.group(1));
		if (numberOfCit < 1) throw new ScrapingException("received " + numberOfCit + " citations");
		sc.setBibtexResult(bibtexResult);
		return true;

	}

	/** FIXME: refactor
	 * @param queryURL
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
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

		//seems that the application needs this cookie even before getting the other cookies, strange :-\
		urlConn.setRequestProperty("Cookie", "I2KBRCK=1");

		urlConn.connect();
		/*
		 * extract cookie from connection
		 */
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");

		StringBuffer cookieString = new StringBuffer();

		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}

		//This is neccessary, otherwise we don't get the Bibtex file.
		cookieString.append("I2KBRCK=1");

		urlConn.disconnect();

		return cookieString.toString();
	}

	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return "JSTOR";
	}

	public String getSupportedSiteURL() {
		return JSTOR_HOST_NAME;
	}
}
