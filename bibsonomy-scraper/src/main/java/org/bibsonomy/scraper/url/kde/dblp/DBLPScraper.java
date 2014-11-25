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
package org.bibsonomy.scraper.url.kde.dblp;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author wbi
 */
public class DBLPScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "University of Trier Digital Bibliography & Library Project";
	private static final String DBLP_HOST_NAME1  = "http://dblp.uni-trier.de";
	private static final String SITE_URL  = DBLP_HOST_NAME1+"/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String DBLP_HOST1  = "dblp.uni-trier.de";
	private static final String DBLP_HOST_NAME2  = "http://search.mpi-inf.mpg.de/dblp/";
	private static final String DBLP_HOST2  = "search.mpi-inf.mpg.de";
	private static final String DBLP_PATH2  = "/dblp/";

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST1), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST2), Pattern.compile(DBLP_PATH2 + ".*")));
	}
	
	/*
	 * These are no mirrors, they just link to above hosts
	 */
	/*
	private static final String DBLP_HOST_NAME3  = "http://www.sigmod.org/dblp/";
	private static final String DBLP_HOST_NAME4  = "http://www.vldb.org/dblp/";
	private static final String DBLP_HOST_NAME5  = "http://sunsite.informatik.rwth-aachen.de/dblp/";
	 */
	private static final Pattern DBLP_PATTERN = Pattern.compile("(<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>)+", Pattern.MULTILINE | Pattern.DOTALL);

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
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
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException {
		return url.toString().replace("bibtex", "bib") + ".bib";
	}
}
