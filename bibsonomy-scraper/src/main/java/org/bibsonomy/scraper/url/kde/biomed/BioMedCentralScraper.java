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
package org.bibsonomy.scraper.url.kde.biomed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class BioMedCentralScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "BioMed Central";
	private static final String BIOMEDCENTRAL_HOST_NAME  = "http://www.biomedcentral.com";
	private static final String SITE_URL = BIOMEDCENTRAL_HOST_NAME+"/";

	private static final String info = "This Scraper parse a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final String BIOMEDCENTRAL_HOST  = "biomedcentral.com";
	private static final String BIOMEDCENTRAL_BIBTEX_PATH = "citation";
	private static final String BIOMEDCENTRAL_BIBTEX_PARAMS = "format=bibtex&include=cit&direct=on&action=submit";
	
	private static final Pattern DOI_PATTERN_FROM_URL_FOR_SUBHOST_JBIOMEDSEM = Pattern.compile("/articles/(.+?)$");
	
	private static final String FORMAT_BIBTEX_FLAVOUR_CITATION = "?format=bibtex&flavour=citation";
	private static final String DOWNLOAD_URL_FOR_SUBHOST_JBIOMEDSEM = "http://citation-needed.services.springer.com/v2/references/";
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + BIOMEDCENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		String url = sc.getUrl().toString();

		/*
		 * extract only from the host jbiomedsem.biomedcentral.com/
		 */
		if ("jbiomedsem.biomedcentral.com".equals(sc.getUrl().getHost())) {
			Matcher m = DOI_PATTERN_FROM_URL_FOR_SUBHOST_JBIOMEDSEM.matcher(url);
			if (m.find()) {
				try {
					final String bibtexResult = WebUtils.getContentAsString(new URL(DOWNLOAD_URL_FOR_SUBHOST_JBIOMEDSEM + m.group(1) + FORMAT_BIBTEX_FLAVOUR_CITATION));
					sc.setBibtexResult(bibtexResult);
					return true;
				} catch (IOException ex) {
					throw new ScrapingFailureException(ex);
				}
			}
		}
		if (!(url.endsWith("/" + BIOMEDCENTRAL_BIBTEX_PATH + "/") || 
				url.endsWith("/" + BIOMEDCENTRAL_BIBTEX_PATH) ||
				url.endsWith(BIOMEDCENTRAL_BIBTEX_PATH))) {
			if (!url.endsWith("/")) {
				url += "/" + BIOMEDCENTRAL_BIBTEX_PATH;
			} else {
				url += BIOMEDCENTRAL_BIBTEX_PATH;
			}
		}

		try {
			sc.setUrl(new URL(url));
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		}
		
		try {
			String bibResult = WebUtils.getPostContentAsString(sc.getUrl(), BIOMEDCENTRAL_BIBTEX_PARAMS);
			if (bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}
		} catch (IOException ex) {
			throw new ScrapingFailureException(ex);
		}

		return false;
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
