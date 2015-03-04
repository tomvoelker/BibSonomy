/*
 * Created on Oct 29, 2003
 * 
 * @author henkel@cs.colorado.edu
 *  
 */
package bibtex.expansions;

import java.util.LinkedList;
import java.util.List;


/**
 * @author henkel
 */
public abstract class AbstractExpander {
	
	private final List<ExpansionException> exceptions;
	private ExpansionException[] exceptionsAsArrays;
	private final boolean throwAllExpansionExceptions;

	/**
	 * @param throwAllExpansionExceptions
	 *            Setting this to true means that all exceptions will be thrown
	 *            immediately. Otherwise, the expander will skip over things it
	 *            can't expand and you can use getExceptions to retrieve the
	 *            exceptions later
	 */

	protected AbstractExpander(boolean throwAllExpansionExceptions) {
		this.throwAllExpansionExceptions = throwAllExpansionExceptions;
		this.exceptions = throwAllExpansionExceptions ? null : new LinkedList<ExpansionException>();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.expansions.Expander#getExceptions()
	 */
	public ExpansionException[] getExceptions() {
		return this.exceptionsAsArrays;
	}

	/**
	 * Call this at the end of your expand(BibtexFile) implementation.
	 */
	protected void finishExpansion() {
		if(this.exceptions==null) return;
		this.exceptionsAsArrays = new ExpansionException[this.exceptions.size()];
		this.exceptions.toArray(this.exceptionsAsArrays);
		this.exceptions.clear();
	}

	/**
	 * Call this whenever you want to throw an ExpansionException. This method may or may not throw
	 * the exception, depending on the throwAllExpansionExceptions flag.
	 * 
	 * @param ex
	 * @throws ExpansionException
	 */
	protected void throwExpansionException(final ExpansionException ex) throws ExpansionException {
		if (this.throwAllExpansionExceptions)
			throw ex;
		else {
			try {
				throw ex;
			} catch (ExpansionException e) {
				this.exceptions.add(e);
			}
		}
	}

}
