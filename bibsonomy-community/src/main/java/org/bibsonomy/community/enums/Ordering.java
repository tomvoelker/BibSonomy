package org.bibsonomy.community.enums;

/**
 * Defines some ordering criteria for lists of entities
 * 
 */
public enum Ordering {
	/** for ordering by adding time (desc) */
	ADDED,
	/** for ordering by popularity (desc) */
	POPULAR,
	/** for ordering by folkrank (desc) */
	FOLKRANK,	
	/** for ordering items by frequency (desc) */
	FREQUENCY,	
	/** for ordering items alphabetically */
	ALPH,
	/** for retrieving a random sample */
	RANDOM;
	

	/**
	 * Retrieve Order by name
	 * 
	 * @param name
	 *            the requested order (e.g. "folkrank")
	 * @return the corresponding Order enum
	 */
	public static Ordering getOrderByName(String name) {
		try {
			return Ordering.valueOf(name.toUpperCase());
		} catch (NullPointerException np) {
			throw new IllegalArgumentException("No order specified!");
		} catch (IllegalArgumentException ia) {
			throw new IllegalArgumentException("Requested order not supported. Possible values are 'added', 'popular', 'alph', 'frequency' or 'folkrank'");
		}
	}
}

