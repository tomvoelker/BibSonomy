/*
 * Created on Mar 29, 2003
 *
 * @author henkel@cs.colorado.edu
 * 
 */
package bibtex.expansions;

/**
 * Exception thrown by an MacroReferenceExpander object. 
 * 
 * @author henkel
 */
public class MacroReferenceExpansionException extends ExpansionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5255662562139124653L;

	MacroReferenceExpansionException(final String message) {
		super(message);
	}
}