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
package org.bibsonomy.scraper.url.kde.osa;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class OSAScraper extends AbstractUrlScraper implements ReferencesScraper{
	private static final Log log = LogFactory.getLog(OSAScraper.class);
	
	private static final String SITE_NAME = "Optical Society of America";
	private static final String OSA_HOST_NAME  = "http://www.opticsinfobase.org";
	private static final String SITE_URL  = OSA_HOST_NAME+"/";
	private static final String info = "This Scraper parses a publication from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String OSA_HOST  = "opticsinfobase.org";
	

	private static final String OSA_BIBTEX_DOWNLOAD_PATH = "/custom_tags/IB_Download_Citations.cfm";

	private static final Pattern actionsPattern = Pattern.compile("<select name=\"(actions[^\"]*)\"");
	
	private static final Pattern inputPattern = Pattern.compile("<input\\b[^>]*>");
	private static final Pattern valuePattern = Pattern.compile("value=\"[^\"]*\"");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + OSA_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	final static Pattern references_pattern = Pattern.compile("(?s)<h3>References</h3>\\s+<div .*>\\s+<ol>(.*)</ol>");
	
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String id = null;

		final Matcher inputMatcher = inputPattern.matcher(sc.getPageContent());

		while(inputMatcher.find()) {
			String input = inputMatcher.group();
			if(input.contains("name=\"articles\"")) {
				Matcher valueMatcher = valuePattern.matcher(input);

				if(valueMatcher.find()) {
					String value = valueMatcher.group();
					id = value.substring(7,value.length()-1);
					break;
				}
			}
		}

		String actions = null;
		Matcher actionsMatcher = actionsPattern.matcher(sc.getPageContent());
		if(actionsMatcher.find())
			actions = actionsMatcher.group(1);

		String bibResult = null;

		try {
			URL citUrl = new URL(OSA_HOST_NAME + OSA_BIBTEX_DOWNLOAD_PATH);
			bibResult = getContent(citUrl, getCookies(sc.getUrl()), id, actions);
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		if(bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
		}
		throw new ScrapingFailureException("getting bibtex failed");
	}

	/** FIXME: refactor
	 * @param queryURL
	 * @param cookie
	 * @param id
	 * @param actions
	 * @return
	 * @throws IOException
	 */
	private static String getContent(URL queryURL, String cookie, String id, String actions) throws IOException {
		/*
		 * get BibTex-File from ACS
		 */
		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		//insert cookie
		urlConn.setRequestProperty("Set-Cookie", cookie);

		StringBuffer sbContent = new StringBuffer();

		sbContent.append("Articles=");
		sbContent.append(UrlUtils.safeURIEncode(id) + "&");
		sbContent.append("ArticleAction=");
		sbContent.append(UrlUtils.safeURIEncode("save_bibtex2") + "&");
		sbContent.append(actions + "=");
		sbContent.append(UrlUtils.safeURIEncode("save_bibtex2"));

		urlConn.setRequestProperty("Content-Length", String.valueOf(sbContent.length()));

		DataOutputStream stream = new DataOutputStream(urlConn.getOutputStream());

		stream.writeBytes(sbContent.toString());
		stream.flush();
		stream.close();

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

	/**
	 * FIXME: refactor
	 * @param queryURL
	 * @return
	 * @throws IOException
	 */
	private static String getCookies(URL queryURL) throws IOException {
		final HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();

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
		final List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");

		final StringBuffer cookieString = new StringBuffer();

		for (final String cookie : cookies) {
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
	public boolean scrapeReferences(ScrapingContext scrapingContext)throws ScrapingException {
		try{
			Matcher m = references_pattern.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			if(m.find()){
				scrapingContext.setReferences(m.group(1));
				return true;
			}
		} catch(final Exception e) {
			log.error("error while scraping references for " + scrapingContext.getUrl(), e);
		}
		return false;
	}


}
