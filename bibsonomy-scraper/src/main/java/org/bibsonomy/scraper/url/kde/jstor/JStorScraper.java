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
package org.bibsonomy.scraper.url.kde.jstor;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
 */
public class JStorScraper extends AbstractUrlScraper {

	private static final String info = "This Scraper parses a publication from " + href("http://www.jstor.org/", "JSTOR");

	private static final String JSTOR_HOST  = "jstor.org";
	private static final String JSTOR_HOST_NAME  = "http://www.jstor.org";
	private static final String JSTOR_ABSTRACT_PATH = "/pss/";
	private static final String JSTOR_EXPORT_PATH = "/action/exportSingleCitation";
	private static final String JSTOR_STABLE_PATH = "/stable/";
	private static final String JSTOR_DISCOVER_PATH = "/discover/";
	private static final String JSTOR_DOI_ABS_PATH = "/doi/abs/";
	private static final String EXPORT_PAGE_URL = "https://www.jstor.org/action/exportSingleCitation?singleCitation=true&doi=";
	
	private static final Pattern PAGE_CONTENT_DOI_PATTERN = Pattern.compile("(?m)<div id=\"doi\" class=\"hide\">([^>]+?)<");
	private static final Pattern EXPORT_URL_DOI_PATTERN = Pattern.compile("doi=([^\\&]++)");
	
	private static final Pattern EXPORT_LINK_PATTERN = Pattern.compile("href=\"([^\"]++).*?id=\"export\"");
	private static final Pattern SUBMIT_ACTION_NODOI_PATTERN = Pattern.compile("<input.*?id=\"noDoi\".*?value=\"([^\"]++)\"");
	private static final Pattern SUBMIT_ACTION_DOI_PATTERN = Pattern.compile("<input.*?name=\"doi\".*?value=\"([^\"]*+)\"");
	private static final Pattern NUMBER_CITS_EXPORTED_PATTERN = Pattern.compile("NUMBER OF CITATIONS : (\\d++)");

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	private static final Pattern SUBMIT_ACTION_PATTERN = Pattern.compile("href=\"javascript:submitActionInNewWindow[^']++'([^']++)");

	private static final Pattern DOI = Pattern.compile("<div id=\"page1\" data-doi=\"(.*?)\"");
	private static final Pattern DOIFROMABS = Pattern.compile("/abs/(.+?)$");
	
	static {
		final Pattern hostPattern = Pattern.compile(".*" + JSTOR_HOST);
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_ABSTRACT_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_EXPORT_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_STABLE_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_DISCOVER_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_DOI_ABS_PATH + ".*")));
	}
	
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		//also one of the cookies we need is named "tenacious"
		final HttpClient client = WebUtils.getHttpClient();
		String bibtexResult = null;
		
		try {
			
			//targeting download page and from there submit action
			String downloadPage, submitAction;
			
			String url = sc.getUrl().toExternalForm();
			
			if (url.contains(JSTOR_DOI_ABS_PATH)) 
				url = EXPORT_PAGE_URL + exportDOIFromUrl(url);
			
			if(!url.contains(EXPORT_PAGE_URL))
				url = EXPORT_PAGE_URL + exportDOIFromSourceCode(url);
				
			//get the page content or at least get the cookies
			GetMethod getMethod = new GetMethod(url);
			final String page = WebUtils.getContentAsString(client, getMethod);
			
			if (page == null) {
				throw new ScrapingException("Cannot access requested location");
			}

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
						if (!m.find()) {
							throw new ScrapingException("Cannot continue. JStor Scraper must get updated");
						}
						exportPageLink = m.group(1).replace("&amp;", "&");				}
				}
				
				//is the export page link present?
				if (!present(exportPageLink)) {
					throw new ScrapingException("Cannot continue, finally not having submit action");
				}
				
				//download export page
				getMethod = new GetMethod(exportPageLink);
				downloadPage = WebUtils.getContentAsString(client, getMethod);
				
				// get the submit action from the export page
				m = SUBMIT_ACTION_PATTERN.matcher(downloadPage);
				
				//did we find it?
				if (!m.find()) {
					throw new ScrapingException("Downloaded export page but didn't find submit action");
				}
				
				submitAction = m.group(1);
			}
			
			//get some data from the page and switch submit action
			final Matcher noDoiMatcher = SUBMIT_ACTION_NODOI_PATTERN.matcher(downloadPage);
			final Matcher doiMatcher = SUBMIT_ACTION_DOI_PATTERN.matcher(downloadPage);
			if (!noDoiMatcher.find() || !doiMatcher.find()) {
				throw new ScrapingException("Couldn't get required data for export form");
			}
			final String noDoi = noDoiMatcher.group(1);
			URI actionURL;
			if ("noDoi".equalsIgnoreCase(noDoi)) {
				actionURL = getMethod.getURI();
			} else {
				actionURL = new URI(submitAction, true);
			}
			
			//post export form
			final PostMethod postMethod = new PostMethod();
			postMethod.setURI(actionURL);
			postMethod.addParameter("redirectUri", getMethod.getPath());
			postMethod.addParameter("noDoi", noDoi);
			postMethod.addParameter("doi", doiMatcher.group(1));
			bibtexResult = WebUtils.getPostContentAsString(client, postMethod);

		} catch (final IOException ex) {
			throw new ScrapingException(ex);
		}
		
		// check the result
		if (!present(bibtexResult)) {
			throw new ScrapingException("Could not submit export form");
		}
		
		final Matcher numberOfCitMatcher = NUMBER_CITS_EXPORTED_PATTERN.matcher(bibtexResult);
		if (!numberOfCitMatcher.find()) {
			throw new ScrapingException("no citations received");
		}
		
		final int numberOfCit = Integer.parseInt(numberOfCitMatcher.group(1));
		if (numberOfCit < 1) {
			throw new ScrapingException("received " + numberOfCit + " citations");
		}
		sc.setBibtexResult(bibtexResult);
		return true;
	}
	
	private static String exportDOIFromUrl(String url) throws ScrapingException {
		final Matcher m = DOIFROMABS.matcher(url);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	private static String exportDOIFromSourceCode(String url) throws ScrapingException {
		try {
			final Matcher m = DOI.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
			} catch(IOException e) {
				throw new ScrapingException("DOI not found");
			}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return "JSTOR";
	}

	@Override
	public String getSupportedSiteURL() {
		return JSTOR_HOST_NAME;
	}
}
