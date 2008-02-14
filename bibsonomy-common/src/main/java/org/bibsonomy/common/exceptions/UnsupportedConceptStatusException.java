package org.bibsonomy.common.exceptions;

import org.bibsonomy.common.enums.ConceptStatus;

/**
 * Exception thrown if an unsupported conceptStatus for concept queries is requested
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class UnsupportedConceptStatusException extends RuntimeException {

	/**
	 * uid
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Exception for unsupported types of ConceptStatus
	 * @see ConceptStatus
	 * @param status the requested status
	 */
	public UnsupportedConceptStatusException(final String status) {
		super("ConceptStatus " + status + " is not supported");
	}
}