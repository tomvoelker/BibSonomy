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
package org.bibsonomy.scraper.url.kde.jmlr;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.HTMLMetaDataHighwirePressToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for papers from http://jmlr.csail.mit.edu/
 * @author tst
 */
public class JMLRScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Journal of Machine Learning Research";
	private static final String SITE_URL = "http://jmlr.csail.mit.edu/";
	private static final String INFO = "Scraper for papers from " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "jmlr.csail.mit.edu";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<h3>Abstract</h3>([\\s\\S]*?)<font color");
	private static final HTMLMetaDataHighwirePressToBibtexConverter converter = new HTMLMetaDataHighwirePressToBibtexConverter();
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		String bibtex;

		try {
			String bibtexUrl = sc.getUrl().toString().replaceAll("(?=.)([A-z]+?)$", "bib");
			bibtex =  WebUtils.getContentAsString(bibtexUrl);
		} catch (IOException e) {
			bibtex = converter.toBibtex(sc.getPageContent());
		}

		if (!present(bibtex)){
			return false;
		}

		try {
			String abstractUrl = sc.getUrl().toString().replaceAll("(?=.)([A-z]+?)$", "html");
			Matcher m_abstract = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(abstractUrl));

			if (m_abstract.find()){
				String bibtexAbstract =  m_abstract.group(1).replaceAll("<.*?>", "").trim();
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex,"abstract", bibtexAbstract);
			}
		} catch (IOException ignored) {
			//we didn't get an abstract, but we still have the bibtex. We will ignore this error
		}
		sc.setBibtexResult(bibtex);
		return true;
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
