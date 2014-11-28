/*
 * Created on Mar 29, 2003
 *
 * @author henkel@cs.colorado.edu
 * 
 */
package bibtex.expansions;

/**
 * Exception thrown by an Expander object. 
 * 
 * @author henkel
 */
public class ExpansionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5255662562139124653L;

	ExpansionException(final Throwable cause){
		super(cause);
	}
	
	ExpansionException(final String message) {
		super(message);
	}
}