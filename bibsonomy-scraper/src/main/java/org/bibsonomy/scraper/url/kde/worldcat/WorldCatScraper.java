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

package org.bibsonomy.scraper.url.kde.worldcat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper for http://www.worldcat.org 
 * @author tst
 */
public class WorldCatScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Worldcat";
	private static final String SITE_URL = "http://www.worldcat.org/";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String WORLDCAT_URL = "http://www.worldcat.org/search?qt=worldcat_org_all&q=";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "worldcat.org"), Pattern.compile("/oclc/")));

	private static final Pattern PATTERN_GET_FIRST_SEARCH_RESULT = Pattern.compile("<a href=\"([^\\\"]*brief_results)\">");
	
	private static final RisToBibtexConverter converter = new RisToBibtexConverter();

	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		try {
			final String bibtex = getBibtex(sc.getUrl(), false);

			if(bibtex != null){
				sc.setBibtexResult(bibtex);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	/**
	 * search publication on worldcat.org with a given isbn and returns it as bibtex
	 * @param isbn isbn for search
	 * @return publication as bibtex
	 * @throws IOException 
	 * @throws ScrapingException
	 */
	public static String getBibtexByISBN(final String isbn) throws IOException, ScrapingException{
		return getBibtex(new URL(WORLDCAT_URL + ISBNUtils.cleanISBN(isbn)), true);
	}


	/**
	 * builds a worldcat.org URL with the given ISBN
	 * @param isbn valid ISBN
	 * @return URL from worldcat.org, null if no ISBN is give 
	 * @throws MalformedURLException 
	 */
	public static URL getUrlForIsbn(final String isbn) throws MalformedURLException{
		final String checkISBN = ISBNUtils.extractISBN(isbn);

		// build worldcat.org URL
		if (checkISBN != null)
			return new URL(WORLDCAT_URL + checkISBN);
		return null;
	}
	
	private static String getBibtex(final URL publPageURL, final boolean search) throws IOException, ScrapingException {
		final Matcher matcherFirstSearchResult = PATTERN_GET_FIRST_SEARCH_RESULT.matcher(WebUtils.getContentAsString(publPageURL));
		
		URL publUrl = null;
		if(matcherFirstSearchResult.find())
			publUrl = new URL(publPageURL.getProtocol() + "://" + publPageURL.getHost() + matcherFirstSearchResult.group(1));
		else
			publUrl = publPageURL;
		
		
		String exportUrl = null;
		if(search)
			exportUrl = publUrl.getProtocol() + "://" + publUrl.getHost() + publUrl.getPath() + "?" + publUrl.getQuery() + "&page=endnote&client=worldcat.org-detailed_record";
		else
			exportUrl = publUrl.getProtocol() + "://" + publUrl.getHost() + publUrl.getPath() + "?page=endnote&client=worldcat.org-detailed_record";

		final String endnote = WebUtils.getContentAsString(new URL(exportUrl));

		/*
		 * convert RIS to BibTeX
		 */
		final String bibtex = converter.RisToBibtex(endnote);
		/*
		 * add URL
		 */
		return BibTexUtils.addFieldIfNotContained(bibtex, "url", publPageURL.toString());
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
