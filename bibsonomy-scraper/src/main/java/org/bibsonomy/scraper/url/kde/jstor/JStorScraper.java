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

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
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
	private static final String JSTOR_DOWNLOAD_SUBMIT_ACTION_YESDOI = "https://www.jstor.org/action/downloadSingleCitationSec?format=bibtex&include=abs&singleCitation=true";
	
	private static final Pattern INDEX_PATTERN_FOR_ABSTRACT_PATH = Pattern.compile("/pss/(\\d++)"); 
	private static final Pattern EXPORT_LINK_PATTERN = Pattern.compile("href=\"([^\"]++).*?id=\"export\"");
	private static final Pattern SUBMIT_ACTION_NODOI_PATTERN = Pattern.compile("<input.*?id=\"noDoi\".*?value=\"([^\"]++)\"");
	private static final Pattern SUBMIT_ACTION_SUFFIX_PATTERN = Pattern.compile("<input.*?name=\"suffix\".*?value=\"([^\"]++)\"");
	private static final Pattern SUBMIT_ACTION_FILENAME_PATTERN = Pattern.compile("<input.*?name=\"downloadFileName\".*?value=\"([^\"]++)\"");
	private static final Pattern NUMBER_CITS_EXPORTED_PATTERN = Pattern.compile("NUMBER OF CITATIONS : (\\d++)");

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	static {
		final Pattern hostPattern = Pattern.compile(".*" + JSTOR_HOST);
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_ABSTRACT_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_EXPORT_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_STABLE_PATH + ".*")));
	}
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {

		sc.setScraper(this);

		//keeping cookies around
		HashMap<String, String> cookies = new HashMap<String, String>();

		String url = sc.getUrl().toString();
		
		URL exportURL = null;
		
		//Stable URL
		if (url.contains(JSTOR_STABLE_PATH)) {
			Matcher m = Pattern.compile("/stable/(\\d++)").matcher(url);
			if (!m.find()) throw new ScrapingException("/pss/ path without id");
			try {
				exportURL = new URL("https://www.jstor.org/action/exportSingleCitation?singleCitation=true&suffix=" + m.group(1));
				startSessionForURL(exportURL, cookies);
			} catch (MalformedURLException ex) {
			}
		}
		
		//Abstract path => try to build export page url
		if (url.contains(JSTOR_ABSTRACT_PATH)) {
			Matcher m = INDEX_PATTERN_FOR_ABSTRACT_PATH.matcher(url);
			if (!m.find()) throw new ScrapingException("/pss/ path without id");
			try {
				exportURL = new URL("https://www.jstor.org/action/exportSingleCitation?singleCitation=true&suffix=" + m.group(1));
				startSessionForURL(exportURL, cookies);
			} catch (MalformedURLException ex) {
			}
		}
		
		//export page
		if (url.contains(JSTOR_EXPORT_PATH)) {
			exportURL = sc.getUrl();
			if (exportURL.getProtocol().equalsIgnoreCase("http")) {
				try {
					exportURL = new URL("https", exportURL.getHost(), exportURL.getFile());
				} catch (MalformedURLException ex) {
				}
			}
			startSessionForURL(exportURL, cookies);
		}
		
		String bibtexResult = submitExportPage(exportURL, cookies);
		
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
	
	private static String cookiesMap2String(Map<String, String> cookies) {
		StringBuffer cookiesBuffer = new StringBuffer();
		for (String key : cookies.keySet()) {
			cookiesBuffer.append(key);
			cookiesBuffer.append('=');
			cookiesBuffer.append(cookies.get(key));
			cookiesBuffer.append("; ");
		}
		return cookiesBuffer.toString();
	}
	
	private static void startSessionForURL(URL url, Map<String, String> cookies) throws ScrapingException {
		//first request to get a cookie and a redirect
		HttpURLConnection c = null;
		//second request to get redirected back and maybe a cookie
		HttpURLConnection c5 = null;
		try {
			
			//get a cookie and a redirect
			c = (HttpURLConnection) url.openConnection();
			c.setInstanceFollowRedirects(false);
			c.connect();
			for (String cookie : c.getHeaderFields().get("Set-Cookie")) {
				String[] keyval = cookie.substring(0, cookie.indexOf(';')).split("=");
				cookies.put(keyval[0], keyval[1]);
			}
			String redirectLocation = c.getHeaderFields().get("Location").get(0);
			
			//get another redirect and maybe a new cookie
			URL redirectURL = new URL(redirectLocation);
			c5 = (HttpURLConnection) redirectURL.openConnection();
			c5.setInstanceFollowRedirects(false);
			c5.addRequestProperty("Cookie", cookiesMap2String(cookies));
			c5.connect();
			for (String cookie : c5.getHeaderFields().get("Set-Cookie")) {
				String[] keyval = cookie.substring(0, cookie.indexOf(';')).split("=");
				cookies.put(keyval[0], keyval[1]);
			}
			redirectLocation = c5.getHeaderFields().get("Location").get(0);
			
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		} finally {
			if (c != null) c.disconnect();
			if (c5 != null) c5.disconnect();
		}
	}
	
	private static String submitExportPage(URL exportURL, Map<String, String> cookies) throws ScrapingException {
		InputStream in = null;
		HttpURLConnection c3 = null;
		try {
			c3 = (HttpURLConnection) exportURL.openConnection();
			c3.setInstanceFollowRedirects(false);
			c3.addRequestProperty("Cookie", cookiesMap2String(cookies));
			c3.connect();
			for (String cookie : c3.getHeaderFields().get("Set-Cookie")) {
				String[] keyval = cookie.substring(0, cookie.indexOf(';')).split("=");
				cookies.put(keyval[0], keyval[1]);
			}
			in = c3.getInputStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			int b;
			StringWriter out = new StringWriter();
			while ((b = bin.read()) >= 0) {
				out.write(b);
			}
			String exportPage = out.toString();
			Matcher noDoiMatcher = SUBMIT_ACTION_NODOI_PATTERN.matcher(exportPage);
			Matcher suffixMatcher = SUBMIT_ACTION_SUFFIX_PATTERN.matcher(exportPage);
			Matcher fileNameMatcher = SUBMIT_ACTION_FILENAME_PATTERN.matcher(exportPage);
			if (!noDoiMatcher.find() || !suffixMatcher.find() || !fileNameMatcher.find()) throw new ScrapingException("noDoi flag not found");
			URL actionURL;
			String noDoi = noDoiMatcher.group(1);
			if ("noDoi".equalsIgnoreCase(noDoi)) {
				actionURL = exportURL;
			} else {
				actionURL = new URL(JSTOR_DOWNLOAD_SUBMIT_ACTION_YESDOI);
			}
			String postContent = "redirectUri=" + URLEncoder.encode(exportURL.getFile(), "UTF-8")
					+ "&noDoi=" + noDoi
					+ "&suffix=" + suffixMatcher.group(1)
					+ "&downloadFileName=" + fileNameMatcher.group(1);
			return WebUtils.getPostContentAsString(cookiesMap2String(cookies), actionURL, postContent);
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		} finally {
			if (in != null) try {
				in.close();
			} catch (IOException ex) {
			}
			if (c3 != null) c3.disconnect();
		}
	}
	
	@SuppressWarnings("unused")
	private static URL getExportLinkAsURL(URL pageURL, Map<String, String> cookies) throws ScrapingException {
		//third request to get page content and some cookies
		HttpURLConnection c7 = null;
		//page content streaming
		InputStream in = null;
		try {
			
			startSessionForURL(pageURL, cookies);
			
			//now getting page content and some cookies
			c7 = (HttpURLConnection) pageURL.openConnection();
			c7.setInstanceFollowRedirects(false);
			c7.addRequestProperty("Cookie", cookiesMap2String(cookies));
			c7.connect();
			for (String cookie : c7.getHeaderFields().get("Set-Cookie")) {
				String[] keyval = cookie.substring(0, cookie.indexOf(';')).split("=");
				cookies.put(keyval[0], keyval[1]);
			}
			StringWriter out = new StringWriter();
			in = new BufferedInputStream(c7.getInputStream());
			int b;
			while ((b = in.read()) >= 0) {
				out.write(b);
			}
			
			//find export link
			Matcher exportLinkMatcher = EXPORT_LINK_PATTERN.matcher(out.toString());
			if (!exportLinkMatcher.find()) throw new ScrapingException("Exportlink not found");
			String exportLink = exportLinkMatcher.group(1);
			
			//return export link as URL
			return new URL(exportLink.replace("&amp;", "&"));
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		} finally {
			if (in != null) try {
				in.close();
			} catch (IOException ex) {
			}
			if (c7 != null) c7.disconnect();
		}
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
