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
package org.bibsonomy.scraper.url.kde.plos;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for X.plosjournals.org
 * @author tst
 */
public class PlosScraper extends GenericBibTeXURLScraper implements ReferencesScraper {

	private static final String SITE_NAME = "PLoS";
	private static final String SITE_URL = "http://www.plos.org/journals/index.php";
	private static final String INFO = "Scraper for journals from " + href(SITE_URL, SITE_NAME)+".";

	/*
	 * ending of plos journal URLs
	 */
	private static final String PLOS_BIOLOGY_HOST_ENDING = "plosbiology.org";
	private static final String PLOS_MEDICINE_HOST_ENDING = "plosmedicine.org";
	private static final String PLOS_COMPUTATIONAL_BIOLOGY_ENDING = "ploscompbiol.org";
	private static final String PLOS_GENETICS_ENDING = "plosgenetics.org";
	private static final String PLOS_PATHOGENS_ENDING = "plospathogens.org";
	private static final String PLOS_ONE_ENDING = "plosone.org";
	private static final String PLOS_NEGLECTED_TROPICAL_DISEASES_ENDING = "plosntds.org";

	private static final String HTTP = "http://";

	/*
	 * download url prefix
	 */
	private static final String PLOS_DOWNLOAD_URL_PREFIX = "/article/getBibTexCitation.action?articleURI=";

	private static final String PLOS_INFO_PATTERN_STRING = "(info:doi/.*/\\w+.\\w+.\\d+)";
	private static final Pattern PLOS_INFO_PATTERN = Pattern.compile(PLOS_INFO_PATTERN_STRING);
	private static final Pattern REFERENCES = Pattern.compile("(?s)<ol class=\"references\">(.*)</ol>");
	/**
	 * get INFO
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_BIOLOGY_HOST_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_MEDICINE_HOST_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_COMPUTATIONAL_BIOLOGY_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_GENETICS_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_PATHOGENS_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_ONE_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PLOS_NEGLECTED_TROPICAL_DISEASES_ENDING), AbstractUrlScraper.EMPTY_PATTERN));
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

	@Override
	public String getDownloadURL(URL url) throws ScrapingException {
		String decodedUrl = UrlUtils.safeURIDecode(url.toString());

		final Matcher _m = PLOS_INFO_PATTERN.matcher(decodedUrl);

		if (!_m.find()) return null;

		final String info = _m.group(1);
		if (!ValidationUtils.present(info)) return null;

		if (decodedUrl.contains(PLOS_BIOLOGY_HOST_ENDING)) 
			return HTTP + PLOS_BIOLOGY_HOST_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		if (decodedUrl.contains(PLOS_MEDICINE_HOST_ENDING)) 
			return HTTP + PLOS_MEDICINE_HOST_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		if (decodedUrl.contains(PLOS_COMPUTATIONAL_BIOLOGY_ENDING)) 
			return HTTP + PLOS_COMPUTATIONAL_BIOLOGY_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		if (decodedUrl.contains(PLOS_GENETICS_ENDING)) 
			return HTTP + PLOS_GENETICS_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		if (decodedUrl.contains(PLOS_PATHOGENS_ENDING)) 
			return HTTP + PLOS_PATHOGENS_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		if (decodedUrl.contains(PLOS_ONE_ENDING)) 
			return HTTP + PLOS_ONE_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		if (decodedUrl.contains(PLOS_NEGLECTED_TROPICAL_DISEASES_ENDING))
			return HTTP + PLOS_NEGLECTED_TROPICAL_DISEASES_ENDING + PLOS_DOWNLOAD_URL_PREFIX + info;

		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try{
			Matcher m = REFERENCES.matcher(WebUtils.getContentAsString(sc.getUrl().toString()));
			if(m.find()){
				sc.setReferences(m.group(1));
				return true;
			}
		}catch(IOException ex){
			throw new ScrapingException(ex);
		}
		return false;
	}
}
