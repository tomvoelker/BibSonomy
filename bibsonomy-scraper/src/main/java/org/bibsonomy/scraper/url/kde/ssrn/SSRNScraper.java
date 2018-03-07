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
package org.bibsonomy.scraper.url.kde.ssrn;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

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
	private static final String SSRN_BIBTEX_PATH   = "sol3/RefExport.cfm";
	private static final String SSRN_BIBTEX_PATH_QUERY = SSRN_BIBTEX_PATH + "?abstract_id=";
	private static final String SSRN_BIBTEX_PARAMS = "?function=download&format=2&abstract_id=";

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
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		final String id = getId(sc.getUrl().toString());

		if (ValidationUtils.present(id)) {
			final String downloadLink = SSRN_BASE_URL + SSRN_BIBTEX_PATH + SSRN_BIBTEX_PARAMS + id;
			String cookies = null;


			try {
				cookies = getCookies(sc.getUrl());
			} catch (IOException ex) {
				throw new InternalFailureException("Could not store cookies from " + sc.getUrl());
			}

			
			try {
				final String content = WebUtils.getContentAsString(new URL(downloadLink), cookies);
				final Document doc = XmlUtils.getDOM(content);
				final NodeList list = doc.getElementsByTagName("input");

				String bibtex  = null;
				
				for (int i = 0; i < list.getLength(); i++) {
					final NamedNodeMap attributes = list.item(i).getAttributes();

					if (ValidationUtils.present(attributes.getNamedItem("value"))) {
						bibtex = attributes.getNamedItem("value").getNodeValue().replaceAll("},", "},\n");
						final String bibtexKey = BibTexUtils.generateBibtexKey(getMatch(bibtex, AUTHOR_PATTERN), getMatch(bibtex, EDITOR_PATTERN), getMatch(bibtex, YEAR_PATTERN), getMatch(bibtex, TITLE_PATTERN));
						bibtex = bibtex.replaceFirst(" ", bibtexKey + ",\n ");
					}
				}
				
				if (ValidationUtils.present(bibtex)) {
					sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(bibtex, "abstract", getAbstract(sc.getUrl())));
					sc.setScraper(this);
					return true;
				}
			} catch (MalformedURLException ex) {
				throw new InternalFailureException("The url "+ downloadLink + " is not valid");
			} catch (IOException ex) {
				throw new ScrapingFailureException("BibTex download failed. Result is null!");
			}


		} else {
			throw new ScrapingFailureException("ID for donwload link is missing.");
		}

		return false;
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
	 * @param authors
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

	private static String getCookies(URL queryURL) throws IOException {
		final StringBuffer cookieString = new StringBuffer(WebUtils.getCookies(queryURL));

		cookieString.append(" ; CFCLIENT_SSRN=loginexpire%3D%7Bts%20%272009%2D12%2D12%2012%3A35%3A00%27%7D%23blnlogedin%3D1401777%23;domain=hq.ssrn.com;path=/; ");
		//login: wbi@cs.uni-kassel.de
		cookieString.append("SSRN_LOGIN=092026079048019002070010027035037114047052089011063088001026083003082103106066127064089084103; ");
		cookieString.append("SSRN_PW=002008020074048016097064090009116110016084070087029069024; ");

		return cookieString.toString();
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