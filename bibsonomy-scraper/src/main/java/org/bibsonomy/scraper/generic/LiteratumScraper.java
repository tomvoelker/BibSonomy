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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for sites running the online publishing software Atypon Literatum.
 * 
 * @author rja
 */
public abstract class LiteratumScraper extends AbstractUrlScraper {

	private static final Log log = LogFactory.getLog(LiteratumScraper.class);

	private static final String BIBTEX_DOWNLOAD_PATH = "/action/downloadCitation";

	// to extract the DOI from the URL
	private static final Pattern PATH_ABSTRACT_PATTERN = Pattern.compile("/doi(/?(abs|full|pdf|pdfplus|book))?/(10\\.\\d+/[^\\s\"'}]+)?$");
	private static final int PATH_ABSTRACT_PATTERN_DOI_GROUP = 3;
	// to extract the DOI from the query
	private static final Pattern QUERY_DOI_PATTERN = Pattern.compile("doi=([^&]+)");
	private static final int QUERY_DOI_PATTERN_DOI_GROUP = 1;
	// to extract the abstract from the HTML
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstractSection.*?\">\\s*<p.*?>(.+?)</p>");
	private static final int ABSTRACT_PATTERN_ABSTRACT_GROUP = 1;


	/**
	 * 
	 * @param sc
	 * @return the scraped BibTeX
	 * @throws ScrapingException
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final URL url = sc.getUrl();
		final String doi = getDOI(url);

		if (present(doi)) {
			try {
				final List<NameValuePair> downloadData = this.getData(doi);
				final StringBuilder citUrl = new StringBuilder("https://" + url.getHost() + BIBTEX_DOWNLOAD_PATH);
				final String cookies = getCookies(url);
				String bibtex;
				if (this.doPost()) {
					bibtex = WebUtils.getContentAsString(citUrl.toString(), cookies, downloadData, null);
				}else {
					citUrl.append(constructDownloadQuery(downloadData));
					bibtex = WebUtils.getContentAsString(citUrl.toString(), cookies, null, null);
				}

				if (present(bibtex)) {
					// download and add abstract, if necessary
					final String bibtexWithAbstract = addAbstract(bibtex, url, cookies);
					// postprocess BibTeX
					sc.setBibtexResult(postProcessBibtex(sc, bibtexWithAbstract));
					return true;
				}

			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

		}
		throw new ScrapingFailureException("getting BibTeX failed (could not extract id)");
	}

	/**
	 * @param bibtex
	 * @param url
	 * @param cookies
	 * @return the bibtex with the abstract added
	 */
	protected String addAbstract(final String bibtex, final URL url, final String cookies) {
		if (downloadAbstract()) {
			try {
				return BibTexUtils.addFieldIfNotContained(bibtex, "abstract", getAbstract(url, cookies));
			} catch (IOException e) {
				log.warn("error while scraping the abstract for " + url, e);
			}
		}
		return bibtex;
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String getCookies(final URL url) throws IOException {
		// do we need cookies for this publisher?
		if (requiresCookie()) {
			return WebUtils.getCookies(url);
		} 
		return null;
	}

	/**
	 * Override this method if a cookie must be retrieved before downloading BibTeX
	 * @return <code>true</code> if cookie shall be downloaded
	 */
	protected boolean requiresCookie() {
		return false;
	}

	/**
	 * If the abstract is not contained in the BibTeX, this method should return <code>true</code>
	 * such that it is downloaded separately.
	 * 
	 * @return <code>false</code>
	 */
	protected boolean downloadAbstract() {
		return false;
	}

	/**
	 * The Parameter, which should be used for a post or get request
	 * @param doi
	 * @return parameter which shoudld used for a get or post request
	 */
	protected List<NameValuePair> getData(final String doi) {
		ArrayList<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("doi", doi));
		data.add(new BasicNameValuePair("format", "bibtex"));
		data.add(new BasicNameValuePair("include", "abs"));
		data.add(new BasicNameValuePair("submit", "Download"));
		return data;
	}

	/**
	 * @return if a POST-request or a GET-Request should be made
	 */
	protected boolean doPost(){
		return false;
	}

	/**
	 * Attempts to extract the id (DOI) from the URL.
	 * 
	 * @param url
	 * @return
	 */
	private static String getDOI(final URL url) {
		final Matcher m1 = PATH_ABSTRACT_PATTERN.matcher(url.getPath());
		if (m1.find()) {
			return UrlUtils.safeURIDecode(m1.group(PATH_ABSTRACT_PATTERN_DOI_GROUP));
		}
		final Matcher m2 = QUERY_DOI_PATTERN.matcher(url.getQuery());
		if (m2.find()) {
			return UrlUtils.safeURIDecode(m2.group(QUERY_DOI_PATTERN_DOI_GROUP));
		}
		return null;
	}

	/**
	 * Constructs a query for an url with the params
	 * @param params of the query
	 * @return
	 */
	private static String constructDownloadQuery(List<NameValuePair> params){
		String query = "?";
		for (NameValuePair param : params) {
			query += param.getName() + "=" + UrlUtils.safeURIEncode(param.getValue()) + "&";
		}
		query = query.substring(0, query.length()-1);
		return query;
	}

	/**
	 * @param url
	 * @param cookies
	 * @return the abstract
	 * @throws IOException
	 */
	protected static String getAbstract(final URL url, final String cookies) throws IOException {
		final String contentAsString = WebUtils.getContentAsString(url, cookies);
		final Matcher m = ABSTRACT_PATTERN.matcher(contentAsString);
		if (m.find()) {
			return m.group(ABSTRACT_PATTERN_ABSTRACT_GROUP).trim();
		}
		return null;
	}

	/**
	 * Override this method in case the scraped BibTeX needs to be "polished".
	 * 
	 * @param scrapingContext
	 * @param bibtex
	 * @return the postprocessed bibtex
	 */
	protected String postProcessBibtex(ScrapingContext scrapingContext, String bibtex) {
		return bibtex;
	}

}