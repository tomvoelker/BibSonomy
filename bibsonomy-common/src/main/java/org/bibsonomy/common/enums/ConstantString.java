package org.bibsonomy.common.enums;

/**
 * Constants that are used in SQL statements
 *
 * @author Christian Schenk
 * @version $Id$
 */
public enum ConstantString {
	/*
	 * HTTP constants
	 */
	HTTP_COOKIE_SPAMMER_KEY("_lPost");

	private final String str;

	private ConstantString(final String str) {
		this.str = str;
	}

	public String getStr() {
		return this.str;
	}
}