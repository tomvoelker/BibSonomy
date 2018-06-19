package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.xml.parse.ParserPool;

/**
 * adapter for the {@link HTTPSOAP11DecoderImpl} to add the correct {@link URIComparator}
 * @author dzo
 */
@Deprecated // remove when updating the saml spring security system
public class HTTPSOAP11DecoderImpl extends org.opensaml.saml2.binding.decoding.HTTPSOAP11DecoderImpl {

	/**
	 * default constructor
	 * @param pool
	 * @param comparator
	 */
	public HTTPSOAP11DecoderImpl(final ParserPool pool, final URIComparator comparator) {
		super(pool);

		this.setURIComparator(comparator);
	}
}
