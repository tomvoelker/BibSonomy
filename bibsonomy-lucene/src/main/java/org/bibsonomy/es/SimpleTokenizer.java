package org.bibsonomy.es;

import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class SimpleTokenizer implements Iterable<String> {

	private static final Pattern nonStandardCharsPattern = Pattern.compile("[^\\p{Alpha}\\p{Digit}\\w]");
	private static final Pattern delimiterPattern = Pattern.compile("\\w*]");
	
	private final String queryString;
	
	/**
	 * 
	 */
	public SimpleTokenizer(String queryString) {
		this.queryString = nonStandardCharsPattern.matcher(queryString).replaceAll("");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		return new Scanner(queryString).useDelimiter(delimiterPattern);
	}

}
