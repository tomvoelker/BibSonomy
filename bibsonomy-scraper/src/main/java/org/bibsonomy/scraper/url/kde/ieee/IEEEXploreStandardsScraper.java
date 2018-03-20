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
package org.bibsonomy.scraper.url.kde.ieee;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/** Scraper for IEEE Explore
 * @author rja
 *
 */
public class IEEEXploreStandardsScraper extends AbstractUrlScraper {
	private static final String SITE_NAME 	= "IEEEXplore Standards";
	private static final String SITE_URL  	= "http://ieeexplore.ieee.org/";
	private static final String info 		= "This scraper creates a BibTeX entry for the standards at " + href(SITE_URL, SITE_NAME)+".";
	private static final String DOWNLOAD_URL = SITE_URL + "xpl/downloadCitations";

	private static final String IEEE_HOST        	 	  = "ieeexplore.ieee.org";
	private static final String IEEE_STANDARDS_PATH   	  = "xpl";

	private static final Pattern PATTERN1 = Pattern.compile("arnumber=([^&]*)");

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();


	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + IEEE_HOST), Pattern.compile("/" + IEEE_STANDARDS_PATH + ".*")));
	}


	@Override
	protected boolean scrapeInternal (ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final Matcher matcher = PATTERN1.matcher(sc.getUrl().toString());
		if (matcher.find()) {
			final String id = matcher.group(1);
			final String bibtex = getBibTeX(sc, id);

			if (ValidationUtils.present(bibtex)) {
				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex);
				return true;
			} 
		} 
		return false;
	}
	
	/**
	 * @param sc
	 * @param id
	 * @return the resulting BibTeX
	 * @throws InternalFailureException
	 */
	protected static String getBibTeX(final ScrapingContext sc, final String id) throws InternalFailureException {
		// using own client because I do not want to configure any client to allow circular redirects
		final Builder defaultRequestConfig = WebUtils.getDefaultRequestConfig();
		defaultRequestConfig.setCircularRedirectsAllowed(true);
		final HttpClient client = WebUtils.getHttpClient(defaultRequestConfig.build());

		try {
			// better get the page first
			final String url = sc.getUrl().toExternalForm();
			WebUtils.getContentAsString(client, url);

			// FIXME: this is copief from IEEEXploreBookScraper -> merge both scrapers?
			//create a post method
			//			final PostMethod method = new PostMethod(DOWNLOAD_URL);
			final List<NameValuePair> postData = new ArrayList<NameValuePair>(4);
			postData.add(new BasicNameValuePair("citations-format", "citation-abstract"));
			postData.add(new BasicNameValuePair("fromPage", ""));
			postData.add(new BasicNameValuePair("download-format", "download-bibtex"));
			postData.add(new BasicNameValuePair("recordIds", id));

			// now get bibtex
			String bibtex = WebUtils.getContentAsString(client, DOWNLOAD_URL, null, postData, null);

			if (bibtex != null) {
				// clean up
				bibtex = bibtex.replace("<br>", "");

				// append url
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", url);

				return bibtex.trim();
			}
			return null;
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		} catch (HttpException ex) {
			throw new InternalFailureException(ex);
		}
	}


	@Override
	public String getInfo() {
		return info;
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

}