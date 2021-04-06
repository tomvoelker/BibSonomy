package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.xml.parse.ParserPool;

/**
 * adapter to add the correct {@link URIComparator}
 *
 * @author dzo
 */
@Deprecated // remove when updating the saml spring security system
public class HTTPPostDecoderSaml2 extends HTTPPostDecoder {

	/**
	 * @param comparator the comparator to use
	 */
	public HTTPPostDecoderSaml2(final URIComparator comparator) {
		super();
		this.setURIComparator(comparator);
	}

	/**
	 *
	 * @param pool the pool to use
	 */
	public HTTPPostDecoderSaml2(final ParserPool pool, final URIComparator comparator) {
		super(pool);
		this.setURIComparator(comparator);
	}
}
