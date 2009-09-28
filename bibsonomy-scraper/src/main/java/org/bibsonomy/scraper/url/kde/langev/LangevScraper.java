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

package org.bibsonomy.scraper.url.kde.langev;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class LangevScraper extends AbstractUrlScraper {

	private static final String info = "ISRL Scraper: This scraper parses a publication page from the " + href("http://www.isrl.uiuc.edu/", "The Graduate School of Library and Information Science at the University of Illinois");
	
	private static final String ISRL_HOST  = "isrl.uiuc.edu";
	private static final Pattern ISRL_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + ISRL_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final Matcher m = ISRL_PATTERN.matcher(sc.getPageContent());	
		if (m.matches()) {
			sc.setBibtexResult(m.group(1));
			return true;
		}else
			throw new PageNotSupportedException("no bibtex snippet found");
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return "GSLIS";
	}

	public String getSupportedSiteURL() {
		// TODO Auto-generated method stub
		return "http://www.lis.illinois.edu/";
	}

}
