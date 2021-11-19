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
package org.bibsonomy.scraper.url.kde.pubmed;


import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/**
 *
 * @author Christian Kramer
 *
 */
public class PubMedScraper extends GenericRISURLScraper {
	private static final String SITE_NAME = "PubMed";
	private static final String SITE_URL = "https://www.ncbi.nlm.nih.gov/";
	private static final String INFO = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "ncbi.nlm.nih.gov";
	private static final String PUBMED_EUTIL_HOST = "eutils.ncbi.nlm.nih.gov";
	private static final String UK_PUBMED_CENTRAL_HOST = "ukpmc.ac.uk";
	private static final String EUROPE_PUBMED_CENTRAL_HOST = "europepmc.org";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<>();

	private static final Pattern PMID_FROM_URL = Pattern.compile("\\d+");

	static {
		PATTERNS.add(new Pair<>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<>(Pattern.compile(".*" + PUBMED_EUTIL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<>(Pattern.compile(".*" + UK_PUBMED_CENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		PATTERNS.add(new Pair<>(Pattern.compile(".*" + EUROPE_PUBMED_CENTRAL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final String path = url.getPath();
		final Matcher ma = PMID_FROM_URL.matcher(path);
		if (ma.find()) {
			final String pubmedId = ma.group();
			return "https://api.ncbi.nlm.nih.gov/lit/ctxp/v1/pubmed/?format=ris&id=" + pubmedId;
		}

		return null;
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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