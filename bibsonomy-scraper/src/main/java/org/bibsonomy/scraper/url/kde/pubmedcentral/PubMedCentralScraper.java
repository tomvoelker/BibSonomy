/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.pubmedcentral;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/** Scrapder for PubMed (http://www.pubmedcentral.nih.gov).
 * 
 * @author rja
 *
 */
public class PubMedCentralScraper extends GenericRISURLScraper {
	private static final String SITE_URL = "http://www.pubmedcentral.nih.gov/";
	private static final String SITE_NAME = "PubMedCentral";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String HOST = "pubmedcentral.nih.gov";
	private static final String NEWER_HOST = "ncbi.nlm.nih.gov";
	
	private static final Pattern IDS = Pattern.compile("articles/(.*?)/");
	
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NEWER_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	
	private static final Pattern PUBMED_LINK_PATTERN = Pattern.compile("<a[^>]*?href=\"(/pubmed/\\d++/)\"[^>]*+>PubMed</a>");
	

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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException {
		final Matcher m  = IDS.matcher(url.toExternalForm());
		if(m.find())
			return "http://" + url.getHost().toString() + "/pmc/utils/ctxp/?ids=" + m.group(1) + "&report=ris&format=ris";
		return null;
	}

	
}