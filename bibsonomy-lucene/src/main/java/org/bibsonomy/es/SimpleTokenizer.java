package org.bibsonomy.es;

import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A very simple tokenizer
 *
 * @author jensi
 */
public class SimpleTokenizer implements Iterable<String> {

	private static final Pattern nonStandardCharsPattern = Pattern.compile("[^\\p{Alpha}\\p{Digit}\\s]");
	private static final Pattern delimiterPattern = Pattern.compile("\\s{1,}");
	
	private final String toBeTokenized;
	
	/**
	 * @param toBeTokenized the string to be tokenized
	 */
	public SimpleTokenizer(String toBeTokenized) {
		if (toBeTokenized == null) {
			this.toBeTokenized = toBeTokenized;
		} else {
			this.toBeTokenized = nonStandardCharsPattern.matcher(toBeTokenized).replaceAll("");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		if (this.toBeTokenized == null) {
			return Collections.<String>emptyList().iterator();
		}
		return new Scanner(toBeTokenized).useDelimiter(delimiterPattern);
	}

}
