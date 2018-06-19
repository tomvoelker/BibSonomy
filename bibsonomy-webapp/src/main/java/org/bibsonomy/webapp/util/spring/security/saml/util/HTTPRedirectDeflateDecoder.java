package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.xml.parse.ParserPool;

/**
 * adapter for the {@link HTTPRedirectDeflateDecoder} to add the correct {@link URIComparator}
 * @author dzo
 */
@Deprecated // remove when updating the saml spring security system
public class HTTPRedirectDeflateDecoder extends org.opensaml.saml2.binding.decoding.HTTPRedirectDeflateDecoder {

	/**
	 * @param comparator
	 */
	public HTTPRedirectDeflateDecoder(final URIComparator comparator) {
		super();

		this.setURIComparator(comparator);
	}

	/**
	 *
	 * @param pool
	 * @param comparator
	 */
	public HTTPRedirectDeflateDecoder(final ParserPool pool, final URIComparator comparator) {
		super(pool);
	}
}
