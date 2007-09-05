package scraper;

/**
 * If a Scraper throws an exception it should extend from this exception or throw this exception.
 *
 */
public class ScrapingException extends Exception {

	private static final long serialVersionUID = -5322213549739868471L;

	public ScrapingException(String s) {
		super(s);
	}
	
	public ScrapingException(Exception s) {
		super(s);
	}
}
