package org.bibsonomy.model.enums;

/**
 * Defines some ordering criteria for lists of entities
 * 
 * @author Jens Illig
 * @version $Id$
 */
public enum Order {
	/** for ordering by adding time (desc) */
	ADDED,
	/** for ordering by popularity (desc) */
	POPULAR, 
	/** for ordering by folkrank (desc) */
	FOLKRANK;
	
	/**
	 * Retrieve Order by name
	 * 
	 * @param name the requested order (e.g. "folkrank")
	 * @return the corresponding Order enum
	 */
	public static Order getOrderByName(String name) {
		try {
			return Order.valueOf(name.toUpperCase());
		}
		catch (NullPointerException np) {
			throw new IllegalArgumentException("No order specified!");
		}
		catch (IllegalArgumentException ia) {
			throw new IllegalArgumentException("Requested order not supported. Possible values are 'added', 'popular' or 'folkrank'");
		}
	}	
}