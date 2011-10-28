package org.bibsonomy.rest.util;

import static org.bibsonomy.util.UrlUtils.safeURIDecode;

import java.util.StringTokenizer;


/**
 * @author wla
 * @version $Id$
 */
public class URLDecodingStringTokenizer extends StringTokenizer {

	/**
	 * 
	 * @param str
	 */
	public URLDecodingStringTokenizer(final String str) {
		super(str);
	}
	
	/**
	 * 
	 * @param str
	 * @param delim
	 */
	public URLDecodingStringTokenizer(final String str, final String delim) {
		super(str, delim);
	}
	
	/**
	 * @param str
	 * @param delim
	 * @param returnDelims
	 */
	public URLDecodingStringTokenizer(final String str, final String delim, final boolean returnDelims) {
		super(str, delim, returnDelims);
	}

	@Override
	public String nextToken() {
		final String token = super.nextToken();
		return safeURIDecode(token);
	}

}
