package org.bibsonomy.ibatis.enums;

/**
 * Constants that are used in SQL statements
 *
 * @author Christian Schenk
 */
public enum ConstantChar {
	/*
	 * HTTP constants
	 */
	HTTP_COOKIE_SPAMMER_CONTAINS('3');

	private final char ch;

	private ConstantChar(final char ch) {
		this.ch = ch;
	}

	public char getCh() {
		return this.ch;
	}
}