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

package org.bibsonomy.scraper.url.kde.nature;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for publication from nature.com
 * @author tst
 */
public class NatureScraper extends PostprocessingGenericURLScraper {

	private static final String SITE_URL = "http://www.nature.com/";

	private static final String SITE_NAME = "Nature";

	/**
	 * Host
	 */
	private static final String HOST = "nature.com";

	/**
	 * INFO
	 */
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME)+".";

	/**
	 * pattern for links
	 */
	private static final Pattern linkPattern = Pattern.compile("<a\\b[^<]*</a>");

	/**
	 * pattern for href field
	 */
	private static final Pattern hrefPattern = Pattern.compile("href=\"[^\"]*\"");

	/**
	 * name from download link
	 */
	private static final String CITATION_DOWNLOAD_LINK_NAME = ">Export citation<";
	private static final String CITATION_DOWNLOAD_LINK_NAME2 = ">Citation<";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("(?s)Abstract.*\\s+<p>(.*)</p>\\s+<div class=\"article-keywords inline-list cleared\">");
	/**
	 * get INFO
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * Gets the page content of a publication page. It can't be commonly applied since it violates
	 * RFC 2616.
	 * 
	 * @param url The url to the publication page
	 * @return the publication page as a String
	 * @throws IOException
	 */
	private static String getPageContent(URL url) throws IOException {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.connect();
			//sometimes the page is behind an gzip stream. this will be indicated by response code 401.
			if (con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				in = new GZIPInputStream(con.getErrorStream());
			} else {
				in = con.getInputStream();
			}
			StringBuilder sb = WebUtils.inputStreamToStringBuilder(in, WebUtils.extractCharset(con.getContentType()));
			return sb.toString();
		} finally {
			if (con != null) con.disconnect();
			try {
				if (in != null) in.close();
			} catch (IOException ex) {}
		}
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
	@Override
	public String getBibTeXURL(URL url) {
		try {
			// get publication page
			final String publicationPage = getPageContent(url);
			// extract download citation link
			final Matcher linkMatcher = linkPattern.matcher(publicationPage);
			while(linkMatcher.find()){
				String link = linkMatcher.group();
				// check if link is download link
				if(link.contains(CITATION_DOWNLOAD_LINK_NAME) || link.contains(CITATION_DOWNLOAD_LINK_NAME2)){
					// get href attribute
					final Matcher hrefMatcher = hrefPattern.matcher(link);
					if(hrefMatcher.find()){
						String href = hrefMatcher.group();
						href = href.substring(6, href.length()-1);
						// download citation (as ris)
						return "http://" + url.getHost() + "/" + href;
					} 
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		try{
			final RisToBibtexConverter converter = new RisToBibtexConverter();
			return BibTexUtils.addFieldIfNotContained(converter.risToBibtex(result), "abstract", abstractParser(sc.getUrl()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	private static String abstractParser(URL url){
		try{
		Matcher m = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(url));
		if(m.find())
			return m.group(1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
