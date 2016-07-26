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
package org.bibsonomy.scraper.url.kde.spires;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Scraper for the SLAC National Accelerator Laboratory
 * @author rja
 *
 */
public class SpiresScraper extends AbstractUrlScraper implements ReferencesScraper, CitedbyScraper{
	private static final String SITE_NAME = "SLAC National Accelerator Laboratory";
	private static final String SITE_URL = "http://slac.stanford.edu/";
	private static final String FORMAT_WWWBRIEFBIBTEX = "FORMAT=WWWBRIEFBIBTEX";

	private static final String info = "Gets publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	private static Pattern BRIEFBIBTEX_PATTERN = Pattern.compile("<a href=\"?(/spires/find/hep/www\\?.*?\\&FORMAT=WWWBRIEFBIBTEX)\"?>");
	private static Pattern BIBTEX_PATTERN = Pattern.compile("<a href=\"(.*?)\".*?>BibTeX</a>");
	
	private static Pattern REFERENCES_URL_PATTERN = Pattern.compile("<a href=\"(.*)\">References</a>");
	private static Pattern REFERENCES_PATTERN = Pattern.compile("(?s)<table>(.*)</table>");
	private static Pattern CITEDBY_PATTERN = Pattern.compile("(?s)<table>(.*)</table>");
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "slac.stanford.edu"), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "www-library.desy.de"), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "inspirehep.net"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			try {
				final URL url = sc.getUrl();
				
				URL bibtexUrl = url;
				if (!url.getQuery().contains(FORMAT_WWWBRIEFBIBTEX)) { 
					//we are looking for some pattern in the source of the page
					Matcher m = BRIEFBIBTEX_PATTERN.matcher(sc.getPageContent());
					//if we do not find, we maybe find a link :-)
					if (!m.find()) {
						Matcher m2 = BIBTEX_PATTERN.matcher(sc.getPageContent());
						if (!m2.find()) throw new ScrapingFailureException("no download link found");
						bibtexUrl = new URL(bibtexUrl, m2.group(1));
					} else {
						bibtexUrl = new URL(url.getProtocol() + "://" + url.getHost() + m.group(1));
					}
				}
				
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
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", url.toString());
				
				//-- bibtex string may not be empty
				if (bibtex != null && ! "".equals(bibtex)) {
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

				
			} catch (IOException e) {
				throw new InternalFailureException(e);
			}
	}
	private static String getReferenceURL(ScrapingContext sc) throws IOException{
		Matcher m = REFERENCES_URL_PATTERN.matcher(WebUtils.getContentAsString(sc.getUrl()));
		if(m.find())
			return m.group(1);
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
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			String url = getReferenceURL(scrapingContext).replace("references", "citations");
			String citedby = null;
			Matcher m = CITEDBY_PATTERN.matcher(WebUtils.getContentAsString(url));
			if(m.find())
				citedby = m.group(1);
			
			if(citedby != null){
				scrapingContext.setCitedBy(citedby);
				return true;
			}
				
		} catch (IOException e) {
			
			throw new InternalFailureException(e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		try{
			String url = getReferenceURL(scrapingContext);
			String references = null;
			Matcher m = REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(url));
			if(m.find())
				references = m.group(1);
			if(references != null){
				scrapingContext.setReferences(references);
				return true;
			}
			
		}catch(IOException e){
			throw new InternalFailureException(e);
		}
		return false;
	}

}