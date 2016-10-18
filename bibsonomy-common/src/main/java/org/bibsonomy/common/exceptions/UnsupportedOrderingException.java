package org.bibsonomy.common.exceptions;

/**
 * Exception thrown if an unsupported ordering is requested
 *
 * @author dzo
 */
public class UnsupportedOrderingException extends RuntimeException {
	private static final long serialVersionUID = 1221062491499989190L;

	/**
	 * @param order
	 */
	public UnsupportedOrderingException(String order) {
		super("Order '" + order + "' not supported");
	}
}
