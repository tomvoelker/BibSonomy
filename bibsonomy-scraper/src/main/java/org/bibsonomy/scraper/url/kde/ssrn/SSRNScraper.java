/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.ssrn;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for http://www.ssrn.com/
 * @author tst
 */
public class SSRNScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(SSRNScraper.class);

	private static final String SITE_NAME	   = "SSRN";
	private static final String SSRN_HOST      = "papers.ssrn.com";
	private static final String SSRN_BASE_URL  = "https://" + SSRN_HOST + "/";
	private static final String INFO		   = "This Scraper parses a publication from " + href(SSRN_BASE_URL, SITE_NAME) +
			"and extracts the adequate BibTeX entry.";

	private static final String SSRN_ABSTRACT_PATH = "sol3/papers.cfm?abstract_id=";
	private static final String SSRN_LOGIN_URL = "https://hq.ssrn.com/login/cfc/hqLoginServices.cfc?method=signinService";
	private static final String SSRN_BIBTEX_PATH   = "sol3/RefExport.cfm";
	private static final String SSRN_BIBTEX_PATH_QUERY = SSRN_BIBTEX_PATH + "?abstract_id=";
	private static final String SSRN_BIBTEX_PARAMS = "?function=download&format=2&abstract_id=";

	private static final Pattern BIBTEX_PATTERN	= Pattern.compile("<input type=\"Hidden\" name=\"hdnContent\" value=\"([\\s\\S]*?)\">");
	private static final Pattern AUTHOR_PATTERN	= Pattern.compile("author\\s*=\\s*[{]+(.+)[}]+");
	private static final Pattern EDITOR_PATTERN	= Pattern.compile("editor\\s*=\\s*[{]+(.+)[}]+");
	private static final Pattern TITLE_PATTERN	= Pattern.compile("title\\s*=\\s*[{]+(.+)[}]+");
	private static final Pattern YEAR_PATTERN	= Pattern.compile("year\\s*=\\s*[{]+(.+)[}]+");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<h3>Abstract</h3>\\s*<p>(.*?)</p>");

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SSRN_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		//the bibtex can only be obtained when logged in. Key from a 10Min E-Mail, which already expired
		final String loginKey = "aXVvNDMwODJAY3VvbHkuY29tfEhUNUsmUVVZXzRhYyY5U3xbb2JqZWN0IE9iamVjdF0=";
		final URL url = sc.getUrl();
		final String id = getId(url.toString());
		try {
			if (ValidationUtils.present(id)) {
				//post to log in and get the SSRN_LOGIN Cookie
				HttpPost post = new HttpPost(SSRN_LOGIN_URL);
				post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("key", loginKey))));
				HttpResponse execute = WebUtils.getHttpClient().execute(post);
				Header[] headers = execute.getHeaders("Set-Cookie");
				String cookie = null;
				for (Header header : headers) {
					if (header.getValue().startsWith("SSRN_LOGIN")){
						cookie = header.getValue();
					}
				}

				HttpGet get = new HttpGet(SSRN_BASE_URL + SSRN_BIBTEX_PATH + SSRN_BIBTEX_PARAMS + id);
				get.addHeader("Cookie", cookie);
				String contentAsString = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);

				Matcher m_bibtex = BIBTEX_PATTERN.matcher(contentAsString);
				if (m_bibtex.find()){
					String bibtex = m_bibtex.group(1);
					//bibtex is missing key, so we create one
					final String bibtexKey = BibTexUtils.generateBibtexKey(
									getMatch(bibtex, AUTHOR_PATTERN),
									getMatch(bibtex, EDITOR_PATTERN),
									getMatch(bibtex, YEAR_PATTERN),
									getMatch(bibtex, TITLE_PATTERN));

					bibtex = bibtex.replaceFirst(" ", bibtexKey + ", ");
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", getAbstract(url));
					sc.setBibtexResult(bibtex);
					return true;
				}else {
					throw new ScrapingException("can't get Bibtex");
				}
			} else {
				throw new ScrapingFailureException("ID for donwload link is missing.");
			}
		} catch (IOException | HttpException e) {
			throw new ScrapingException(e);
		}

	}

	/**
	 * @param url
	 * @return
	 */
	private static String getId(String url) {
		if (url.startsWith(SSRN_BASE_URL + SSRN_ABSTRACT_PATH)) {
			return url.substring(url.indexOf(SSRN_ABSTRACT_PATH) + SSRN_ABSTRACT_PATH.length());
		}

		if (url.startsWith(SSRN_BASE_URL + SSRN_BIBTEX_PATH)) {
			return url.substring(url.indexOf(SSRN_BIBTEX_PATH_QUERY) + SSRN_BIBTEX_PATH_QUERY.length(), url.indexOf("&function"));
		}
		return null;
	}

	private static String getAbstract(URL url){
		try{
			final Matcher m = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				final String abs = m.group(1);
				if (abs.endsWith("<P>")) {
					return abs.substring(0, abs.length()-3).trim();
				}
				return abs.trim();
			}
		} catch(final Exception e) {
			log.warn("error while getting abstract for " + url, e);
		}
		return null;
	}

	/**
	 * @param bibtex
	 * @param pattern
	 * @return
	 */
	private static String getMatch(final String bibtex, final Pattern pattern) {
		final Matcher m = pattern.matcher(bibtex);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	@Override
	public String getInfo() {
		return INFO;
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
		return SSRN_BASE_URL;
	}

}