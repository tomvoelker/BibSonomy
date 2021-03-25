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
package org.bibsonomy.scraper.url.kde.inspire;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for INSPIRE. The upgrade of SPIRES.
 * 
 * @author clemens
 */
public class InspireScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(InspireScraper.class);

	private static final String SITE_NAME = "INSPIRE";
	private static final String SITE_URL = "http://inspirehep.net/";

	private static final Pattern URL_ID_PATTERN = Pattern.compile("/record/([0-9]+)");
	private static final Pattern URL_DOWNLOAD_PATTERN = Pattern.compile("/export/hx");
	private static final String URL_FORMAT_WWWBRIEFBIBTEX = "FORMAT=WWWBRIEFBIBTEX";

	private static final Pattern BIBTEX_DIV_PATTERN = Pattern.compile("<div class=\"pagebodystripemiddle\"><pre>(.+)</pre>", Pattern.DOTALL);
	private static final Pattern BRIEFBIBTEX_PATTERN = Pattern.compile("<a href=\"?(/spires/find/hep/www\\?.*?\\&FORMAT=WWWBRIEFBIBTEX)\"?>");
	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a href=\"(.*?)\".*?>BibTeX</a>");
	
	private static final String INFO = "Gets publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "inspirehep.net"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	private static final Pattern HTML_ABSTRACT_PATTERN = Pattern.compile("(?i).*Abstract(.*)<span>(.*)</span>");
	private static final Pattern HTML_REFERENCES_PATTERN = Pattern.compile("(?s)<div id=\'referenceinp_link_box\'>(.*)<div id='referenceinp_link_box'>");

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {
			final URL url = sc.getUrl();
			final URL bibtexUrl;

			// test the different options to get the BibTeX export URL from URL and HTML content
			if (ValidationUtils.present(url.getQuery()) && url.getQuery().contains(URL_FORMAT_WWWBRIEFBIBTEX)) { 
				bibtexUrl = url;
			} else {
				final Matcher idMatcher = URL_ID_PATTERN.matcher(url.toExternalForm());
				if (idMatcher.find()) {
					bibtexUrl = new URL(SITE_URL + "record/" + idMatcher.group(1) + URL_DOWNLOAD_PATTERN);
				} else {
					//we are looking for some pattern in the source of the page
					final Matcher m = BRIEFBIBTEX_PATTERN.matcher(sc.getPageContent());
					//if we do not find, we maybe find a link :-)
					if (!m.find()) {
						final Matcher m2 = BIBTEX_PATTERN.matcher(sc.getPageContent());
						if (!m2.find()) throw new ScrapingFailureException("no download link found");
						bibtexUrl = new URL(url, m2.group(1));
					} else {
						bibtexUrl = new URL(url.getProtocol() + "://" + url.getHost() + m.group(1));
					}
				}
			}

			String bibtex = getBibTeX(bibtexUrl);

			/*
			 * add URL
			 */
			bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", url.toExternalForm());
			bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractParser(url));

			//-- bibtex string may not be empty
			if (bibtex != null && ! "".equals(bibtex)) {
				sc.setBibtexResult(bibtex);
				return true;
			}
			throw new ScrapingFailureException("getting bibtex failed");

		} catch (Exception e) {
			throw new InternalFailureException(e);
		}
	}

	private static String getBibTeX(final URL bibtexUrl) throws IOException {
		final String html = WebUtils.getContentAsString(bibtexUrl);
		final Matcher m = BIBTEX_DIV_PATTERN.matcher(html);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	private static String abstractParser(URL url){
		try {
			final Matcher m = HTML_ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(url));
			if(m.find())
				return m.group(2);
		} catch(Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try{
			final Matcher m = HTML_REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(sc.getUrl().toString() + "/references"));
			if(m.find()) {
				sc.setReferences(m.group(1));
				return true;
			}
		}catch(IOException e) {
			log.error("error while getting references" + sc.getUrl(), e);
		}
		return false;
	}
}
