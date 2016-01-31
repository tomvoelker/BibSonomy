/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.worldcat;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper for http://www.worldcat.org 
 * @author tst
 */
public class WorldCatScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Worldcat";
	private static final String SITE_URL = "http://www.worldcat.org/";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String WORLDCAT_URL = "http://www.worldcat.org/search?qt=worldcat_org_all&q=";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "worldcat.org"), Pattern.compile("/oclc/")));

	private static final Pattern PATTERN_GET_FIRST_SEARCH_RESULT = Pattern.compile("<a href=\"([^\\\"]*brief_results)\">");
	
	private static final RisToBibtexConverter converter = new RisToBibtexConverter();

	//for preprocessing of Worldcat RIS
	private static final String KEY_VALUE_SEPARATOR = "  - ";
	private static final String LINE_DELIMITER = "\n";
	private static final Pattern AUTHOR_CORRECT_PATTERN = Pattern.compile("^[^,]+, [^,]+$");
	private static final Pattern AUTHOR_EXTRACTOR_PATTERN = Pattern.compile("([^,]+, [^,]+),");

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		try {
			final String bibtex = getBibtex(sc.getUrl(), false);

			if (present(bibtex)) {
				sc.setBibtexResult(bibtex);
				return true;
			} else
				throw new ScrapingFailureException("getting bibtex failed");
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	/**
	 * search publication on worldcat.org with a given isbn and returns it as bibtex
	 * @param isbn isbn for search
	 * @return publication as bibtex
	 * @throws IOException 
	 * @throws ScrapingException
	 */
	public static String getBibtexByISBN(final String isbn) throws IOException, ScrapingException{
		String bibtex = getBibtex(new URL(WORLDCAT_URL + ISBNUtils.cleanISBN(isbn)), true);
		return BibTexUtils.addFieldIfNotContained(bibtex, "isbn", isbn);
	}
	
	/**
	 * search publication on worldcat.org with a give issn and returns it as bibtex
	 * @param issn
	 * @return publication as bibtex
	 * @throws IOException
	 * @throws ScrapingException
	 */
	public static String getBibtexByISSN(final String issn) throws IOException, ScrapingException{
		return getBibtex(new URL(WORLDCAT_URL + issn), true);
	}


	/**
	 * builds a worldcat.org URL with the given ISBN
	 * @param isbn valid ISBN
	 * @return URL from worldcat.org, null if no ISBN is give 
	 * @throws MalformedURLException 
	 */
	public static URL getUrlForIsbn(final String isbn) throws MalformedURLException{
		final String checkISBN = ISBNUtils.extractISBN(isbn);

		// build worldcat.org URL
		if (present(checkISBN))
			return new URL(WORLDCAT_URL + checkISBN);
		return null;
	}

	/**
	 * search publication on worldcat.org with a given isbn and returns it as bibtex.
	 * before converting RIS to bibtex RIS gets equipped with specified replacementURL.
	 * @param isbn
	 * @param replacementURL
	 * @return bibtex string
	 * @throws IOException
	 * @throws ScrapingException
	 */
	public static String getBibtexByISBNAndReplaceURL(final String isbn, final String replacementURL) throws IOException, ScrapingException{
		URL publPageURL = new URL(WORLDCAT_URL + ISBNUtils.cleanISBN(isbn));
		String ris = getRIS(publPageURL, true);
		if (ris == null) return null;
		Matcher m = Pattern.compile("UR\\s{2}-\\s(.*\\n)+?\\p{Upper}\\p{Alnum}\\s{2}-\\s").matcher(ris);
		if (m.find()) {
			ris = ris.replace(m.group(1), replacementURL + LINE_DELIMITER);
		} else {
			ris = ris.replaceFirst("ER\\s{2}-\\s[\\n.]*\\z", "UR  - " + replacementURL + "\nER  - ");
		}
		String bibtex = converter.risToBibtex(ris);
		return BibTexUtils.addFieldIfNotContained(bibtex, "isbn", isbn);
	}
	
	/**
	 * TODO: document what we fix here
	 * temporary fix for and RIS export error of Worldcat, which exports data not fitting to the
	 * RIS standart
	 * 
	 * -> because the issue only happens with RIS from worldcat, this is placed here and not
	 * in the {@link RisToBibtexConverter}
	 * 
	 * @param ris the ris string to preprocess
	 * 
	 * @return the preprocessed ris
	 */
	private static String preprocessWorldcatRIS(final String ris) {
		
		//get all entries
		final String[] entries = ris.split(LINE_DELIMITER);
		
		final StringBuilder correctRIS = new StringBuilder();
		
		for (final String entry : entries) {
			final String key = entry.split(KEY_VALUE_SEPARATOR)[0].trim();
			
			// check whether the field is the main author's one
			if (key.equals("A1") || key.equals("AU")) {
				final String value = entry.split(KEY_VALUE_SEPARATOR)[1];
				
				final Matcher correctAuthor = AUTHOR_CORRECT_PATTERN.matcher(value);
				if (correctAuthor.matches()) {
					correctRIS.append("A1" + KEY_VALUE_SEPARATOR + value + LINE_DELIMITER);
				} else {
					final Matcher authorExtractor = AUTHOR_EXTRACTOR_PATTERN.matcher(value);
					
					//is the ris not well formatted?
					while (authorExtractor.find()) {
						final String author = authorExtractor.group(1);
						if (present(author)) {
							correctRIS.append("A1" + KEY_VALUE_SEPARATOR + author.trim() + LINE_DELIMITER);
						}
					}
				}
			}
			//not an author field -> keep it
			else {
				correctRIS.append(entry + LINE_DELIMITER);
			}
		}
		
		return correctRIS.toString();
	}

	private static String getRIS(final URL publPageURL, final boolean search) throws IOException, ScrapingException {
		final String publPageContent = WebUtils.getContentAsString(publPageURL);
		final Matcher matcherFirstSearchResult = PATTERN_GET_FIRST_SEARCH_RESULT.matcher(publPageContent);
		
		final URL publUrl;
		if (matcherFirstSearchResult.find()) {
			publUrl = new URL(publPageURL.getProtocol() + "://" + publPageURL.getHost() + matcherFirstSearchResult.group(1));
		// search not successful
		} else if (search && publPageContent.contains("div class=\"error-results\" id=\"div-results-none\"")) {
			throw new ScrapingException("Content not available.");
		} else {
			publUrl = publPageURL;
		}
		
		String exportUrl = publUrl.getProtocol() + "://" + publUrl.getHost() + publUrl.getPath() + "?page=endnote&client=worldcat.org-detailed_record";
		/*
		 * append query
		 */
		if (search) {
			exportUrl += "&" + publUrl.getQuery();
		}
		
		final String ris = WebUtils.getContentAsString(new URL(exportUrl));
		if (!ris.startsWith("TY")) {
			return null;
		}
		
		return preprocessWorldcatRIS(ris);
	}
	
	private static String getBibtex(final URL publPageURL, final boolean search) throws IOException, ScrapingException {
		String ris = getRIS(publPageURL, search);
		if (ris == null) return null;
		
		/*
		 * convert RIS to BibTeX
		 */
		final String bibtex = converter.risToBibtex(ris);
		
		/*
		 * add URL
		 */
		return BibTexUtils.addFieldIfNotContained(bibtex, "url", publPageURL.toString());
	}
	
	@Override
	public String getInfo() {
		return INFO;
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
