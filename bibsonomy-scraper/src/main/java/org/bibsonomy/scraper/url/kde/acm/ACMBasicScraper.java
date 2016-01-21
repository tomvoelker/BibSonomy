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
package org.bibsonomy.scraper.url.kde.acm;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.util.id.DOIUtils;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Scrapes the ACM digital library
 * @author rja
 *
 */
public class ACMBasicScraper extends AbstractUrlScraper implements ReferencesScraper, CitedbyScraper {
	private static final Log log = LogFactory.getLog(ACMBasicScraper.class);
	
	private static final String ACM_BASE_TAB_URL = "http://dl.acm.org/tab_";
	private static final String SITE_NAME = "ACM Digital Library";
	private static final String SITE_URL = "http://portal.acm.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final String CACM_DOMAIN = "cacm.acm.org";
	
	private static final List<Pair<Pattern,Pattern>> patterns = Arrays.asList(
		new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + "[(portal)(dl)].acm.org"), 
			Pattern.compile("(/beta)?/citation.cfm.*")
		),
		new Pair<Pattern, Pattern>(
				Pattern.compile(".*" + "queue.acm.org"), 
				Pattern.compile("/detail.cfm.*")
			),
			
		new Pair<Pattern, Pattern>(
				Pattern.compile(".*" + CACM_DOMAIN),
				Pattern.compile("/magazines/*")
				),
				
		new Pair<Pattern, Pattern>(
				Pattern.compile(".*" + "doi.acm.org"),
				EMPTY_PATTERN
		)
	);
	
	private static final String BROKEN_END = new String("},\n}");
	//get the publication's id, take the part behind the dot if present
	private static final Pattern URL_PARAM_ID_PATTERN = Pattern.compile("id=(\\d+(?:\\.(\\d+))?)");
	private static final Pattern DOI_URL_ID_PATTERN = Pattern.compile("/(\\d+(?:\\.(\\d+))?)");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div style=\"display:inline\">(\\s*<p>\\s*)?((?s).+?)(\\s*<\\/p>\\s*)?<\\/div>", Pattern.MULTILINE);
	
	// to get publication id for CACM
	private static final Pattern CACM_ID = Pattern.compile("<a href=(.*?)/citation.cfm\\?id=.*?\\.(.*?)&amp\\;coll=portal");
	
	/** remove tags in abstract */
	private static final String CLEANUP_ABSTRACT = "<[\\da-zA-Z\\s]*>|<\\s*/\\s*[\\da-zA-Z\\s]*>|\\r\\n|\\n";
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		try {
			/*
			 * extract the id from the URL
			 */
			final String id;
			final String query = sc.getUrl().getQuery();
			final Matcher matcher;
			if (query == null) {
				/*
				 * for cacm journals: extract the id from the page content
				 */
				// TODO: maybe a separate cacm scraper?
				if (sc.getUrl().toString().contains(CACM_DOMAIN)) {
					matcher = CACM_ID.matcher(sc.getPageContent());
				} else {
					matcher = DOI_URL_ID_PATTERN.matcher(sc.getUrl().toExternalForm());
				}
			} else {
				matcher = URL_PARAM_ID_PATTERN.matcher(query);
			}
			
			if (matcher == null) 
				return false;
			
			/*
			 * if present take the id behind the dot
			 */
			if (matcher.find()) {
				id = ((matcher.group(2) != null) ? matcher.group(2) : matcher.group(1));
				sc.getTmpMetadata().setId(id);
			} else {
				return false;
			}
		
			//pretty good idea to use an own client, since the session in the common client can become invalid
			final HttpClient client = WebUtils.getHttpClient();
			
			/*
			 * Scrape entries from popup BibTeX site. BibTeX entry on these
			 * pages looks like this: <PRE id="155273">@article{155273,
			 * author = {The Author}, title = {This is the title}...}</pre>
			 */
			final StringBuffer bibtexEntries = extractBibtexEntries(client, SITE_URL, "exportformats.cfm?expformat=bibtex&id=" + id);

			final String abstrct = WebUtils.getContentAsString(client, SITE_URL + "/tab_abstract.cfm?usebody=tabbody&id=" + id);
			if (present(abstrct)) {
				/*
				 * extract abstract from HTML
				 */
				final Matcher matcher2 = ABSTRACT_PATTERN.matcher(abstrct);
				if (matcher2.find()) {
					final String extractedAbstract = matcher2.group(2);
					if(extractedAbstract != null) {
						//add abstract, remove tags and replace html entities with utf-8 pendants
						BibTexUtils.addFieldIfNotContained(bibtexEntries, "abstract", HtmlUtils.htmlUnescape(extractedAbstract.replaceAll(CLEANUP_ABSTRACT, "")));
					}
				} else {
					// log if abstract is not available
					log.info("ACMBasicScraper: Abstract not available");
				}
			} else {
				// log if abstract is not available
				log.info("ACMBasicScraper: Abstract not available");
			}

			/*
			 * Some entries (e.g., http://portal.acm.org/citation.cfm?id=500737.500755) seem
			 * to have broken BibTeX entries with a "," too much at the end. We remove this
			 * here.
			 *
			 * Some entries have the following end: "},\n} \n" instead of the BROKEN_END String.
			 * So we have to adjust the starting index by the additional 2 symbols.
			 */
			final int indexOf = bibtexEntries.indexOf(BROKEN_END, bibtexEntries.length() - BROKEN_END.length() - 2);
			if (indexOf > 0) {
				bibtexEntries.replace(indexOf, bibtexEntries.length(), "}\n}");
			}

			final String result = DOIUtils.cleanDOI(bibtexEntries.toString().trim());
			if (present(result)) {
				sc.setBibtexResult(result);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}
	}

	/**
	 * This method walks through the dom of the given url
	 * and tries to extract the bibtex entries.
	 * 
	 * Structure is:
	 * 
	 * ...
	 * <PRE>
	 * 	Bibtex Entry
	 * </PRE>
	 * ...
	 * 
	 * 
	 * @param siteUrl
	 * @param pathsToScrape
	 * @return extracted bibtex entries
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static StringBuffer extractBibtexEntries(HttpClient client, final String siteUrl, final String path) throws MalformedURLException, IOException{
		final StringBuffer bibtexEntries = new StringBuffer();
		
		//get content for siteUrl
		final String siteContent = WebUtils.getContentAsString(client, siteUrl + path);

		// create a DOM with each
		final Document doc = XmlUtils.getDOM(siteContent);

		// fetch the nodelist
		final NodeList pres = doc.getElementsByTagName("pre");

		// and extract the bibtex entry
		for (int i = 0; i < pres.getLength(); i++) {
			final Node currNode = pres.item(i);
			bibtexEntries.append(XmlUtils.getText(currNode));
		}

		return bibtexEntries;
	}

	@Override
	public String getInfo() {
		return INFO;
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
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		return scrapeMetaData(scrapingContext, "citings");
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		return scrapeMetaData(scrapingContext, "references");
	}

	private static boolean scrapeMetaData(ScrapingContext scrapingContext, final String kind) {
		final HttpClient client = WebUtils.getHttpClient();
		final String id = scrapingContext.getTmpMetadata().getId();
		final String url = ACM_BASE_TAB_URL + kind +  ".cfm?id=" + id;
		try{
			final String reference = WebUtils.getContentAsString(client, url);
			if(present(reference)){
				scrapingContext.setReferences(reference);
				scrapingContext.setCitedBy(reference);
				return true;
			}
		} catch(Exception e) {
			log.error("error while scraping references by for " + scrapingContext.getUrl(), e);
		}
		return false;
	}
}