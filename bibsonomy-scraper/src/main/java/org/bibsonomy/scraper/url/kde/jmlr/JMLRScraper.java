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
package org.bibsonomy.scraper.url.kde.jmlr;

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
 * Scraper for papers from http://jmlr.csail.mit.edu/
 * @author tst
 */
public class JMLRScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(JMLRScraper.class);
	
	private static final String SITE_NAME = "Journal of Machine Learning Research";
	private static final String SITE_URL = "http://jmlr.csail.mit.edu/";
	private static final String INFO = "Scraper for papers from " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "jmlr.csail.mit.edu";

	private static final String PATH = "/papers/";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern titlePattern = Pattern.compile("<h2>([^<]*)</h2>");
	private static final Pattern authorPattern = Pattern.compile("<i>([^<]*)</i></b>");
	private static final Pattern pageYearPattern = Pattern.compile("</i></b>([^<]*)</p>");
	private static final Pattern volumePattern = Pattern.compile("/papers/([^/]*)/");
	private static final Pattern yearPattern = Pattern.compile("(\\d{4})");
	private static final Pattern pagePattern = Pattern.compile(":([^,]*),");
	private static final Pattern lastnamePattern = Pattern.compile(" ([\\S]*) and");
	private static final Pattern abstractPattern = Pattern.compile("(?s)<h3>Abstract</h3>(.*)<font");
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		if (sc.getUrl().getPath().startsWith(PATH) && sc.getUrl().getPath().endsWith(".html")){
			String pageContent = sc.getPageContent();

			// get title (directly)
			String title = null;
			final Matcher titleMatcher = titlePattern.matcher(pageContent);
			if(titleMatcher.find())
				title = titleMatcher.group(1);

			// get author (directly)
			String author = null;
			final Matcher authorMatcher = authorPattern.matcher(pageContent);
			if(authorMatcher.find())
				author = authorMatcher.group(1);
			// clean up author string
			if(author != null)
				author = author.replace(",", " and");

			// get volume (from url)
			String volume = null;
			final Matcher volumeMatcher = volumePattern.matcher(sc.getUrl().getPath());
			if(volumeMatcher.find())
				volume = volumeMatcher.group(1);

			// get pageYear (directly)
			String pageYear = null;
			final Matcher pageYearMatcher = pageYearPattern.matcher(pageContent);
			if(pageYearMatcher.find())
				pageYear = pageYearMatcher.group(1);

			// extract year from pageYear string
			String year = null;
			final Matcher yearMatcher = yearPattern.matcher(pageYear);
			if(yearMatcher.find())
				year = yearMatcher.group(1);

			// extarct page from pageYear string
			String page = null;
			final Matcher pageMatcher = pagePattern.matcher(pageYear);
			if(pageMatcher.find())
				page = pageMatcher.group(1);

			/*
			 * build bibtex
			 */
			final StringBuffer bibtex = new StringBuffer("@proceedings{");

			// build bibtex key
			if(year != null && author != null){
				final Matcher lastnameMatcher = lastnamePattern.matcher(author);
				if(lastnameMatcher.find()) // combination lastname and year
					bibtex.append(lastnameMatcher.group(1)).append(year);
				else // only year
					bibtex.append(year);
			}else // default value
				bibtex.append("jmlrKey");
			bibtex.append(",\n");

			// add title
			appendField(bibtex, "title", title);
			// add author
			appendField(bibtex, "author", author);
			// add year
			appendField(bibtex, "year", year);
			// add page
			appendField(bibtex, "page", page);
			// add volume
			appendField(bibtex, "volume", volume);
			// add url
			appendField(bibtex, "url", sc.getUrl().toString());

			// remove last ","
			bibtex.deleteCharAt(bibtex.lastIndexOf(","));

			// last part
			bibtex.append("}");

			// finish
			sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(bibtex.toString(),"abstract",abstractParser(sc.getUrl())));
			return true;

		}
		throw new PageNotSupportedException("Select a page with the abtract view from a JMLR paper.");
	}
	private static String abstractParser(URL url){
		try{
			Matcher m = abstractPattern.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
		}catch(Exception e){
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
	private static void appendField(final StringBuffer bibtex, final String fieldName, final String fieldValue) {
		if (fieldValue != null) bibtex.append(fieldName + " = {" + fieldValue + "},\n");
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
