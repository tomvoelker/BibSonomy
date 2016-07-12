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
package org.bibsonomy.scraper.url.kde.langev;

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
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * this scraper is now disabled because the website http://www.isrl.uiuc.edu/ is no longer available
 */
public class LangevScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(LangevScraper.class);
	
	private static final String SITE_NAME = "The Graduate School of Library and Information Science at the University of Illinois";
	private static final String SITE_URL = "http://www.isrl.uiuc.edu/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String ISRL_HOST  = "isrl.uiuc.edu";
	private static final Pattern ISRL_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + ISRL_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("(?s)(Abstract|Description).*\\s+<blockquote>\\s+(.*)\\s+</blockquote>");
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		final Matcher m = ISRL_PATTERN.matcher(sc.getPageContent());
		if (m.matches()) {
			sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(m.group(1),"abstract",abstractParser(sc.getUrl())));
			return true;
		}
		
		throw new PageNotSupportedException("no bibtex snippet found");
	}

	private static String abstractParser(URL url){
		try{
			Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(2);
			}
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

}
