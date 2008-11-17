package org.bibsonomy.rest.validation;

import org.bibsonomy.model.BibTex;

/** Validates the given model.
 * 
 * @author rja
 * @version $Id$
 */
public interface ModelValidator {

	/** Check the bibtex for correctness.
	 * @param bibtex
	 */
	public void checkBibTeX(final BibTex bibtex);
	
}
