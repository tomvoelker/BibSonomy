package org.bibsonomy.common.exceptions;

/**
 * a requested format is not supported
 *
 * @author dzo
 */
public class UnsupportedFormatException extends RuntimeException {
	private static final long serialVersionUID = 8025290508736426152L;
	
	private String format;

	/**
	 * @param format
	 */
	public UnsupportedFormatException(final String format) {
		super("format '" + format + "' not supported");
		this.format = format;
	}
	
	/**
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}
}
