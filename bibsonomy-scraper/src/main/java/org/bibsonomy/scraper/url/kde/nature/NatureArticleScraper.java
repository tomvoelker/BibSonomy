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
package org.bibsonomy.scraper.url.kde.nature;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.scraper.url.kde.biomed.BioMedCentralScraper;

/**
 * Scraper for Nature
 *
 * @author Johannes
 */
public class NatureArticleScraper extends GenericBibTeXURLScraper {	
	
	private static final String SITE_NAME = "Nature";
	private static final String SITE_URL = "https://www.nature.com/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";
	private static final Pattern PATH_PATTERN = Pattern.compile("/articles/(.+)(\\?.*)?$");
	
	private static final Pattern DOI_PATTERN = Pattern.compile("([a-z]{3,})([0-9]{4})([0-9]{2,})");
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + "www.nature.com"), Pattern.compile("articles/.*")));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/** 
	 *  
	 * NOTE: This function is almost identical in {@link BioMedCentralScraper} 
	 * but here the DOI prefix is not contained in the URL but needs to be 
	 * added as a constant (10.1038 for Nature). (And hopefully, that's the only one.)-: 
	 * 
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		//return url.toString().replaceAll("\\?cacheBust=.*", "") + ".ris";
		/*
		 * input:  https://www.nature.com/articles/s41467-019-08746-5
		 * output: https://citation-needed.springer.com/v2/references/10.1038/s41467-019-08746-5?format=refman&flavour=citation
		 */
		final Matcher m = PATH_PATTERN.matcher(url.getPath());
		if (m.find()) {
			return "https://citation-needed.springer.com/v2/references/10.1038/" + fixDOI(m.group(1)) + "?format=bibtex&flavour=citation";
		}
		return null;
	}

	/**
	 * FIXME: For some URL strings dots (.) need to be inserted. Instead 
	 * of scraping the page, we do this here with a heuristic. Let's see 
	 * when it will break. :-(
	 * 
	 * example:
	 * 
	 * url:           http://www.nature.com/articles/nenergy201741
     * result:        https://citation-needed.springer.com/v2/references/10.1038/nenergy201741?format=bibtex&flavour=citation
	 * but should be: https://citation-needed.springer.com/v2/references/10.1038/nenergy.2017.41?format=bibtex&flavour=citation
	 * 
	 * url            https://www.nature.com/articles/onc2014416
	 * should be:     https://citation-needed.springer.com/v2/references/10.1038/onc.2014.416?format=refman&flavour=citation
	 * 
	 * @param doi
	 * @return
	 */
	private String fixDOI(final String doi) {
		final Matcher m = DOI_PATTERN.matcher(doi);
		if (m.find()) {
			return m.group(1) + "." + m.group(2) + "." + m.group(3);
		}
		return doi;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
