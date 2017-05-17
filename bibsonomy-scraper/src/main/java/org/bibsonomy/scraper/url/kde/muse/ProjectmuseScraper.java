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
package org.bibsonomy.scraper.url.kde.muse;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
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
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Scraper for muse.jhu.edu
 * @author tst
 */
public class ProjectmuseScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(ProjectmuseScraper.class);
	
	private static final String SITE_NAME = "Project MUSE";
	private static final String SITE_URL = "http://muse.jhu.edu/";
	private static final String INFO = "Scraper for citations from " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "muse.jhu.edu";

	private static final Pattern references_pattern = Pattern.compile("<meta name=\"citation_reference\" content=\"(.*?)\">");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern ENDNOTE_PATTERN = Pattern.compile("<h2>Endnote<\\/h2>\\s*<p>(.*?)<\\/p>", Pattern.DOTALL); 
	private static final Pattern ID_PATTERN = Pattern.compile("([^/]+$)");
	private static final Pattern TYPE_PATTERN = Pattern.compile(SITE_URL + "(.*)/.*");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<meta name=\"citation_abstract\" content=\"<p>(.*?)<\\/p>\">", Pattern.DOTALL);
	
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		try {
			Matcher idMatcher = ID_PATTERN.matcher(sc.getUrl().toString());
			if (idMatcher.find()) {
				String id = idMatcher.group(1);
				
				Matcher typeMatcher = TYPE_PATTERN.matcher(sc.getUrl().toString());
				if (typeMatcher.find()) {
					String type = typeMatcher.group(1);

					String content = WebUtils.getContentAsString(SITE_URL + "view_citations?type=" + type + "&id=" + id);
					Matcher m = ENDNOTE_PATTERN.matcher(content);
					if (m.find()) {
						//Projectmuse says Endnote, but it's actually ris
						String bibtex = new RisToBibtexConverter().toBibtex(m.group(1));
						
						//add abstract		
						bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractParser(sc.getUrl()));
						
						sc.setBibtexResult(bibtex);
						return true;
					} 
				} 
			} 		
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}

	private static String abstractParser(URL url){
		try{
			Matcher m = ABSTRACT_PATTERN.matcher(HtmlUtils.htmlUnescape(WebUtils.getContentAsString(url)));
			if(m.find()) {
				return m.group(1).trim();
			}
		} catch (final IOException e) {
			log.error("error while getting abstract " + url, e);
		}
		return null;
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
	public boolean scrapeReferences(ScrapingContext scrapingContext)throws ScrapingException {
		try {
			final Matcher m = references_pattern.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			
			StringBuffer matches = new StringBuffer();
			while (m.find()) {
				matches.append(m.group() + "\n");
			}
			if (matches.length() != 0) {
				scrapingContext.setReferences(matches.toString());
				return true;
			}
		} catch (final Exception e) {
			log.error("error while scraping references " + scrapingContext.getUrl(), e);
		}
		return false;
	}
}
