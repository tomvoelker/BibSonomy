/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.scielo;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author Mohammed Abed
 */
public class SCIELOScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "SciELO Scientific Electronic Library Online";
	/*
	 * scielo contains many subdomains. They can all be found on http://www.scielo.org/.
	 * Most subdomains have a bibtex for their articles, but others like scielo.org and scielo.br must be scraped with the HighwirePressScraper.
	 */
	private static final String SITE_URL = "http://www.scielo.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";

	private static final Pattern URL_PATTERN = Pattern.compile("pid=(.*?)(?:&|\\Z)");
	private static final String DOWNLOADLINK = "/scielo.php?download&format=BibTex&pid=";
	private static final Pattern KEY_PATTERN = Pattern.compile("@[A-z]+\\{(.*?),");
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.org.bo"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.org.ar"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.cl"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.sa.cr"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.sld.cu"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.senescyt.gob.ec"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.org.mx"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.iics.una.py"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.org.pe"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.pt"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.org.za"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.isciii.es"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.edu.uy"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "ve.scielo.org"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "cienciaecultura.bvs.br"), Pattern.compile("scielo.php")));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scielo.edu.uy"), Pattern.compile("scielo.php")));

	}

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final Matcher m = URL_PATTERN.matcher(url.toString());
		if (m.find()) {
			return "http://" + url.getHost() + DOWNLOADLINK + m.group(1);
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
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		String fixedBibtex = bibtex;
		Matcher m_keyFix = KEY_PATTERN.matcher(fixedBibtex);
		if (m_keyFix.find()){
			String bibtexKey = m_keyFix.group(1);
			String fixedBibtexKey = bibtexKey.replace(" ", "_");
			fixedBibtex = fixedBibtex.replace(bibtexKey, fixedBibtexKey);
		}
		return fixedBibtex;
	}
}
