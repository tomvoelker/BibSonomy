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

package org.bibsonomy.scraper.url.kde.metapress;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpConnection;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for RIS citations from Metapress.com
 * @author tst
 * @version $Id$
 */
public class MetapressScraper extends AbstractUrlScraper {

	private static final String SITE_URL = "http://metapress.com/";
	private static final String SITE_NAME = "Meta Press";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "metapress.com";

	private static final String PREFIX_DOWNLOAD_URL = "http://www.metapress.com/export.mpx?code=";

	private static final String SUFFIX_DOWNLOAD_URL = "&mode=ris";

	private static final Pattern patternHref = Pattern.compile("content/([^/]*)/");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		final Matcher matcherHref = patternHref.matcher(sc.getUrl().toString());

		if(matcherHref.find()){
			String url = PREFIX_DOWNLOAD_URL + matcherHref.group(1) + SUFFIX_DOWNLOAD_URL;

			String ris = null;
			
			try {
				URL downloadUrl = new URL(url);
				CookieManager cookieManager = new CookieManager();
				getCookies(downloadUrl, cookieManager);
//				String cookie = WebUtils.getCookies(sc.getUrl());
				ris = WebUtils.getContentAsString(downloadUrl, cookieList2String(cookieManager.getCookieStore().get(downloadUrl.toURI())));
				
				if(ris!=null){
					RisToBibtexConverter converter = new RisToBibtexConverter();
					String bibtex = converter.risToBibtex(ris);

					//replace /r with /n
					bibtex = bibtex.replace("\r", "\n");

					if(bibtex != null){
						sc.setBibtexResult(bibtex);
						return true;
					}else
						throw new ScrapingFailureException("convert to bibtex failed");
				}else
					throw new ScrapingFailureException("ris download failed");

			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			} catch (URISyntaxException ex) {
				throw new InternalFailureException(ex);
			}
		}else
			throw new PageNotSupportedException("no RIS download available");
	}
	
	private static String cookieList2String(List<HttpCookie> cookies) {
		StringBuffer sb = new StringBuffer();
		for (HttpCookie cookie : cookies) {
			sb.append(cookie);
			sb.append("; ");
		}
		return sb.toString();
	}
	
	private static void getCookies(URL url, CookieManager cookieManager) throws IOException, URISyntaxException {
		HttpURLConnection con;
		while (true) {
			con = (HttpURLConnection) url.openConnection();
			con.setInstanceFollowRedirects(false);
			con.addRequestProperty("User-Agent", "Opera/9.80 (Windows NT 6.1; U; Edition Campaign 21; de) Presto/2.10.289 Version/12.02");
			con.addRequestProperty("Cookie", cookieList2String(cookieManager.getCookieStore().get(url.toURI())));
			try {
				con.connect();
				cookieManager.put(con.getURL().toURI(), con.getHeaderFields());
			} finally {
				con.disconnect();
			}
			if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				url = new URL(con.getHeaderField("Location"));
			} else {
				break;
			}
		}
	}

	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
