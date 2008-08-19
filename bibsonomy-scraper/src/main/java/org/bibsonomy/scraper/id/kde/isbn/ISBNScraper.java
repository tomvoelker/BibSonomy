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
	
	private static final String INFO = "ISBN support in scraped snippet";

	private static final String PATTERN_ISBN_10 = "(\\d{9}\\d?x?)";
	
	private static final String PATTERN_ISBN_13 = "(\\d{12}\\d?x?)";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getSelectedText() != null){
			String snippet = cleanISBN(sc.getSelectedText());
			String isbn = null;
			
			// check ISBN13
			isbn = getISBN13(snippet);
			if(isbn == null)
				// check ISBN10
				isbn = getISBN10(snippet);

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

	/**
	 * Search substring with ISBN10 format and returns it.
	 * @param snippet 
	 * @return ISBN10, null if no ISBN10 is available
	 */
	public static String getISBN10(String snippet){

		if(snippet != null){
			Pattern isbnPattern = Pattern.compile(PATTERN_ISBN_10, Pattern.CASE_INSENSITIVE);
			Matcher isbnMatcher = isbnPattern.matcher(snippet);
			
			if(isbnMatcher.find()){
				return isbnMatcher.group(1);
			}
		}
		
		return null;
	}

	/**
	 * Search substring with ISBN13 format and returns it.
	 * @param snippet 
	 * @return ISBN13, null if no ISBN13 is available
	 */
	public static String getISBN13(String snippet){
		
		if(snippet != null){
			Pattern isbnPattern = Pattern.compile(PATTERN_ISBN_13, Pattern.CASE_INSENSITIVE);
			Matcher isbnMatcher = isbnPattern.matcher(snippet);
			
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
		String checkISBN = null;
		
		// check ISBN13
		checkISBN = getISBN13(isbn);
		if(checkISBN == null)
			// check ISBN10
			checkISBN = getISBN10(isbn);

		// build worldcat.org URL
		if(checkISBN != null)
			return new URL("http://www.worldcat.org/search?qt=worldcat_org_all&q=" + checkISBN);
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
