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
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Scraper for INSPIRE. The upgrade of SPIRES.
 * 
 * @author clemens
 */
public class InspireScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(InspireScraper.class);
	
	private static final String SITE_NAME = "INSPIRE";
	private static final String SITE_URL = "http://inspirehep.net/";
	
	private static final Pattern pattern_id = Pattern.compile("/record/([0-9]+)");
	private static final Pattern pattern_download = Pattern.compile("/export/hx");

	private static final String info = "Gets publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "inspirehep.net"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	private static final Pattern pattern_abstract = Pattern.compile("(?i).*Abstract(.*)<span>(.*)</span>");
	private static final Pattern references = Pattern.compile("(?s)<div id=\'referenceinp_link_box\'>(.*)<div id='referenceinp_link_box'>");
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		try {
			final String url = sc.getUrl().toString();
			
			final Matcher idMatcher = pattern_id.matcher(url);
			
			if(idMatcher.find()) {
				URL bibtexUrl = new URL(SITE_URL + "record/" + idMatcher.group(1) + pattern_download);
				final Document temp = XmlUtils.getDOM(bibtexUrl);
				
				//extract the bibtex snippet which is embedded in pre tags
				String bibtex = null;
				final NodeList nl = temp.getElementsByTagName("pre"); //get the pre tags (normally one)
				for (int i = 0; i < nl.getLength(); i++) {
					Node currNode = nl.item(i);
					if (currNode.hasChildNodes()){
						bibtex = currNode.getChildNodes().item(0).getNodeValue();	
					}
				}
				
				/*
				 * add URL
				 */
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", url);
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractParser(sc.getUrl()));
				
				//-- bibtex string may not be empty
				if (bibtex != null && ! "".equals(bibtex)) {
					sc.setBibtexResult(bibtex);
					return true;
				}
			}
			throw new ScrapingFailureException("getting bibtex failed");
			
		} catch (Exception e) {
			throw new InternalFailureException(e);
		}
	}
	
	private static String abstractParser(URL url){
		try {
			Matcher m = pattern_abstract.matcher(WebUtils.getContentAsString(url));
			if(m.find())
				return m.group(2);
		} catch(Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
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
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try{
			final Matcher m = references.matcher(WebUtils.getContentAsString(sc.getUrl().toString() + "/references"));
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
