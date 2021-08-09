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
package org.bibsonomy.scraper.url.kde.arxiv;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.OAIToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.bibsonomy.util.ValidationUtils.present;


/**
 * Scraper for arXiv.
 * 
 * @author rja
 */
public class ArxivScraper extends AbstractUrlScraper {
	/** OAI to bibtex converter */
	private static final OAIToBibtexConverter OAI_CONVERTER = new OAIToBibtexConverter();
	private static final String info = "This scraper parses a publication page from " + href(ArxivUtils.SITE_URL, ArxivUtils.SITE_NAME)+".";

	protected static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(ArxivUtils.ARXIV_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	public boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		final URL url = sc.getUrl();
		final String selection = sc.getSelectedText();
		final String identifier;
		// get arxiv identifier from URL or selection
		if (!present(selection) && ArxivUtils.isArxivUrl(url)) {
			identifier = ArxivUtils.extractArxivIdentifier(url.toString());
		} else {
			identifier = ArxivUtils.extractArxivIdentifier(selection);
		}

		if (present(identifier)) {
			try {
				// build url for oai_dc export
				final String exportURL = "https://export.arxiv.org/oai2?verb=GetRecord&identifier=oai:arXiv.org:" + identifier + "&metadataPrefix=oai_dc";

				// download oai_dc reference
				final String reference = WebUtils.getContentAsString(exportURL);

				String bibtex = OAI_CONVERTER.toBibtex(reference);

				// add arxiv citation to note
				if (bibtex.contains("note = {")) {
					bibtex = bibtex.replace("note = {", "note = {cite arxiv:" + identifier + "\n");
					// if note not exist
				} else {
					bibtex = bibtex.replaceFirst("},", "},\nnote = {cite arxiv:" + identifier + "},");
				}
				// set result
				sc.setBibtexResult(bibtex);
				return true;
			} catch (IOException e) {
				throw new InternalFailureException(e);
			}
		}

		throw new ScrapingFailureException("no arxiv id found in URL");
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
		return ArxivUtils.isArxivUrl(scrapingContext.getUrl())
				|| ArxivUtils.isArxivUrl(scrapingContext.getSelectedText())
				|| ArxivUtils.containsStrictArxivIdentifier(scrapingContext.getSelectedText());
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
	public String getSupportedSiteName() {
		return ArxivUtils.SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return ArxivUtils.SITE_URL;
	}
}
