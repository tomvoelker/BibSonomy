/*
 * Created on Mar 28, 2003
 *
 * @author henkel@cs.colorado.edu
 * 
 */
package bibtex.expansions;

/**
 * @author henkel
 */
public class PersonListParserException extends ExpansionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final String entryKey;
	
	PersonListParserException(final String message, final String entryKey) {
		super(message);
		this.entryKey = entryKey;
	}

	public String getEntryKey() {
		return entryKey;
	}
}