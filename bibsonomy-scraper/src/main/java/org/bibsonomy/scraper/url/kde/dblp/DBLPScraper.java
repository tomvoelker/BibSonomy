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
package org.bibsonomy.scraper.url.kde.dblp;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * This scraper can extract Data from the following hosts
 * 1. dblp.uni-trier.de
 * 2. search.mpi-inf.mpg.de
 * 3. dblp.dagstuhl.de
 * 4. dblp.org
 * 
 * @author wbi
 */
public class DBLPScraper extends GenericBibTeXURLScraper {
	
	private static final String SITE_NAME = "University of Trier Digital Bibliography & Library Project";
	private static final String DBLP_HOST_NAME1  = "http://dblp.uni-trier.de";
	private static final String SITE_URL  = DBLP_HOST_NAME1+"/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME)+".";
	private static final Pattern ALTERNATIVES = Pattern.compile("/rec/(bibtex|xml|rdf|ris|html|bib1|bib2)/(.+)(\\.xml|\\.rdf|\\.ris|\\.bib)?");
	private static final String DBLP_HOST1= "dblp.uni-trier.de";
	private static final String DBLP_HOST2  = "search.mpi-inf.mpg.de";
	private static final String DBLP_HOST3 = "dblp.dagstuhl.de";
	private static final String DBLP_HOST4 = "dblp.org";
	private static final String DBLP_PATH2  = "/dblp/";

	private static final List<Pair<Pattern,Pattern>> patterns = Arrays.asList(
		new Pair<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST1) , ALTERNATIVES),
		new Pair<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST2), Pattern.compile(DBLP_PATH2 + ".*")),
		new Pair<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST3) , ALTERNATIVES),
		new Pair<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST4) , ALTERNATIVES)
	);
	
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
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
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		/*
		 * FIXME: can't we extract the id of the publication from the url
		 * and then build the download url?
		 */
		String newURL = url.toString();
		String extesnion = getExtension(newURL);
		String path = getPath(newURL);
		if (extesnion != null) {
			newURL = newURL.replace("." + extesnion, ".bib");
			return newURL.replace("/" + extesnion, "/bib");
		}
		else if (path != null) {
			return newURL.replace("/" + path, "/bib") + ".bib";
		}
		else {
			return newURL;
		}
	}
	
	// FIXME: what about bib1, bib2?
	private String getPath(String url) {
		if (url.contains("/html")) {
			return "html";
		}
		else if (url.contains("/bibtex"))
			return "bibtex";
		else
			return null;
	}
	
	
	private String getExtension(String url) {
		if (url.contains(".xml")) {
			return "xml";
		}
		else if (url.contains(".rdf")) {
			return "rdf";
		}
		else if (url.contains(".ris")) {
			return "ris";
		}
		else
			return null;
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex.replaceAll("timesta.*\\n", "");
	}
}
