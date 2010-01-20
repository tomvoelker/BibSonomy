package org.bibsonomy.common.enums;

/**
 * @author rja
 * @version $Id$
 */
public enum ConceptUpdateOperation {
	
	/**
	 * Completely updates the given concept.
	 */
	UPDATE,
	
	/**
	 * Sets the status of the given concept to picked.  
	 */
	PICK,
	
	/**
	 * Sets the status of the given concept to UNpicked.
	 */
	UNPICK,
	
	/**
	 * Sets the status of all concepts of the given grouping entity to picked.
	 */
	PICK_ALL,
	
	/**
	 * Sets the status of all concepts of the given grouping entity to UNpicked. 
	 */
	UNPICK_ALL;
}
