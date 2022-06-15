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
package org.bibsonomy.scraper.url.kde.osa;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class OSAScraper extends GenericBibTeXURLScraper implements ReferencesScraper{
	private static final Log log = LogFactory.getLog(OSAScraper.class);

	private static final String SITE_NAME = "Optical Society of America";
	private static final String SITE_URL  = "https://www.osapublishing.org/";
	private static final String info = "This Scraper parses a publication from the " + href(SITE_URL, SITE_NAME)+".";
	private static final String OLD_HOST  = "osapublishing.org";
	private static final String NEW_HOST  = "opg.optica.org";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<>();
	static{
		PATTERNS.add(new Pair<>(Pattern.compile(".*" + OLD_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<>(Pattern.compile(".*" + NEW_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	private static final Pattern ID_PATTERN = Pattern.compile("<form.*?>\\s*<input.*?value=\"(\\d*)\">\\s+<input .*?value=\"(?:export_bibtex)?\"></form>");

	final static Pattern references_pattern = Pattern.compile("(?s)<h3>References</h3>\\s+<div .*>\\s+<ol>(.*)</ol>");


	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		return "https://opg.optica.org/custom_tags/IB_Download_Citations.cfm";
	}

	@Override
	protected List<NameValuePair> getDownloadData(URL url, String cookies) throws ScrapingException {
		try {
			String pageContent = WebUtils.getContentAsString(url);
			Matcher m_id = ID_PATTERN.matcher(pageContent);
			if (!m_id.find()){
				throw new ScrapingException("Couldn't find ID");
			}
			ArrayList<NameValuePair> postData = new ArrayList<>();
			postData.add(new BasicNameValuePair("ArticleAction", "export_bibtex"));
			postData.add(new BasicNameValuePair("articles", m_id.group(1)));

			return postData;
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
