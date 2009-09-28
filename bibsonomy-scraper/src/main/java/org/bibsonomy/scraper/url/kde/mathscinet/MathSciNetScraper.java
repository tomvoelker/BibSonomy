/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.url.kde.mathscinet;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for ams.org/mathscinet.
 * Reference can be entered as posts(bibtex page and overview page) and as snippet.
 * @author tst
 *
 */
public class MathSciNetScraper extends AbstractUrlScraper {
	
	private static final String INFO = "MathSciNetScraper: Extracts publications from " + href("http://www.ams.org/mathscinet/" , "MathSciNet") + 
	". Publications can be entered as a marked bibtex snippet or by posting the page of the reference.";

	
	/*
	 * URL components
	 */
	private static final String URL_MATHSCINET_HOST = "ams.org";
	private static final String URL_MATHSCINET_PATH = "/mathscinet";
	private static final String URL_MATHSCINET_FMT_PARAMETER = "fmt=bibtex";
		
	/*
	 * regualar expressions for complete elements
	 */
	private static final Pattern prePattern   = Pattern.compile("<pre>.*</pre>", Pattern.DOTALL);
	
	
	/*
	 * regualar expressions for attributes
	 */
	private static final Pattern pg1Pattern = Pattern.compile("<input type=\"hidden\" name=\"pg1\" value=\"([^\"]*)\" />");
	private static final Pattern s1Pattern = Pattern.compile("<input type=\"hidden\" name=\"s1\" value=\"([^\"]*)\" />");
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile("^.*" + URL_MATHSCINET_HOST), Pattern.compile(URL_MATHSCINET_PATH + ".*")));

	/**
	 * Extract a reference from a bibtex page as post and snippet. It also extracts a single references from its overview page (get its bibtex link and download it). 
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			String urlToBibtex = null;
			/*
			 * check of snippet
			 */			
			if(sc.getSelectedText() != null && sc.getUrl().toString().contains(URL_MATHSCINET_FMT_PARAMETER)){
				sc.setBibtexResult(sc.getSelectedText());
				return true;
				
			/*
			 * no snippet, check content from url
			 */
			}else if(sc.getUrl().toString().contains(URL_MATHSCINET_FMT_PARAMETER)){
				// is bibtex page
				urlToBibtex = sc.getUrl().toString();
				
			}else{
					// html page, extract bibtex URL
					String page = sc.getPageContent();
					
					/*
					 * get all elements for bibtex url
					 * - parameter pg1
					 * - parameter s1
					 */
					String s1 = null;
					Matcher s1Matcher = s1Pattern.matcher(page);
					if(s1Matcher.find())
						s1 = s1Matcher.group(1);
					
					String pg1 = null;
					Matcher pg1Matcher = pg1Pattern.matcher(page);
					if(pg1Matcher.find())
						pg1 = pg1Matcher.group(1);
					
					// build link to bibtex
					if(pg1 != null && s1 != null){
						urlToBibtex = "http://www." + URL_MATHSCINET_HOST + "/mathscinet/search/publications.html?" + URL_MATHSCINET_FMT_PARAMETER + "&pg1=" + pg1 + "&s1=" + s1;

					// values for URL are missing
					}else
						throw new PageNotSupportedException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
					
			}
			
			/*
			 * download bibtex page and extract bibtex
			 */
			if(urlToBibtex != null){
				try {
					String bibtexPage = WebUtils.getContentAsString(new URL(urlToBibtex));
					
					//search pre element, which contains the bibtex reference
					Matcher preMatcher = prePattern.matcher(bibtexPage);
					
					// extract reference
					if(preMatcher.find()){
						String bibtex = preMatcher.group();
						bibtex = bibtex.substring(5, bibtex.length()-6);
						
						sc.setBibtexResult(bibtex);
						return true;
						
					// can't find bibtex
					}else
						throw new PageNotSupportedException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
					
				} catch (IOException e) {
					throw new InternalFailureException(e);
				}
			// can't find url for bibtex
			}else
				throw new PageNotSupportedException("MathSciNetScraper: This MathSciNet page is not supported. Can't extract link to bibtex.");
	}
	
	public String getInfo() {
		return INFO;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return "AMS";
	}

	public String getSupportedSiteURL() {
		return "http://ams.org/";
	}
	
}
