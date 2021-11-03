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
package org.bibsonomy.scraper.url.kde.iucr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * scraper for jornals from iucr.org. Because of the frame structure of 
 * journals.iucr.org pages only issues can be scraped which are sperated in a
 * another tab then the journal itself. The url of the issue in the new tab 
 * points dirctly to the issue and not to jounal page (if you open the issue in 
 * the same tab, then the url in the navgationbar will still point to the journal 
 * page and scraping is not possible.
 * 
 * example:
 * we want the second issue from this journal ->
 * http://journals.iucr.org/b/issues/2008/03/00/issconts.html
 * 
 * if we open the doi-link in the same tab, we get this url ->
 * http://journals.iucr.org/b/issues/2008/03/00/issconts.html
 * 
 * the issue will only be loaded in the central frame of the page and has no 
 * effect on the url. So we cannot recognize which issue was selected by the user.
 * If the user open the doi-link in a new tab, only the content of the central 
 * frame will be loaded and we can get the URL to the page of the issue. Like this
 * one ->
 * http://scripts.iucr.org/cgi-bin/paper?S0108768108005119
 * 
 * The rest is simple: extract the cnor from the url
 * like this -> http://scripts.iucr.org/cgi-bin/biblio?Action=download&cnor=ck5030&saveas=BIBTeX
 * 
 * @author tst
 */
public class IucrScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(IucrScraper.class);
	
	private static final String SITE_NAME = "International Union of Crystallography";
	private static final String SITE_URL = "http://www.iucr.org/";
	private static final String INFO = "Scraper for journals from the " + href(SITE_URL, SITE_NAME) +".";
	
	private static final String HOST = "iucr.org";
	
	private static final List<Pair<Pattern, Pattern>> patterns = Arrays.asList(
					new Pair<>(Pattern.compile(".*scripts." + HOST), AbstractUrlScraper.EMPTY_PATTERN),
					new Pair<>(Pattern.compile(".*journals." + HOST), AbstractUrlScraper.EMPTY_PATTERN)
	);

	/** Download link */
	private static final String DOWNLOAD_URL = "https://scripts.iucr.org/cgi-bin/biblio";
	private static final Pattern CNOR_PATTERN_1 = Pattern.compile("<input type=\"hidden\" value=\"(.{6})\" name=\"cnor\" \\/>");
	private static final Pattern CNOR_PATTERN_2 = Pattern.compile("<input name=\"cnor\" value=\"(.{6})\" type=\"hidden\"/>");

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		return DOWNLOAD_URL;
	}

	@Override
	protected List<NameValuePair> getDownloadData(URL url, String cookies) throws ScrapingException{
		try {
			String html = WebUtils.getContentAsString(url);

			String cnor;
			Matcher m1_cnor = CNOR_PATTERN_1.matcher(html);
			Matcher m2_cnor = CNOR_PATTERN_2.matcher(html);
			if (m1_cnor.find()){
				cnor = m1_cnor.group(1);
			}else {
				if (m2_cnor.find()){
					cnor = m2_cnor.group(1);
				}else {
					throw new ScrapingException("can't get cnor from html of " + url);
				}
			}

			List<NameValuePair> postData = new LinkedList<>();
			postData.add(new BasicNameValuePair("cnor", cnor));

			return postData;
		}catch (IOException e){
			throw new ScrapingException(e);
		}

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
		return SITE_URL;
	}

}
