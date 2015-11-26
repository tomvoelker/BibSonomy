/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * 
 * @author Christian Kramer
 * 
 */
public class PubMedScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "PubMed";
	private static final String SITE_URL = "http://www.ncbi.nlm.nih.gov/";
	private static final String info = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "ncbi.nlm.nih.gov";
	private static final String PUBMED_EUTIL_HOST = "eutils.ncbi.nlm.nih.gov";
	private static final String UK_PUBMED_CENTRAL_HOST = "ukpmc.ac.uk";
	private static final String EUROPE_PUBMED_CENTRAL_HOST = "europepmc.org";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	
	private static Pattern RISLINKPATTERN = Pattern.compile("href=\"((\\.\\./)*+.*?\\?wicket:interface=.*?:export:exportlink::ILinkListener::)");
	private static Pattern PMIDQUERYPATTERN = Pattern.compile("\\d+");
	private static Pattern PMIDPATTERN = Pattern.compile("PMID\\:\\D*(\\d+)");
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PUBMED_EUTIL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + UK_PUBMED_CENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + EUROPE_PUBMED_CENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)
			throws ScrapingException {
		String bibtexresult = null;
		sc.setScraper(this);
	
		// save the original URL
		String _origUrl = sc.getUrl().toString();

		try {
			if (_origUrl.matches("(?im)^.+db=PubMed.+$")) {
				
				Matcher ma = PMIDQUERYPATTERN.matcher(sc.getUrl().getQuery());

				// if the PMID is existent then get the bibtex from hubmed
				if (ma.find()) {
					String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids="
							+ ma.group();
					bibtexresult = WebUtils.getContentAsString(new URL(newUrl));
				}

				// try to scrape with new URL-Pattern
				// avoid crashes
			} else {
				HttpClient client = WebUtils.getHttpClient();
				
				// try to find link for RIS export
				GetMethod method = new GetMethod(sc.getUrl().toExternalForm());
				String pageContent = WebUtils.getContentAsString(client, method);
				
				Matcher risLinkMatcher = RISLINKPATTERN.matcher(pageContent);
				if (risLinkMatcher.find()) {
					HttpURL risURL = new HttpURL(new HttpURL(method.getURI().getURI()), risLinkMatcher.group(1));
					RisToBibtexConverter c = new RisToBibtexConverter();
					String ris = WebUtils.getContentAsString(client, risURL);
					bibtexresult = c.risToBibtex(ris);
				} else {
					
					Matcher ma = PMIDPATTERN.matcher(pageContent);
	
					// if the PMID is existent then get the bibtex from hubmed
					if (ma.find()) {
						String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids="
								+ ma.group(1);
						bibtexresult = WebUtils.getContentAsString(new URL(newUrl));
					} else {
		
						Pattern pa1 = Pattern.compile("meta name=\"citation_pmid\" content=\"(\\d+)\"");
						Matcher ma1 = pa1.matcher(pageContent);
		
						if (ma1.find()) {
							String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids=" + ma1.group(1);
							bibtexresult = WebUtils.getContentAsString(new URL(newUrl));
						}
					}
				}
			}
			

			// replace the humbed url through the original URL
			Pattern pa = Pattern.compile("url = \".*\"");
			Matcher ma = pa.matcher(bibtexresult);

			if (ma.find()) {
				// escape dollar signs 
				bibtexresult = ma.replaceFirst("url = \"" + _origUrl.replace("$", "\\$") + "\"");
			}

			// -- bibtex string may not be empty
			if (present(bibtexresult)) {
				sc.setBibtexResult(bibtexresult);
				return true;
			} else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (IOException e) {
			throw new InternalFailureException(e);
		}
	}

	public String getInfo() {
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}