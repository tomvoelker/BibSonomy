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
/**
 * 
 */
package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author sre
 * 
 */
public class UBKAScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "University Library (UB) Karlsruhe";
	private static final String UBKA_HOST_NAME = "http://primo.bibliothek.kit.edu";
	private static final String SITE_URL = UBKA_HOST_NAME + "/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME) + ".";

	private static final String UBKA_HOST = "primo.bibliothek.kit.edu";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + UBKA_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern UBKA_ID_PATTERN = Pattern.compile("doc=KITSRC(.*?)&");
	private static final String UBKA_BIBTEX_URL = "https://swb.bsz-bw.de/DB=2.1/SET=3/TTL=1/CMD?ACT=SRCHA&IKT=8138&SRT=RLV&TRM=";
	private static final Pattern BIBTEX_PAGE_PATTERN = Pattern.compile("^\\s*<TD align=\"left\" valign=\"top\"><div>&#xA0;</div>(.*)", Pattern.MULTILINE);

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final String id = extractId(sc.getUrl().toString());
		try {
			String bibtexUrl = UBKA_BIBTEX_URL + id + "&PRS=bibtex";
			final ScrapingContext scrapingContext = new ScrapingContext(new URL(bibtexUrl));
			final BibtexScraper bibtexScraper = new BibtexScraper();
			bibtexScraper.scrape(scrapingContext);
			String bibtex = scrapingContext.getBibtexResult();
			if (ValidationUtils.present(bibtex)) {
				sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString()));
				return true;
			}else {
				// the BibtexScraper is not working for test3. So we scrape with a regex.
				Matcher m_bibtex = BIBTEX_PAGE_PATTERN.matcher(scrapingContext.getPageContent());
				if (m_bibtex.find()){
					bibtex = StringEscapeUtils.unescapeHtml(m_bibtex.group(1))
									.replaceAll("<NOBR>", "\n")
									.replaceAll("</?[A-z]+>", "");
					sc.setBibtexResult(bibtex);
					return true;
				}
			}
		} catch (final IOException me) {
			throw new InternalFailureException(me);
		}
		/**
		 * Provisionally fixed broken scraper. Please check
		 */
		throw new ScrapingException("Can't find bibtex in scraped page.");
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

	private static String extractId(final String url) {
		final Matcher m = UBKA_ID_PATTERN.matcher(url);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}
