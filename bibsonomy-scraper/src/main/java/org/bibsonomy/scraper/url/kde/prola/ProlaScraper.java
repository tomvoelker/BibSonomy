/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.url.kde.prola;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for prola.aps.org. It scrapes selected bibtex snippets and selected articles.
 * @author tst
 */
public class ProlaScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "PROLA";
	private static final String PROLA_APS_URL_BASE = "http://prola.aps.org";
	private static final String SITE_URL = PROLA_APS_URL_BASE+"/";
	private static final String INFO = "For selected BibTeX snippets and articles from " + href(SITE_URL , SITE_NAME)+".";

	/*
	 * needed URLs and components
	 */
	private static final String PROLA_APS_HOST = "prola.aps.org";
	private static final String PROLA_APS_BIBTEX_PARAM = "type=bibtex";

	/*
	 * needed regular expressions to extract download link
	 */
	private static final String PATTERN_LINK = "<a\\b[^<]*</a>";

	private static final String PATTERN_HREF = "href=\"[^\"]*\"";

	private static final String PATTERN_LINK_VALUE = ">(.*)<";

	private static final String PATTERN_BIBTEX_ENTRY = "@\\b[^\\{@]*\\{.*";

	/*
	 * value of download link
	 */
	private static final String DOWNLOAD_LINK_VALUE = "BibTeX";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + PROLA_APS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return INFO;
	}

	/**
	 * Extract atricles from prola.aps.org. It works with the article page, the bibtex page and a selected bibtex snippet.
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String prolaPageContent = sc.getPageContent();

		// check if snippet is selected
		if(sc.getSelectedText() != null){
			String bibtex = sc.getSelectedText();

			//remove comments bevor reference
			bibtex = cleanBibtexEntry(bibtex);

			// add downloaded bibtex to result 
			sc.setBibtexResult(bibtex);
			return true;						
		}

		// check if selected page is a bibtex page
		if(sc.getUrl().getQuery() != null && sc.getUrl().getQuery().contains(PROLA_APS_BIBTEX_PARAM)){
			//remove comments bevor reference
			final StringBuffer bibtex = new StringBuffer(cleanBibtexEntry(sc.getPageContent()));
			
			// append url
			BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());

			// add downloaded bibtex to result 
			sc.setBibtexResult(bibtex.toString().trim());
			return true;						
		}

		// no snippet, download bibtex
		String downloadLink = null;

		// extract all links from downloaded page
		Pattern linkPattern = Pattern.compile(PATTERN_LINK);
		Matcher linkMatcher = linkPattern.matcher(prolaPageContent);

		while(linkMatcher.find()){
			String linkMatch = linkMatcher.group();

			// extract the value between the a tags
			Pattern linkValuePattern = Pattern.compile(PATTERN_LINK_VALUE);
			Matcher linkValueMatcher = linkValuePattern.matcher(linkMatch);

			if(linkValueMatcher.find()){
				String linkValue = linkValueMatcher.group();

				// cut of the opening and closing brackets
				linkValue = linkValue.substring(1, linkValue.length()-1);

				// check if the link is the download bibtex link
				if(linkValue.equals(DOWNLOAD_LINK_VALUE)){

					// extracted link is the bibtex download link, search href attribute 
					Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
					Matcher hrefMatcher = hrefPattern.matcher(linkMatch);

					if(hrefMatcher.find()){
						String href = hrefMatcher.group();

						// cut of the leading herf=" and the ending "
						href = href.substring(6, href.length()-1);

						// build url to bibtex of this article
						downloadLink = PROLA_APS_URL_BASE + href;
						break;
					}
				}
			}
		}

		try {
			// check if download link is found
			if(downloadLink != null){

				// download article as bibtex
				String downloadedBibtex = null;
				downloadedBibtex = WebUtils.getContentAsString(downloadLink);

				if(downloadedBibtex != null){

					//remove comments bevor reference
					final StringBuffer bibtex = new StringBuffer(cleanBibtexEntry(downloadedBibtex));
					
					// append url
					BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
					
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibtex.toString().trim());
					
					return true;						
				}else
					throw new ScrapingException("ProlaScraper: can't get bibtex from this article");
			}else
				throw new PageNotSupportedException("ProlaScraper: This prola side has no bibtex download link.");
		} catch (IOException e) {
			throw new InternalFailureException(e);
		}
	}

	/**
	 * This method cuts of eveerything bevor bibtex entry.
	 * @param bibtexSnippet bibtex entry as String
	 * @return cleaned bibtex String
	 */
	private String cleanBibtexEntry(String bibtexSnippet){
		String bibtex = null;

		// search begin of bibtex entry
		Pattern bibPattern = Pattern.compile(PATTERN_BIBTEX_ENTRY, Pattern.DOTALL);
		Matcher bibMatcher = bibPattern.matcher(bibtexSnippet);

		if(bibMatcher.find())
			// cut of everything bevor bibtex entry
			bibtex = bibMatcher.group();

		return bibtex;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		// TODO Auto-generated method stub
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
