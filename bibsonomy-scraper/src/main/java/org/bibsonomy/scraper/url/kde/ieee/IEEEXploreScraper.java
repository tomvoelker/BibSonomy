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

package org.bibsonomy.scraper.url.kde.ieee;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.CompositeScraper;
import org.bibsonomy.scraper.Tuple;


/** General scraper for IEEE Explore
 * @author rja
 *
 */
public class IEEEXploreScraper extends CompositeScraper {
	private static final String info = "IEEEXplore Scraper: This scraper creates a BibTeX entry for the media at " + 
	"<a href=\"http://ieeexplore.ieee.org/\">IEEEXplore</a> . Author: KDE";

	private static final String HOST = "ieeexplore.ieee.org";
	private static final String XPLORE_PATH = "/Xplore";
	private static final String SEARCH_PATH = "/search/";

	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();

	static {
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(XPLORE_PATH + ".*")));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(SEARCH_PATH + ".*")));
	}
	
	public IEEEXploreScraper() {
		/*
		 * TODO: can we remove this scraper and add this code directly into URLCompositeScraper?
		 * Tests for this scraper are broken because this code doesn't starts the chain anymore 
		 */
		addScraper(new IEEEXploreJournalProceedingsScraper());
		addScraper(new IEEEXploreBookScraper());
		addScraper(new IEEEXploreStandardsScraper());
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}