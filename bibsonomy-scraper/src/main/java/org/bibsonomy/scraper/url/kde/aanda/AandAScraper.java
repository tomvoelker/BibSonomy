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
package org.bibsonomy.scraper.url.kde.aanda;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Christian Kramer
 */
public class AandAScraper extends AbstractUrlScraper implements ReferencesScraper{

	private static final String SITE_NAME = "Astronomy and Astrophysics";
	private static final String SITE_URL = "http://www.aanda.org/";
	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final Pattern hostPattern = Pattern.compile(".*" + "aanda.org");
	private static final String downloadUrl = SITE_URL + "index.php?option=com_makeref&task=output&type=bibtex&doi=";
	
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(hostPattern, AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern pat_references = Pattern.compile("(?s)<ul style=\"list-style-type:\" class=\"references\">(.*)<div class=\"pr_annees\"></div>");
	private static final Pattern pat_references_1 = Pattern.compile("(?s)<HR><b>References(.*)</UL>");
	private static final Pattern pat_link_ref = Pattern.compile("<a href=\"(.*)\">References</a></li>");
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		try {
			// need to filter the DOI out of the context, because the DOI is a common but not constant finding in the URL
			final String doi = extractDOI(XmlUtils.getDOM(sc.getPageContent()));

			// if the doi is present
			if (present(doi)) {
				// BibtexScraper will extract the bibtex from the download location
				final ScrapingContext scForBibtexScraper = new ScrapingContext(new URL(downloadUrl + doi));
				/*
				 * TODO: it would be nicer that the scraper can reenvoke the scraper
				 * chain with the new scraping context
				 */
				if (new BibtexScraper().scrape(scForBibtexScraper)) {
					// TODO: decode Tex Macros, Tex Entities. Also @see UBKAScraper.
					sc.setBibtexResult(scForBibtexScraper.getBibtexResult());
					return true;
				}
				
			}
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}
		
		return false;
	}
	
	/**
	 * Extracts the DOI out of the page source.
	 * Structure is as follows:
	 * 
	 *  <tr>
	 *	   <td class="gen">DOI</td>
	 *	   <td></td>
	 *	   <td><a href="...">http://dx.doi.org/10.1051/0004-6361/201014294</a></td>
	 *	</tr>
	 * 
	 * @param document
	 * @return
	 */
	private static String extractDOI(final Document document){
		final NodeList tdS = document.getElementsByTagName("td");
		for (int i = 0; i < tdS.getLength(); i++) {
			final Node node = tdS.item(i);
			if (node.hasChildNodes()){
				if ("DOI".equals(node.getFirstChild().getNodeValue())) {
					return node.getParentNode().getLastChild().getFirstChild().getFirstChild().getNodeValue().replaceFirst("http:\\/\\/dx\\.doi\\.org\\/", "");
				}
			}
		}
		return null;
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
	public String getInfo() {
		return INFO;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		String references = null;
		try{
			final Matcher m = pat_link_ref.matcher(WebUtils.getContentAsString(scrapingContext.getUrl().toString()));
			if (m.find()) {
				String url = "http://" + scrapingContext.getUrl().getHost().toString() + m.group(1);
				Matcher m2 = pat_references.matcher(WebUtils.getContentAsString(url));
				if (m2.find()) {
					references = m2.group(1);
				} else {
					Matcher m3 = pat_references_1.matcher(WebUtils.getContentAsString(url));
					if(m3.find())
						references = m3.group(1);
				}
			}
			if (references != null) {
				scrapingContext.setReferences(references);
				return true;
			}
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}
			
		return false;
	}
}
