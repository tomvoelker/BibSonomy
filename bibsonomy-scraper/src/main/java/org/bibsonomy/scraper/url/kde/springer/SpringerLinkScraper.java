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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.ISBNUtils;



/** Scraper für SpringerLink.
 * 
 * @author rja
 *
 */
public class SpringerLinkScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "SpringerLink";
	private static final String SITE_URL = "http://www.springerlink.com/";

	private static final Pattern CONTENT_PATTERN = Pattern.compile("content/(.+?)(/|$)");
	private static final Pattern ID_PATTERN = Pattern.compile("id=([^\\&]*)");

	private static final Pattern VIEW_STATE_PATTERN = Pattern.compile("id=\"__VIEWSTATE\" value=\"(.+?)\"");
	private static final Pattern EVENT_VALIDATION_PATTERN = Pattern.compile("id=\"__EVENTVALIDATION\" value=\"(.+?)\"");
	
	private static final Pattern SESSION_PATTERN = Pattern.compile("ASP\\.NET_SessionId=(\\w*+);");

	private static final Pattern YEAR_PATTERN_FOR_BIBTEX = Pattern.compile("(year[^\\{]*+\\{(.*?)\\})");
	private static final Pattern YEAR_PATTERN_FOR_PAGE = Pattern.compile("(?s)<div class=\"secondary\">.*?((20|19)\\d{2}+).*?</div>");
	
	private static final Pattern EXPORT_LINK_PATTERN = Pattern.compile("href=\"(/export-citation/[^\"]++)\"");
	private static final Pattern BIBTEX_LINK_PATTERN = Pattern.compile("class=\"bib\"[^>]*?href=\"([^\"]++)\"");
	
	private static final Pattern ABSTRACT_PATTERN_FOR_PAGE = Pattern.compile("(?ms)<div class=\"abstract-content formatted\" itemprop=\"description\">.*?<p class=\"a-plus-plus\">([^<]*+)");
	
	private static final String SPRINGER_CITATION_HOST_COM = "springerlink.com";
	private static final String SPRINGER_CITATION_HOST_DE = "springerlink.de";
	private static final String SPRINGER_CITATION_HOST_NEW = "link.springer.com";
	private static final String SPRINGER_LINK_METAPRESS = "springerlink.metapress.com";

	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";


	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();

	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_COM), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_DE), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST_NEW), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_LINK_METAPRESS), AbstractUrlScraper.EMPTY_PATTERN));
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final String url = sc.getUrl().toString();
		
		/*
		 * SpringerLink has setup a redirect to a new improved site.
		 * Let's see if we can scrape there first
		 */
		try {
			//get the publication page
			final HttpClient client = WebUtils.getHttpClient();
			final GetMethod method = new GetMethod(url);
			final String page = WebUtils.getContentAsString(client, method);
			
			//had the server returned response code 200?
			if (!present(page)) throw new ScrapingException("server did not return response code 200 for URL " + method.getURI());
			
			//fetch abstract
			Matcher abstractMatcher = ABSTRACT_PATTERN_FOR_PAGE.matcher(page);
			String abstractText = null;
			if (abstractMatcher.find()) {
				abstractText = abstractMatcher.group(1);
			}
			
			//see if there is a export link on the publication page		
			final Matcher exportLinkMatcher = EXPORT_LINK_PATTERN.matcher(page);
			if (exportLinkMatcher.find()) {
				//get the export panel page
				final URI uri = new URI(method.getURI(), exportLinkMatcher.group(1), true);
				final String panel = WebUtils.getContentAsString(client, uri);
				
				//had the server returned response code 200?
				if (!present(panel)) throw new ScrapingException("server did not return response code 200 for URL " + uri);
					
				//see if there is a BibTeX file offered on the export panel page
				final Matcher bibFileMatcher = BIBTEX_LINK_PATTERN.matcher(panel);
				if (!bibFileMatcher.find()) throw new ScrapingException("could not find link to BibTeX file");
				
				//download the BibTeX file now
				String relative = bibFileMatcher.group(1);
				String bibTeXResult = WebUtils.getContentAsString(client, new URI(uri, relative, true));
				if (!present(bibTeXResult)) throw new ScrapingException("BibTeX file not present");
				
				//add abstract
				if (present(abstractText)) {
					bibTeXResult = BibTexUtils.addFieldIfNotContained(bibTeXResult, "abstract", abstractText);
				}
				//fix missing bibtex key
				final int indexOfBrace = bibTeXResult.indexOf('{') + 1;
				if(indexOfBrace == bibTeXResult.indexOf('\n')){
					bibTeXResult = bibTeXResult.substring(0, indexOfBrace ) + "noKey," + bibTeXResult.substring(indexOfBrace);
				}
				//done
				sc.setBibtexResult(bibTeXResult);
				
				
				return true;
			}
			
			//alternatively look for isbn and use WorldCatScraper
			else {
				String isbn = ISBNUtils.extractISBN(page);
				if (present(isbn)) {
					String bibtex = WorldCatScraper.getBibtexByISBNAndReplaceURL(isbn, sc.getUrl().toString());
					if (!present(bibtex)) return false;
					sc.setBibtexResult(bibtex);
					return true;
				}
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}

		/*
		 * There was no export link and no isbn found on the specified location.
		 * Now try to scrape it the old SpringerLink way.
		 */
		try {
			/*
			 *  extract document ID
			 */
			final String docid;
			final Matcher mContent = CONTENT_PATTERN.matcher(url);
			final Matcher mId = ID_PATTERN.matcher(url);
			if (mContent.find()) {
				docid = mContent.group(1);
			} else if (mId.find()) {
				docid = mId.group(1);
			} else {
				/*
				 * could not find ID
				 */
				return false;
			}

			/*
			 * We need to get this page to extract the hidden form fields 
			 * "__VIEWSTATE" and "__EVENTVALIDATION".
			 *  
			 * Without those form fields we don't get access to the BibTeX
			 * entry.
			 */
			final Matcher sessionMatcher = SESSION_PATTERN.matcher(WebUtils.getCookies(new URL(url)));
			if (!sessionMatcher.find()) throw new ScrapingException("No Session Cookie!");
			final String cookies = "ASP.NET_SessionId=" + sessionMatcher.group(1) + "; CookiesSupported=True; highlighterEnabled=true; MUD=MP";
			final String exportURL = SITE_URL + "content/" + docid + "/export-citation/";
			final String formPage = WebUtils.getContentAsString(exportURL, cookies);

			final Matcher viewStateMatcher = VIEW_STATE_PATTERN.matcher(formPage);
			final Matcher eventValidationMatcher = EVENT_VALIDATION_PATTERN.matcher(formPage);
			if (viewStateMatcher.find() && eventValidationMatcher.find()) {
				/*
				 * both form fields found! :-)
				 */
				final String postContent = 
					"__VIEWSTATE=" + UrlUtils.safeURIEncode(viewStateMatcher.group(1)) + 
					"&ctl00%24ctl14%24cultureList=de-de" +
					"&ctl00%24ctl14%24SearchControl%24BasicSearchForTextBox=" +
					"&ctl00%24ctl14%24SearchControl%24BasicAuthorOrEditorTextBox=" +
					"&ctl00%24ctl14%24SearchControl%24BasicPublicationTextBox=" +
					"&ctl00%24ctl14%24SearchControl%24BasicVolumeTextBox=" +
					"&ctl00%24ctl14%24SearchControl%24BasicIssueTextBox=" +
					"&ctl00%24ctl14%24SearchControl%24BasicPageTextBox=" +
					"&ctl00%24ContentPrimary%24ctl00%24ctl00%24Export=AbstractRadioButton" +
					"&ctl00%24ContentPrimary%24ctl00%24ctl00%24CitationManagerDropDownList=BibTex" +
					"&ctl00%24ContentPrimary%24ctl00%24ctl00%24ExportCitationButton=Zitierung+exportieren+" +
					"&__EVENTVALIDATION=" + UrlUtils.safeURIEncode(eventValidationMatcher.group(1));
				final String bibtexEntry = WebUtils.getPostContentAsString(cookies ,new URL(exportURL), postContent);

				/*
				 * Job done
				 */
				if (present(bibtexEntry)) {
					String cleanEntry = cleanEntry(bibtexEntry);
					cleanEntry = insertYearIfNotContained(cleanEntry, sc);
					sc.setBibtexResult(cleanEntry);
					
					return true;
				} 
			}
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (MalformedURLException e) {
			throw new InternalFailureException(e);
		} catch (IOException e) {
			throw new InternalFailureException(e);
		}
	}
	
	private static String insertYearIfNotContained(String bibtex, ScrapingContext sc) throws ScrapingException {
		if (!bibtexContainsYear(bibtex)) {
			String page = sc.getPageContent();
			Matcher m = YEAR_PATTERN_FOR_PAGE.matcher(page);
			if (m.find()) {
				return insertYear(bibtex, m.group(1));
			}
		}
		return bibtex;
	}
	
	private static boolean bibtexContainsYear(String bibtex) {
		Matcher yearMatcher = YEAR_PATTERN_FOR_BIBTEX.matcher(bibtex);
		if (yearMatcher.find()) {
			String year = yearMatcher.group(2).trim();
			if (year.length() == 4) {
				return true;
			}
		}
		return false;
	}
	
	private static String insertYear(final String bibtex, final String year) {
		Matcher m = YEAR_PATTERN_FOR_BIBTEX.matcher(bibtex);
		if (!m.find()) return BibTexUtils.addFieldIfNotContained(bibtex, "year", year);
		String emptyYear = m.group(1);
		return bibtex.replace(emptyYear, "year={" + year + "}");
	}

	/**
	 * Cleans up some things in the BibTeX entry.
	 * 
	 * @param s
	 * @return
	 */
	private static String cleanEntry(final String s) {
		/*
		 * The DOI is hidden in the "note" field -> rename it to "doi".
		 */
		final String s1 = s.replace("note = {", "doi = {");
		/*
		 * The publisher field not only contains the publisher name (i.e.,
		 * "Springer") but also the address (i.e., "Berlin / Heidelberg"). We
		 * split the field into two fields "publisher" and "address".
		 */
		final String s2 = s1.replace("Springer Berlin", "Springer},\n   address = {Berlin");
		/*
		 * There is a space between the entry type and the first "{" - that is
		 * not valid BibTeX. Therefore, we remove that space.
		 */
		final String s3 = s2.replaceFirst(" \\{", "{");
		
		return s3;
	}

	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
