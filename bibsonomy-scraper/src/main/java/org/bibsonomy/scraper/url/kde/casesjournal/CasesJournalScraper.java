/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.casesjournal;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author claus
 * @version $Id$
 */
public class CasesJournalScraper extends AbstractUrlScraper {
	
	private static final String INFO = "CasesJournal: For selected BibTeX snippets and articles from " + href("http://casesjournal.com/" , "CASES_JOURNAL");

	/*
	 * needed URLs and components
	 */
	private static final String CASES_JOURNAL_HOST = "casesjournal.com";
	private static final String CASES_JOURNAL_PATH  = "/casesjournal";
	private static final String CASES_JOURNAL_URL_BASE = "http://casesjournal.com";
	
	private static final String CASES_JOURNAL_ID_PREFIX = "view/";
	private static final String CASES_JOURNAL_BIBTEX_PATH = "/rt/captureCite/{id}/0/BibtexCitationPlugin";
	private static final String CASES_JOURNAL_TAG_NAME = "pre";
	private static final String CASES_JOURNAL_ID_PATTERN = "{id}";
	private static final String CASES_JOURNAL_BIBTEX_KEY_PATTERN = "@article\\s*\\{(\\s*\\w+\\s*\\w*),.*";
	private static final String SPACE_PATTERN = "\\s+";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + CASES_JOURNAL_HOST), Pattern.compile(CASES_JOURNAL_PATH + ".*")));

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext)
			throws ScrapingException {
		
		final String url = scrapingContext.getUrl().toString();
		String bibtex = null;
		String id = null;
		
		scrapingContext.setScraper(this);
		
		if (url.startsWith(CASES_JOURNAL_URL_BASE + CASES_JOURNAL_PATH)) {
			id = url.substring(url.lastIndexOf(CASES_JOURNAL_ID_PREFIX) + CASES_JOURNAL_ID_PREFIX.length());
		}
		
		URL downloadUrl;
		try {
			downloadUrl = new URL(CASES_JOURNAL_URL_BASE + CASES_JOURNAL_PATH
					+ CASES_JOURNAL_BIBTEX_PATH.replace(CASES_JOURNAL_ID_PATTERN, id));
			final Document doc = XmlUtils.getDOM(downloadUrl);
			bibtex = extractBibtex(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalFailureException(e);
		}
		
		/*
		 * append URL
		 */
		BibTexUtils.addFieldIfNotContained(bibtex, "url", url);
		
		if(bibtex != null) {
			scrapingContext.setBibtexResult(bibtex);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");
		
	}

	/**
	 * Extracts the BibTeX Snippet, included in <pre>...bibtex...</pre> Tag
	 * 
	 * @param doc
	 * @return 
	 */
	private String extractBibtex(final Document doc) {
		String bibtex = null;
		
		final NodeList list = doc.getElementsByTagName(CASES_JOURNAL_TAG_NAME);
		
		if (list.getLength() > 0) {
			Node node = list.item(0);
			bibtex = XmlUtils.getText(node);
			
			// remove spaces in bibtex key
			Pattern p = Pattern.compile(CASES_JOURNAL_BIBTEX_KEY_PATTERN, Pattern.DOTALL);
			Matcher m = p.matcher(bibtex);

			if (m.matches()) {
				bibtex = bibtex.replace(m.group(1), m.group(1).replaceAll(SPACE_PATTERN, ""));
			}
		}
		
		return bibtex;
	}

	public String getInfo() {
		return INFO;
	}

	
}
