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
package org.bibsonomy.scraper.url.kde.pubmed;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * 
 * @author Christian Kramer
 * 
 */
public class PubMedScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "PubMed";
	private static final String SITE_URL = "http://www.ncbi.nlm.nih.gov/";
	private static final String INFO = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "ncbi.nlm.nih.gov";
	private static final String PUBMED_EUTIL_HOST = "eutils.ncbi.nlm.nih.gov";
	private static final String UK_PUBMED_CENTRAL_HOST = "ukpmc.ac.uk";
	private static final String EUROPE_PUBMED_CENTRAL_HOST = "europepmc.org";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();

	private static final Pattern RISLINKPATTERN = Pattern.compile("href=\"((\\.\\./)*+.*?\\?wicket:interface=.*?:export:exportlink::ILinkListener::)");
	private static final Pattern PMIDQUERYPATTERN = Pattern.compile("\\d+");
	private static final Pattern PMIDPATTERN = Pattern.compile("PMID\\:\\D*(\\d+)");

	private static final Pattern PATTERN_PMID = Pattern.compile("meta name=\"citation_pmid\" content=\"(\\d+)\"");
	private static final Pattern PATTERN_URL = Pattern.compile("url = \".*\"");
	private static final Pattern PATTERN_URL1 = Pattern.compile("(?im)^.+db=PubMed.+$");

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PUBMED_EUTIL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + UK_PUBMED_CENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + EUROPE_PUBMED_CENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	private static final RisToBibtexConverter RIS2BIB = new RisToBibtexConverter();

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		// save the original URL
		final String url = sc.getUrl().toString();

		String pubmedId = null;
		String bibtex = null;
		try {
			final Matcher mu = PATTERN_URL1.matcher(url);
			if (mu.find()) {
				final Matcher ma = PMIDQUERYPATTERN.matcher(sc.getUrl().getQuery());
				// if the PMID is existent then extract it
				if (ma.find()) {
					pubmedId = ma.group();
				}

				// try to scrape with new URL-Pattern
				// avoid crashes
			} else {
				final HttpClient client = WebUtils.getHttpClient();

				// try to find link for RIS export
				final HttpGet method = new HttpGet(sc.getUrl().toExternalForm());
				final String pageContent = WebUtils.getContentAsString(client, method);

				final Matcher risLinkMatcher = RISLINKPATTERN.matcher(pageContent);
				if (risLinkMatcher.find()) {
					final URL risUrl = new URL(sc.getUrl().toExternalForm() + "/" + risLinkMatcher.group(1));
					bibtex = RIS2BIB.toBibtex(WebUtils.getContentAsString(client, risUrl.toURI(), null));
				} else {

					final Matcher ma = PMIDPATTERN.matcher(pageContent);

					// if the PMID is existent then get the bibtex from hubmed
					if (ma.find()) {
						pubmedId = ma.group(1);
					} else {
						final Matcher ma1 = PATTERN_PMID.matcher(pageContent);
						if (ma1.find()) {
							pubmedId = ma1.group(1);
						}
					}
				}
			}
			/*
			 * FIXME: this results in a 
			 * Exception in thread "main" java.net.SocketTimeoutException: Read timed out
			 * 
			 * However, during debugging it sometimes works ... seems to be a timing issue
			 * (or a server fooling us).
			 */
			if (ValidationUtils.present(pubmedId)) {
				bibtex = WebUtils.getContentAsString(new URL("http://www.hubmed.org/export/bibtex.cgi?uids=" + pubmedId));	
			}



			if (present(bibtex)) {
				// replace the broken URL through the original URL
				final Matcher ma = PATTERN_URL.matcher(bibtex);
				if (ma.find()) {
					// escape dollar signs 
					bibtex = ma.replaceFirst("url = \"" + url.replace("$", "\\$") + "\"");
				}

				sc.setBibtexResult(bibtex);
				return true;
			} 
			throw new ScrapingFailureException("getting bibtex failed");

		} catch (IOException e) {
			throw new InternalFailureException(e);
		} catch (HttpException e) {
			throw new InternalFailureException(e);
		} catch (URISyntaxException e) {
			throw new InternalFailureException(e);
		}
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

}