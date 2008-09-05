package org.bibsonomy.scraper.id.kde.isbn;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;

/**
 * Scraper for ISBN support. Searchs for ISBN in snippet and use WorldcatScraper for download. 
 * @author tst
 * @version $Id$
 */
public class ISBNScraper implements Scraper {
	
	private static final String WORLDCAT_URL = "http://www.worldcat.org/search?qt=worldcat_org_all&q=";

	private static final String INFO = "ISBN support in scraped snippet";

	/*
	 * patterns to match ISBN 10 and 13
	 */
	private static final Pattern isbn10Pattern = Pattern.compile("(\\d{9}\\d?x?)", Pattern.CASE_INSENSITIVE);
	private static final Pattern isbn13Pattern = Pattern.compile("(\\d{12}\\d?x?)", Pattern.CASE_INSENSITIVE);

	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getSelectedText() != null){
			final String isbn = getISBN(cleanISBN(sc.getSelectedText()));

			if(isbn != null){
				WorldCatScraper worldcatScraper = new WorldCatScraper();
				String bibtex = null;
				try {
					bibtex = worldcatScraper.getBibtexByISBN(isbn, sc);
					
					if(bibtex != null){
						sc.setBibtexResult(bibtex);
						sc.setScraper(this);
						return true;
					}else
						throw new ScrapingFailureException("bibtex download from worldcat failed");
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
			}
		}
		return false;
	}

	public static String getISBN(String snippet) {
		String isbn;
		// check ISBN13
		isbn = extractISBN(isbn13Pattern, snippet);
		if(isbn == null)
			// check ISBN10
			isbn = extractISBN(isbn10Pattern, snippet);
		return isbn;
	}

	/**
	 * Search substring with pattern format and returns it.
	 * @param pattern - the pattern to match snippet.
	 * @param snippet 
	 * @return ISBN, null if no ISBN is available
	 */
	private static String extractISBN(final Pattern pattern, final String snippet){
		if(snippet != null){
			final Matcher isbnMatcher = pattern.matcher(snippet);
			if(isbnMatcher.find()){
				return isbnMatcher.group(1);
			}
		}
		return null;
	}

	/**
	 * builds a worldcat.org URL with the given ISBN
	 * @param isbn valid ISBN
	 * @return URL from worldcat.org, null if no ISBN is give 
	 * @throws MalformedURLException 
	 */
	public static URL getUrlForIsbn(String isbn) throws MalformedURLException{
		final String checkISBN = getISBN(isbn);

		// build worldcat.org URL
		if(checkISBN != null)
			return new URL(WORLDCAT_URL + checkISBN);
		return null;
	}
	
	/**
	 * remove seperation signs between numbers
	 * @param snippet from ScrapingContext
	 * @return snippet without " " and "-"
	 */
	public static String cleanISBN(String snippet){
		snippet = snippet.replace(" ", "");
		snippet = snippet.replace("-", "");
		return snippet;
	}
}
