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

package org.bibsonomy.scraper.url.kde.plos;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for X.plosjournals.org
 * @author tst
 */
public class PlosScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "PLoS";
	private static final String SITE_URL = "http://www.plos.org/journals/index.php";
	private static final String INFO = "Scraper for journals from " + href(SITE_URL, SITE_NAME)+".";

	/**
	 * ending of plos journal URLs
	 */
	private static final String PLOS_HOST_ENDING = "plosbiology.org";

	/**
	 * title value from link to a citation page
	 */
	private static final String PATTERN_CITATION_PAGE_LINK = "<a href=\\\"([^\\\"]*)\\\">Citation</a>";

	/**
	 * name of a citation download link
	 */
	private static final String PATTERN_CITATION_BIBTEX_LINK = "<a href=\\\"([^\\\"]*)\\\" title=\\\"BibTex Citation\\\">BibTex</a>";

	/*
	 * regex
	 */

	/**
	 * pattern for links
	 */
	private static final String PATTERN_LINK = "<a\\b[^<]*</a>";

	/**
	 * pattern for href field
	 */
	private static final String PATTERN_HREF = "href=\\\"([^\\\"])*\\\"";

	/**
	 * get INFO
	 */
	public String getInfo() {
		return INFO;
	}

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + PLOS_HOST_ENDING), AbstractUrlScraper.EMPTY_PATTERN));

	
	/**
	 * Scrapes journals from plos.org 
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			// citation as endnote
			String citation = null;

			// may be journal page or citation page
			String journalPage = sc.getPageContent();
			String citationPage = null;

			// search link to citation in journal page
			Pattern linkPagePattern = Pattern.compile(PATTERN_CITATION_PAGE_LINK);
			Matcher linkPageMatcher = linkPagePattern.matcher(journalPage);
			if(linkPageMatcher.find()){
				String citationPageLinkHref = linkPageMatcher.group(1);
				citationPageLinkHref = "http://" + sc.getUrl().getHost() + citationPageLinkHref;
				
				// citation page found
				citationPage = WebUtils.getContentAsString(new URL(citationPageLinkHref));
			}

			// no citation found, may be current page is already the citation page
			if(citationPage == null)
				citationPage = journalPage;

			// search link to citation (in endnote)
			Pattern bibtexLinkPattern = Pattern.compile(PATTERN_CITATION_BIBTEX_LINK);
			Matcher bibtexLinkMatcher = bibtexLinkPattern.matcher(citationPage);
			while(bibtexLinkMatcher.find()){
				String citationLinkHref = bibtexLinkMatcher.group(1);
				citationLinkHref = "http://" + sc.getUrl().getHost() +  citationLinkHref;
				citation = WebUtils.getContentAsString(new URL(citationLinkHref));
			}

			/*
			 * http://www.plosbiology.org/article/getBibTexCitation.action?articleURI=info%3Adoi%2F10.1371%2Fjournal.pbio.0060010
			 * http://biology.plosjournals.org/perlserv//article/getBibTexCitation.action;jsessionid=5EE0CE24FCEEE9262A6A82B96BD2310E?articleURI=info%3Adoi%2F10.1371%2Fjournal.pbio.0060010
			 */
			// build bibtex
			if(citation != null){
				
				sc.setBibtexResult(citation);
				return true;

			}else
				throw new ScrapingFailureException("endnote is not available");

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
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
