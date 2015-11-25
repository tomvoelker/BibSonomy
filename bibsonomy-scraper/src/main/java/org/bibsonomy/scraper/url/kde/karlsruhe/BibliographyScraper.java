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
package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraper for liinwww.ira.uka.de/bibliography
 * @author tst
 */
public class BibliographyScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "LIIN";
	private static final String SITE_URL = "http://liinwww.ira.uka.de/";
	private static final String INFO = "Scrapes BibTeX refrences from " + href(SITE_URL, SITE_NAME);
	
	private static final String HOST = "liinwww.ira.uka.de";
	private static final String PATH = "/cgi-bin/bibshow";
	
	private static final String BIBTEX_START_BLOCK = "<pre class=\"bibtex\">";
	private static final String BIBTEX_END_BLOCK = "</pre>";
	
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH + ".*")));
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
			sc.setScraper(this);
			
			String page = sc.getPageContent();
			
			if (page.indexOf(BIBTEX_START_BLOCK) > -1) {
				// cut off first part
				page = page.substring(page.indexOf(BIBTEX_START_BLOCK)+20);
				
				// cut off end
				page = page.substring(0, page.indexOf(BIBTEX_END_BLOCK));
				
				// clean up - links and span
				page = page.replaceAll("<[^>]*>", "");
				
				/*
				 * TODO: uncomment theese lines to add the url to our bibtex entry.
				 * but here is the problem: the example url contains a '}'. means:
				 * we have to change something inside the PseudoLexer. (Line 270)
				 * 
				// append url
				page = BibTexUtils.addFieldIfNotContained(page, "url", sc.getUrl().toString());
				
				// remove multiple commas
				Pattern p = Pattern.compile(",(\\s*),", Pattern.MULTILINE);
				Matcher m = p.matcher(page);
				page = m.replaceAll(",$1");*/
				
				sc.setBibtexResult(page);
				return true;
			}else
				throw new ScrapingException("Can't find bibtex in scraped page.");

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
}
